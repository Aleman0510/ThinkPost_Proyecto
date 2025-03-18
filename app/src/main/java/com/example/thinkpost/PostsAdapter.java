package com.example.thinkpost;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<DocumentSnapshot> mPosts;

    public PostsAdapter(List<DocumentSnapshot> posts) {
        mPosts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        DocumentSnapshot post = mPosts.get(position);

        holder.tvUsername.setText(post.getString("username"));
        holder.tvContent.setText(post.getString("content"));

        // Formatear fecha
        Date timestamp = post.getDate("timestamp");
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvTimestamp.setText(sdf.format(timestamp));
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvContent, tvTimestamp;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }

    public void updateData(List<DocumentSnapshot> newPosts) {
        mPosts = newPosts;
        notifyDataSetChanged();
    }
}