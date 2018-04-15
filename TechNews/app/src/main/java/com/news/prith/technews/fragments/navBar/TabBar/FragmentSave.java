package com.news.prith.technews.fragments.navBar.TabBar;

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

public class FragmentSave extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;
    MyAdapter adapter;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout bookmarkLayout, saveNewsLayout, connectionErrorLayout;
    Button retryBtn;
    DatabaseReference databaseReference;
    TextView saveTextView;
    String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save_option, container, false);

        // locating views
        bookmarkLayout = view.findViewById(R.id.bookmarkLayout);
        saveNewsLayout = view.findViewById(R.id.saveNewsLayout);
        connectionErrorLayout = view.findViewById(R.id.connectionErrorLayout);
        retryBtn = view.findViewById(R.id.retryBtn);
        progressBar = view.findViewById(R.id.progressBar);
        saveTextView = view.findViewById(R.id.emptySaveView);
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
        // initializing firebase database path
        databaseReference = FirebaseDatabase.getInstance().getReference("news");

        try {
            username = Profile.getCurrentProfile().getFirstName()+" "+Profile.getCurrentProfile().getLastName();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        // calling function which checks the network status
        // which gets news data according to the network connection
        getSavedNews(false);

        return view;
    }

    @Override
    public void onRefresh() {
        // calling function which checks the network status while swiping up most
        // which gets news data according to the network connection
        NewsModel.deleteSavedNews();
        getSavedNews(true);
    }

    //creating a new model to get saved news and insert data into recycler view
    public void getSavedNews(boolean isRefreshing){
        if(isRefreshing){
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);
        }
        if(username == null) {
            Toast.makeText(getContext(), getString(R.string.enableFeature), Toast.LENGTH_SHORT).show();
            saveNewsLayout.setVisibility(View.VISIBLE);
            saveTextView.setText(getString(R.string.enableFeature));
        }else {
            final List<NewsModel> checkModel = NewsModel.getSavedNews();
            if (checkModel.size() > 0) {
                adapter = new MyAdapter(getContext(), checkModel);
                Log.i("received Model", "" + checkModel.size());
                recyclerView.setAdapter(adapter);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                FirebaseConnection firebaseConnection = new FirebaseConnection(progressBar,
                        getContext(), swipeRefreshLayout);
                firebaseConnection.getSaveNews(username);
                firebaseConnection.setDataListener(new DataListener() {
                    @Override
                    public void onDataReceived(boolean flag) {
                        List<NewsModel> checkType = NewsModel.getSavedNews();
                        if(checkType.size() == 0){
                            saveNewsLayout.setVisibility(View.VISIBLE);
                            saveTextView.setText(getString(R.string.emptySaveNews));
                        }else {
                            adapter = new MyAdapter(getContext(), checkType);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        }
    }
}
