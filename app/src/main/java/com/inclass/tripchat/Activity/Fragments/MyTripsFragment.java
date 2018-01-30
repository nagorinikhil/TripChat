package com.inclass.tripchat.Activity.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inclass.tripchat.Activity.Adapter.RecyclerMyTripsAdapter;
import com.inclass.tripchat.Activity.Interface.MyTripsInterface;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTripsFragment extends Fragment implements MyTripsInterface {

    FirebaseUser firebaseUser;
    DatabaseReference databaseTripsReference;
    ArrayList<Trip> myTripArrayList;
    RecyclerView recyclerView;
    ValueEventListener listener;
    RecyclerMyTripsAdapter adapter;

    public MyTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        myTripArrayList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseTripsReference = FirebaseDatabase.getInstance().getReference("Trips");

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_MyTripsFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerMyTripsAdapter(getActivity(),R.layout.item_my_trips,myTripArrayList,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        listener = databaseTripsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myTripArrayList.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    Trip trip = child.getValue(Trip.class);
                    if(trip.getCreatorID().equals(firebaseUser.getUid()) && trip.isActive()){
                        myTripArrayList.add(trip);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseTripsReference.removeEventListener(listener);
    }

    @Override
    public void removeTrip(String tripId) {
        databaseTripsReference.child(tripId).child("active").setValue(false);
    }
}
