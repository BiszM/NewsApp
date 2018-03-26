package com.example.prith.technews;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.prith.technews.Model.NewsModel;
import com.example.prith.technews.Model.UserModel;
import com.example.prith.technews.interfaces.DataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by prith on 3/7/2018.
 */

public class FirebaseConnection {

    private static final DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("news");
    private static final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
    private ProgressBar progressBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DataListener listener;
    private boolean oneTime = false;

    // constructor to get activity views and context
    public FirebaseConnection(ProgressBar progressBar, Context context,
                              SwipeRefreshLayout swipeRefreshLayout){
        this.progressBar = progressBar;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        userRef.keepSynced(true);

    }

    // constructor to get activity context
    FirebaseConnection(Context context){
        this.context = context;
    }

    public void setDataListener(DataListener dataListener){
        this.listener = dataListener;
    }

    public void getNews(final String getNewsType){
        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot homeSnapshot: dataSnapshot.getChildren()){
                    callToGetNews(homeSnapshot, getNewsType);
                }
                listener.onDataReceived(true);
                if(progressBar!=null){
                    progressBar.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void callToGetNews(DataSnapshot dataSnapshot, String checkType){
        String author = (String) dataSnapshot.child("author").getValue();
        String description = (String) dataSnapshot.child("description").getValue();
        String image = (String) dataSnapshot.child("image").getValue();
        String title = (String) dataSnapshot.child("title").getValue();
        String type = (String) dataSnapshot.child("type").getValue();
        String website = (String) dataSnapshot.child("website").getValue();

        if(type.equals(checkType)){
            NewsModel newsModel = new NewsModel();
            newsModel.author = author;
            newsModel.description = description;
            newsModel.image = image;
            newsModel.title = title;
            newsModel.website = website;
            newsModel.type = type;
            newsModel.save();
        }
    }

    void setEmail(final String fbId, final String email, final String username, final String gender, final String image){
        oneTime = true;
        final boolean[] checkEmail = {true};
        final String[] userKey = {null};
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String getFbId = (String) dataSnapshot1.child("fbId").getValue();
                    Log.i("check", "fb_id" + getFbId);
                    if(oneTime) {
                        if (checkEmail[0]) {
                            if (getFbId == null) {
                                Log.i("check", "fb id null: ");
                                userKey[0] = userRef.push().getKey();
                                checkEmail[0] = false;
                            } else {
                                if (getFbId.matches(fbId)) {
                                    userKey[0] = null;
                                    checkEmail[0] = false;
                                } else {
                                    userKey[0] = userRef.push().getKey();
                                }
                            }
                        }

                        if (!checkEmail[0]) {
                            UserModel userModel = new UserModel(fbId, username, email, gender, image);
                            userRef.child(userKey[0]).setValue(userModel);
                        }
                        oneTime = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void saveNews(final String fbId, final String author, final String title,
                  final String description, final String image, final String website) {
        oneTime = true;
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String getId = (String) dataSnapshot1.child("fbId").getValue();
                    boolean saveStatus = true;
                    DataSnapshot newsPath = dataSnapshot1.child("saved_news");
                    if(newsPath.exists()) {
                        if(getId.matches(fbId)){
                            if (oneTime) {
                                boolean checkImage = true;
                                String newKey=null, userKey = null;
                                for (DataSnapshot dataSnapshot2 : newsPath.getChildren()) {
                                    String imageCheck = (String) dataSnapshot2.child("image")
                                            .getValue();
                                    if (!imageCheck.matches(image)) {
                                        newKey = dataSnapshot1.getRef()
                                                .child("saved").push().getKey();
                                        userKey = dataSnapshot1.toString();
                                        checkImage = false;
                                    } else {
                                        newKey = null;
                                        userKey = null;
                                        break;
                                    }
                                }
                                if(!checkImage) {
                                    if (newKey != null && userKey != null) {
                                        NewsModel newsModel = new
                                                NewsModel(author, title, description, image, website, true, false);
                                        dataSnapshot1.getRef()
                                                .child("saved_news")
                                                .child(newKey)
                                                .setValue(newsModel);
                                    }
                                }
                            oneTime = false;
                            }
                         }
                    } else {
                        if(getId.matches(fbId)) {
                            if (oneTime) {
                                String newKey = dataSnapshot1.getRef().child("saved").push().getKey();
                                String userKey = dataSnapshot1.toString();
                                NewsModel newsModel = new NewsModel(author, title, description,
                                        image, website, true, false);
                                dataSnapshot1.getRef().child("saved_news").child(newKey).setValue(newsModel);
                                oneTime = false;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void bookmarkNews(final String fbId, final String author, final String title,
                      final String description, final String image, final String website) {
        oneTime = true;
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String getId = (String) dataSnapshot1.child("fbId").getValue();
                    DataSnapshot newsPath = dataSnapshot1.child("bookmarked_news");
                    if(newsPath.exists()) {
                        if(getId.matches(fbId)){
                            if (oneTime) {
                                boolean checkImage = true;
                                String newKey=null, userKey = null;
                                for (DataSnapshot dataSnapshot2 : newsPath.getChildren()) {
                                    String imageCheck =
                                            (String) dataSnapshot2.child("image").getValue();
                                    if (!imageCheck.matches(image)) {
                                        newKey = dataSnapshot1.getRef()
                                                .child("bookmarked").push().getKey();
                                        userKey = dataSnapshot1.toString();
                                        checkImage = false;
                                    } else {
                                        newKey = null;
                                        userKey = null;
                                        break;
                                    }
                                }
                                if(!checkImage) {
                                    if (newKey != null && userKey != null) {
                                        NewsModel newsModel = new NewsModel(author, title,
                                                description, image, website,
                                                false, true);
                                        dataSnapshot1.getRef().child("bookmarked_news")
                                                .child(newKey).setValue(newsModel);
                                    }
                                }
                                oneTime = false;
                            }
                        }
                    } else {
                        if(getId.matches(fbId)) {
                            if (oneTime) {
                                String newKey = dataSnapshot1.getRef().child("bookmarked").push().getKey();
                                String userKey = dataSnapshot1.toString();
                                NewsModel newsModel = new NewsModel(author, title, description,
                                        image, website, false, true);
                                dataSnapshot1.getRef().child("bookmarked_news")
                                        .child(newKey).setValue(newsModel);
                                oneTime = false;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getBookmarkNews(){
        Log.i("check", "get bookmark function");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    DataSnapshot newsSnapshot = userSnapshot.child("bookmarked_news");
                    for(DataSnapshot dataSnapshot1: newsSnapshot.getChildren()) {
                        getNews(dataSnapshot1, "bookmark");
                    }
                }
                listener.onDataReceived(true);
                if(progressBar!=null){
                    progressBar.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getSaveNews(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    DataSnapshot newsSnapshot = userSnapshot.child("saved_news");
                    for(DataSnapshot dataSnapshot1: newsSnapshot.getChildren()) {
                        getNews(dataSnapshot1, "save");
                    }
                }
                listener.onDataReceived(true);
                if(progressBar!=null){
                    progressBar.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getNews(DataSnapshot dataSnapshot, String fab){
        String author = (String) dataSnapshot.child("author").getValue();
        String description = (String) dataSnapshot.child("description").getValue();
        String image = (String) dataSnapshot.child("image").getValue();
        String title = (String) dataSnapshot.child("title").getValue();
        String type = (String) dataSnapshot.child("type").getValue();
        String website = (String) dataSnapshot.child("website").getValue();
        boolean bookmarked = (boolean) dataSnapshot.child("bookmarked").getValue();
        boolean saved = (boolean) dataSnapshot.child("save").getValue();
        author = author.replace("<i>","");
        author = author.replace("</i>","");
        Log.i("author", author);
        switch (fab) {
            case "save":
                saved = true;
                break;
            case "bookmark":
                bookmarked = true;
                break;
            default:
                saved = false;
                bookmarked = false;
                break;
        }

        NewsModel newsModel = new NewsModel();
        newsModel.author = author;
        newsModel.description = description;
        newsModel.image = image;
        newsModel.title = title;
        newsModel.website = website;
        newsModel.type = type;
        newsModel.bookmarked = bookmarked;
        newsModel.save = saved;
        newsModel.save();
    }
}
