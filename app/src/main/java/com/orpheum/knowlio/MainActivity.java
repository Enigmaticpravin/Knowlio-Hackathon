package com.orpheum.knowlio;

import static android.app.ProgressDialog.show;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orpheum.knowlio.Adapter.NotificationAdapter;
import com.orpheum.knowlio.Adapter.PostAdapter;
import com.orpheum.knowlio.Class.Notifications;
import com.orpheum.knowlio.Class.Post;
import com.orpheum.knowlio.Class.Users;
import com.orpheum.knowlio.Utilities.NetworkUtils;
import com.orpheum.knowlio.databinding.ActivityMainBinding;

import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TextView username,username1;
    PostAdapter postAdapter;
    private boolean isSearchVisible = false;
    DatabaseReference reference9;
    ChildEventListener childEventListener;
    NotificationAdapter notificationAdapter;
    List<Notifications> notificationsList = new ArrayList<>();
    CircleImageView profilePic;
    List<Post> postList = new ArrayList<Post>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = (TextView) findViewById(R.id.username);
        username1 = (TextView) findViewById(R.id.username1);
        profilePic = findViewById(R.id.profilePic);

        binding.postRv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        postAdapter = new PostAdapter(MainActivity.this, postList);
        binding.postRv.setHasFixedSize(true);
        binding.postRv.setItemViewCacheSize(5);
        binding.postRv.setItemAnimator(new DefaultItemAnimator());
        binding.postRv.setAdapter(postAdapter);

        binding.hamburgerBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getRootView().getContext(), R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.hamburger_bottom);
                bottomSheetDialog.show();

                TextView rateActivity = (TextView) bottomSheetDialog.findViewById(R.id.rateBtn);
                TextView community = (TextView) bottomSheetDialog.findViewById(R.id.closeResponse);
                TextView about = (TextView) bottomSheetDialog.findViewById(R.id.reportBtn);
                TextView logout = (TextView) bottomSheetDialog.findViewById(R.id.deleteBtn);
                TextView closeBtn = (TextView) bottomSheetDialog.findViewById(R.id.closeBtn);

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                community.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, GuidelinesActivity.class));
                        bottomSheetDialog.dismiss();
                    }
                });

                about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                        bottomSheetDialog.dismiss();
                    }
                });
                rateActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, RateSortingActivity.class));
                        bottomSheetDialog.dismiss();
                    }
                });

                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(view.getRootView().getContext(), R.style.DialogCorner);
                        dialog.setContentView(R.layout.logoutdialog);
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
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("token", null);
                                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isComplete()){
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                            finish();
                                            bottomSheetDialog.dismiss();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        binding.all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.all.setBackgroundResource(R.drawable.radio_normalround);
                binding.all.setTextColor(Color.WHITE);
                binding.religion.setBackgroundResource(R.drawable.round_bg);
                binding.politics.setBackgroundResource(R.drawable.round_bg);
                binding.technology.setBackgroundResource(R.drawable.round_bg);
                binding.education.setBackgroundResource(R.drawable.round_bg);
                binding.medication.setBackgroundResource(R.drawable.round_bg);
                binding.agriculture.setBackgroundResource(R.drawable.round_bg);
                binding.finance.setBackgroundResource(R.drawable.round_bg);
                binding.religion.setTextColor(Color.BLACK);
                binding.politics.setTextColor(Color.BLACK);
                binding.technology.setTextColor(Color.BLACK);
                binding.education.setTextColor(Color.BLACK);
                binding.medication.setTextColor(Color.BLACK);
                binding.agriculture.setTextColor(Color.BLACK);
                binding.finance.setTextColor(Color.BLACK);
                fetchPosts();
            }
        });

        binding.religion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.religion.setBackgroundResource(R.drawable.radio_normalround);
                binding.religion.setTextColor(Color.WHITE);
                binding.all.setBackgroundResource(R.drawable.round_bg);
                binding.politics.setBackgroundResource(R.drawable.round_bg);
                binding.technology.setBackgroundResource(R.drawable.round_bg);
                binding.education.setBackgroundResource(R.drawable.round_bg);
                binding.medication.setBackgroundResource(R.drawable.round_bg);
                binding.agriculture.setBackgroundResource(R.drawable.round_bg);
                binding.finance.setBackgroundResource(R.drawable.round_bg);
                binding.all.setTextColor(Color.BLACK);
                binding.politics.setTextColor(Color.BLACK);
                binding.technology.setTextColor(Color.BLACK);
                binding.education.setTextColor(Color.BLACK);
                binding.medication.setTextColor(Color.BLACK);
                binding.agriculture.setTextColor(Color.BLACK);
                binding.finance.setTextColor(Color.BLACK);
                fetchCategory("Religion");
            }
        });

        binding.notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getRootView().getContext(), R.style.SheetDialog);
                BottomSheetBehavior<View> bottomSheetBehavior;
                View bottomsheetview = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.bottom_notifications, null);
                bottomSheetDialog.setContentView(bottomsheetview);
                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomsheetview.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                RelativeLayout linearLayout = bottomsheetview.findViewById(R.id.dialogContainer);
                assert linearLayout != null;
                linearLayout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                bottomSheetDialog.show();
                RecyclerView recyclerView = (RecyclerView) bottomsheetview.findViewById(R.id.notificationRv);
                TextView cancelBTn = (TextView) bottomsheetview.findViewById(R.id.closeBtn);
                RelativeLayout emptyLAyout = (RelativeLayout) bottomsheetview.findViewById(R.id.emptyLay);
                cancelBTn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                notificationAdapter = new NotificationAdapter(MainActivity.this, notificationsList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setItemViewCacheSize(5);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(notificationAdapter);
                DatabaseReference reference9 = FirebaseDatabase.getInstance().getReference("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference9.keepSynced(true);
                reference9.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        notificationsList.clear();
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Notifications post = snapshot.getValue(Notifications.class);
                            notificationsList.add(0,post);
                            i++;
                        }

                        notificationAdapter.notifyDataSetChanged();
                        if (i == 0){
                         emptyLAyout.setVisibility(View.VISIBLE);
                        } else {
                            emptyLAyout.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(binding.searchEt.getText().toString())){
                    hideLayout();
                } else {
                    showLayout();
                }
                searchPost();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSearchVisible) {
                    animateTransition();
                } else {
                    animateReverseTransition();
                }
            }
        });

        binding.politics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.politics.setBackgroundResource(R.drawable.radio_normalround);
                binding.politics.setTextColor(Color.WHITE);
                binding.religion.setBackgroundResource(R.drawable.round_bg);
                binding.all.setBackgroundResource(R.drawable.round_bg);
                binding.technology.setBackgroundResource(R.drawable.round_bg);
                binding.education.setBackgroundResource(R.drawable.round_bg);
                binding.medication.setBackgroundResource(R.drawable.round_bg);
                binding.agriculture.setBackgroundResource(R.drawable.round_bg);
                binding.finance.setBackgroundResource(R.drawable.round_bg);
                binding.religion.setTextColor(Color.BLACK);
                binding.all.setTextColor(Color.BLACK);
                binding.technology.setTextColor(Color.BLACK);
                binding.education.setTextColor(Color.BLACK);
                binding.medication.setTextColor(Color.BLACK);
                binding.agriculture.setTextColor(Color.BLACK);
                binding.finance.setTextColor(Color.BLACK);
                fetchCategory("Politics");
            }
        });

        binding.technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.technology.setBackgroundResource(R.drawable.radio_normalround);
                binding.technology.setTextColor(Color.WHITE);
                binding.religion.setBackgroundResource(R.drawable.round_bg);
                binding.politics.setBackgroundResource(R.drawable.round_bg);
                binding.all.setBackgroundResource(R.drawable.round_bg);
                binding.education.setBackgroundResource(R.drawable.round_bg);
                binding.medication.setBackgroundResource(R.drawable.round_bg);
                binding.agriculture.setBackgroundResource(R.drawable.round_bg);
                binding.finance.setBackgroundResource(R.drawable.round_bg);
                binding.religion.setTextColor(Color.BLACK);
                binding.politics.setTextColor(Color.BLACK);
                binding.all.setTextColor(Color.BLACK);
                binding.education.setTextColor(Color.BLACK);
                binding.medication.setTextColor(Color.BLACK);
                binding.agriculture.setTextColor(Color.BLACK);
                binding.finance.setTextColor(Color.BLACK);
                fetchCategory("Technology");
            }
        });

        binding.education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.education.setBackgroundResource(R.drawable.radio_normalround);
                binding.education.setTextColor(Color.WHITE);
                binding.religion.setBackgroundResource(R.drawable.round_bg);
                binding.politics.setBackgroundResource(R.drawable.round_bg);
                binding.technology.setBackgroundResource(R.drawable.round_bg);
                binding.all.setBackgroundResource(R.drawable.round_bg);
                binding.medication.setBackgroundResource(R.drawable.round_bg);
                binding.agriculture.setBackgroundResource(R.drawable.round_bg);
                binding.finance.setBackgroundResource(R.drawable.round_bg);
                binding.religion.setTextColor(Color.BLACK);
                binding.politics.setTextColor(Color.BLACK);
                binding.technology.setTextColor(Color.BLACK);
                binding.all.setTextColor(Color.BLACK);
                binding.medication.setTextColor(Color.BLACK);
                binding.agriculture.setTextColor(Color.BLACK);
                binding.finance.setTextColor(Color.BLACK);
                fetchCategory("Education");
            }
        });

        binding.medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.medication.setBackgroundResource(R.drawable.radio_normalround);
                binding.medication.setTextColor(Color.WHITE);
                binding.religion.setBackgroundResource(R.drawable.round_bg);
                binding.politics.setBackgroundResource(R.drawable.round_bg);
                binding.technology.setBackgroundResource(R.drawable.round_bg);
                binding.education.setBackgroundResource(R.drawable.round_bg);
                binding.all.setBackgroundResource(R.drawable.round_bg);
                binding.agriculture.setBackgroundResource(R.drawable.round_bg);
                binding.finance.setBackgroundResource(R.drawable.round_bg);
                binding.religion.setTextColor(Color.BLACK);
                binding.politics.setTextColor(Color.BLACK);
                binding.technology.setTextColor(Color.BLACK);
                binding.education.setTextColor(Color.BLACK);
                binding.all.setTextColor(Color.BLACK);
                binding.agriculture.setTextColor(Color.BLACK);
                binding.finance.setTextColor(Color.BLACK);
                fetchCategory("Medication");
            }
        });

        binding.agriculture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.agriculture.setBackgroundResource(R.drawable.radio_normalround);
                binding.agriculture.setTextColor(Color.WHITE);
                binding.religion.setBackgroundResource(R.drawable.round_bg);
                binding.politics.setBackgroundResource(R.drawable.round_bg);
                binding.technology.setBackgroundResource(R.drawable.round_bg);
                binding.education.setBackgroundResource(R.drawable.round_bg);
                binding.medication.setBackgroundResource(R.drawable.round_bg);
                binding.all.setBackgroundResource(R.drawable.round_bg);
                binding.finance.setBackgroundResource(R.drawable.round_bg);
                binding.religion.setTextColor(Color.BLACK);
                binding.politics.setTextColor(Color.BLACK);
                binding.technology.setTextColor(Color.BLACK);
                binding.education.setTextColor(Color.BLACK);
                binding.medication.setTextColor(Color.BLACK);
                binding.all.setTextColor(Color.BLACK);
                binding.finance.setTextColor(Color.BLACK);
                fetchCategory("Agriculture");
            }
        });

        binding.finance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.finance.setBackgroundResource(R.drawable.radio_normalround);
                binding.finance.setTextColor(Color.WHITE);
                binding.religion.setBackgroundResource(R.drawable.round_bg);
                binding.politics.setBackgroundResource(R.drawable.round_bg);
                binding.technology.setBackgroundResource(R.drawable.round_bg);
                binding.education.setBackgroundResource(R.drawable.round_bg);
                binding.medication.setBackgroundResource(R.drawable.round_bg);
                binding.agriculture.setBackgroundResource(R.drawable.round_bg);
                binding.all.setBackgroundResource(R.drawable.round_bg);
                binding.religion.setTextColor(Color.BLACK);
                binding.politics.setTextColor(Color.BLACK);
                binding.technology.setTextColor(Color.BLACK);
                binding.education.setTextColor(Color.BLACK);
                binding.medication.setTextColor(Color.BLACK);
                binding.agriculture.setTextColor(Color.BLACK);
                binding.all.setTextColor(Color.BLACK);
                fetchCategory("Finance");
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fetchData(FirebaseAuth.getInstance().getCurrentUser().getUid());
                fetchPosts();
            }
        });

        binding.ideateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( NetworkUtils.isNetworkAvailable(MainActivity.this)){
                    Intent intent = new Intent(MainActivity.this, PostActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) MainActivity.this, binding.ideateBtn, "go").toBundle();
                        startActivity(intent, bundle);
                    } else {
                        startActivity(intent);
                    }
                } else {
                    Dialog dialog = new Dialog(view.getRootView().getContext(), R.style.DialogCorner);
                    dialog.setContentView(R.layout.nointernet);
                    dialog.show();

                    TextView cancel = (TextView) dialog.findViewById(R.id.noBtn);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

            }
        });

    }


    private void searchPost() {
        DatabaseReference reference9 = FirebaseDatabase.getInstance().getReference("Posts");
        reference9.keepSynced(true);
        reference9.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getProblemTxt().toLowerCase().contains(binding.searchEt.getText().toString().trim().toLowerCase())){
                        postList.add(0,post);
                        i++;
                    }

                }
                postAdapter.notifyDataSetChanged();

                if (binding.radioGroup.getVisibility() == View.GONE){
                    if (i == 0){
                        binding.emptyLay.setVisibility(View.VISIBLE);
                    } else {
                        binding.emptyLay.setVisibility(View.GONE);
                    }
                } else {
                    binding.emptyLay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showLayout() {
        binding.radioGroup.setVisibility(View.VISIBLE);
        binding.ideateBtn.setVisibility(View.VISIBLE);
    }

    private void hideLayout() {
        binding.radioGroup.setVisibility(View.GONE);
        binding.ideateBtn.setVisibility(View.GONE);
    }

    private void animateTransition() {
        int endRadius = (int) Math.hypot(binding.welcomeBack.getWidth(), binding.welcomeBack.getHeight());

        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                binding.welcomeBack,
                binding.welcomeBack.getRight(),
                binding.welcomeBack.getTop() + binding.welcomeBack.getHeight() / 2,
                endRadius,
                0
        );
        revealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                binding.welcomeBack.setVisibility(View.INVISIBLE);

                binding.searchEt.setVisibility(View.VISIBLE);

                binding.searchEt.setTranslationX(binding.searchEt.getWidth());
                binding.searchEt.animate().translationX(0).setDuration(500).start();

                binding.searchBtn.setImageResource(R.drawable.round_clear_24_white);
                binding.searchBtn.setBackgroundResource(R.drawable.red_gradient);

                isSearchVisible = true;
            }
        });

        revealAnimator.start();
    }

    private void animateReverseTransition() {
        int endRadius = (int) Math.hypot(binding.welcomeBack.getWidth(), binding.welcomeBack.getHeight());

        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                binding.welcomeBack,
                binding.welcomeBack.getRight(),
                binding.welcomeBack.getTop() + binding.welcomeBack.getHeight() / 2,
                0,
                endRadius
        );
        revealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                binding.welcomeBack.setVisibility(View.VISIBLE);
                binding.searchEt.setText(null);
                binding.searchEt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.searchBtn.setImageResource(R.drawable.round_search_24);
                binding.searchBtn.setBackgroundResource(R.drawable.stroke_bg);

                isSearchVisible = false;
            }
        });

        revealAnimator.start();
    }

    private void fetchCategory(String finance) {
        DatabaseReference reference9 = FirebaseDatabase.getInstance().getReference("Posts");
        reference9.keepSynced(true);
        reference9.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getCategory().equals(finance)){
                        postList.add(0,post);
                        i++;
                    }

                }

                if (i == 0){
                    binding.emptyLay.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyLay.setVisibility(View.GONE);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchPosts() {
        reference9 = FirebaseDatabase.getInstance().getReference("Posts");
        reference9.keepSynced(true);

        if (childEventListener != null) {
            reference9.removeEventListener(childEventListener);
        }
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(0, post);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.emptyLay.setVisibility(View.GONE);
                        postAdapter.notifyItemInserted(0);
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
                // Handle changes to existing posts if needed
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                postAdapter.notifyDataSetChanged();
                String deletedPostId = dataSnapshot.getKey();
                for (int i = 0; i < postList.size(); i++) {
                    if (postList.get(i).getPostid().equals(deletedPostId)) {
                        postList.remove(i);
                        int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postAdapter.notifyItemRemoved(finalI);
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {
                // Handle changes in the order of posts if needed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        };

        reference9.addChildEventListener(childEventListener);

    }

    private void fetchData(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users != null){
                    String[] nameParts = users.getUsername().split(" ");
                    if (nameParts.length > 0) {
                        String firstName = nameParts[0];
                       username.setText(firstName);
                       username1.setText(firstName+",");
                    }

                    Glide.with(MainActivity.this).asBitmap().placeholder(R.drawable.placeholder).load(users.getProfilePic()).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove the child event listener to prevent memory leaks
        if (reference9 != null && childEventListener != null) {
            reference9.removeEventListener(childEventListener);
        }
    }
}