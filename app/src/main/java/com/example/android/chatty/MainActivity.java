package com.example.android.chatty;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

//Real chat page.
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    //This is for the viewpager on the action bar.
    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;

    private DatabaseReference mUserRef;

    //This is for tab layout
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //This is for toolbar to show
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chatty");

        if (mAuth.getCurrentUser() != null) {

            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

        }



        //This is for viewpager on action bar.
        mViewPager = (ViewPager) findViewById(R.id.main_pagers);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionPagerAdapter);

        //This is for tab layout after view pager is set.
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        //The final step after having view pager set on a custom view pager adapter is to
        //setup view pager and tabs to be used together functionally and visually.
        //E.g changing colours of text when chainging tabs and color indication of tabs when at current tab.
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#1db04b"));
        mTabLayout.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#60d1e0"));
        mTabLayout.setupWithViewPager(mViewPager);




    } //****** This is where onCreate ENDS. //******

    //On start is a method to check is user logged in or not.
    //This method need not to be called by onCreate. Just to be placed at MainAcitivy as a method will do.
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToStartScreen();
        }
        else {
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }


    }

    //Method created specifically to send user back to StartActivity.java.
    private void sendToStartScreen() {

        Intent mainIntent = new Intent(MainActivity.this , StartActivity.class);
        startActivity(mainIntent);
        finish();

    }


    //This is for the three dots button on the ActionBar
    //To show whatever is set inside the three dots when clicked on the right top corner of action bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu , menu);

        return true;
    }


    //This is for the selection item of the three dots menu button on Action Bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        //This is when selection of "Log Out" is clicked.
        if (item.getItemId() == R.id.main_logout_btn) {

            FirebaseAuth.getInstance().signOut();
            sendToStartScreen();
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        //This is when selection of "Account Settings" is clicked.
        } else if (item.getItemId() == R.id.main_account_btn) {
            Intent accountSettingsIntent = new Intent(MainActivity.this , AccountSettings.class);
            startActivity(accountSettingsIntent);

        //This is when selection of  "All Users" is clicked.
        }else if (item.getItemId() == R.id.main_all_users_btn) {
            Intent allUsersIntent = new Intent(MainActivity.this , AllUsersActivity.class);
            startActivity(allUsersIntent);
        }


        return true;
    }
}
