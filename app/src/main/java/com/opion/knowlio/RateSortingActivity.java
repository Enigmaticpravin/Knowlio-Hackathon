package com.opion.knowlio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opion.knowlio.Adapter.PostAdapter;
import com.opion.knowlio.Adapter.RatingAdapter;
import com.opion.knowlio.Class.AIFeedback;
import com.opion.knowlio.Class.Post;
import com.opion.knowlio.databinding.ActivityRateSortingBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RateSortingActivity extends AppCompatActivity {

    ActivityRateSortingBinding binding;
    List<AIFeedback> aiFeedbackList = new ArrayList<>();
    RatingAdapter ratingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRateSortingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rateRv.setLayoutManager(new LinearLayoutManager(RateSortingActivity.this));

        ratingAdapter = new RatingAdapter(RateSortingActivity.this, aiFeedbackList);
        binding.rateRv.setHasFixedSize(true);
        binding.rateRv.setItemViewCacheSize(5);
        binding.rateRv.setItemAnimator(new DefaultItemAnimator());
        binding.rateRv.setAdapter(ratingAdapter);
        DatabaseReference reference9 = FirebaseDatabase.getInstance().getReference("AIFeedback").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference9.keepSynced(true);
        reference9.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                aiFeedbackList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AIFeedback post = snapshot.getValue(AIFeedback.class);
                    aiFeedbackList.add(0,post);

                }
                Collections.sort(aiFeedbackList, new Comparator<AIFeedback>() {
                    @Override
                    public int compare(AIFeedback aiFeedback, AIFeedback t1) {
                        return aiFeedback.getRating().compareTo(t1.getRating());
                    }
                });
                ratingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}