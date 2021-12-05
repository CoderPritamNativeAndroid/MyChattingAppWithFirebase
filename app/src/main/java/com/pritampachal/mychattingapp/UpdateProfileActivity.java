package com.pritampachal.mychattingapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private String currentUserName;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private ImageView imageViewBackButton,imageViewProfilePicture;
    private EditText editText;
    private Button button;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityResultLauncher<String> activityResultLauncher;
    private Uri uriMainImagePath;
    private String strMainImage,newName;

    private void updateAllStringDataOnFirebaseFirestore() {
        Map<String,Object> map=new HashMap<>();
        map.put("name",""+newName);
        map.put("image",""+strMainImage);
        map.put("uid",""+firebaseAuth.getUid());
        map.put("status","Online");
        firebaseFirestore.collection("users").document(""+firebaseAuth.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(UpdateProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(UpdateProfileActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UpdateProfileActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateImageOnStorage() {
        StorageReference storageReferenceProfilePicture=FirebaseStorage.getInstance().getReference().child("Images").child(""+firebaseAuth.getUid()).child("Profile Pic");
        //below-all, Profile Picture Compression
        Bitmap bitmap=null;
        try {
            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriMainImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] myData=byteArrayOutputStream.toByteArray();
        //below-all, Profile Picture Store to Firebase
        UploadTask uploadTask=storageReferenceProfilePicture.putBytes(myData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReferenceProfilePicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        strMainImage=uri.toString();
                        updateAllStringDataOnFirebaseFirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateProfileActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UpdateProfileActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(UpdateProfileActivity.this,ChatActivity.class);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep always screen on
        currentUserName=getIntent().getStringExtra("currentUserName");
        progressDialog=new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setMessage("Please Wait...");
        toolbar=findViewById(R.id.toolbarOfUpdateProfile);
        imageViewBackButton=findViewById(R.id.imageViewBackButtonOfUpdateProfile);
        imageViewProfilePicture=findViewById(R.id.imageViewGetNewUserImage);
        editText=findViewById(R.id.editTextGetNewUserName);
        button=findViewById(R.id.buttonUpdateProfile);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        setSupportActionBar(toolbar);
        editText.setText(currentUserName);
        //below-all, fetch profile-picture
        FirebaseStorage.getInstance().getReference().child("Images").child(""+firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                strMainImage=uri.toString();
                Picasso.get().load(uri).into(imageViewProfilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfileActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpdateProfileActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                uriMainImagePath=result;
                imageViewProfilePicture.setImageURI(result);
            }
        });
        imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch("image/*");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newName=editText.getText().toString().trim();
                if(newName.equals("")) {
                    Toast.makeText(UpdateProfileActivity.this, "Name field Required", Toast.LENGTH_SHORT).show();
                } else if(uriMainImagePath!=null) {
                    progressDialog.show();
                    //start, update-data into RealTime Database
                    UserProfileClass userProfileClass =new UserProfileClass(""+newName,""+firebaseAuth.getUid());
                    FirebaseDatabase.getInstance().getReference(""+firebaseAuth.getUid()).setValue(userProfileClass);
                    //end, update-data into RealTime Database
                    updateImageOnStorage(); //call, my own function
                } else {
                    progressDialog.show();
                    //start, update-data into RealTime Database
                    UserProfileClass userProfileClass =new UserProfileClass(""+newName,""+firebaseAuth.getUid());
                    FirebaseDatabase.getInstance().getReference(""+firebaseAuth.getUid()).setValue(userProfileClass);
                    //end, update-data into RealTime Database
                    updateAllStringDataOnFirebaseFirestore(); //call, my own function
                }
            }
        });
    }
}
