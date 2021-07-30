package com.codepath.gem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.gem.ChatActivity;
import com.codepath.gem.ExperienceDetailsActivity;
import com.codepath.gem.R;
import com.codepath.gem.adapters.ConversationsAdapter;
import com.codepath.gem.models.Conversation;
import com.codepath.gem.models.Experience;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ConversationsFragment extends Fragment implements ConversationsAdapter.OnConversationListener {

    public static final String TAG = "ConversationsFragment";
    public RecyclerView rvAllConversations;
    public ConversationsAdapter conversationsAdapter;
    public List<Conversation> allConversations;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvAllConversations = view.findViewById(R.id.rvAllConversations);
        allConversations = new ArrayList<>();
        conversationsAdapter = new ConversationsAdapter(getContext(), allConversations, this);
        rvAllConversations.setAdapter(conversationsAdapter);
        rvAllConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        queryConversations();
    }

    private void queryConversations() {
        List<Conversation> allQueriedConversations = new ArrayList<>();

        // query where current user is the 'sender'
        ParseQuery<Conversation> querySender = ParseQuery.getQuery(Conversation.class);
        querySender.whereEqualTo(Conversation.KEY_USER_RECEIVER, ParseUser.getCurrentUser());
        querySender.setLimit(20);
        querySender.addDescendingOrder(Conversation.KEY_UPDATED_AT);
        querySender.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> conversations, ParseException e) {
                if (e != null) {
                    // issue getting conversations
                    Toast.makeText(getContext(), "error getting conversations:(", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error getting conversations");
                } else {
                    allQueriedConversations.addAll(conversations);
                }
            }
        });

        // query where current user is the 'receiver'
        ParseQuery<Conversation> queryReceiver = ParseQuery.getQuery(Conversation.class);
        queryReceiver.whereEqualTo(Conversation.KEY_USER_SENDER, ParseUser.getCurrentUser());
        queryReceiver.setLimit(20);
        queryReceiver.addDescendingOrder(Conversation.KEY_UPDATED_AT);
        queryReceiver.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> conversations, ParseException e) {
                if (e != null) {
                    // issue getting conversations
                    Toast.makeText(getContext(), "error getting conversations:(", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error getting conversations");
                } else {
                    allQueriedConversations.addAll(conversations);
                }
            }
        });

        Handler handler = new Handler(); // required for queries to finish first
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                allConversations.addAll(allQueriedConversations);
                conversationsAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    @Override
    public void onConversationClick(int position) {
        Toast.makeText(getContext(), "on click!!", Toast.LENGTH_SHORT).show();
        Conversation clickedConversation = allConversations.get(position);
        ParseUser userToSend = null;
        ParseUser userOne = clickedConversation.getUserSender();
        String userOneName = "";
        try {
            userOneName = userOne.fetchIfNeeded().getString("username");
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
        ParseUser userTwo = clickedConversation.getUserReceiver();
        String userTwoName = "";
        try {
            userTwoName = userTwo.fetchIfNeeded().getString("username");
        } catch (ParseException e) {
            Log.e(TAG, "Something has gone terribly wrong with Parse", e);
        }
        if (userOneName.equals(ParseUser.getCurrentUser().getUsername())) {
            userToSend = userOne;
        } else if (userTwoName.equals(ParseUser.getCurrentUser().getUsername())) {
            userToSend = userTwo;
        }
        if (userToSend != null) {
            Intent toChat = new Intent(getContext(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(ChatActivity.KEY_SEND_USER, userToSend);
            toChat.putExtras(bundle);
            startActivity(toChat);
        }
    }
}