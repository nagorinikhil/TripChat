package com.inclass.tripchat.Activity.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inclass.tripchat.Activity.Adapter.RecyclerTripDetailActivityAdapter;
import com.inclass.tripchat.Activity.Interface.TripDetailInterface;
import com.inclass.tripchat.Activity.POJO.Places;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.R;
import java.util.ArrayList;

public class TripDetailActivity extends AppCompatActivity implements TripDetailInterface{

    RecyclerView recyclerView;
    RecyclerTripDetailActivityAdapter adapter;
    String tripId;
    DatabaseReference databaseTripsReference, databaseFriendsReference, databaseUserReference;
    ValueEventListener listener, listener1, listener2;
    Trip myTrip;
    ArrayList<Places> placesArrayList;
    FirebaseUser firebaseUser;
    ArrayList<String> friendsArrayList, tripFriendsArrayList, friendsNameArrayList;
    DataSnapshot userDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tripDetailActivity);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        tripId = getIntent().getStringExtra("TripId");
        placesArrayList = new ArrayList<>();
        friendsArrayList = new ArrayList<>();
        tripFriendsArrayList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_TripDetailActivity);
        adapter = new RecyclerTripDetailActivityAdapter(TripDetailActivity.this,R.layout.item_trip_detail_places,placesArrayList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        databaseTripsReference = FirebaseDatabase.getInstance().getReference("Trips").child(tripId);
        databaseFriendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Places p = new Places();
                p.setPlaceName(place.getName().toString());
                p.setLatitude(place.getLatLng().latitude);
                p.setLongitude(place.getLatLng().longitude);
                placesArrayList.add(p);
                databaseTripsReference.child("placesArrayList").setValue(placesArrayList);
            }

            @Override
            public void onError(Status status) {

            }
        });

        findViewById(R.id.button_mapTripDetailActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TripDetailActivity.this, "Welcome to Map Navigation", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TripDetailActivity.this, TripMapsActivity.class);
                intent.putExtra("TripId",tripId);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_chatRoomTripDetailActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TripDetailActivity.this, "Welcome to Chat Room", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TripDetailActivity.this, ChatActivity.class);
                intent.putExtra("TripId",tripId);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.addPersonTripDetail) {
            if(myTrip.getCreatorID().equals(firebaseUser.getUid())){
                friendsNameArrayList = new ArrayList<>();
                for(int i=0;i<friendsArrayList.size();i++){
                    if(!tripFriendsArrayList.contains(friendsArrayList.get(i))){
                        friendsNameArrayList.add(i,userDataSnapshot.child(friendsArrayList.get(i)).child("fName").getValue(String.class) + " " +userDataSnapshot.child(friendsArrayList.get(i)).child("lName").getValue(String.class));
                    } else {
                        friendsArrayList.remove(i);
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(TripDetailActivity.this);

                builder.setTitle("Select Friends")
                        .setMultiChoiceItems(friendsNameArrayList.toArray(new CharSequence[friendsArrayList.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                                if(isChecked){
                                    tripFriendsArrayList.add(friendsArrayList.get(which));
                                } else if(tripFriendsArrayList.contains(friendsArrayList.get(which))){
                                    tripFriendsArrayList.remove(friendsArrayList.get(which));
                                }
                            }
                        })
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseTripsReference.child("friendArrayList").setValue(tripFriendsArrayList);
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tripFriendsArrayList = new ArrayList<>();
                                dialogInterface.cancel();
                            }
                        })
                        .show();

                return true;
            } else {
                Toast.makeText(this, "You cannot add Friends", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        listener = databaseTripsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placesArrayList.clear();
                myTrip = dataSnapshot.getValue(Trip.class);
                getSupportActionBar().setTitle(myTrip.getTitle());
                if(myTrip.getFriendArrayList()!=null){
                    tripFriendsArrayList = myTrip.getFriendArrayList();
                }
                if(myTrip.getPlacesArrayList()!=null){
                    Log.d("PlaceArray", myTrip.getPlacesArrayList().size()+"");
                    for(int i=0;i< myTrip.getPlacesArrayList().size();i++){
                        placesArrayList.add(myTrip.getPlacesArrayList().get(i));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listener1 = databaseFriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsArrayList.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    friendsArrayList.add(child.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listener2 = databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDataSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseTripsReference.removeEventListener(listener);
        databaseUserReference.removeEventListener(listener2);
        databaseFriendsReference.removeEventListener(listener1);
    }

    @Override
    public void removePlace(int position) {
        placesArrayList.remove(position);
        databaseTripsReference.child("placesArrayList").setValue(placesArrayList);
    }
}
