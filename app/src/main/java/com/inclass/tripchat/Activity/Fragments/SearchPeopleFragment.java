package com.inclass.tripchat.Activity.Fragments;

import android.annotation.SuppressLint;
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
import com.inclass.tripchat.Activity.Adapter.RecyclerSearchPeopleAdapter;
import com.inclass.tripchat.Activity.Interface.SearchPeopleInterface;
import com.inclass.tripchat.Activity.POJO.FriendRequest;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPeopleFragment extends Fragment implements SearchPeopleInterface {

    DatabaseReference databaseUserReference, databaseFriendReference, databaseFriendRequestReference;
    ArrayList<User> userArrayList;
    ArrayList<String> friendArrayList;
    ArrayList<FriendRequest> friendReqSentArrayList, friendReqReceivedArrayList;
    ArrayList<String> friendReqSentArrayListString,friendReqReceivedArrayListString;
    FirebaseUser firebaseUser;
    RecyclerSearchPeopleAdapter adapter;
    RecyclerView recyclerView;
    ValueEventListener userListener, friendListener, friendRequestListener;

    public SearchPeopleFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SearchPeopleFragment(ArrayList<User> userArrayList, ArrayList<String> friendArrayList, ArrayList<FriendRequest> friendReqSentArrayList, ArrayList<FriendRequest> friendReqReceivedArrayList) {
        this.userArrayList = userArrayList;
        this.friendArrayList = friendArrayList;
        this.friendReqSentArrayList = friendReqSentArrayList;
        this.friendReqReceivedArrayList = friendReqReceivedArrayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_people, container, false);

        userArrayList = new ArrayList<>();
        friendArrayList = new ArrayList<>();
        friendReqSentArrayList = new ArrayList<>();
        friendReqReceivedArrayList = new ArrayList<>();
        friendReqSentArrayListString = new ArrayList<>();
        friendReqReceivedArrayListString = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseFriendReference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        databaseFriendRequestReference = FirebaseDatabase.getInstance().getReference("Friend_Requests");

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_SearchPeople);

        adapter = new RecyclerSearchPeopleAdapter(getActivity(),R.layout.item_search_people,userArrayList,friendArrayList,friendReqSentArrayListString,friendReqReceivedArrayListString, this);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        userListener = databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(getActivity(), "Not Null", Toast.LENGTH_SHORT).show();

                userArrayList.clear();

                for(DataSnapshot child:dataSnapshot.getChildren()){
                    User user = child.getValue(User.class);
                    if(!user.getUserId().equals(firebaseUser.getUid())){
                        userArrayList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        friendRequestListener = databaseFriendRequestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(getActivity(), "Not Null", Toast.LENGTH_SHORT).show();

                friendReqReceivedArrayListString.clear();
                friendReqSentArrayListString.clear();
                friendReqSentArrayList.clear();
                friendReqReceivedArrayList.clear();

                for(DataSnapshot child: dataSnapshot.getChildren()){
                    if(child.child("sender").getValue().equals(firebaseUser.getUid())){
                        friendReqSentArrayList.add(child.getValue(FriendRequest.class));
                        friendReqSentArrayListString.add(child.child("reciever").getValue(String.class));
                    } else if(child.child("reciever").getValue().equals(firebaseUser.getUid())){
                        friendReqReceivedArrayList.add(child.getValue(FriendRequest.class));
                        friendReqReceivedArrayListString.add(child.child("sender").getValue(String.class));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        friendListener = databaseFriendReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(getActivity(), "Not Null", Toast.LENGTH_SHORT).show();
                friendArrayList.clear();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    friendArrayList.add(child.getValue().toString());
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
        databaseFriendReference.removeEventListener(friendListener);
        databaseUserReference.removeEventListener(userListener);
        databaseFriendRequestReference.removeEventListener(friendRequestListener);
    }

    @Override
    public void addFriendRequest(String receiverId) {
        String id = databaseFriendRequestReference.push().getKey();
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setReciever(receiverId);
        friendRequest.setReqId(id);
        friendRequest.setSender(firebaseUser.getUid());
        databaseFriendRequestReference.child(id).setValue(friendRequest);
    }
}
