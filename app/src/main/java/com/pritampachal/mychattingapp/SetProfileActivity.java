package com.pritampachal.mychattingapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetProfileActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private ImageView imageView;
    private EditText editText;
    private Button button;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityResultLauncher<String> activityResultLauncher;
    private Uri uriMain;
    private String name;

    private void createAllDataIntoFirebaseFirestore() {
        StorageReference storageReferenceProfilePicture=FirebaseStorage.getInstance().getReference().child("Images").child(""+firebaseAuth.getUid()).child("Profile Pic");
        //start, Profile Picture Compression
        Bitmap bitmap=null;
        try {
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uriMain);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] myData=byteArrayOutputStream.toByteArray();
        //end, Profile Picture Compression
        UploadTask uploadTask=storageReferenceProfilePicture.putBytes(myData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReferenceProfilePicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String,Object> map=new HashMap<>();
                        map.put("name",""+name);
                        map.put("image",""+uri.toString());
                        map.put("uid",""+firebaseAuth.getUid());
                        map.put("status","Online");
                        firebaseFirestore.collection("users").document(""+firebaseAuth.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(SetProfileActivity.this, "Profile Picture SET successfully", Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(SetProfileActivity.this,ChatActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(SetProfileActivity.this,""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SetProfileActivity.this,""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SetProfileActivity.this,""+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(SetProfileActivity.this, "Don't Press Back\nSave Your Profile", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep always screen on
        progressDialog=new ProgressDialog(SetProfileActivity.this);
        progressDialog.setMessage("Please Wait...");
        imageView=findViewById(R.id.imageViewGetUserProfilePicture);
        editText=findViewById(R.id.editTextGetUserName);
        button=findViewById(R.id.buttonSaveProfile);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                uriMain=result;
                imageView.setImageURI(result);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch("image/*");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=editText.getText().toString().trim();
                if(name.equals("")) {
                    Toast.makeText(SetProfileActivity.this, "Name field Required", Toast.LENGTH_SHORT).show();
                } else if(uriMain==null) {
                    Toast.makeText(SetProfileActivity.this, "Profile Picture Required", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    //start, create-data into RealTime Database
                    UserProfileClass userProfileClass =new UserProfileClass(""+name,""+firebaseAuth.getUid());
                    FirebaseDatabase.getInstance().getReference(""+firebaseAuth.getUid()).setValue(userProfileClass);
                    //end, create-data into RealTime Database
                    createAllDataIntoFirebaseFirestore(); //call, my own function
                }
            }
        });
    }
}
