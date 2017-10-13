package com.example.android.chatty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

/**
 * Created by Soul on 10/1/2017.
 */

public class RegisterActivity extends AppCompatActivity {


    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mButton;


    private Toolbar mToolbar;

    //Progress Dialog
    private ProgressDialog mProgressDialog;
    private DatabaseReference mRef;
    private DatabaseReference mTokenRef;

    //Firebase Auth.
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase Auth/
        mAuth = FirebaseAuth.getInstance();
        mTokenRef = FirebaseDatabase.getInstance().getReference().child("users");


        //Android Fields/
        mDisplayName = (TextInputLayout) findViewById(R.id.req_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.req_email);
        mPassword = (TextInputLayout) findViewById(R.id.req_password);
        mButton = (Button) findViewById(R.id.buttonCreate);
        mToolbar = (Toolbar) findViewById(R.id.register_tool_bar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Get the words typed in the edit textbox
                String display_name = mDisplayName.getEditText().getText().toString();
                String user_email = mEmail.getEditText().getText().toString();
                String user_pw = mPassword.getEditText().getText().toString();


                //If fields not empty then proceed with registering users.
                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(user_email) ||
                        !TextUtils.isEmpty(user_pw)) {

                //Show Progress Dialog Box while User create Account to minimize delay time.
                    mProgressDialog.setTitle("Registering User");
                    mProgressDialog.setMessage("Please Wait While We Create Your Account!");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                //Register users from getting the words typed in from all 3 fields.
                    register_user(display_name, user_email, user_pw);

                } else {
                    //If is empty then message will popup and not allow to proceed.
                    Toast.makeText(RegisterActivity.this, "Cannot leave any field empty",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void register_user(final String display_name , final String user_email , final String user_pw) {

        mAuth.createUserWithEmailAndPassword(user_email , user_pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //This if statement is to check whether registering user to Firebase Authentication
                // is successful or not.
                if (task.isSuccessful()) {

                    //Store new user data to Firebase Database
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid =currentUser.getUid();

                    mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String , String> userInfo = new HashMap<>();
                    userInfo.put("display_name" ,display_name);
                    userInfo.put("user_email" , user_email);
                    userInfo.put("user_password" , user_pw);
                    userInfo.put("user_status", "Hi, I'm using Chatty");
                    userInfo.put("image" , "default");
                    userInfo.put("thumb_image" , "default");
                    userInfo.put("device_token", device_token);

                    mRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            //This if is use to see whether pushing data entered by users to database
                            // is successful or not.
                            if (task.isSuccessful()) {
                                //Dismiss Progress Dialog Box before moving to new Intent if task(Registering
                                //user) is successful.
                                mProgressDialog.dismiss();

                                String currentUserId = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                mTokenRef.child(currentUserId).child("device_token").setValue(deviceToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                              //Move to new Intent after registering users.
                              Intent registerIntent = new Intent(RegisterActivity.this , MainActivity.class);

                               //This Line of Codes Doesn't allow user to go back to StartActivity when pressing
                               //back button in MainActivity.
                               registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(registerIntent);
                               finish();

                               }
                               });





                            }
                            //This else is for user to push data to database.
                            else  {
                                Toast.makeText(RegisterActivity.this , "Registering User to" +
                                        "Database Failed. Please Try Again" , Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



                }
                //This else statement is to check registering user to Firebase Authentication.
                else {
                    //Hide dialog box if error happens
                    mProgressDialog.hide();

                    //Let users know there is error in creating account
                    Toast.makeText(RegisterActivity.this , "Failed to Create Account" , Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
