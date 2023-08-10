package com.opion.knowlio.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;
import com.opion.knowlio.Class.Post;
import com.opion.knowlio.Class.Responses;
import com.opion.knowlio.Class.Users;
import com.opion.knowlio.FullPostActivity;
import com.opion.knowlio.LoginActivity;
import com.opion.knowlio.PhotoViewActivity;
import com.opion.knowlio.PostActivity;
import com.opion.knowlio.R;
import com.opion.knowlio.Utilities.FCMMessages;
import com.opion.knowlio.Utilities.TimeAgoConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {

    private Context mContext;
    private static LocalDateTime after8Hours;
    String url = "https://api.openai.com/v1/completions";
    List<Responses> friendsList = new ArrayList<>();
    private List<Post> postList;
    public PostAdapter(Context context, List<Post> posts){
        mContext = context;
        postList = posts;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        Post post = postList.get(position);

        holder.problemTxt.setText(post.getProblemTxt());
        String formattedTime = TimeAgoConverter.getTimeAgo(post.getTime());
        holder.dateTv.setText(formattedTime + " • "+post.getCategory());

        if (post.getFileName() != null && !post.getFileName().equals("")){
            holder.fileDetail.setVisibility(View.VISIBLE);
            holder.fileName.setText(post.getFileName());
        } else {
            holder.fileDetail.setVisibility(View.GONE);
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getRootView().getContext(), R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.more_bottom);
                bottomSheetDialog.show();

                TextView closeResponse = (TextView) bottomSheetDialog.findViewById(R.id.closeResponse);
                TextView deleteBtn = (TextView) bottomSheetDialog.findViewById(R.id.deleteBtn);
                TextView closeBtn = (TextView) bottomSheetDialog.findViewById(R.id.closeBtn);
                TextView reportBtn = (TextView) bottomSheetDialog.findViewById(R.id.reportBtn);

                if (post.getPublisherid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    deleteBtn.setVisibility(View.VISIBLE);
                    reportBtn.setVisibility(View.VISIBLE);
                    closeResponse.setVisibility(View.VISIBLE);
                } else {
                    deleteBtn.setVisibility(View.GONE);
                    reportBtn.setVisibility(View.VISIBLE);
                    closeResponse.setVisibility(View.GONE);
                }
                reportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getRootView().getContext(), R.style.SheetDialog);
                        bottomSheetDialog.setContentView(R.layout.bottomreport);
                        bottomSheetDialog.show();
                        ImageView cancel = bottomSheetDialog.findViewById(R.id.cancel_action);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomSheetDialog.dismiss();
                            }
                        });
                        TextView sendBtn = bottomSheetDialog.findViewById(R.id.sendBtn);
                        RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.radioGroup);

                        RadioButton radioButton1 = bottomSheetDialog.findViewById(R.id.r1);
                        RadioButton radioButton2 = bottomSheetDialog.findViewById(R.id.r2);
                        RadioButton radioButton3 = bottomSheetDialog.findViewById(R.id.r3);

                        sendBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String reason = "";

                                if (radioButton1.isChecked()){
                                    reason = radioButton1.getText().toString();
                                } else if (radioButton2.isChecked()){
                                    reason = radioButton2.getText().toString();
                                } else {
                                    reason = radioButton3.getText().toString();
                                }
                                if (reason.equals("")){
                                    Toast.makeText(mContext, "Select a reason first", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Report").child(post.getPublisherid());
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("reporter", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("reported", post.getPublisherid());
                                    hashMap.put("reason", reason);
                                    reference.setValue(hashMap);
                                    bottomSheetDialog.dismiss();
                                    Toast.makeText(mContext, "The author has been reported.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                if (post.isClosed()){
                    closeResponse.setText("Open the response");
                } else {
                    closeResponse.setText("Close the response");
                }

                closeResponse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post.isClosed()){
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("closed", false);
                            reference.updateChildren(hashMap);
                            closeResponse.setText("Open the response");
                            bottomSheetDialog.dismiss();
                        } else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("closed", true);
                            reference.updateChildren(hashMap);
                            closeResponse.setText("Close the response");
                            bottomSheetDialog.dismiss();
                        }
                    }
                });

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(view.getRootView().getContext(), R.style.DialogCorner);
                        dialog.setContentView(R.layout.deletepost);
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
                                FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid()).removeValue();
                                if (!post.getAttachedFile().equals("")){
                                    FirebaseStorage.getInstance().getReferenceFromUrl(post.getAttachedFile()).delete();
                                }
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Responses");
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            Responses responses = dataSnapshot.getValue(Responses.class);
                                            if (responses != null && responses.getPostid().equals(post.getPostid())) {
                                                FirebaseDatabase.getInstance().getReference("Responses").child(responses.getRespondid()).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                dialog.dismiss();
                                bottomSheetDialog.dismiss();
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PhotoViewActivity.class);
                intent.putExtra("image", post.getAttachedFile());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Responses");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsList.clear();
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Responses friends = snapshot.getValue(Responses.class);
                    assert friends != null;
                    if (friends.getPostid().equals(post.getPostid())){
                        friendsList.add(friends);
                        i++;
                    }
                }

                if (i == 0){
                    DatabaseReference refer = FirebaseDatabase.getInstance().getReference("Views").child(post.getPostid());
                    refer.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int views = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                views++;
                            }

                            holder.extraCount.setText("No Response Yet  •  "+views + " Views");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    DatabaseReference refer = FirebaseDatabase.getInstance().getReference("Views").child(post.getPostid());
                    int finalI = i;
                    refer.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int views = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                views++;
                            }

                            holder.extraCount.setText(finalI +" Responses  •  "+views+" Views");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(post.getPublisherid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users != null){
                    Glide.with(mContext).load(users.getProfilePic()).into(holder.image_profile);
                    holder.username.setText(users.getUsername());
                    holder.respondBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (post.getPublisherid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Toast.makeText(mContext, "You can't respond to your own idea/problem.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (post.isClosed()){
                                    Toast.makeText(mContext, "This author no longer accepts responses.", Toast.LENGTH_SHORT).show();
                                } else {
                                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getRootView().getContext(), R.style.SheetDialog);
                                    BottomSheetBehavior<View> bottomSheetBehavior;
                                    View bottomsheetview = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.bottom_respond, null);
                                    bottomSheetDialog.setContentView(bottomsheetview);
                                    bottomSheetBehavior = BottomSheetBehavior.from((View) bottomsheetview.getParent());
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    RelativeLayout linearLayout = bottomsheetview.findViewById(R.id.dialogContainer);
                                    assert linearLayout != null;
                                    linearLayout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView cancelBtn = (TextView) bottomsheetview.findViewById(R.id.closeBtn);
                                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                    TextView usernameTxt = (TextView) bottomsheetview.findViewById(R.id.replyTxt);
                                    String[] nameParts = users.getUsername().split(" ");
                                    if (nameParts.length > 0) {
                                        String firstName = nameParts[0];
                                        usernameTxt.setText("Replying to "+firstName+"'s problem");
                                    }

                                    TextView publishBtn = (TextView) bottomsheetview.findViewById(R.id.publishBtn);
                                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText replyEt = (EditText) bottomsheetview.findViewById(R.id.replyEt);
                                    publishBtn.setOnClickListener(new View.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onClick(View view) {
                                            if (TextUtils.isEmpty(replyEt.getText().toString().trim())){
                                                Toast.makeText(mContext, "Response field is empty", Toast.LENGTH_SHORT).show();
                                            } else {
                                                getResponseNews("Take this input: "+post.getProblemTxt()+" A user has responded: "+replyEt.getText().toString().trim()+" Analyze if the response by the user is relevant to the input provided by the author. Simply answer in 'Revelant' if it's and 'Irrelevant' if it is not.", bottomSheetDialog, replyEt, post.getPostid(), post.getPublisherid());
                                            }
                                        }
                                    });
                                    bottomSheetDialog.show();
                                }
                            }
                        }
                    });
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, FullPostActivity.class);
                            intent.putExtra("postid", post.getPostid());
                            intent.putExtra("text", post.getProblemTxt());
                            intent.putExtra("category", post.getCategory());
                            intent.putExtra("image", post.getAttachedFile());
                            intent.putExtra("time", holder.dateTv.getText().toString());
                            intent.putExtra("userid", post.getPublisherid());
                            intent.putExtra("userimage", users.getProfilePic());
                            intent.putExtra("username", users.getUsername());
                            intent.putExtra("ai", post.getAiResponse());
                            Pair[] pairs = new Pair[5];
                            pairs[0] = new Pair<View, String>(holder.image_profile, "imageTransition");
                            pairs[1] = new Pair<View, String>(holder.moreBtn, "moreTransition");
                            pairs[2] = new Pair<View, String>(holder.username, "nameTransition");
                            pairs[3] = new Pair<View, String>(holder.dateTv, "timeTransition");
                            pairs[4] = new Pair<View, String>(holder.problemTxt, "textTransition");
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs);
                            mContext.startActivity(intent, options.toBundle());
                            if (!post.getPublisherid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Views");
                                String key = reference2.push().getKey();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("postid", post.getPostid());
                                hashMap.put("key", key);
                                hashMap.put("viewerid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                reference2.child(post.getPostid()).child(key).setValue(hashMap);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void publishPost(String responseMsg, Dialog pd, BottomSheetDialog bottomSheetDialog, EditText replyEt, String postid, String publisherid) {
        TextView txt = (TextView) pd.findViewById(R.id.head);
        txt.setText("Almost Done!");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Responses");
        String key = reference.push().getKey();
        LocalDateTime ldt = LocalDateTime.now(ZoneId.systemDefault());
        after8Hours = ldt.plusHours(0);//only time
        DateTimeFormatter dtfTimeFormat24Ht = DateTimeFormatter.ofPattern("HH:mm a", Locale.ENGLISH);
        System.out.println(dtfTimeFormat24Ht.format(after8Hours));
        //only date
        DateTimeFormatter dtfTimeFormat24Hd = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ENGLISH);
        System.out.println(dtfTimeFormat24Hd.format(after8Hours));
        String postTime = String.valueOf(dtfTimeFormat24Ht.format(after8Hours));
        String postDate = String.valueOf(dtfTimeFormat24Hd.format(after8Hours));
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("respondid", key);
        hashMap.put("postid", postid);
        hashMap.put("matching", responseMsg);
        hashMap.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("respondTxt", replyEt.getText().toString().trim());
        hashMap.put("time", postTime+", "+postDate);
        reference.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                bottomSheetDialog.dismiss();
                pd.dismiss();
                sendNotification(publisherid, postid);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String publisherid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
        String key = reference.push().getKey();
        LocalDateTime ldt = LocalDateTime.now(ZoneId.systemDefault());
        after8Hours = ldt.plusHours(0);//only time
        DateTimeFormatter dtfTimeFormat24Ht = DateTimeFormatter.ofPattern("HH:mm a", Locale.ENGLISH);
        System.out.println(dtfTimeFormat24Ht.format(after8Hours));
        //only date
        DateTimeFormatter dtfTimeFormat24Hd = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ENGLISH);
        System.out.println(dtfTimeFormat24Hd.format(after8Hours));
        String postTime = String.valueOf(dtfTimeFormat24Ht.format(after8Hours));
        String postDate = String.valueOf(dtfTimeFormat24Hd.format(after8Hours));
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("authorid", publisherid);
        hashMap.put("commentorid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("text", "has responded to your post");
        hashMap.put("type", "post");
        hashMap.put("postid", postid);
        hashMap.put("time", postTime+", "+postDate);
        hashMap.put("notificationid", key);
        hashMap.put("seen", false);
        reference.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);
                            new FCMMessages().sendMessageSingle(mContext, users.getToken(), "New Response", "Your idea/problem has a new response. Check it out.", null);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView image_profile;
        public TextView username, dateTv, problemTxt, fileName, respondBtn, extraCount;
        ImageView cancel;
        RelativeLayout fileDetail;
        ImageView moreBtn;
        public ImageViewHolder(View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.profilePic);
            problemTxt = itemView.findViewById(R.id.problemTxt);
            cancel = itemView.findViewById(R.id.cancel);
            extraCount = itemView.findViewById(R.id.extracount);
            respondBtn = itemView.findViewById(R.id.respondBtn);
            username = itemView.findViewById(R.id.username);
            fileName = itemView.findViewById(R.id.fileName);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            fileDetail = itemView.findViewById(R.id.fileDetail);
            dateTv = itemView.findViewById(R.id.dateTv);

        }
    }

    private void getResponseNews(String s, BottomSheetDialog bottomSheetDialog, EditText replyEt, String postid, String publisherid) {
        Dialog dialog = new Dialog(mContext, R.style.DialogCorner);
        dialog.setContentView(R.layout.customprogress);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView head = (TextView) dialog.findViewById(R.id.head);
        head.setText("Analyzing response...");
        RequestQueue queue = Volley.newRequestQueue(mContext);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "text-davinci-003");
            jsonObject.put("prompt", s);
            jsonObject.put("temperature", 0);
            jsonObject.put("max_tokens", 500);
            jsonObject.put("top_p", 1);
            jsonObject.put("frequency_penalty", 0.0);
            jsonObject.put("presence_penalty", 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseMsg = response.getJSONArray("choices").getJSONObject(0).getString("text");
                            publishPost(responseMsg.trim(), dialog, bottomSheetDialog, replyEt, postid, publisherid);
//                            responseTv.setText(responseMsg);
                        } catch (JSONException e) {
                            Toast.makeText(mContext, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAGAPI", "Error is : " + error.getMessage() + "\n" + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer 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");
                return params;
            }
        };

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) {
            }
        });

        queue.add(postRequest);
    }

}
