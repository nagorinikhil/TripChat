package com.inclass.tripchat.Activity.Fragments;


import android.app.ProgressDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.inclass.tripchat.Activity.Activity.HomeActivity;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private EditText editTextFName, editTextLName, editTextEmail, editTextPwd, editTextCnfPwd;
    private Button buttonSignUp;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fAuthlistener;
    private ImageView imageViewAttach;
    private RadioGroup radioGroupGender;
    StorageReference storageRef;
    FirebaseStorage storage;
    UploadTask uploadTask;
    Uri profilePicUrl;
    DatabaseReference databaseUserReference,databaseFriendsReference;
    String gender = "";
    Uri filepath = null;
    FirebaseUser user;
    ProgressDialog progressDialog;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Log.d("SIGNUP ON CREATE", "ONCREATE VIEW");

        progressDialog = new ProgressDialog(getActivity());

        editTextFName = (EditText)view.findViewById(R.id.editText_FNameSignUp);
        editTextLName = (EditText)view.findViewById(R.id.editText_LNameSignUp);
        editTextEmail = (EditText)view.findViewById(R.id.editText_EmailSignUp);
        editTextPwd = (EditText)view.findViewById(R.id.editText_PwdSignUp);
        editTextCnfPwd = (EditText)view.findViewById(R.id.editText_CnfPwdSignUp);
        buttonSignUp = (Button)view.findViewById(R.id.button_SignUp);
        imageViewAttach = (ImageView)view.findViewById(R.id.imageView_attachPicSignUp);
        radioGroupGender = (RadioGroup)view.findViewById(R.id.radioGroup_genderEditProfile);

        fAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        //storageRef = storage.getReference("ProfileImage");
        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseFriendsReference = FirebaseDatabase.getInstance().getReference("Friends");

        imageViewAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
            }
        });


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    progressDialog.show();
                    final String email = editTextEmail.getText().toString();
                    String pwd = editTextPwd.getText().toString();
                    final String fName = editTextFName.getText().toString();
                    final String lName = editTextLName.getText().toString();

                    if(radioGroupGender.getCheckedRadioButtonId() == R.id.radioButton_maleSignUp)
                        gender = "Male";
                    else if(radioGroupGender.getCheckedRadioButtonId() == R.id.radioButton_femaleSignUp)
                        gender = "Female";
                    else if(radioGroupGender.getCheckedRadioButtonId() == -1){
                        gender="";
                    }

                    fAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();

                                user = FirebaseAuth.getInstance().getCurrentUser();

                                if(filepath != null){
                                    storageRef = storage.getReference("images/"+filepath.getLastPathSegment());
                                    uploadTask = storageRef.putFile(filepath);

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Photo cannot upload", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(getActivity(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
                                            profilePicUrl = taskSnapshot.getDownloadUrl();

                                            User usr = new User();
                                            usr.setfName(fName);
                                            usr.setlName(lName);
                                            usr.setEmail(email);
                                            usr.setImageUrl(profilePicUrl.toString());
                                            usr.setUserId(user.getUid());
                                            usr.setGender(gender);

                                            databaseUserReference.child(user.getUid()).setValue(usr);
                                            databaseFriendsReference.child(user.getUid()).setValue("");

                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(fName + " " + lName)
                                                    .setPhotoUri(profilePicUrl)
                                                    .build();

                                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent i = new Intent(getActivity(), HomeActivity.class);
                                                    startActivity(i);
                                                    getActivity().finish();
                                                }
                                            });

                                        }
                                    });
                                } else {
                                    User usr = new User();
                                    usr.setfName(fName);
                                    usr.setlName(lName);
                                    usr.setEmail(email);
                                    usr.setImageUrl("");
                                    usr.setUserId(user.getUid());
                                    usr.setGender(gender);

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fName + " " + lName)
                                            .setPhotoUri(profilePicUrl)
                                            .build();

                                    user.updateProfile(profileUpdates);

                                    databaseUserReference.child(user.getUid()).setValue(usr);
                                    databaseFriendsReference.child(user.getUid()).setValue("");

                                    progressDialog.dismiss();

                                    Intent i = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            }
                            else{
                                Toast.makeText(getActivity(), "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    private boolean validate() {
        boolean valid = true;
        if(TextUtils.isEmpty(editTextEmail.getText().toString())){
            valid = false;
            editTextEmail.setError("Required");
        }
        if(TextUtils.isEmpty(editTextFName.getText().toString())) {
            valid = false;
            editTextFName.setError("Required");
        }
        if(TextUtils.isEmpty(editTextLName.getText().toString())){
            valid = false;
            editTextLName.setError("Required");
        }

        String pwd = editTextPwd.getText().toString();
        if(TextUtils.isEmpty(pwd)){
            valid = false;
            editTextPwd.setError("Required");
        }

        String cnfPwd = editTextCnfPwd.getText().toString();
        if (TextUtils.isEmpty(cnfPwd)){
            valid = false;
            editTextCnfPwd.setError("Required");
        }
        if(!pwd.equals(cnfPwd)){
            valid = false;
            editTextCnfPwd.setError("Password do not match");
        }

        if(radioGroupGender.getCheckedRadioButtonId()==-1){
            valid = false;
            Toast.makeText(getActivity(), "Select Gender", Toast.LENGTH_SHORT).show();
        }
        return valid;
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
