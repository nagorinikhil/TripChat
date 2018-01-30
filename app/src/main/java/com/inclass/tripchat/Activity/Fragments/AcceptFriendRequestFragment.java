package com.inclass.tripchat.Activity.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.inclass.tripchat.Activity.Adapter.RecyclerFriendRequestAdapter;
import com.inclass.tripchat.Activity.Interface.FriendRequestInterface;
import com.inclass.tripchat.Activity.POJO.FriendRequest;
import com.inclass.tripchat.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AcceptFriendRequestFragment extends Fragment implements FriendRequestInterface{

    ArrayList<FriendRequest> friendReqReceivedArrayList;
    DatabaseReference databaseFriendRequestReference, databaseUserReference;
    FirebaseUser firebaseUser;
    DataSnapshot userDataSnapshot;
    RecyclerFriendRequestAdapter adapter;
    RecyclerView recyclerView;

    public AcceptFriendRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accept_friend_request, container, false);

        friendReqReceivedArrayList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseFriendRequestReference = FirebaseDatabase.getInstance().getReference("Friend_Requests");
        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_FriendRequest);

        adapter = new RecyclerFriendRequestAdapter(getActivity(),R.layout.item_accept_request, friendReqReceivedArrayList, userDataSnapshot, this);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        databaseFriendRequestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(getActivity(), "Not Null", Toast.LENGTH_SHORT).show();
                friendReqReceivedArrayList.clear();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    if(child.child("reciever").getValue().equals(firebaseUser.getUid())){
                        friendReqReceivedArrayList.add(child.getValue(FriendRequest.class));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseUserReference.addValueEventListener(new ValueEventListener() {
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
    public void removeRequest(String requestId) {
        databaseFriendRequestReference.child(requestId).removeValue();
    }

    @Override
    public void addfriend(String senderId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Friends").child(senderId);
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        databaseReference.child(firebaseUser.getUid()).setValue(firebaseUser.getUid());
        databaseReference1.child(senderId).setValue(senderId);

    }
}
