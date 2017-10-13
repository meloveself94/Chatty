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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Soul on 10/2/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPw;
    private Button mLoginBtn;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase initialize login Authentication.
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" Login");

        mProgressDialog = new ProgressDialog(this);

        mRef = FirebaseDatabase.getInstance().getReference().child("users");


        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPw = (TextInputLayout) findViewById(R.id.login_pw);
        mLoginBtn = (Button) findViewById(R.id.btnLogin);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get the text typed into the two fields.
                String email = mLoginEmail.getEditText().getText().toString();
                String pw = mLoginPw.getEditText().getText().toString();

               //If fields not empty then proceed with loging-in users.
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pw)) {


                    mProgressDialog.setTitle("Please Wait a Moment");
                    mProgressDialog.setMessage("Logging in User");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    loginUser(email , pw);

                } else {
                    //If is empty then message will popup and not allow to proceed.
                    Toast.makeText(LoginActivity.this, "Cannot leave any field empty",
                            Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    private void loginUser(String email, String pw) {

        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    //Dismiss dialog if signing in is successful before proceding to MainActivity.
                    mProgressDialog.dismiss();

                    String currentUserId = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mRef.child(currentUserId).child("device_token").setValue(deviceToken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent loginIntent = new Intent(LoginActivity.this , MainActivity.class);

                                    //This Line of Codes Doesn't allow user to go back to StartActivity when pressing
                                    //back button in MainActivity.
                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginIntent);
                                    finish();

                                }
                            });




                }
                else {

                    //Hide progress bar and let user know sign in failed.
                    mProgressDialog.hide();

                    //Let users know there is error in creating account when wrong password or email entered.
                    Toast.makeText(LoginActivity.this , "Cannot Sign In, Please check fields and" +
                            " try again" , Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

}
