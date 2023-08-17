package com.orpheum.knowlio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import com.orpheum.knowlio.databinding.ActivityGuidelinesBinding;

public class GuidelinesActivity extends AppCompatActivity {

    ActivityGuidelinesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        binding = ActivityGuidelinesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });

    }
}