package com.example.android.chatty;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Soul on 10/2/2017.
 */

class SectionPagerAdapter extends FragmentPagerAdapter {

    //Empty Constructor
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    //Get and set methods
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            //Case zero is referring to the Request Fragment.
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    //This method is to set the names for Tabs with adapter so that each tabs has it's name
    //corresponding to each position of the tabs. This method is no need to be called anywhere.
    //Just creating this method will automatically display the names setting in the "" for the tabs.

    public CharSequence getPageTitle(int position) {

        switch (position) {

            case 0 :
                return "Request";

            case 1 :
                return "Chats";

            case 2:
                return "Friends";

            default:
                return null;
        }

    }
}
