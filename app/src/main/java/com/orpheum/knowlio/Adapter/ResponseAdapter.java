package com.orpheum.knowlio.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.orpheum.knowlio.Class.Post;
import com.orpheum.knowlio.Class.Responses;
import com.orpheum.knowlio.Class.Users;
import com.orpheum.knowlio.R;
import com.orpheum.knowlio.Utilities.TimeAgoConverter;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Responses> quoteList;

    public ResponseAdapter(Context context, List<Responses> quotes){
        mContext = context;
        quoteList = quotes;

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.response_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final Responses quoteImage = quoteList.get(position);

        holder.commentTxt.setText(quoteImage.getRespondTxt());
        String formattedTime = TimeAgoConverter.getTimeAgo(quoteImage.getTime());
        holder.date.setText(formattedTime);

        holder.releventTxt.setText(quoteImage.getMatching());

        if (quoteImage.getTag() != null){
            if (quoteImage.getTag().equals("useful")){
                holder.tagIv.setText("Helpful");
                holder.tagIv.setTextColor(Color.WHITE);
                int tintColor = ContextCompat.getColor(mContext, R.color.green);
                ViewCompat.setBackgroundTintList(holder.tagIv, ColorStateList.valueOf(tintColor));
            }
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getRootView().getContext(), R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.responsemore_bottom);
                bottomSheetDialog.show();

                TextView closeResponse = (TextView) bottomSheetDialog.findViewById(R.id.closeResponse);
                TextView deleteBtn = (TextView) bottomSheetDialog.findViewById(R.id.deleteBtn);
                TextView closeBtn = (TextView) bottomSheetDialog.findViewById(R.id.closeBtn);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(quoteImage.getPostid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);
                        if (!post.getPublisherid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            closeResponse.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (quoteImage.getTag() != null){
                    if (quoteImage.getTag().equals("useful")){
                        closeResponse.setText("Remove Helpful tag");
                    } else {
                        closeResponse.setText("Tag as Helpful");
                    }
                }


                closeResponse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (closeResponse.getText().toString().equals("Tag as Helpful")){
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Responses").child(quoteImage.getRespondid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("tag", "useful");
                            reference.updateChildren(hashMap);
                            bottomSheetDialog.dismiss();
                        } else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Responses").child(quoteImage.getRespondid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("tag", "useless");
                            reference.updateChildren(hashMap);
                            bottomSheetDialog.dismiss();
                        }
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(view.getRootView().getContext(), R.style.DialogCorner);
                        dialog.setContentView(R.layout.deleteresponse);
                        dialog.show();

                        TextView cancel = (TextView) dialog.findViewById(R.id.noBtn);
                        TextView join = (TextView) dialog.findViewById(R.id.yesBtn);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                bottomSheetDialog.dismiss();
                            }
                        });

                        join.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase.getInstance().getReference("Responses").child(quoteImage.getRespondid()).removeValue();
                                dialog.dismiss();
                                bottomSheetDialog.dismiss();
                            }
                        });
                    }
                });

            }
        });

        try {
            if (quoteImage.getMatching() != null){
                if (quoteImage.getMatching().equals("Relevant")){
                    holder.icon.setImageResource(R.drawable.star);
                    holder.releventTxt.setText("Most Relevant");
                    holder.releventTxt.setTextColor(Color.parseColor("#43A047"));
                    holder.icon.setColorFilter(Color.parseColor("#43A047"));
                } else {
                    holder.icon.setVisibility(View.GONE);
                    holder.releventTxt.setText(View.GONE);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            holder.icon.setVisibility(View.GONE);
            holder.releventTxt.setVisibility(View.GONE);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(quoteImage.getPublisherid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if (user != null){
                    holder.username.setText(user.getUsername());
                    Glide.with(mContext).asBitmap().load(user.getProfilePic()).placeholder(R.drawable.placeholder).into(holder.image_profile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image_profile;
        TextView username, date, commentTxt, releventTxt, tagIv;
        ImageView icon, moreBtn;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.profilePic);
            tagIv = itemView.findViewById(R.id.tagIv);
            username = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.dateTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            icon = itemView.findViewById(R.id.icon);
            releventTxt = itemView.findViewById(R.id.releventTxt);
            commentTxt = itemView.findViewById(R.id.comment);

        }
    }
}
