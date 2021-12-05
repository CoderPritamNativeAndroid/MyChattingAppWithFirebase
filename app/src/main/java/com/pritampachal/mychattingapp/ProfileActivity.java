package com.pritampachal.mychattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imageViewBackButton,imageViewProfilePicture;
    private EditText editText;
    private TextView textView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ProfileActivity.this,ChatActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseFirestore.collection("users").document(""+firebaseAuth.getUid()).update("status","Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseFirestore.collection("users").document(""+firebaseAuth.getUid()).update("status","Offline");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep always screen on
        toolbar=findViewById(R.id.toolbarOfViewProfile);
        imageViewBackButton=findViewById(R.id.imageViewBackButtonOfViewProfile);
        imageViewProfilePicture=findViewById(R.id.imageViewViewUserImage);
        editText=findViewById(R.id.editTextViewUserName);
        textView=findViewById(R.id.textViewMoveToUpdateProfile);
        setSupportActionBar(toolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
        FirebaseDatabase.getInstance().getReference(""+firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfileClass userProfileClass=snapshot.getValue(UserProfileClass.class);
                editText.setText(userProfileClass.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed To Fetch", Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseStorage.getInstance().getReference().child("Images").child(""+firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageViewProfilePicture);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,UpdateProfileActivity.class);
                intent.putExtra("currentUserName",""+editText.getText().toString().trim());
                startActivity(intent);
                finish();
            }
        });
    }
}
