package com.codepath.gem.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.gem.R;
import com.codepath.gem.models.Conversation;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;


public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    public static final String TAG = "ConversationsAdapter";
    Context context;
    List<Conversation> allConversations;
    OnConversationListener onConversationListener;

    public ConversationsAdapter(Context c, List<Conversation> convos, OnConversationListener onConvoListener) {
        this.context = c;
        this.allConversations = convos;
        this.onConversationListener = onConvoListener;
    }

    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view, onConversationListener);
    }

    @Override
    public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
        Conversation conversation = allConversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return allConversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivConvoProfileImage;
        TextView tvConvoUsername;
        ParseUser currUser;
        ParseUser otherUser;
        OnConversationListener onConversationListener;

        public ViewHolder(View itemView, OnConversationListener onConversationListener) {
            super(itemView);
            ivConvoProfileImage = itemView.findViewById(R.id.ivConvoProfileImage);
            tvConvoUsername = itemView.findViewById(R.id.tvConvoUsername);
            currUser = ParseUser.getCurrentUser();
            this.onConversationListener = onConversationListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Conversation conversation) {
            if (conversation.getUserSender().getObjectId().equals(currUser.getObjectId())) {
                otherUser = conversation.getUserReceiver();
            } else {
                otherUser = conversation.getUserSender();
            }
            String otherUsername = "";
            try {
                otherUsername = otherUser.fetchIfNeeded().getString("username");
            } catch (ParseException e) {
                Log.e(TAG, "Something has gone terribly wrong with Parse", e);
            }
            tvConvoUsername.setText(otherUsername);
            ParseFile profilePic = otherUser.getParseFile("profilePic");
            if (profilePic != null) {
                Glide.with(context)
                        .load(profilePic.getUrl())
                        .circleCrop()
                        .into(ivConvoProfileImage);
            }
        }

        @Override
        public void onClick(View v) {
            onConversationListener.onConversationClick(getAdapterPosition());
        }
    }

    public interface OnConversationListener {
        void onConversationClick(int position);
    }
}
