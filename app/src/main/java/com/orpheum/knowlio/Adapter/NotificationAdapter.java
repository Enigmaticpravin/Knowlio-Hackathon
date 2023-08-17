package com.orpheum.knowlio.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orpheum.knowlio.Class.Notifications;
import com.orpheum.knowlio.Class.Post;
import com.orpheum.knowlio.Class.Users;
import com.orpheum.knowlio.FullPostActivity;
import com.orpheum.knowlio.R;
import com.orpheum.knowlio.Utilities.TimeAgoConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Notifications> notificationsList;

    public NotificationAdapter(Context context, List<Notifications> notifications){
        mContext = context;
        notificationsList = notifications;

    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final Notifications notifications = notificationsList.get(position);
        try {
            if (notifications != null){
                holder.notifTxt.setText(notifications.getText());
                String formattedTime = TimeAgoConverter.getTimeAgo(notifications.getTime());
                holder.date.setText("• "+formattedTime);
                if (notifications.isSeen()){
                    holder.tagIv.setVisibility(View.GONE);
                } else {
                    holder.tagIv.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notifications.getType()!= null && notifications.getType().equals("post")){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(notifications.getPostid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Post post = snapshot.getValue(Post.class);
                            if (post != null) {
                                DatabaseReference refer = FirebaseDatabase.getInstance().getReference("Users").child(post.getPublisherid());
                                refer.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users users = snapshot.getValue(Users.class);
                                        if (users!=null){
                                            Intent intent = new Intent(mContext, FullPostActivity.class);
                                            intent.putExtra("postid", post.getPostid());
                                            intent.putExtra("text", post.getProblemTxt());
                                            intent.putExtra("category", post.getCategory());
                                            intent.putExtra("image", post.getAttachedFile());
                                            intent.putExtra("time", post.getTime());
                                            intent.putExtra("userid", post.getPublisherid());
                                            intent.putExtra("userimage", users.getProfilePic());
                                            intent.putExtra("username", users.getUsername());
                                            intent.putExtra("ai", post.getAiResponse());
                                            Pair[] pairs = new Pair[2];
                                            pairs[0] = new Pair<View, String>(holder.image_profile, "imageTransition");
                                            pairs[1] = new Pair<View, String>(holder.username, "nameTransition");
                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs);
                                            mContext.startActivity(intent, options.toBundle());
                                            DatabaseReference refer2 = FirebaseDatabase.getInstance().getReference("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(notifications.getNotificationid());
                                            HashMap<String, Object> hashMap2 = new HashMap<>();
                                            hashMap2.put("seen", true);
                                            refer2.updateChildren(hashMap2);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        if (Objects.equals(notifications.getCommentorid(), "system")){
            holder.image_profile.setImageResource(R.drawable.file_management);
            holder.username.setText("Warning!");
            holder.tagIv.setVisibility(View.GONE);
            holder.username.setTextColor(mContext.getResources().getColor(R.color.primaryRed));
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(notifications.getCommentorid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users user = snapshot.getValue(Users.class);
                    if (user!=null){
                        holder.username.setText(user.getUsername());
                        Glide.with(mContext).asBitmap().load(user.getProfilePic()).placeholder(R.drawable.placeholder).into(holder.image_profile);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image_profile;
        TextView username, date, notifTxt, tagIv;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.profilePic);
            tagIv = itemView.findViewById(R.id.tagIv);
            username = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.dateTv);
            notifTxt = itemView.findViewById(R.id.notifText);

        }
    }
}
