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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpAuthenticationActivity extends AppCompatActivity {
    private String otpCodeReceived;
    private ProgressDialog progressDialog;
    private EditText editText;
    private TextView textView;
    private Button button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authentication);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep always screen on
        otpCodeReceived=getIntent().getStringExtra("myOTP");
        progressDialog=new ProgressDialog(OtpAuthenticationActivity.this);
        progressDialog.setMessage("Please Wait...");
        editText=findViewById(R.id.editTextVerifyOTP);
        textView=findViewById(R.id.textViewVerifyOTP);
        button=findViewById(R.id.buttonVerifyOTP);
        firebaseAuth=FirebaseAuth.getInstance();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OtpAuthenticationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp1;
                otp1=editText.getText().toString().trim();
                if(otp1.equals("")) {
                    Toast.makeText(OtpAuthenticationActivity.this, "OTP required", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(""+otpCodeReceived,""+otp1);
                    firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(OtpAuthenticationActivity.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent=new Intent(OtpAuthenticationActivity.this,SetProfileActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(OtpAuthenticationActivity.this, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
