package com.news.prith.technews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.prith.technews.Model.NewsModel;
import com.news.prith.technews.fragments.navBar.FragmentBike;
import com.news.prith.technews.fragments.navBar.FragmentCar;
import com.news.prith.technews.fragments.navBar.FragmentHome;
import com.news.prith.technews.fragments.navBar.FragmentLaptop;
import com.news.prith.technews.fragments.navBar.FragmentPhone;
import com.news.prith.technews.fragments.navBar.MyNews;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener{

    private TextView welcomeUser;
    private ImageView profilePic;
    private CallbackManager callbackManager;
    private static String TAG = "MainActivity";
    private String onFragment;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Mapping hashkey
//        try {
//            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo("com.news.prith.technews",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
//                messageDigest.update(signature.toByteArray());
//                Log.d("KeyHash", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        //fragments sidebars
        onFragment = "home";
        FragmentHome fragmentHome = new FragmentHome();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contentMain, fragmentHome);
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        //login session
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        LoginButton loginButton = headerView.findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // save accessToken to SharedPreference
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject jsonObject,
                                                            GraphResponse response) {

                                        // Getting FB User Data
                                        try {
                                            displayUserInfo(jsonObject);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "first_name, last_name, email, gender, id");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Login attempt cancelled.");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Login attempt failed.");
                    }
                }
        );


        profilePic = headerView.findViewById(R.id.profilePic);
        welcomeUser = headerView.findViewById(R.id.userWelcome);

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            String fullName = profile.getFirstName() + " " + profile.getLastName();
            String profile_pic = profile.getProfilePictureUri(100, 100).toString();

            welcomeUser.setText(fullName);
            Picasso.with(this)
                    .load(profile_pic)
                    .resize(150, 150)
                    .onlyScaleDown()
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(profilePic);
        }



        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    //write your code here what to do when user logout
                    welcomeUser.setText(getResources().getString(R.string.welcome));
                    profilePic.setImageResource(R.mipmap.ic_launcher_round);
                }
            }
        }.startTracking();
    }


    @SuppressLint("SetTextI18n")
    public void displayUserInfo(JSONObject object) throws JSONException {
        String first_name="", last_name="", email="", gender="", image="", fbId="", username="";
        URL profile_pic = null;

        try{
            fbId = object.getString("id");
            Log.i("userId2", fbId);
            profile_pic = new URL("https://graph.facebook.com/" + fbId + "/picture?type=large");
            image = profile_pic.toString();
            if (object.has("first_name"))
                first_name = object.getString("first_name");
            if (object.has("last_name"))
                last_name = object.getString("last_name");
            if (object.has("email"))
                email = object.getString("email");
            if (object.has("gender"))
                gender = object.getString("gender");
            username = first_name+" "+last_name;

        } catch (Exception e) {
            Log.d(TAG, "Exception : "+e.toString());
        }

        //checking user in firebase database
        FirebaseConnection firebaseConnection = new FirebaseConnection(MainActivity.this);
        firebaseConnection.setEmail(fbId, email, username, gender, image);

        welcomeUser.setText(username);
        Picasso.with(this)
                .load(profile_pic.toString())
                .resize(150, 150)
                .onlyScaleDown()
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.error_placeholder)
                .into(profilePic);


        Log.i("fbInfo", fbId+"|"+username+"|"+email+"|"+gender+"|"+image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (id == R.id.nav_home) {
            onFragment = "home";
            FragmentHome fragmentHome = new FragmentHome();
            fragmentTransaction.replace(R.id.contentMain, fragmentHome, "home");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_phone) {
            onFragment = "mobile";
            FragmentPhone fragmentPhone = new FragmentPhone ();
            fragmentTransaction.replace(R.id.contentMain, fragmentPhone, "mobile");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_laptop) {
            onFragment = "laptop";
            FragmentLaptop fragmentLaptop = new FragmentLaptop();
            fragmentTransaction.replace(R.id.contentMain, fragmentLaptop, "laptop");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_car) {
            onFragment = "car";
            FragmentCar fragmentCar= new FragmentCar();
            fragmentTransaction.replace(R.id.contentMain, fragmentCar, "car");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_bike) {
            onFragment = "bike";
            FragmentBike fragmentBike= new FragmentBike();
            fragmentTransaction.replace(R.id.contentMain, fragmentBike, "bike");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_my_news) {
            onFragment = "my_news";
            MyNews myNews = new MyNews();
            fragmentTransaction.replace(R.id.contentMain, myNews);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i("fragment", onFragment);
        Log.i("Text", newText);
        newText = newText.toLowerCase();
        ArrayList<NewsModel> al = new ArrayList<>();

        List<NewsModel> searchModel = null;
        switch (onFragment) {
            case "home":
                searchModel = NewsModel.getNewsDetails("home");
                break;
            case "mobile":
                searchModel = NewsModel.getNewsDetails("mobile");
                break;
            case "car":
                searchModel = NewsModel.getNewsDetails("car");
                break;
            case "bike":
                searchModel = NewsModel.getNewsDetails("bike");
                break;
            case "laptop":
                searchModel = NewsModel.getNewsDetails("laptop");
                break;
        }

        for(NewsModel model2: searchModel){
            String name = model2.title.toLowerCase();
            if(name.contains(newText) ) {
                Log.i("Text", newText);
                Log.i("Text", "title: "+ name);
                al.add(model2);
            }
        }
        if (onFragment.matches("home")) {
            FragmentHome.adapter.searchFilter(al);
        }else if (onFragment.matches("mobile")) {
            FragmentPhone.adapter.searchFilter(al);
        }else if (onFragment.matches("car")) {
            FragmentCar.adapter.searchFilter(al);
        }else if (onFragment.matches("bike")) {
            FragmentBike.adapter.searchFilter(al);
        }else if (onFragment.matches("laptop")) {
            FragmentLaptop.adapter.searchFilter(al);
        }
       //   MyAdapter adapter = new MyAdapter(MainActivity.this, al);
       //     adapter.searchFilter(al);
        return true;
    }
}
