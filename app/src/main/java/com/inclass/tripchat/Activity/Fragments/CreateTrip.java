package com.inclass.tripchat.Activity.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import com.inclass.tripchat.Activity.POJO.Chat;
import com.inclass.tripchat.Activity.POJO.Places;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.Activity.POJO.TripChat;
import com.inclass.tripchat.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTrip extends Fragment {

    private EditText editTextTitle, editTextLocation;
    private ImageView imageViewAttach, imageViewAdd;
    private Button buttonCreate;

    FirebaseUser firebaseUser;
    DatabaseReference databaseTripsReference, databaseFriendsReference, databaseUserReference, databaseTripChatReference;
    ArrayList<String> friendsArrayList, tripFriendsArrayList, friendsNameArrayList;
    ValueEventListener listener, listener1;
    DataSnapshot userDataSnapshot;
    Uri filepath;
    StorageReference storageRef;
    FirebaseStorage storage;
    UploadTask uploadTask;
    Uri loactionImageUrl;
    Places destPlace;

    public CreateTrip() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_trip, container, false);

        //editTextLocation = (EditText)view.findViewById(R.id.editText_LocationCreateTrip);
        editTextTitle = (EditText)view.findViewById(R.id.editText_TitleCreateTrip);
        imageViewAdd = (ImageView)view.findViewById(R.id.imageView_AddFriendCreateTrip);
        imageViewAttach = (ImageView)view.findViewById(R.id.imageView_PicCreateTrip);
        buttonCreate = (Button)view.findViewById(R.id.button_createCreateTrip);

        storage = FirebaseStorage.getInstance();

        friendsArrayList = new ArrayList<>();
        tripFriendsArrayList = new ArrayList<>();
        friendsNameArrayList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseTripsReference = FirebaseDatabase.getInstance().getReference("Trips");
        databaseFriendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseTripChatReference = FirebaseDatabase.getInstance().getReference("TripChats");

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_createTrip);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destPlace = new Places();
                destPlace.setPlaceName(place.getName().toString());
                destPlace.setLatitude(place.getLatLng().latitude);
                destPlace.setLongitude(place.getLatLng().longitude);
                //databaseTripsReference.child("placesArrayList").setValue(placesArrayList);
            }

            @Override
            public void onError(Status status) {

            }
        });

        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friendsNameArrayList = new ArrayList<String>();
                for(int i=0;i<friendsArrayList.size();i++){
                    friendsNameArrayList.add(i,userDataSnapshot.child(friendsArrayList.get(i)).child("fName").getValue(String.class) + " " +userDataSnapshot.child(friendsArrayList.get(i)).child("lName").getValue(String.class));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tripFriendsArrayList = new ArrayList<String>();
                        dialogInterface.cancel();
                    }
                })
                .show();

            }
        });

        imageViewAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    if(filepath != null) {
                        storageRef = storage.getReference("images/" + filepath.getLastPathSegment());
                        uploadTask = storageRef.putFile(filepath);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                loactionImageUrl = taskSnapshot.getDownloadUrl();

                                Trip trip = new Trip();
                                ArrayList<Chat> chatArrayList = new ArrayList<Chat>();
                                String id = databaseTripsReference.push().getKey();
                                trip.setActive(true);
                                trip.setCreator(firebaseUser.getDisplayName());
                                trip.setCreatorID(firebaseUser.getUid());
                                trip.setTitle(editTextTitle.getText().toString());
                                //trip.setLocation(editTextLocation.getText().toString());
                                trip.setLocation(destPlace.getPlaceName());
                                trip.setDestination(destPlace);
                                trip.setFriendArrayList(tripFriendsArrayList);
                                trip.setImageUrl(loactionImageUrl.toString());
                                trip.setTripId(id);
                                databaseTripsReference.child(id).setValue(trip);

                                ArrayList<TripChat> tripChatArrayList = new ArrayList<TripChat>();
                                TripChat tripChat = new TripChat();
                                tripChat.setUid(firebaseUser.getUid());
                                tripChat.setChatArrayList(new ArrayList<Chat>());
                                tripChatArrayList.add(tripChat);
                                for(int i=0;i<tripFriendsArrayList.size();i++){
                                    tripChat = new TripChat();
                                    tripChat.setUid(tripFriendsArrayList.get(i));
                                    tripChat.setChatArrayList(new ArrayList<Chat>());
                                    tripChatArrayList.add(tripChat);
                                }
                                databaseTripChatReference.child(id).setValue(tripChatArrayList);

                                //editTextLocation.setText("");
                                editTextTitle.setText("");
                                Toast.makeText(getActivity(), "Trip Created", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        Trip trip = new Trip();
                        ArrayList<Chat> chatArrayList = new ArrayList<Chat>();
                        String id = databaseTripsReference.push().getKey();
                        trip.setActive(true);
                        trip.setCreator(firebaseUser.getDisplayName());
                        trip.setCreatorID(firebaseUser.getUid());
                        trip.setTitle(editTextTitle.getText().toString());
                        //trip.setLocation(editTextLocation.getText().toString());
                        trip.setLocation(destPlace.getPlaceName());
                        trip.setDestination(destPlace);
                        trip.setFriendArrayList(tripFriendsArrayList);
                        trip.setImageUrl("");
                        trip.setTripId(id);
                        databaseTripsReference.child(id).setValue(trip);

                        ArrayList<TripChat> tripChatArrayList = new ArrayList<TripChat>();
                        TripChat tripChat = new TripChat();
                        tripChat.setUid(firebaseUser.getUid());
                        tripChat.setChatArrayList(new ArrayList<Chat>());
                        tripChatArrayList.add(tripChat);
                        for(int i=0;i<tripFriendsArrayList.size();i++){
                            tripChat = new TripChat();
                            tripChat.setUid(tripFriendsArrayList.get(i));
                            tripChat.setChatArrayList(new ArrayList<Chat>());
                            tripChatArrayList.add(tripChat);
                        }
                        databaseTripChatReference.child(id).setValue(tripChatArrayList);

                        //editTextLocation.setText("");
                        editTextTitle.setText("");
                        Toast.makeText(getActivity(), "Trip Created", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            Log.d("PAth = ",filepath.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        listener = databaseFriendsReference.addValueEventListener(new ValueEventListener() {
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

        listener1 = databaseUserReference.addValueEventListener(new ValueEventListener() {
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
    public void onPause() {
        super.onPause();
        databaseFriendsReference.removeEventListener(listener);
        databaseUserReference.removeEventListener(listener1);
    }

    public boolean validate(){
        boolean valid = true;

        if(TextUtils.isEmpty(editTextTitle.getText().toString())){
            valid = false;
            editTextTitle.setError("Required");
        }
        /*if(TextUtils.isEmpty(editTextLocation.getText().toString())){
            valid = false;
            editTextLocation.setError("Required");
        }*/

        return valid;
    }

}