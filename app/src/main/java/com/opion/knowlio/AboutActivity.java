package com.opion.knowlio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opion.knowlio.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    ActivityAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.logo.setVisibility(View.VISIBLE);
                binding.head.setVisibility(View.VISIBLE);
                binding.subtitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(AboutActivity.this, LoginActivity.class);
                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(binding.logo, "imageTransition");
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AboutActivity.this, pairs);
                            startActivity(intent, options.toBundle());
                            finish();

                        }
                    }
                }, 1500);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        binding.logo.startAnimation(slideUpAnimation);
        binding.head.startAnimation(slideUpAnimation);
        binding.subtitle.startAnimation(slideUpAnimation);
    }
    }