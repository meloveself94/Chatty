package com.example.android.chatty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Soul on 10/3/2017.
 */

public class AllUsersActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = findViewById(R.id.all_user_app_bar);
        //Always set support action bar then only get support action bar.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All Users");

        mRef = FirebaseDatabase.getInstance().getReference().child("users");

        mRecyclerView = findViewById(R.id.all_users_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<SingleUser , UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<SingleUser, UsersViewHolder>
                        (SingleUser.class, R.layout.single_users, UsersViewHolder.class, mRef ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, SingleUser user, int position) {

                viewHolder.setUserName(user.getDisplay_name());
                viewHolder.setUserStatus(user.getUser_status());
                viewHolder.setUserImage(user.getThumb_image(),getApplicationContext());

                final String userId = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(AllUsersActivity.this , ProfileActivity.class);
                        profileIntent.putExtra("user_id" , userId);
                        startActivity(profileIntent);



                    }
                });

            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        //A view that can be used by a firebase adapter.
        //mView represents the relative layout for all single user. (meaning everything in 1 user box
        // including photo, name and status.
        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);

            //mView will be used for onClickListener for the RecyclerView item.
            mView = itemView;


        }


        public void setUserName(String display_name) {

            TextView mUserNameView =  mView.findViewById(R.id.single_name);
            mUserNameView.setText(display_name);


        }

        public void setUserStatus(String user_status) {

            TextView mUserStatusView = mView.findViewById(R.id.single_status);
            mUserStatusView.setText(user_status);

        }

        public void setUserImage(String thumb_image , Context ctx) {

            CircleImageView mUserImageView = mView.findViewById(R.id.circular_single);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defaultavatar).into(mUserImageView);
        }
    }
}
