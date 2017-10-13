package com.example.android.chatty;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Soul on 10/11/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<SingleMessage> mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<SingleMessage> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_chat_message , parent , false);

        return new MessageViewHolder (v);

    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.single_left_box);
            profileImage = (CircleImageView) view.findViewById(R.id.single_avatar);

        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder , int i) {

        mAuth = FirebaseAuth.getInstance();

        String currentUserId = mAuth.getCurrentUser().getUid();

        SingleMessage c = mMessageList.get(i);

        String from_user = c.getFrom();

        //if it is current user who is the one sending message to others.
        if (from_user.equals(currentUserId)) {

        viewHolder.messageText.setBackgroundColor(Color.WHITE);
        viewHolder.messageText.setTextColor(Color.BLACK);

        }
        // else if it is the other user who is sending message to current user
        else {

        viewHolder.messageText.setBackgroundResource(R.drawable.message_text);
        viewHolder.messageText.setTextColor(Color.WHITE);




        }
        viewHolder.messageText.setText(c.getMessages());

    }

    @Override
    public int getItemCount() {

        return mMessageList.size();
    }
}
