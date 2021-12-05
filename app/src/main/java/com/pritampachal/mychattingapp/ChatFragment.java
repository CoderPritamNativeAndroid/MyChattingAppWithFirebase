package com.pritampachal.mychattingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ChatFragment extends Fragment {
    private FirestoreRecyclerAdapter<FirebaseModelClass,RecyclerViewHolderClass> firestoreRecyclerAdapter;

    public ChatFragment() {
    }

    private ImageView imageView;
    private class RecyclerViewHolderClass extends RecyclerView.ViewHolder {
        private TextView textViewName,textViewStatus;

        public RecyclerViewHolderClass(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageViewOfUser);
            textViewName=itemView.findViewById(R.id.textViewNameOfUser);
            textViewStatus=itemView.findViewById(R.id.textViewStatusOfUser);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(firestoreRecyclerAdapter!=null) {
            firestoreRecyclerAdapter.stopListening();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewMy=inflater.inflate(R.layout.fragment_chat, container, false);
        RecyclerView recyclerView=viewMy.findViewById(R.id.recyclerViewChatFragment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        Query query=firebaseFirestore.collection("users").whereNotEqualTo("uid",""+firebaseAuth.getUid());
        FirestoreRecyclerOptions<FirebaseModelClass> firestoreRecyclerOptions=new FirestoreRecyclerOptions.Builder<FirebaseModelClass>().setQuery(query,FirebaseModelClass.class).build();
        firestoreRecyclerAdapter=new FirestoreRecyclerAdapter<FirebaseModelClass, RecyclerViewHolderClass>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerViewHolderClass holder, int position, @NonNull FirebaseModelClass model) {
                holder.textViewName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(imageView);
                if(model.getStatus().equals("Online")) {
                    holder.textViewStatus.setText(model.getStatus());
                    holder.textViewStatus.setTextColor(Color.GREEN);
                } else {
                    holder.textViewStatus.setText(model.getStatus());
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(),SpecificChatActivity.class);
                        intent.putExtra("uid1",""+model.getUid());
                        intent.putExtra("name1",""+model.getName());
                        intent.putExtra("image1",""+model.getImage());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1=LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_fragment_chat_layout,parent,false);
                return new RecyclerViewHolderClass(view1);
            }
        };
        recyclerView.setAdapter(firestoreRecyclerAdapter);
        return viewMy;
    }
}
