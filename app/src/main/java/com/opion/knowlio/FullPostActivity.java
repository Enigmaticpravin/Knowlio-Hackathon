package com.opion.knowlio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.opion.knowlio.Adapter.ResponseAdapter;
import com.opion.knowlio.Class.AIFeedback;
import com.opion.knowlio.Class.Post;
import com.opion.knowlio.Class.Responses;
import com.opion.knowlio.Class.Users;
import com.opion.knowlio.Utilities.FCMMessages;
import com.opion.knowlio.databinding.ActivityFullPostBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FullPostActivity extends AppCompatActivity {

    ActivityFullPostBinding binding;
    String postid, userid, text, image, category, time, userimage, username, aiResponse;
    List<Responses> responsesList = new ArrayList<>();
    private static LocalDateTime after8Hours;
    String url = "https://api.openai.com/v1/completions", apiKey;
    private boolean isExpanded = false;
    boolean isClosed = false;
    ResponseAdapter responseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postid = getIntent().getStringExtra("postid");
        userid = getIntent().getStringExtra("userid");
        text = getIntent().getStringExtra("text");
        userimage = getIntent().getStringExtra("userimage");
        username = getIntent().getStringExtra("username");
        image = getIntent().getStringExtra("image");
        category = getIntent().getStringExtra("category");
        aiResponse = getIntent().getStringExtra("ai");
        time = getIntent().getStringExtra("time");

        binding.aiResponse.setText(aiResponse.trim());
        binding.responsesRv.setLayoutManager(new LinearLayoutManager(FullPostActivity.this));
        responseAdapter = new ResponseAdapter(FullPostActivity.this, responsesList);
        binding.responsesRv.setHasFixedSize(true);
        binding.responsesRv.setAdapter(responseAdapter);

        binding.aiResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTextView();
            }
        });

        DatabaseReference refere = FirebaseDatabase.getInstance().getReference("APIKey");
        refere.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getValue(String.class);
                apiKey = key;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullPostActivity.this, PhotoViewActivity.class);
                intent.putExtra("image", image);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        DatabaseReference refernce = FirebaseDatabase.getInstance().getReference("AIFeedback").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(postid);
        refernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AIFeedback aiFeedback = snapshot.getValue(AIFeedback.class);
                if (aiFeedback != null && aiFeedback.getPostid().equals(postid)){
                    binding.ratingBar.setRating(Float.parseFloat(aiFeedback.getRating()));
                } else {
                    binding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            binding.ratingBar.setRating(v);
                            if (v < 3) {
                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FullPostActivity.this, R.style.SheetDialog);
                                BottomSheetBehavior<View> bottomSheetBehavior;
                                View bottomsheetview = LayoutInflater.from(FullPostActivity.this).inflate(R.layout.usersuggestion, null);
                                bottomSheetDialog.setContentView(bottomsheetview);
                                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomsheetview.getParent());
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                RelativeLayout linearLayout = bottomsheetview.findViewById(R.id.dialogContainer);
                                assert linearLayout != null;
                                linearLayout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                bottomSheetDialog.show();
                                TextView cancel = bottomsheetview.findViewById(R.id.closeBtn);
                                EditText inputEt = bottomsheetview.findViewById(R.id.inputEt);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (TextUtils.isEmpty(inputEt.getText().toString().trim())) {
                                            Toast.makeText(FullPostActivity.this, "Please enter your feedback to submit.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AIFeedback");
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            hashMap.put("feedback", inputEt.getText().toString().trim());
                                            hashMap.put("postid", postid);
                                            hashMap.put("rating", "" + v);
                                            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(postid).updateChildren(hashMap);
                                            bottomSheetDialog.dismiss();
                                            Toast.makeText(FullPostActivity.this, "Feedback sent.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else if (v > 3){
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AIFeedback");
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("feedback", "none");
                                hashMap.put("postid", postid);
                                hashMap.put("rating", "" + v);
                                reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(postid).updateChildren(hashMap);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fetchComments();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null){
                    isClosed = post.isClosed();
                    if (!post.isClosed()){
                        binding.respondBtn.setClickable(true);
                        binding.respondBtn.setText("Write your response here...");
                        Drawable newDrawableStart = getResources().getDrawable(R.drawable.round_edit_24);
                        binding.respondBtn.setCompoundDrawablesWithIntrinsicBounds(newDrawableStart, null, null, null);
                    } else {
                        binding.respondBtn.setClickable(false);
                        binding.respondBtn.setText("This problem no longer accepts responses");
                        Drawable newDrawableStart = getResources().getDrawable(R.drawable.baseline_block_24);
                        binding.respondBtn.setCompoundDrawablesWithIntrinsicBounds(newDrawableStart, null, null, null);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FullPostActivity.this, R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.more_bottom);
                bottomSheetDialog.show();

                TextView closeResponse = (TextView) bottomSheetDialog.findViewById(R.id.closeResponse);
                TextView deleteBtn = (TextView) bottomSheetDialog.findViewById(R.id.deleteBtn);
                TextView closeBtn = (TextView) bottomSheetDialog.findViewById(R.id.closeBtn);
                TextView reportBtn = (TextView) bottomSheetDialog.findViewById(R.id.reportBtn);

                if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    closeBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    reportBtn.setVisibility(View.VISIBLE);
                    closeResponse.setVisibility(View.VISIBLE);
                } else {
                    closeBtn.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);
                    reportBtn.setVisibility(View.VISIBLE);
                    closeResponse.setVisibility(View.GONE);
                }
                reportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
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
                                    Toast.makeText(FullPostActivity.this, "Select a reason first", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Report").child(userid);
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("reporter", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("reported", userid);
                                    hashMap.put("reason", reason);
                                    reference.setValue(hashMap);
                                    bottomSheetDialog.dismiss();
                                    Toast.makeText(FullPostActivity.this, "The author has been reported.", Toast.LENGTH_SHORT).show();
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

                if (isClosed){
                    closeResponse.setText("Open the response");
                } else {
                    closeResponse.setText("Close the response");
                }

                closeResponse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isClosed){
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("closed", false);
                            reference.updateChildren(hashMap);
                            closeResponse.setText("Open the response");
                            bottomSheetDialog.dismiss();
                        } else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
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
                                FirebaseDatabase.getInstance().getReference("Posts").child(postid).removeValue();
                                if (!image.equals("")){
                                    FirebaseStorage.getInstance().getReferenceFromUrl(image).delete();
                                }
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Responses");
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            Responses responses = dataSnapshot.getValue(Responses.class);
                                            if (responses != null && responses.getPostid().equals(postid)) {
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
                                finishAfterTransition();
                            }
                        });
                    }
                });
            }
        });

        binding.dateTv.setText(time);
        binding.problemTxt.setText(text);
        binding.username.setText(username);
        Glide.with(FullPostActivity.this).asBitmap().placeholder(R.drawable.placeholder).load(userimage).into(binding.profilePic);
        binding.respondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Toast.makeText(FullPostActivity.this, "You can't respond to your own idea/problem.", Toast.LENGTH_SHORT).show();
                } else {
                    sendComment();
                }
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });

        if (image.equals("")){
            binding.imageFrame.setVisibility(View.GONE);
        } else {
            binding.imageFrame.setVisibility(View.VISIBLE);
            Glide.with(FullPostActivity.this).asBitmap()
                    .placeholder(R.drawable.placeholder)
                    .load(image)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(binding.postImage);
        }

    }
    private void toggleTextView() {
        Transition transition = new AutoTransition();
        transition.setDuration(200);

        TransitionManager.beginDelayedTransition((ViewGroup) binding.aiResponse.getParent(), transition);

        if (isExpanded) {
            // Collapse the TextView
            binding.aiResponse.setMaxLines(7);
            binding.aiResponse.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            // Expand the TextView
            binding.aiResponse.setMaxLines(Integer.MAX_VALUE);
            binding.aiResponse.setEllipsize(null);
        }
        isExpanded = !isExpanded;
    }
    private void sendComment() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FullPostActivity.this, R.style.SheetDialog);
        BottomSheetBehavior<View> bottomSheetBehavior;
        View bottomsheetview = LayoutInflater.from(FullPostActivity.this).inflate(R.layout.bottom_respond, null);
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
        TextView publishBtn = (TextView) bottomsheetview.findViewById(R.id.publishBtn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText replyEt = (EditText) bottomsheetview.findViewById(R.id.replyEt);

        TextView usernameTxt = (TextView) bottomsheetview.findViewById(R.id.replyTxt);
        String[] nameParts = username.split(" ");
        if (nameParts.length > 0) {
            String firstName = nameParts[0];
            usernameTxt.setText("Replying to "+firstName+"'s problem");
        }
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(replyEt.getText().toString().trim())){
                    Toast.makeText(FullPostActivity.this, "Response field is empty", Toast.LENGTH_SHORT).show();
                } else {
//                    publishPost(bottomSheetDialog, replyEt);
                    getResponseNews("Take this input: "+text+" A user has responded: "+replyEt.getText().toString().trim()+" Analyze if the response by the user is relevant to the input provided by the author. Simply answer in 'Revelant' if it's and 'Irrelevant' if it is not.", bottomSheetDialog, replyEt);
                }
            }
        });
        bottomSheetDialog.show();
    }
    private void getResponseNews(String s, BottomSheetDialog bottomSheetDialog, EditText replyEt) {
        Dialog dialog = new Dialog(FullPostActivity.this, R.style.DialogCorner);
        dialog.setContentView(R.layout.customprogress);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView head = (TextView) dialog.findViewById(R.id.head);
        head.setText("Analyzing response...");
        RequestQueue queue = Volley.newRequestQueue(FullPostActivity.this);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "text-ada-001");
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
                            publishPost(responseMsg.trim(), dialog, bottomSheetDialog, replyEt);
//                            responseTv.setText(responseMsg);
                        } catch (JSONException e) {
                            Toast.makeText(FullPostActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                params.put("Authorization", "Bearer "+apiKey);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void publishPost(String aiResponse, Dialog dialog, BottomSheetDialog bottomSheetDialog, EditText replyEt) {
        TextView head = (TextView) dialog.findViewById(R.id.head);
        head.setText("Please Wait..");
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
        hashMap.put("matching", aiResponse);
        hashMap.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("respondTxt", replyEt.getText().toString().trim());
        hashMap.put("time", postTime+", "+postDate);
        reference.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                bottomSheetDialog.dismiss();
                dialog.dismiss();
                sendNotification(userid);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
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
        hashMap.put("authorid", userid);
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
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);
                            new FCMMessages().sendMessageSingle(FullPostActivity.this, users.getToken(), "New Response", "Your idea/problem has a new response. Check it out.", null);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void fetchComments() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Responses");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                responsesList.clear();
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Responses friends = snapshot.getValue(Responses.class);
                    if (friends != null && friends.getPostid().equals(postid)) {
                        responsesList.add(0, friends);
                        i++;
                    }
                }
                responseAdapter.notifyDataSetChanged();

                if (i == 0){
                    binding.emptyLay.setVisibility(View.VISIBLE);
//                    binding.recu.setVisibility(View.GONE);
                } else {
                    binding.emptyLay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}