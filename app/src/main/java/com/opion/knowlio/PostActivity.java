package com.opion.knowlio;

import static com.opion.knowlio.R.id.backBtn;
import static com.opion.knowlio.R.id.categoryBtn;
import static com.opion.knowlio.R.id.head;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opion.knowlio.Class.Users;
import com.opion.knowlio.Utilities.FCMMessages;
import com.opion.knowlio.databinding.ActivityPostBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    ActivityPostBinding binding;
    Uri attachUri;
    Vibrator vibrator;
    String fileName = "";
    String category = "";
    private static LocalDateTime after8Hours;
    String url = "https://api.openai.com/v1/completions";
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        config();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        storageReference = FirebaseStorage.getInstance().getReference();
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 1);
            }
        });

        if (!category.equals("")){
            binding.categoryBtn.setImageResource(R.drawable.round_edit_24);
        } else {
            binding.categoryBtn.setImageResource(R.drawable.round_category_24);
        }

        binding.categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getRootView().getContext(), R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.category_bottom);
                bottomSheetDialog.show();

                RadioButton checkBox1 = (RadioButton) bottomSheetDialog.findViewById(R.id.technology);
                RadioButton checkBox2 = (RadioButton) bottomSheetDialog.findViewById(R.id.education);
                RadioButton checkBox3 = (RadioButton) bottomSheetDialog.findViewById(R.id.politics);
                RadioButton checkBox4 = (RadioButton) bottomSheetDialog.findViewById(R.id.religion);
                RadioButton checkBox5 = (RadioButton) bottomSheetDialog.findViewById(R.id.medication);
                RadioButton checkBox6 = (RadioButton) bottomSheetDialog.findViewById(R.id.agriculture);
                RadioButton checkBox7 = (RadioButton) bottomSheetDialog.findViewById(R.id.finance);

                TextView closeBtn = (TextView) bottomSheetDialog.findViewById(R.id.closeBtn);

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                checkBox1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category = checkBox1.getText().toString();
                    }
                });

                checkBox2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                              category = checkBox2.getText().toString();
                    }
                });

                checkBox3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category = checkBox3.getText().toString();
                    }
                });

                checkBox4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category = checkBox4.getText().toString();
                    }
                });
                checkBox5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category = checkBox5.getText().toString();
                    }
                });

                checkBox6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category = checkBox6.getText().toString();
                    }
                });

                checkBox7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category = checkBox7.getText().toString();
                    }
                });
            }
        });
        binding.publishBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.inputEt.getText().toString().trim())){
                    Toast.makeText(PostActivity.this, "Enter your idea/problem first", Toast.LENGTH_SHORT).show();
                } else {
                    if (category.equals("")){
                        shakeImage();
                    } else {
                        getResponse("Give appropriate analysis for this idea or solution in paragraph: " +binding.inputEt.getText().toString().trim());
                    }
                }
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachUri != null){
                    attachUri = null;
                    binding.attachBtn.setVisibility(View.VISIBLE);
                    binding.fileDetail.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getResponse(String s) {
        Dialog dialog = new Dialog(PostActivity.this, R.style.DialogCorner);
        dialog.setContentView(R.layout.customprogress);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView head = (TextView) dialog.findViewById(R.id.head);
        head.setText("Generating AI-generated Response...");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
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
                            getLabelResponse("Evaluate if this input contains explicit content, then print 'explicit', or if the content is provocative, print it 'provoke', or if it is both provocative and explicit, print 'expo' else if it's good to go, simply print 'none', but print results in one word only: "+ binding.inputEt.getText().toString().trim(), head, responseMsg, dialog);
//                            publishPost(responseMsg.trim(), pd);
//                            responseTv.setText(responseMsg);
                        } catch (JSONException e) {
                            Toast.makeText(PostActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

    private void getLabelResponse(String s, TextView head, String respone, Dialog dialog) {
        head.setText("Analysing content...");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

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
                            if (responseMsg.trim().equals("explicit")){
                                sendExplicitNotification();
                                dialog.dismiss();
                                finishAfterTransition();
                            } else if (responseMsg.trim().equals("expo")){
                                sendExpoNotification();
                                dialog.dismiss();
                                finishAfterTransition();
                            } else if (responseMsg.trim().equals("provoke")){
                                sendProvokeNotif();
                                dialog.dismiss();
                                finishAfterTransition();
                            } else {
                                publishPost(respone.trim(), responseMsg.trim(), dialog);
                            }
//                            responseTv.setText(responseMsg);
                        } catch (JSONException e) {
                            Toast.makeText(PostActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendProvokeNotif() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        hashMap.put("authorid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("commentorid", "system");
        hashMap.put("text", "You recently posted a content that's not suitable for others. We have deleted it. Refrain from posting such contents.");
        hashMap.put("type", "provoke");
        hashMap.put("time", postTime+", "+postDate);
        hashMap.put("notificationid", key);
        hashMap.put("seen", false);
        reference.child(key).setValue(hashMap);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendExpoNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        hashMap.put("authorid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("commentorid", "system");
        hashMap.put("text", "You recently posted a content that's not suitable for others. We have deleted it. You might get banned if you repeat it.");
        hashMap.put("type", "expo");
        hashMap.put("time", postTime+", "+postDate);
        hashMap.put("notificationid", key);
        hashMap.put("seen", false);
        reference.child(key).setValue(hashMap);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendExplicitNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        hashMap.put("authorid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("commentorid", "system");
        hashMap.put("text", "You recently posted a content that's not suitable for others. We have deleted it. Refrain from posting such contents.");
        hashMap.put("type", "explicit");
        hashMap.put("time", postTime+", "+postDate);
        hashMap.put("notificationid", key);
        hashMap.put("seen", false);
        reference.child(key).setValue(hashMap);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void publishPost(String aiResponse, String label, Dialog dialog) {
        TextView txt = (TextView) dialog.findViewById(R.id.head);
        txt.setText("Posting the idea now...");
        if (attachUri != null){
            StorageReference refer = storageReference.child("Attachments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("document"+System.currentTimeMillis());
            refer.putFile(attachUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri uri = uriTask.getResult();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
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
                    hashMap.put("postid", key);
                    hashMap.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("time", postTime+", "+postDate);
                    hashMap.put("problemTxt", binding.inputEt.getText().toString().trim());
                    hashMap.put("category", category);
                    hashMap.put("edited", false);
                    hashMap.put("closed", false);
                    hashMap.put("label", label);
                    hashMap.put("fileName", fileName);
                    hashMap.put("aiResponse", aiResponse);
                    hashMap.put("attachedFile", uri.toString());
                    reference.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()){
                                finishAfterTransition();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    TextView head = (TextView) dialog.findViewById(R.id.head);
                    head.setText("Attachment Uploaded "+(int)progress+"%");
                }
            });
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
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
            hashMap.put("postid", key);
            hashMap.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("time", postTime+", "+postDate);
            hashMap.put("problemTxt", binding.inputEt.getText().toString().trim());
            hashMap.put("edited", false);
            hashMap.put("closed", false);
            hashMap.put("label", label);
            hashMap.put("aiResponse", aiResponse);
            hashMap.put("attachedFile", "");
            hashMap.put("fileName", fileName);
            hashMap.put("category", category);
            reference.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        finishAfterTransition();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void config() {
        findViewById(android.R.id.content).setTransitionName("go");
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.addTarget(android.R.id.content);
        transform.setDuration(500);
        getWindow().setSharedElementEnterTransition(transform);
        getWindow().setSharedElementReturnTransition(transform);
    }

    private void shakeImage() {
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        binding.categoryBtn.startAnimation(shakeAnimation);
        vibrator.vibrate(500);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            attachUri = data.getData();
            binding.attachBtn.setVisibility(View.INVISIBLE);
            binding.fileDetail.setVisibility(View.VISIBLE);
            String fileNm = "";
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(attachUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fileNm = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
            binding.fileName.setText(fileNm);
            fileName = fileNm;
        }
    }
}