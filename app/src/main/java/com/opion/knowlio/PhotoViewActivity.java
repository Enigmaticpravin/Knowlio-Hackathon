package com.opion.knowlio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.opion.knowlio.databinding.ActivityMainBinding;
import com.opion.knowlio.databinding.ActivityPhotoViewBinding;

public class PhotoViewActivity extends AppCompatActivity {

    ActivityPhotoViewBinding binding;
    String image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        image = getIntent().getStringExtra("image");

        Glide.with(PhotoViewActivity.this).asBitmap().load(image).placeholder(R.drawable.placeholder).into(binding.mainImage);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}