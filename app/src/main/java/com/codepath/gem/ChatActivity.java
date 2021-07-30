package com.codepath.gem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.gem.adapters.ChatAdapter;
import com.codepath.gem.models.Conversation;
import com.codepath.gem.models.Experience;
import com.codepath.gem.models.Message;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";
    public static final String KEY_SEND_USER = "sendUser";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    EditText etMessage;
    ImageButton ibSend;
    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    Boolean mFirstLoad; // tracks initial load to scroll to bottom of list view
    Experience exp;
    Conversation convo;
    ParseUser currUser, expHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etMessage = (EditText) findViewById(R.id.etMessage);
        ibSend = (ImageButton) findViewById(R.id.ibSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        expHost = getIntent().getExtras().getParcelable(KEY_SEND_USER);
        mAdapter = new ChatAdapter(ChatActivity.this, ParseUser.getCurrentUser(), expHost, mMessages);
        rvChat.setAdapter(mAdapter);
        currUser = ParseUser.getCurrentUser();


        // associate the LayoutManager with the RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true); // order messages from newest to oldest
        rvChat.setLayoutManager(linearLayoutManager);

        // When send button is clicked, create message object on Parse
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewMessage();
            }
        });

        setConvo();
        Handler handler = new Handler(); // required for setConvo() to finish first
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshMessages();
            }
        }, 400);
    }

    private void setConvo() {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        if (compareUserName(currUser, expHost) > 0) { // expHost < currUser
            query.whereEqualTo(Conversation.KEY_USER_SENDER, expHost);
            query.whereEqualTo(Conversation.KEY_USER_RECEIVER, currUser);
        } else if (compareUserName(currUser, expHost) < 0) { // currUser < expHost
            query.whereEqualTo(Conversation.KEY_USER_SENDER, currUser);
            query.whereEqualTo(Conversation.KEY_USER_RECEIVER, expHost);
        }
        query.getFirstInBackground(new GetCallback<Conversation>() {
            public void done(Conversation c, ParseException e) {
                if (e != null) {
                    final int statusCode = e.getCode();
                    if (statusCode == ParseException.OBJECT_NOT_FOUND) {
                        // conversation did not exist on the parse backend
                        Log.i(TAG, "convo doesn't exist, make convo");
                        Toast.makeText(ChatActivity.this, "convo doesn't exist!", Toast.LENGTH_SHORT).show();
                        createConvo();

                    }
                } else {
                    Log.i(TAG, "convo already exists, no need for new convo");
                    Toast.makeText(ChatActivity.this, "convo already exists!", Toast.LENGTH_SHORT).show();
                    convo = c;
                }
            }
        });
    }

    // creates a new conversation in lexicographic order of usernames
    private void createConvo() {
        convo = new Conversation();
        if (compareUserName(currUser, expHost) > 0) {
            convo.setUserSender(expHost);
            convo.setUserReceiver(currUser);
        } else {
            convo.setUserSender(currUser);
            convo.setUserReceiver(expHost);
        }
        convo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ChatActivity.this, "successfully created convo on Parse",
                            Toast.LENGTH_SHORT).show();
                    refreshMessages();
                } else {
                    Log.e(TAG, "Failed to save convo", e);
                }
            }
        });
    }

    // compares usernames to ensure lexicographic ordering
    public static int compareUserName(ParseUser userOne, ParseUser userTwo) {
        String nameOne = "";
        try {
            nameOne = userOne.fetchIfNeeded().getString("username");
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }

        String nameTwo = "";
        try {
            nameTwo = userTwo.fetchIfNeeded().getString("username");
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
        if (nameOne != null && nameTwo != null) {
            return nameOne.compareToIgnoreCase(nameTwo);
        }
        return 0;
    }

    private void sendNewMessage() {
        String data = etMessage.getText().toString();
        Message message = new Message();
        message.setSender(currUser);
        message.setBody(data);
        message.setConversation(convo);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ChatActivity.this, "successfully created message on Parse",
                            Toast.LENGTH_SHORT).show();
                    refreshMessages();
                } else {
                    Log.e(TAG, "Failed to save message", e);
                }
            }
        });
        etMessage.setText(null);
    }

    // query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.whereEqualTo(Message.KEY_CONVERSATION, convo);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages IN CHAT ACTIVITY" + e);
                }
            }
        });
    }

    // create a handler which can run code periodically
    static final long POLL_INTERVAL = TimeUnit.SECONDS.toMillis(3);
    Handler myHandler = new android.os.Handler();
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Only start checking for new messages when the app becomes active in foreground
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }

    @Override
    protected void onPause() {
        // Stop background task from refreshing messages, to avoid unnecessary traffic & battery drain
        myHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }
}