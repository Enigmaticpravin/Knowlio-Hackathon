package com.opion.knowlio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    LinearLayout loginBtn;
    private GoogleSignInClient gSignInClient;
    private static final int RC_SIGN_IN = 101;
//    public static int default_web_client_id = 1900000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))// Request the ID token for authentication
                .requestEmail() // Request the user's email
                .build();

        // Create a GoogleSignInClient instance using the options configured above
        gSignInClient = GoogleSignIn.getClient(this,gso);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = gSignInClient.getSignInIntent();
                // Start the Google Sign-In process by launching the intent
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the returned result is from Google Sign-In
        if(requestCode == RC_SIGN_IN){
            // Get the signed-in account from the intent data
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Attempt to get the signed-in Google account
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Authenticate with Firebase using the obtained account
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                // Handle Google Sign-In failure by displaying an error message
                Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // Create authentication credentials using the Google ID token
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

        // Use Firebase Authentication to sign in with the obtained credentials
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // If sign-in is successful, get the current user and display a welcome message
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        registerUser(user);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Toast.makeText(LoginActivity.this, "Google Sign-In Successful. Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            // You can navigate to the next activity here
                        }else {
                            // If sign-in is not successful, display a failure message
                            Toast.makeText(LoginActivity.this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUser(FirebaseUser user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("username", user.getDisplayName());
        hashMap.put("profilePic", user.getPhotoUrl().toString());
        String time = String.valueOf(System.currentTimeMillis());
        hashMap.put("joined", time);
        hashMap.put("email", user.getEmail());
        reference.child(user.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        });

    }
}