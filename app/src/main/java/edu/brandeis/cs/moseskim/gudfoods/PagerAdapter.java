package edu.brandeis.cs.moseskim.gudfoods;

/**
 * Created by Jon on 11/15/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Bundle usernameBundle;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String username) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        usernameBundle = new Bundle();
        usernameBundle.putString("username", username);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                BrowseFragment tab1 = new BrowseFragment();
                tab1.setArguments(usernameBundle);
                return tab1;
            case 1:
                SwipedListFragment tab2 = new SwipedListFragment();
                tab2.setArguments(usernameBundle);
                return tab2;
            case 2:
                TrendingFragment tab3 = new TrendingFragment();
                tab3.setArguments(usernameBundle);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
