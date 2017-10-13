package com.example.android.chatty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Soul on 10/2/2017.
 */

public class StatusUpdate extends AppCompatActivity{

    //Normal Fields
    private Toolbar mToolbar;
    private TextInputLayout mStatusUpdate;
    private Button mSaveBtn;

    //Firebase
    private DatabaseReference mRef;
    private FirebaseUser currentUser;


    //Progress
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser.getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid);

        mToolbar = (Toolbar) findViewById(R.id.status_update_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        String status_value = getIntent().getStringExtra("status_value");

        mStatusUpdate = (TextInputLayout) findViewById(R.id.status_input);
        mSaveBtn = (Button) findViewById(R.id.saveStatus);

        mStatusUpdate.getEditText().setText(status_value);


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Progress
                mProgressDialog = new ProgressDialog(StatusUpdate.this);
                mProgressDialog.setTitle("Save Changes");
                mProgressDialog.setMessage("Please Wait While Changes Are Being Saved");
                mProgressDialog.show();

                String saveText = mStatusUpdate.getEditText().getText().toString();

                mRef.child("user_status").setValue(saveText).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            mProgressDialog.dismiss();
                            Intent backToAccountSettingsIntent = new Intent(StatusUpdate.this , AccountSettings.class);
                            startActivity(backToAccountSettingsIntent);
                            finish();
                        }

                        else {

                            Toast.makeText(StatusUpdate.this, "There was an error updating status"
                            , Toast.LENGTH_SHORT).show();

                        }

                    }
                });



            }
        });

    }
}
