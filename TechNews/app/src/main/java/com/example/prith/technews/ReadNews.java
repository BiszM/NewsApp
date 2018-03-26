package com.example.prith.technews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.facebook.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ReadNews extends AppCompatActivity {

    private Context context;
    private String fbId, title, description, image, author, site;
    private FirebaseConnection firebaseConnection = new FirebaseConnection(ReadNews.this);

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news);

        // navigating views
        ScrollView scrollView = findViewById(R.id.scrollView);
        ImageView mainImage = findViewById(R.id.mainImage);
        TextView titleView = findViewById(R.id.titleView);
        TextView descView = findViewById(R.id.descView);
        TextView newsInfo = findViewById(R.id.newsInfo);

        // getting session profile email
        Profile profile = Profile.getCurrentProfile();
        if(profile!= null) {
            fbId = profile.getId();
        }
        // getting bundle data passed from intent
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        description = bundle.getString("description");
        image = bundle.getString("image");
        author = "<i>"+bundle.getString("author")+"</i>";
        site = bundle.getString("site");
        Log.d("siteName", ""+site);
        if(site.contains("techcrunch")){
            site = "techcrunch";
        }else if(site.contains("techradar")){
            site = "techradar";
        }else if(site.contains("autoweek")){
            site = "autoweek";
        }else if(site.contains("rideapart")){
            site = "rideapart";
        }else if(site.contains("verge")){
            site = "verge";
        }

        String website = "<b> <font color='"+getResources().getColor(R.color.colorPrimary)+"'>"+
                site.toUpperCase()+"</font></b> - ";
        // Setting bundle data to the views
        titleView.setText(title);
        descView.setText(description);
        newsInfo.setText(Html.fromHtml( website + author));
        // use of picasso library to download and show image
        Picasso.with(this)
                .load(image)
                .resize(520,350)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.error_placeholder)
                .into(mainImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        // TODO Auto-generated method stub
                        Toast.makeText(ReadNews.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                });

        AHBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.back, R.drawable.ic_back_arrow, R.color.back);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.bookmark, R.drawable.ic_bookmark, R.color.bookmark);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.save, R.drawable.ic_save, R.color.save);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.share, R.drawable.ic_share, R.color.share);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);


        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

        // Display color under navigation bar (API 21+)
        // Don't forget these lines in your style-v21
        // <item name="android:windowTranslucentNavigation">true</item>
        // <item name="android:fitsSystemWindows">true</item>
        bottomNavigation.setBehaviorTranslationEnabled(true);

        // Manage titles
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);

        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(true);

        bottomNavigation.setCurrentItem(3);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...
                if(position == 0){
                    ReadNews.this.finish();
                }else if(position == 1){
                    Toast.makeText(ReadNews.this, "Bookmarked", Toast.LENGTH_SHORT).show();
                    firebaseConnection.bookmarkNews(fbId, author, title,
                            description, image, site);
                }else if(position == 2){
                    Toast.makeText(ReadNews.this, "Saved", Toast.LENGTH_SHORT).show();
                    firebaseConnection.saveNews(fbId, author, title,
                    description, image, site);
                }else if(position == 3){
                }else{
                }

                    return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override public void onPositionChange(int y) {
                // Manage the new y position
                Log.d("DemoActivity", "BottomNavigation Position: " + y);

            }
        });
    }
}
