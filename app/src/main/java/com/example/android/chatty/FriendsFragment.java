package com.example.android.chatty;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mRecycler;
    private DatabaseReference mRef;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;
    private String mCurrent_user;
    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container , false);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user = mAuth.getCurrentUser().getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("accepted_friends").child(mCurrent_user);
        mRef.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);


        mRecycler = mMainView.findViewById(R.id.friendsList);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //Return the inflated view
        return mMainView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<SingleFriend , FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<SingleFriend, FriendsViewHolder>
                        (SingleFriend.class, R.layout.single_friend, FriendsViewHolder.class, mRef) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, final SingleFriend friends, final int position) {

                        viewHolder.setDate(friends.getDate());

                        final String list_user_id = getRef(position).getKey();

                        mUsersDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                          final String userName = dataSnapshot.child("display_name").getValue().toString();
                          String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                          if (dataSnapshot.hasChild("online")) {

                              String userOnline =   dataSnapshot.child("online").getValue().toString();
                              viewHolder.setUserOnline(userOnline);
                          }


                          viewHolder.setName(userName);
                          viewHolder.setUserImage(userThumb , getContext());

                          viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View view) {

                                  CharSequence options[] = new CharSequence []{"Open Profile" ,"Send Message"};

                                 final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                  builder.setTitle("Select Options");
                                  builder.setItems(options, new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {

                                          if(i == 0) {

                                              Intent profileIntent = new Intent(getContext() , ProfileActivity.class);
                                              profileIntent.putExtra("user_id" , list_user_id);
                                              startActivity(profileIntent);

                                          }

                                           else if (i == 1) {


                                              Intent chatIntent = new Intent(getContext() , ChatActivity.class);
                                              chatIntent.putExtra("user_id" , list_user_id);
                                              chatIntent.putExtra("user_name", userName);
                                              startActivity(chatIntent);



                                          }

                                         //set Click event for each options here

                                      }
                                  });

                                  builder.show();
                              }
                          });





                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
                mRecycler.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        //A view that can be used by a firebase adapter.
        //mView represents the relative layout for all single user. (meaning everything in 1 user box
        // including photo, name and status.
        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            //mView will be used for onClickListener for the RecyclerView item.
            mView = itemView;


        }

        public void setDate(String date) {

            TextView userStatusView = mView.findViewById(R.id.single_status);
            userStatusView.setText(date);

        }


        public void setName(String name) {

            TextView userNameView = mView.findViewById(R.id.single_name);
            userNameView.setText(name);
        }

        public void setUserImage(String userThumb, Context ctx) {

            CircleImageView mUserImageView = mView.findViewById(R.id.circular_single);
            Picasso.with(ctx).load(userThumb).placeholder(R.drawable.defaultavatar).into(mUserImageView);
        }

        public void setUserOnline(String online_status) {

            ImageView mUserOnline = mView.findViewById(R.id.single_online);

            if (online_status.equals("true")) {

                mUserOnline.setVisibility(View.VISIBLE);

            }

            else {

                mUserOnline.setVisibility(View.INVISIBLE);
            }


        }


    }
}
