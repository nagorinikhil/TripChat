package com.inclass.tripchat.Activity.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inclass.tripchat.Activity.Activity.HomeActivity;
import com.inclass.tripchat.Activity.POJO.User;
import com.inclass.tripchat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private ImageView imageViewLogin;
    private Button buttonGoogleLogin;
    private EditText editTextUsername, editTextPassword;
    private String username, pwd;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";
    DatabaseReference databaseUserReference,databaseFriendsReference;
    ValueEventListener userEventListener;

    DataSnapshot mySnapshot;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Log.d("ONCREATE VIEW", "ONCREATE VIEW");
        progressDialog = new ProgressDialog(getActivity());
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        buttonGoogleLogin = (Button)view.findViewById(R.id.button_GoogleLogin);
        imageViewLogin = (ImageView)view.findViewById(R.id.imageView_Login);
        editTextPassword = (EditText)view.findViewById(R.id.editText_PasswordLogin);
        editTextUsername = (EditText)view.findViewById(R.id.editText_UsernameLogin);

        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseFriendsReference = FirebaseDatabase.getInstance().getReference("Friends");

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                googleSignIn();
            }
        });


        imageViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    progressDialog.show();
                    username = editTextUsername.getText().toString();
                    pwd = editTextPassword.getText().toString();

                    mAuth.signInWithEmailAndPassword(username,pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                editTextUsername.setText("");
                                editTextPassword.setText("");
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getActivity(), HomeActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.d("SignIn", "In SignIn Function");
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d("Activity Result", "Success");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                Log.d("Activity Result", "Fail");
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Successful Sign In", Toast.LENGTH_SHORT).show();
                            Log.d("MYSnapshot","HELLO");

                            userEventListener = databaseUserReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    mySnapshot = dataSnapshot;
                                    Log.d("AddMYSnapshot",mySnapshot.toString());
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                    if(!mySnapshot.hasChild(firebaseUser.getUid())){
                                        Log.d("Snapshot",mySnapshot.toString());
                                        User user = new User();
                                        user.setfName(firebaseUser.getDisplayName().substring(0,firebaseUser.getDisplayName().indexOf(" ")));
                                        user.setlName(firebaseUser.getDisplayName().substring(firebaseUser.getDisplayName().indexOf(" ")));
                                        user.setEmail(firebaseUser.getEmail());
                                        user.setImageUrl(firebaseUser.getPhotoUrl().toString());
                                        user.setUserId(firebaseUser.getUid());
                                        user.setGender("");
                                        databaseUserReference.child(firebaseUser.getUid()).setValue(user);
                                        databaseFriendsReference.child(firebaseUser.getUid()).setValue("");

                                        databaseUserReference.removeEventListener(userEventListener);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            progressDialog.dismiss();
                            Intent i = new Intent(getActivity(), HomeActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }

                    }
                });
    }


    private boolean validate() {
        boolean valid = true;
        String email = editTextUsername.getText().toString();
        if(TextUtils.isEmpty(email)){
            valid = false;
            editTextUsername.setError("Required");
        }
        String pwd = editTextPassword.getText().toString();
        if(TextUtils.isEmpty(pwd)) {
            valid = false;
            editTextPassword.setError("Required");
        }

        return valid;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mAuth.addAuthStateListener(mAuthListener);
        Log.d("OnResume", "ONRESUME");
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }*/
        Log.d("ONPause","ONPAUSE");
    }

}