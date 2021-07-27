package com.codepath.gem.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.gem.R;
import com.codepath.gem.models.Message;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

/**
 * @source https://guides.codepath.com/android/Building-Simple-Chat-Client-with-Parse
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    public static final String TAG = "ChatAdapter";
    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;
    private List<Message> mMessages;
    private Context mContext;
    ParseUser userSender;
    ParseUser userReceiver;

    public ChatAdapter(Context context, ParseUser userOne, ParseUser userTwo, List<Message> messages) {
        mMessages = messages;
        mContext = context;
        userSender = userOne;
        userReceiver = userTwo;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        Message message = mMessages.get(position);
        String currId = ParseUser.getCurrentUser().getObjectId();
        return message.getSender().getObjectId() != null && message.getSender().getObjectId().equals(currId);
    }

    // abstract view holder class
    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(Message message);
    }

    // view holder implementation for incoming messages
    public class IncomingMessageViewHolder extends MessageViewHolder {
        ImageView imageOther;
        TextView body;
        TextView name;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            body = (TextView)itemView.findViewById(R.id.tvBody);
            name = (TextView)itemView.findViewById(R.id.tvName);
        }

        @Override
        public void bindMessage(Message message) {
            body.setText(message.getBody());
            String n = "";
            try {
                n = message.getSender().fetchIfNeeded().getString("username");
            } catch (ParseException e) {
                Log.e(TAG, "Something has gone terribly wrong with Parse", e);
            }
            name.setText(n);
            ParseFile profilePic = userReceiver.getParseFile("profilePic");
            if (profilePic != null) {
                Glide.with(mContext)
                        .load(profilePic.getUrl())
                        .circleCrop()
                        .into(imageOther);
            }
        }
    }

    // view holder implementation for outgoing messages
    public class OutgoingMessageViewHolder extends MessageViewHolder {
        ImageView imageMe;
        TextView body;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(Message message) {
            body.setText(message.getBody());
            ParseFile profilePic = userSender.getParseFile("profilePic");
            if (profilePic != null) {
                Glide.with(mContext)
                        .load(profilePic.getUrl())
                        .circleCrop()
                        .into(imageMe);
            }
        }
    }

}