package com.pritampachal.mychattingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapterClass extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<MessagesClass> arrayList;
    private int MESSAGE_SEND=1,MESSAGE_RECEIVED=2;

    private class SendViewHolderClass extends RecyclerView.ViewHolder {
        private TextView textViewMessage,textViewTime;

        public SendViewHolderClass(@NonNull View itemView) {
            super(itemView);
            textViewMessage=itemView.findViewById(R.id.textViewSenderMessage);
            textViewTime=itemView.findViewById(R.id.textViewTimeOfSenderMessage);
        }
    }

    private class ReceivedViewHolderClass extends RecyclerView.ViewHolder {
        private TextView textViewMessage,textViewTime;

        public ReceivedViewHolderClass(@NonNull View itemView) {
            super(itemView);
            textViewMessage=itemView.findViewById(R.id.textViewReceiverMessage);
            textViewTime=itemView.findViewById(R.id.textViewTimeOfReceiverMessage);
        }
    }

    public MessagesAdapterClass(Context context, ArrayList<MessagesClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemViewType(int position) {
        MessagesClass messagesClass=arrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messagesClass.getSenderId())) {
            return MESSAGE_SEND;
        } else {
            return MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MESSAGE_SEND) {
            View view= LayoutInflater.from(context).inflate(R.layout.sender_chat_layout,parent,false);
            return new SendViewHolderClass(view);
        } else {
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_chat_layout,parent,false);
            return new ReceivedViewHolderClass(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesClass messagesClass=arrayList.get(position);
        if(holder.getClass()==SendViewHolderClass.class) {
            SendViewHolderClass sendViewHolderClass=(SendViewHolderClass) holder;
            sendViewHolderClass.textViewMessage.setText(messagesClass.getMessage());
            sendViewHolderClass.textViewTime.setText(messagesClass.getCurrentTime());
        } else {
            ReceivedViewHolderClass receivedViewHolderClass=(ReceivedViewHolderClass) holder;
            receivedViewHolderClass.textViewMessage.setText(messagesClass.getMessage());
            receivedViewHolderClass.textViewTime.setText(messagesClass.getCurrentTime());
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
