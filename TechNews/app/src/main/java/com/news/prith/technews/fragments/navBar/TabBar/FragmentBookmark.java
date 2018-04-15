package com.news.prith.technews.fragments.navBar.TabBar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.news.prith.technews.FirebaseConnection;
import com.news.prith.technews.Model.NewsModel;
import com.news.prith.technews.R;
import com.news.prith.technews.adapter.MyAdapter;
import com.news.prith.technews.interfaces.DataListener;
import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by prith on 2/26/2018.
 */

public class FragmentBookmark extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View view;
    RecyclerView recyclerView;
    MyAdapter adapter;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout bookmarkLayout, saveNewsLayout, connectionErrorLayout;
    Button retryBtn;
    DatabaseReference databaseReference;
    String username;
    TextView bookmarkTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_save_option, container, false);

        // locating views
        bookmarkLayout = view.findViewById(R.id.bookmarkLayout);
        saveNewsLayout = view.findViewById(R.id.saveNewsLayout);
        connectionErrorLayout = view.findViewById(R.id.connectionErrorLayout);
        retryBtn = view.findViewById(R.id.retryBtn);
        progressBar = view.findViewById(R.id.progressBar);
        bookmarkTextView = view.findViewById(R.id.emptyBookmarkView);

        // Controlling visibility
        bookmarkLayout.setVisibility(View.GONE);
        saveNewsLayout.setVisibility(View.GONE);
        connectionErrorLayout.setVisibility(View.GONE);

        // recyclerView initializing
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // controlling swipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_red_light));
        swipeRefreshLayout.setEnabled(false);

        // initializing firebase database path
        databaseReference = FirebaseDatabase.getInstance().getReference("news");
        try {
            username = Profile.getCurrentProfile().getFirstName()+" "+Profile.getCurrentProfile().getLastName();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        Log.i("check", "firebase data "+ username);
        // calling function which checks the network status
        // which gets news data according to the network connection
        checkNetworkStatus(false, false);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetworkStatus(false, true);
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        // calling function which checks the network status while swiping up most
        // which gets news data according to the network connection
        NewsModel.deleteBookmarkedNews();
        checkNetworkStatus(true, false);
    }

    // method which checks whether internet is running and fetching data or showing error
    // according to the network information
    public void checkNetworkStatus(boolean isRefreshing, boolean isRetry){
        // getting the network and connectivity of the mobile data and wifi
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= cm.getActiveNetworkInfo();
        List<NewsModel> checkModel = null;
        if(username == null) {
            bookmarkLayout.setVisibility(View.VISIBLE);
            bookmarkTextView.setText(getString(R.string.enableFeature));
        }else {
            if (networkInfo != null) {
                connectionErrorLayout.setVisibility(View.GONE);
                if (isRetry) {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    callFirebaseData();
                } else {
                    if (isRefreshing) {
                        swipeRefreshLayout.setRefreshing(true);
                        callFirebaseData();
                    } else {
                        checkModel = NewsModel.getBookmarkedNews();
                        if (checkModel.size() > 0) {
                            adapter = new MyAdapter(getContext(), checkModel);
                            recyclerView.setAdapter(adapter);
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            swipeRefreshLayout.setEnabled(true);
                        } else {
                            callFirebaseData();
                        }
                    }
                }
            } else {
                connectionErrorLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    //method which fetches the data from the firebase database
    public void callFirebaseData(){
        FirebaseConnection firebaseConnection = new FirebaseConnection(progressBar,
                getContext(), swipeRefreshLayout);
        firebaseConnection.getBookmarkNews(username);
        firebaseConnection.setDataListener(new DataListener() {
            @Override
            public void onDataReceived(boolean flag) {
                List<NewsModel> checkType = NewsModel.getBookmarkedNews();
                Log.i("size",""+checkType.size());
                if(checkType.size() == 0){
                    bookmarkLayout.setVisibility(View.VISIBLE);
                    bookmarkTextView.setText(getString(R.string.emptySaveNews));
                }else {
                    adapter = new MyAdapter(getContext(), checkType);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

}
