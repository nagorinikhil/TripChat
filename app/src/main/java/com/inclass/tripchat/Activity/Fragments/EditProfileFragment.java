package com.inclass.tripchat.Activity.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    EditText editText_fName, editTextLName;
    Button buttonSave;
    RadioGroup radioGroupGender;
    DatabaseReference databaseUserReference;
    FirebaseUser firebaseUser;
    ValueEventListener listener;
    User user;
    String gender;
    ImageView imageViewProfileImage;
    Uri filepath = null;
    StorageReference storageRef;
    FirebaseStorage storage;
    UploadTask uploadTask;
    Uri profilePicUrl;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        editText_fName = (EditText)view.findViewById(R.id.editText_FNameEditProfile);
        editTextLName = (EditText)view.findViewById(R.id.editText_LNameEditProfile);
        buttonSave = (Button)view.findViewById(R.id.button_SaveEditProfile);
        radioGroupGender = (RadioGroup)view.findViewById(R.id.radioGroup_genderEditProfile);
        imageViewProfileImage = (ImageView)view.findViewById(R.id.imageView_profileImageEditProfile);
        storage = FirebaseStorage.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");

        /*radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if(id == R.id.radioButton_maleSignUp){
                    gender = "Male";
                } if(radioGroupGender.getCheckedRadioButtonId() == R.id.radioButton_femaleSignUp){
                    gender = "Female";
                }
            }
        });*/

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseUser!=null){
                    if(validate()){
                        final String fName = editText_fName.getText().toString();
                        final String lName = editTextLName.getText().toString();

                        if(radioGroupGender.getCheckedRadioButtonId() == R.id.radioButton_maleEditProfile){
                            gender = "Male";
                        }
                        else if(radioGroupGender.getCheckedRadioButtonId() == R.id.radioButton_femaleEditProfile){
                            gender = "Female";
                        }
                        else if(radioGroupGender.getCheckedRadioButtonId() == -1){
                            gender="";
                        }

                        if(filepath != null){
                            storageRef = storage.getReference("images/"+filepath.getLastPathSegment());
                            uploadTask = storageRef.putFile(filepath);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    profilePicUrl = taskSnapshot.getDownloadUrl();

                                    user.setfName(fName);
                                    user.setlName(lName);
                                    user.setGender(gender);
                                    user.setImageUrl(profilePicUrl.toString());
                                    databaseUserReference.child(firebaseUser.getUid()).setValue(user);

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fName + " " + lName)
                                            .setPhotoUri(profilePicUrl)
                                            .build();

                                    firebaseUser.updateProfile(profileUpdates);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        } else {
                            user.setfName(fName);
                            user.setlName(lName);
                            user.setGender(gender);
                            databaseUserReference.child(firebaseUser.getUid()).setValue(user);

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fName + " " + lName)
                                    .build();

                            firebaseUser.updateProfile(profileUpdates);
                        }
                        Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        imageViewProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
            }
        });

        return view;
    }

    private boolean validate() {
        boolean valid = true;

        if(TextUtils.isEmpty(editText_fName.getText().toString())) {
            valid = false;
            editText_fName.setError("Required");
        }
        if(TextUtils.isEmpty(editTextLName.getText().toString())){
            valid = false;
            editTextLName.setError("Required");
        }
        return valid;
    }

    @Override
    public void onResume() {
        super.onResume();
        listener = databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    user = dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);
                }
                editText_fName.setText(user.getfName());
                editTextLName.setText(user.getlName());
                if(user.getGender()!=null) {
                    if (user.getGender().equals("Male"))
                        radioGroupGender.check(R.id.radioButton_maleEditProfile);
                    else if (user.getGender().equals("Female"))
                        radioGroupGender.check(R.id.radioButton_femaleEditProfile);
                }
                if(firebaseUser.getPhotoUrl()!=null){
                    Picasso.with(getActivity())
                            .load(firebaseUser.getPhotoUrl())
                            .resize(200,150)
                            .into(imageViewProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseUserReference.removeEventListener(listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            Log.d("PAth = ",filepath.toString());
        }
    }
}
