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
import com.inclass.tripchat.Activity.Adapter.RecyclerChatRoomListAdapter;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomListFragment extends Fragment {

    DatabaseReference databaseTripsReference, databaseFriendsReference, databaseTripChatReference;
    ArrayList<Trip> tripsArrayList;
    ArrayList<String> friendsArrayList;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    RecyclerChatRoomListAdapter adapter;
    ValueEventListener listener, listener1, listener2;

    public ChatRoomListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_room_list, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_ChatRoomListFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        adapter = new RecyclerChatRoomListAdapter(getActivity(),R.layout.item_chat_room_list, tripsArrayList,firebaseUser.getUid());
        recyclerView.setAdapter(adapter);

        return  view;
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
                    if(friendsArrayList.contains(trip.getCreatorID()) || (trip.getCreatorID().equals(firebaseUser.getUid()) && trip.isActive())){
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

}
