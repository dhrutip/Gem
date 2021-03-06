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
import com.codepath.gem.models.Experience;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ExperiencesAdapter extends RecyclerView.Adapter<ExperiencesAdapter.ViewHolder> {

    public static final String TAG = "ExperiencesAdapter";
    private Context context;
    private List<Experience> experienceList;
    private OnExperienceListener onExperienceListener;

    public ExperiencesAdapter(Context context, List<Experience> experienceList, OnExperienceListener onExperienceListener) {
        this.context = context;
        this.experienceList = experienceList;
        this.onExperienceListener = onExperienceListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivCoverPic;
        private TextView tvTitle;
        private TextView tvDescription;
        private ImageView ivFeedProfilePic;
        private OnExperienceListener onExperienceListener;

        public ViewHolder(@NonNull View itemView, OnExperienceListener onExperienceListener) {
            super(itemView);
            ivCoverPic = itemView.findViewById(R.id.ivCoverPic);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivFeedProfilePic = itemView.findViewById(R.id.ivFeedProfilePic);
            this.onExperienceListener = onExperienceListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Experience experience) {
            tvTitle.setText(experience.getTitle());
            tvDescription.setText(experience.getDescription());
            ParseFile coverPic = experience.getImageOne();
            if (coverPic != null) {
                Glide.with(context)
                        .load(coverPic.getUrl())
                        .into(ivCoverPic);
            }

            ParseFile profilePic = null;
            try {
                profilePic = experience.getHost().fetchIfNeeded().getParseFile("profilePic");
            } catch (ParseException e) {
                Log.e(TAG, "Something has gone terribly wrong with Parse", e);
            }

            if (profilePic != null) {
                Glide.with(context)
                        .load(profilePic.getUrl())
                        .circleCrop()
                        .into(ivFeedProfilePic);
            }
        }

        @Override
        public void onClick(View v) {
            onExperienceListener.onExperienceClicked(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_experience, parent, false);
        final ViewHolder holder = new ViewHolder(view, onExperienceListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Experience experience = experienceList.get(position);
        holder.bind(experience);
    }

    @Override
    public int getItemCount() {
        return experienceList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        experienceList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Experience> list) {
        experienceList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnExperienceListener {
        void onExperienceClicked(int position);
    }

}
