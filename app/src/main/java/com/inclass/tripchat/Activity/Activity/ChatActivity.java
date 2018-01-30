package com.inclass.tripchat.Activity.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.inclass.tripchat.Activity.Adapter.RecyclerChatActivityAdapter;
import com.inclass.tripchat.Activity.Interface.ChatActivityInterface;
import com.inclass.tripchat.Activity.POJO.Chat;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.Activity.POJO.TripChat;
import com.inclass.tripchat.R;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements ChatActivityInterface{

    String tripId;
    DatabaseReference databaseTripsReference, databaseTripChatReference;
    ValueEventListener databaseTripsReferenceListener, databaseTripChatReferenceListener;
    ArrayList<TripChat> tripChatArrayList;
    ArrayList<Chat> myChatArrayList;
    TripChat myTripChat;
    Trip trip;
    FirebaseUser firebaseUser;
    RecyclerChatActivityAdapter adapter;
    RecyclerView recyclerView;
    EditText editTextMessage;
    ImageView imageViewGallery, imageViewSend;
    Uri filepath;
    StorageReference storageRef;
    FirebaseStorage storage;
    UploadTask uploadTask;
    Uri imageUrl;
    int myPosition;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        storage = FirebaseStorage.getInstance();

        tripChatArrayList = new ArrayList<>();
        myChatArrayList = new ArrayList<>();
        myTripChat = new TripChat();
        trip = new Trip();
        tripId = getIntent().getStringExtra("TripId");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseTripsReference = FirebaseDatabase.getInstance().getReference("Trips").child(tripId);
        databaseTripChatReference = FirebaseDatabase.getInstance().getReference("TripChats").child(tripId);

        imageViewGallery = (ImageView)findViewById(R.id.imageView_galleryChatActivity);
        imageViewSend = (ImageView)findViewById(R.id.imageView_sendChatActivity);
        editTextMessage = (EditText)findViewById(R.id.editText_messageChatActivity);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_ChatActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = (Toolbar)findViewById(R.id.toolbar_chatActivity);
        setSupportActionBar(toolbar);

        adapter = new RecyclerChatActivityAdapter(ChatActivity.this,R.layout.item_chat_right,myChatArrayList,firebaseUser.getUid(),this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextMessage.length()>0){
                    Chat chat = new Chat();
                    chat.setImageUrl("");
                    chat.setSender(firebaseUser.getUid());
                    chat.setSenderName(firebaseUser.getDisplayName());
                    chat.setTime(new Date());
                    chat.setMessage(editTextMessage.getText().toString());

                    for(int i=0;i<tripChatArrayList.size();i++){
                        TripChat tripChat = tripChatArrayList.get(i);
                        ArrayList<Chat> chatArrayList = tripChat.getChatArrayList();
                        if(chatArrayList == null){
                            chatArrayList = new ArrayList<Chat>();
                        }
                        chatArrayList.add(chat);
                        tripChat.setChatArrayList(chatArrayList);
                        tripChatArrayList.remove(i);
                        tripChatArrayList.add(i,tripChat);
                    }
                    databaseTripChatReference.setValue(tripChatArrayList);
                    editTextMessage.setText("");
                }
            }
        });

        imageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseTripChatReferenceListener = databaseTripChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tripChatArrayList.clear();
                myChatArrayList.clear();
                Log.d("TripChatArraList",tripChatArrayList.size()+"");
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    tripChatArrayList.add(child.getValue(TripChat.class));
                    if(child.child("uid").getValue(String.class).equals(firebaseUser.getUid())){
                        myPosition = Integer.parseInt(child.getKey());
                        for(DataSnapshot child1 : child.child("chatArrayList").getChildren()){
                            myChatArrayList.add(child1.getValue(Chat.class));
                        }
                    }
                }/*
                if(myTripChat.getChatArrayList()==null){
                    myChatArrayList = new ArrayList<Chat>();
                    adapter.notifyDataSetChanged();
                } else {
                    myChatArrayList = myTripChat.getChatArrayList();
                    Log.d("myChatArrayList",myChatArrayList.size()+"");
                    Log.d("myChatArrayList",myChatArrayList.get(0).getMessage()+"");
                    adapter.notifyDataSetChanged();
                }*/
                Log.d("myChatArrayList",myChatArrayList.size()+"");
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseTripsReferenceListener = databaseTripsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trip = dataSnapshot.getValue(Trip.class);
                getSupportActionBar().setTitle(trip.getTitle());
                if(!trip.isActive()){
                    editTextMessage.setEnabled(false);
                    imageViewGallery.setEnabled(false);
                    imageViewSend.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseTripChatReference.removeEventListener(databaseTripChatReferenceListener);
        databaseTripsReference.removeEventListener(databaseTripsReferenceListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            Log.d("PAth = ",filepath.toString());

            storageRef = storage.getReference("images/"+filepath.getLastPathSegment());
            uploadTask = storageRef.putFile(filepath);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUrl = taskSnapshot.getDownloadUrl();

                    Chat chat = new Chat();
                    chat.setImageUrl(imageUrl.toString());
                    chat.setSender(firebaseUser.getUid());
                    chat.setSenderName(firebaseUser.getDisplayName());
                    chat.setTime(new Date());
                    chat.setMessage("");

                    for(int i=0;i<tripChatArrayList.size();i++){
                        TripChat tripChat = tripChatArrayList.get(i);
                        ArrayList<Chat> chatArrayList = tripChat.getChatArrayList();
                        if(chatArrayList == null){
                            chatArrayList = new ArrayList<Chat>();
                        }
                        chatArrayList.add(chat);
                        tripChat.setChatArrayList(chatArrayList);
                        tripChatArrayList.remove(i);
                        tripChatArrayList.add(i,tripChat);
                    }
                    databaseTripChatReference.setValue(tripChatArrayList);
                }
            });
        }
    }

    @Override
    public void removeChat(int position) {
        myChatArrayList.remove(position);
        TripChat tripChat = tripChatArrayList.get(myPosition);
        tripChat.setChatArrayList(myChatArrayList);
        databaseTripChatReference.child(String.valueOf(myPosition)).setValue(tripChat);
    }
}