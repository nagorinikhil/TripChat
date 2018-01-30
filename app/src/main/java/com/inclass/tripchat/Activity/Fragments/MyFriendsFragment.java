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
import com.inclass.tripchat.Activity.Adapter.RecyclerMyFriendsAdapter;
import com.inclass.tripchat.Activity.Interface.MyFriendsInterface;
import com.inclass.tripchat.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFriendsFragment extends Fragment implements MyFriendsInterface{

    DatabaseReference databaseFriendsReference, databaseUserReference;
    ArrayList<String> uidArrayList;
    DataSnapshot userDataSnapshot;
    RecyclerMyFriendsAdapter adapter;
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    ValueEventListener listener, listener1;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uidArrayList = new ArrayList<>();

        databaseFriendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_MyFriends);

        adapter = new RecyclerMyFriendsAdapter(getActivity(),R.layout.item_my_friends,uidArrayList,userDataSnapshot,this);
        adapter.notifyDataSetChanged();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        listener = databaseFriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uidArrayList.clear();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    uidArrayList.add(child.getValue(String.class));
                    Log.d("Friend",child.getValue(String.class));
                }
                Log.d("uidArrayList Size", uidArrayList.size()+"");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listener1 = databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDataSnapshot = dataSnapshot;
                adapter.setDataSnapshot(userDataSnapshot);
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
        databaseUserReference.removeEventListener(listener1);
    }

    @Override
    public void removeFriend(String friendId) {
        databaseFriendsReference.child(friendId).removeValue();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Friends").child(friendId);
        databaseReference.child(firebaseUser.getUid()).removeValue();
    }
}
