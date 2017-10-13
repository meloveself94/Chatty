package com.example.android.chatty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Soul on 10/1/2017.
 */
//Also known as welcome / sign in page.
public class StartActivity extends AppCompatActivity {

    private Button registerButton;
    private Button loginExistingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        registerButton = (Button) findViewById(R.id.registerNew);
        loginExistingBtn = (Button) findViewById(R.id.loginExisting) ;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        loginExistingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent loginExistIntent = new Intent(StartActivity.this , LoginActivity.class);
                startActivity(loginExistIntent);
            }
        });
    }
}
