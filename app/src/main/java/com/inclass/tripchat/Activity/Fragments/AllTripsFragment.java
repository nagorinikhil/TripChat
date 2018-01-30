package com.inclass.tripchat.Activity.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.inclass.tripchat.Activity.Adapter.RecyclerAllTripsAdapter;
import com.inclass.tripchat.Activity.Interface.AllTripInterface;
import com.inclass.tripchat.Activity.POJO.Chat;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.Activity.POJO.TripChat;
import com.inclass.tripchat.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllTripsFragment extends Fragment implements AllTripInterface{

    DatabaseReference databaseTripsReference, databaseFriendsReference, databaseTripChatReference;
    ArrayList<Trip> tripsArrayList;
    ArrayList<String> friendsArrayList;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    RecyclerAllTripsAdapter adapter;
    ValueEventListener listener, listener1, listener2;

    public AllTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_all_trips, container, false);

        tripsArrayList = new ArrayList<>();
        friendsArrayList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseFriendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        listener = databaseFriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsArrayList.clear();
                Log.d("Inside Friend Snapshot","HELLO");
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    friendsArrayList.add(child.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseTripsReference = FirebaseDatabase.getInstance().getReference("Trips");

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_AllTripFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerAllTripsAdapter(getActivity(),R.layout.item_all_trips, tripsArrayList,firebaseUser.getUid(),this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        listener1 = databaseTripsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Friend List Size", friendsArrayList.size()+"");
                tripsArrayList.clear();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    Trip trip = child.getValue(Trip.class);
                    if((friendsArrayList.contains(trip.getCreatorID()) && trip.isActive()) || (trip.getCreatorID().equals(firebaseUser.getUid()) && trip.isActive())){
                        tripsArrayList.add(trip);
                    } else if(trip.getFriendArrayList()!=null){
                        if((trip.getFriendArrayList().contains(firebaseUser.getUid()))){
                            tripsArrayList.add(trip);
                        }
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
        databaseFriendsReference.removeEventListener(listener);
        databaseTripsReference.removeEventListener(listener1);
    }

    @Override
    public void joinTrip(final int position) {
        Trip trip = tripsArrayList.get(position);
        ArrayList<String> arrayList = trip.getFriendArrayList();
        if(arrayList == null){
            arrayList = new ArrayList<>();
        }
        arrayList.add(firebaseUser.getUid());
        trip.setFriendArrayList(arrayList);

        databaseTripsReference.child(trip.getTripId()).setValue(trip);
        databaseTripChatReference = FirebaseDatabase.getInstance().getReference("TripChats").child(trip.getTripId());

        listener2 = databaseTripChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<TripChat> tripChatArrayList = (ArrayList<TripChat>) dataSnapshot.getValue();
                TripChat tripChat = new TripChat();
                tripChat.setUid(firebaseUser.getUid());
                tripChat.setChatArrayList(new ArrayList<Chat>());
                tripChatArrayList.add(tripChat);
                databaseTripChatReference.setValue(tripChatArrayList);
                databaseTripChatReference.removeEventListener(listener2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void leaveTrip(int position) {
        Trip trip = tripsArrayList.get(position);
        ArrayList<String> arrayList = trip.getFriendArrayList();
        arrayList.remove(firebaseUser.getUid());
        trip.setFriendArrayList(arrayList);

        databaseTripChatReference = FirebaseDatabase.getInstance().getReference("TripChats").child(trip.getTripId());

        listener2 = databaseTripChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child:dataSnapshot.getChildren()){
                    if(child.child("uid").equals(firebaseUser.getUid())){
                        databaseTripChatReference.removeValue();
                    }
                }

                databaseTripChatReference.removeEventListener(listener2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseTripsReference.child(trip.getTripId()).setValue(trip);
    }
}
