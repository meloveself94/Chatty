package com.example.android.chatty;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Soul on 10/9/2017.
 */

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private Toolbar mToolbar;

    private DatabaseReference mRef;
    private DatabaseReference mMessageRef;

    private TextView mNameView;
    private TextView mLastSeenView;
    private CircleImageView mSmallPicView;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;

    private ImageButton chat_addBtn;
    private ImageButton chat_sendBtn;
    private EditText chat_message;

    private RecyclerView mChat_list;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<SingleMessage> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;


    //New Solution to load chats.///
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = (Toolbar) findViewById(R.id.chat_app_bar);



        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");



        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        //---- Custom Action Bar Items---//

        mNameView = (TextView) findViewById(R.id.chat_display_name);
        mLastSeenView = (TextView) findViewById(R.id.chat_last_seen);
        mSmallPicView = (CircleImageView) findViewById(R.id.chat_bar_image);

        chat_addBtn = (ImageButton) findViewById(R.id.chat_add);
        chat_sendBtn = (ImageButton) findViewById(R.id.chat_send);
        chat_message = (EditText) findViewById(R.id.chat_text_view);

        mAdapter = new MessageAdapter(messageList);


        mChat_list = (RecyclerView) findViewById(R.id.chat_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_message_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mChat_list.setHasFixedSize(true);
        mChat_list.setLayoutManager(mLinearLayout);
        mChat_list.setAdapter(mAdapter);

        loadMessages();

        mNameView.setText(userName);

        mRef.child("users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            String online = dataSnapshot.child("online").getValue().toString();
            String image = dataSnapshot.child("image").getValue().toString();

            if (online.equals("true")) {

                mLastSeenView.setText("Online");

            }

            else {

                GetTimeAgo getTimeAgo = new GetTimeAgo();

                long lastTime = Long.parseLong(online);

                String lastSeenTime = getTimeAgo.getTimeAgo(lastTime , getApplicationContext());

                mLastSeenView.setText(lastSeenTime);
            }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child("chat").child(mCurrent_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            if (!dataSnapshot.hasChild(mChatUser)) {

                Map chatAddMap = new HashMap();
                chatAddMap.put("seen" , false);
                chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                Map chatUserMap = new HashMap();
                chatUserMap.put("chat/" + mCurrent_user_id + "/" + mChatUser , chatAddMap);
                chatUserMap.put("chat/" + mChatUser + "/" + mCurrent_user_id ,chatAddMap);

                mRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError != null) {

                            Log.d("Chat_log" , databaseError.getMessage().toString());


                        }

                    }
                });


            }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //For setOnClick for send button

        chat_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Once refresh, refresh page item to add more item.

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();



            }
        });



    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = mRef.child("messages").child(mCurrent_user_id).child(mChatUser);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                SingleMessage singleMessage = dataSnapshot.getValue(SingleMessage.class);

                String messageLastKey = dataSnapshot.getKey();


                if (!mPrevKey.equals(messageLastKey)) {

                    messageList.add(itemPos++ , singleMessage);

                }
                else {
                    mPrevKey = mLastKey;
                }


                if (itemPos == 1) {

                    mLastKey = messageLastKey;
                }



                Log.d("TOTALKEYS" , "Last Key: " + mLastKey + "| Prev Key: " + mPrevKey + "| Message Key: " + messageLastKey);


                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false );

                mLinearLayout.scrollToPositionWithOffset(10 , 0);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void loadMessages() {

        DatabaseReference messageRef = mRef.child("messages").child(mCurrent_user_id).child(mChatUser);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            SingleMessage singleMessage = dataSnapshot.getValue(SingleMessage.class);

            itemPos++;

            if (itemPos == 1 ) {

                String messageLastKey = dataSnapshot.getKey();

                mLastKey = messageLastKey;
                mPrevKey = messageLastKey;

            }

            messageList.add(singleMessage);


            mAdapter.notifyDataSetChanged();

            mChat_list.scrollToPosition(messageList.size() - 1);

            mRefreshLayout.setRefreshing(false );

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage() {

        String message = chat_message.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + mCurrent_user_id + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrent_user_id;

            DatabaseReference user_message_push = mRef.child("messages").child(mCurrent_user_id)
                    .child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("messages" , message);
            messageMap.put("seen", false);
            messageMap.put("type" , "text");
            messageMap.put("time" , ServerValue.TIMESTAMP);
            messageMap.put("from" , mCurrent_user_id);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id , messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id , messageMap);

            chat_message.setText("");

            mRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if (databaseError != null) {

                        Log.d("Chat_log", databaseError.getMessage().toString());



                    }

                }
            });
        }
    }
}
