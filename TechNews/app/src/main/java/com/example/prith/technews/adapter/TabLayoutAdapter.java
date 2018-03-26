package com.example.prith.technews.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.prith.technews.R;
import com.example.prith.technews.fragments.navBar.TabBar.FragmentBookmark;
import com.example.prith.technews.fragments.navBar.MyNews;
import com.example.prith.technews.fragments.navBar.TabBar.FragmentSave;

/**
 * Created by prith on 2/25/2018.
 */

public class TabLayoutAdapter extends FragmentPagerAdapter {
    private MyNews context;
    FragmentBookmark bookmark;
    public TabLayoutAdapter(MyNews context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Fragment fragment=new FragmentSave();
            return fragment;
        } else {
            bookmark = new FragmentBookmark();
            return bookmark;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public void populateData(){
        try {
            bookmark.populateData();
        }catch (NullPointerException e){
            Log.i("NullPointerException", e+"");
        }
    }
}
