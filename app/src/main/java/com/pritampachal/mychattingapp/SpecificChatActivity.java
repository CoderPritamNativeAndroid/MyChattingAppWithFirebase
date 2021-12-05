package com.pritampachal.mychattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SpecificChatActivity extends AppCompatActivity {
    private String uidReceived,nameReceived,imageReceived;
    private Toolbar toolbar;
    private ImageView imageViewBackButtonOfToolbar,imageViewCardViewOfToolbar;
    private CardView cardViewOfSendMessageButton;
    private TextView textView;
    private RecyclerView recyclerView;
    private EditText editText;
    private FirebaseAuth firebaseAuth;
    private String senderMessageRoom,receiverMessageRoom;
    private ArrayList<MessagesClass> arrayList;
    private MessagesAdapterClass messagesAdapterClass;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(SpecificChatActivity.this,ChatActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep always screen on
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //do not show key-board, when Activity-Launch
        uidReceived=getIntent().getStringExtra("uid1");
        nameReceived=getIntent().getStringExtra("name1");
        imageReceived=getIntent().getStringExtra("image1");
        toolbar=findViewById(R.id.toolbarOfSpecificChat);
        imageViewBackButtonOfToolbar=findViewById(R.id.imageViewBackButtonOfSpecificChat);
        imageViewCardViewOfToolbar=findViewById(R.id.imageViewOfSpecificUserProfilePicture);
        textView=findViewById(R.id.textViewNameOfSpecificUser);
        recyclerView=findViewById(R.id.recyclerViewOfSpecificChatActivity);
        editText=findViewById(R.id.editTextTextMultiLineGetMessage);
        cardViewOfSendMessageButton=findViewById(R.id.cardViewOfSendMessageButton);
        setSupportActionBar(toolbar);
        textView.setText(nameReceived);
        editText.requestFocus();
        firebaseAuth=FirebaseAuth.getInstance();
        senderMessageRoom=firebaseAuth.getUid()+uidReceived;
        receiverMessageRoom=uidReceived+firebaseAuth.getUid();
        arrayList=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(SpecificChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapterClass=new MessagesAdapterClass(SpecificChatActivity.this,arrayList);
        recyclerView.setAdapter(messagesAdapterClass);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SpecificChatActivity.this, ""+nameReceived, Toast.LENGTH_SHORT).show();
            }
        });
        //below-all, auto fetch profile-picture
        if(imageReceived.isEmpty()) {
            Toast.makeText(SpecificChatActivity.this, "Profile Picture Not Found!", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.get().load(imageReceived).into(imageViewCardViewOfToolbar);
        }
        //below-all, auto fetch data
        FirebaseDatabase.getInstance().getReference().child("chats").child(""+senderMessageRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    MessagesClass messagesClass=dataSnapshot.getValue(MessagesClass.class);
                    arrayList.add(messagesClass);
                }
                messagesAdapterClass.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        imageViewBackButtonOfToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SpecificChatActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageViewCardViewOfToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SpecificChatActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cardViewOfSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typeMessage=editText.getText().toString().trim();
                if(typeMessage.equals("")) {
                    Toast.makeText(SpecificChatActivity.this, "Type a Message...", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar calendar=Calendar.getInstance();
                    int intDay,intMonth,intYear;
                    intDay=calendar.get(Calendar.DATE);
                    intMonth=calendar.get(Calendar.MONTH)+1;
                    intYear=calendar.get(Calendar.YEAR);
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                    String currentTime1=simpleDateFormat.format(calendar.getTime());
                    String currentTime2=intDay+"-"+intMonth+"-"+intYear+"  "+currentTime1;
                    MessagesClass messagesClass=new MessagesClass(""+typeMessage,""+firebaseAuth.getUid(),""+currentTime2);
                    FirebaseDatabase.getInstance().getReference().child("chats").child(""+senderMessageRoom).child("messages").push().setValue(messagesClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(""+receiverMessageRoom).child("messages").push().setValue(messagesClass);
                        }
                    });
                    editText.setText("");
                    editText.requestFocus();
                }
            }
        });
        messagesAdapterClass.notifyDataSetChanged();
    }
}
