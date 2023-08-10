package com.opion.knowlio;

import static com.opion.knowlio.R.id.backBtn;
import static com.opion.knowlio.R.id.categoryBtn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opion.knowlio.databinding.ActivityPostBinding;

import org.json.JSONException;
import org.json.JSONObject;

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
                        getResponse("Give appropriate and solution for this idea or solution, not in points but in paragraph: "+binding.inputEt.getText().toString().trim());
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
        ProgressDialog pd = new ProgressDialog(PostActivity.this);
        pd.setMessage("Generating AI-generated Response...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // creating a json object on below line.
        JSONObject jsonObject = new JSONObject();
        // adding params to json object.
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

        // on below line making json object request.
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        // on below line getting response message and setting it to text view.
                        try {
                            String responseMsg = response.getJSONArray("choices").getJSONObject(0).getString("text");
                            publishPost(responseMsg.trim(), pd);
//                            responseTv.setText(responseMsg);
                        } catch (JSONException e) {
                            Toast.makeText(PostActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                // adding on error listener
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAGAPI", "Error is : " + error.getMessage() + "\n" + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
// adding headers on below line.
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer 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");
                return params;
            }
        };
        // on below line adding retry policy for our request.
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

        // on below line adding our request to queue.
        queue.add(postRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void publishPost(String aiResponse, ProgressDialog pd) {
        pd.setMessage("Posting the idea now...");
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
                    hashMap.put("fileName", fileName);
                    hashMap.put("aiResponse", aiResponse);
                    hashMap.put("attachedFile", uri.toString());
                    reference.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()){
                                finishAfterTransition();
                                pd.dismiss();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    pd.setMessage("Uploaded "+(int)progress+"%");
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
            hashMap.put("aiResponse", aiResponse);
            hashMap.put("attachedFile", "");
            hashMap.put("fileName", fileName);
            hashMap.put("category", category);
            reference.child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        finishAfterTransition();
                        pd.dismiss();
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