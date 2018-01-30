package com.inclass.tripchat.Activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inclass.tripchat.Activity.Fragments.AboutUsFragment;
import com.inclass.tripchat.Activity.Fragments.ChatRoomListFragment;
import com.inclass.tripchat.Activity.Fragments.CreateTrip;
import com.inclass.tripchat.Activity.Fragments.EditProfileFragment;
import com.inclass.tripchat.Activity.Fragments.FriendsFragment;
import com.inclass.tripchat.Activity.Fragments.HomeFragment;
import com.inclass.tripchat.Activity.Fragments.TripsFragment;
import com.inclass.tripchat.Activity.POJO.FriendRequest;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imageViewNavHeaderProfile;
    private TextView textViewNavHeaderName, textViewNavHeaderEmail;
    FirebaseUser firebaseUser;
    DatabaseReference databaseUserReference;
    ArrayList<User> userArrayList;
    ArrayList<String> friendArrayList;
    ArrayList<FriendRequest> friendReqSentArrayList, friendReqReceivedArrayList;
    ValueEventListener listener;
    User user;

    public static int navItemIndex = 0;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mHandler = new Handler();

        userArrayList = new ArrayList<>();
        friendArrayList = new ArrayList<>();
        friendReqReceivedArrayList = new ArrayList<>();
        friendReqSentArrayList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        navHeader = navigationView.getHeaderView(0);
        imageViewNavHeaderProfile = (ImageView)navHeader.findViewById(R.id.imageView_navHeader);
        textViewNavHeaderName = (TextView)navHeader.findViewById(R.id.textView_navHeaderName);
        textViewNavHeaderEmail = (TextView)navHeader.findViewById(R.id.textView_navHeaderEmail);
        navigationView.setNavigationItemSelectedListener(this);

        loadNavHeader();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            navItemIndex = -1;
            loadHomeFragment(new HomeFragment(),"Home");
        }

    }

    private void loadHomeFragment(final Fragment fragment1, final String tag) {

        if(navItemIndex != -1)
            navigationView.getMenu().getItem(navItemIndex).setChecked(true);
        
        getSupportActionBar().setTitle(tag);
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(tag) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = fragment1;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, tag);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    private void loadNavHeader() {

        if(user!=null){
            textViewNavHeaderName.setText(user.getfName()+" "+user.getlName());
            textViewNavHeaderEmail.setText(user.getEmail());

            if(user.getImageUrl()!=null){
                Picasso.with(this)
                        .load(firebaseUser.getPhotoUrl())
                        .resize(100,100)
                        .into(imageViewNavHeaderProfile);
            }
        }

        /*if(firebaseUser!=null){
            textViewNavHeaderName.setText(firebaseUser.getDisplayName());
            textViewNavHeaderEmail.setText(firebaseUser.getEmail());

            if(firebaseUser.getPhotoUrl()!=null){
                Picasso.with(this)
                        .load(firebaseUser.getPhotoUrl())
                        .resize(100,100)
                        .into(imageViewNavHeaderProfile);
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //FirebaseAuth.getInstance().signOut();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_editProfile) {
            navItemIndex = 0;
            loadHomeFragment(new EditProfileFragment(),"EditProfile");
        } else if (id == R.id.nav_friends) {
            navItemIndex = 1;
            loadHomeFragment(new FriendsFragment(),"Friends");
        } else if (id == R.id.nav_trips) {
            navItemIndex = 2;
            loadHomeFragment(new TripsFragment(),"Trips");
        } else if (id == R.id.nav_createTrip) {
            navItemIndex = 4;
            loadHomeFragment(new CreateTrip(),"CreateTrip");
        } else if (id == R.id.nav_logout) {
            navItemIndex = 5;
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        } else if(id == R.id.nav_aboutUs){
            //navItemIndex = 6;
            loadHomeFragment(new AboutUsFragment(),"About Us");
            drawer.closeDrawers();
        } else if(id == R.id.nav_chatRoomList){
            navItemIndex = 3;
            loadHomeFragment(new ChatRoomListFragment(),"Chat Rooms");
        }
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        item.setChecked(true);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        listener = databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    user = dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);
                }
                loadNavHeader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseUserReference.removeEventListener(listener);
    }
}
