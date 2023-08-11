package com.opion.knowlio.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opion.knowlio.Class.AIFeedback;
import com.opion.knowlio.Class.Notifications;
import com.opion.knowlio.Class.Post;
import com.opion.knowlio.Class.Users;
import com.opion.knowlio.FullPostActivity;
import com.opion.knowlio.R;
import com.opion.knowlio.Utilities.TimeAgoConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ImageViewHolder> {

    private Context mContext;
    private List<AIFeedback> aiFeedbackList;

    String problemTxt, profilePic, username, postid, category, attachedfile, time, publisherid, airesponse;

    public RatingAdapter(Context context, List<AIFeedback> aiFeedbacks){
        mContext = context;
        aiFeedbackList = aiFeedbacks;

    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rating_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final AIFeedback aiFeedback = aiFeedbackList.get(position);

        try {
            if (aiFeedback != null){
                if (aiFeedback.getFeedback().equals("none")){
                    holder.userInput.setVisibility(View.GONE);
                } else {
                    holder.userInput.setText(aiFeedback.getFeedback());
                }
                holder.ratingBar.setRating(Float.parseFloat(aiFeedback.getRating()));
                DatabaseReference refer = FirebaseDatabase.getInstance().getReference("Posts").child(aiFeedback.getPostid());
                refer.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(post.getPublisherid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users = snapshot.getValue(Users.class);
                                String[] nameParts = users.getUsername().split(" ");
                                if (nameParts.length > 0) {
                                    String firstName = nameParts[0];
                                    holder.username.setText("You rated "+firstName+"'s post");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        }

    @Override
    public int getItemCount() {
        return aiFeedbackList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView username, userInput;
        RatingBar ratingBar;
        RelativeLayout toolbar;

        public ImageViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            toolbar = itemView.findViewById(R.id.toolbar);
            userInput = itemView.findViewById(R.id.userInput);

        }
    }
}
