package com.opion.knowlio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
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
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.opion.knowlio.Class.Users;
import com.opion.knowlio.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TextView username,username1;
    private boolean isSearchVisible = false;
    CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = (TextView) findViewById(R.id.username);
        username1 = (TextView) findViewById(R.id.username1);
        profilePic = findViewById(R.id.profilePic);

        binding.hamburgerBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
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
                    // Handle the reverse animation if needed
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
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fetchData(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });

        binding.ideateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) MainActivity.this, binding.ideateBtn, "go").toBundle();
                    startActivity(intent, bundle);
                } else {
                    startActivity(intent);
                }
            }
        });

    }

    private void updateToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("token", token);
                reference3.updateChildren(hashMap);
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
        // Calculate the final radius for the reveal animation
        int endRadius = (int) Math.hypot(binding.welcomeBack.getWidth(), binding.welcomeBack.getHeight());

        // Set up the reveal animation
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
                // Hide the welcomeBack layout
                binding.welcomeBack.setVisibility(View.INVISIBLE);
                // Show the searchEt layout
                binding.searchEt.setVisibility(View.VISIBLE);

                // Slide in the searchEt with translation animation
                binding.searchEt.setTranslationX(binding.searchEt.getWidth());
                binding.searchEt.animate().translationX(0).setDuration(500).start();

                // Update the search button's appearance
                binding.searchBtn.setImageResource(R.drawable.round_clear_24_white);
                binding.searchBtn.setBackgroundResource(R.drawable.red_gradient);

                // Update the state
                isSearchVisible = true;
            }
        });

        revealAnimator.start();
    }

    private void animateReverseTransition() {
        // Calculate the final radius for the reveal animation
        int endRadius = (int) Math.hypot(binding.welcomeBack.getWidth(), binding.welcomeBack.getHeight());

        // Set up the reveal animation
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
                // Show the welcomeBack layout
                binding.welcomeBack.setVisibility(View.VISIBLE);
                binding.searchEt.setText(null);
                // Hide the searchEt layout
                binding.searchEt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Update the search button's appearance
                binding.searchBtn.setImageResource(R.drawable.round_search_24);
                binding.searchBtn.setBackgroundResource(R.drawable.stroke_bg);

                // Update the state
                isSearchVisible = false;
            }
        });

        revealAnimator.start();
    }


    private void fetchData(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users != null){
                    if (users.getToken() == null){
                        updateToken();
                    }
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
}