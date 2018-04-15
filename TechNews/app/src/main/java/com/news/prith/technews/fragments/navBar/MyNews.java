package com.news.prith.technews.fragments.navBar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.news.prith.technews.R;
import com.news.prith.technews.adapter.TabLayoutAdapter;

public class MyNews extends Fragment {
    View view;
    TabLayoutAdapter tablLayoutAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_news, container, false);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        tablLayoutAdapter = new TabLayoutAdapter(this, getChildFragmentManager());
        viewPager.setAdapter(tablLayoutAdapter);
        // configure icons
        int[] imageResId = {
                R.drawable.ic_save2,
                R.drawable.ic_bookmark2 };
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < imageResId.length; i++) {
            tabLayout.getTabAt(i).setIcon(imageResId[i]);
        }


        return view;
    }
}
