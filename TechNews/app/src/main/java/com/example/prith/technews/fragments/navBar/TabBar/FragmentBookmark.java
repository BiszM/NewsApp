package com.example.prith.technews.fragments.navBar.TabBar;

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
import android.widget.Toast;

import com.example.prith.technews.FirebaseConnection;
import com.example.prith.technews.Model.NewsModel;
import com.example.prith.technews.R;
import com.example.prith.technews.adapter.MyAdapter;
import com.example.prith.technews.interfaces.DataListener;
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
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_red_light));
        swipeRefreshLayout.setEnabled(false);

        // initializing firebase database path
        databaseReference = FirebaseDatabase.getInstance().getReference("news");

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
        checkNetworkStatus(true, false);
    }

    // method which checks whether internet is running and fetching data or showing error
    // according to the network information
    public void checkNetworkStatus(boolean isRefreshing, boolean isRetry){
        // getting the network and connectivity of the mobile data and wifi
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= cm.getActiveNetworkInfo();

        if (networkInfo != null){
            connectionErrorLayout.setVisibility(View.GONE);
            if(isRetry){
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                callFirebaseData();
            }else {
                if(isRefreshing){
                    swipeRefreshLayout.setRefreshing(true);
                    callFirebaseData();
                }else{
                    List<NewsModel> checkModel = NewsModel.getBookmarkedNews();
                    if(checkModel.size() > 0) {
                        adapter = new MyAdapter(getContext(), checkModel);
                        recyclerView.setAdapter(adapter);
                        if(progressBar!=null){
                            progressBar.setVisibility(View.GONE);
                        }
                        swipeRefreshLayout.setEnabled(true);
                    }else {
                        callFirebaseData();
                    }
                }
            }
        }else {
            connectionErrorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    //method which fetches the data from the firebase database
    public void callFirebaseData(){
        Log.i("check", "firebase data");
        FirebaseConnection firebaseConnection = new FirebaseConnection(progressBar,
                getContext(), swipeRefreshLayout);
        firebaseConnection.getBookmarkNews();
        firebaseConnection.setDataListener(new DataListener() {
            @Override
            public void onDataReceived(boolean flag) {
                List<NewsModel> checkType = NewsModel.getBookmarkedNews();
                adapter = new MyAdapter(getContext(), checkType);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            recyclerView.setAdapter(adapter);

        }catch (NullPointerException e){
            Log.i("exception", e+"");
        }

        Log.i("hello2", "hello2");


    }

    public void populateData(){
        onResume();
    }
}
