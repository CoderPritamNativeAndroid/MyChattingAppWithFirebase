package com.pritampachal.mychattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private CountryCodePicker countryCodePicker;
    private EditText editText;
    private Button button;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String countryCode,phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep always screen on
        countryCodePicker=findViewById(R.id.countryCodePicker);
        editText=findViewById(R.id.getPhoneNumber);
        button=findViewById(R.id.sendOtpButton);
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait...");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        countryCode=countryCodePicker.getSelectedCountryCodeWithPlus();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode=countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myNumber=editText.getText().toString().trim();
                if(myNumber.equals("")) {
                    Toast.makeText(MainActivity.this, "Phone Number Required", Toast.LENGTH_SHORT).show();
                } else if(myNumber.length()<10) {
                    Toast.makeText(MainActivity.this, "invalid Phone Number", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    phoneNumber=countryCode+myNumber;
                    PhoneAuthOptions phoneAuthOptions=PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phoneNumber).setTimeout(60L, TimeUnit.SECONDS).setActivity(MainActivity.this).setCallbacks(onVerificationStateChangedCallbacks).build();
                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
                }
            }
        });
        onVerificationStateChangedCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //code here, how to automatically fetch-otp
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "OTP Sending...", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(MainActivity.this,OtpAuthenticationActivity.class);
                intent.putExtra("myOTP",""+s);
                startActivity(intent);
                finish();
            }
        };
        firebaseFirestore.collection("users").document(""+firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().exists() && firebaseAuth.getCurrentUser()!=null) {
                        Intent intent=new Intent(MainActivity.this,ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else if(!task.getResult().exists() && firebaseAuth.getCurrentUser()!=null) {
                        Intent intent=new Intent(MainActivity.this,SetProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
