package com.pritampachal.mychattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0) {
            super.onBackPressed();
            finish();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
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
        setContentView(R.layout.activity_chat);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep always screen on
        toolbar=findViewById(R.id.toolbar);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        setSupportActionBar(toolbar);
        Drawable drawable= ContextCompat.getDrawable(ChatActivity.this,R.drawable.ic_baseline_more_vert_24);
        toolbar.setOverflowIcon(drawable);
        FragmentAdapterClass fragmentAdapterClass=new FragmentAdapterClass(ChatActivity.this);
        viewPager.setAdapter(fragmentAdapterClass);
        String[] arr=new String[] {"CHATS","STATUS","CALLS"};
        new TabLayoutMediator(tabLayout,viewPager,((tab,position) -> tab.setText(arr[position]))).attach();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                Intent intent=new Intent(ChatActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.settings:
                Toast.makeText(ChatActivity.this, "Settings Successfully\nHa  Ha  Ha", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }
}
