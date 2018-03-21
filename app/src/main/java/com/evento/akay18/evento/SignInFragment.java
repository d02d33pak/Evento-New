package com.evento.akay18.evento;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "HereIsTheProblem";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static GoogleSignInClient mGoogleSignInClient;

    //Local
    private Button mSignInBtn;
    private SignInButton gSignInBtn;
    private EditText mEmailField, mPwdField;
    private String email, password;
    private View focusView = null;
    private boolean notEmpty = true;
    private ConnectivityManager mConnMgr;
    private NetworkChangeReceiver mReceiver;
    private TextInputLayout emailIL, pwdIL;
    private ProgressDialog progress;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);



        //Instantiate views
        mEmailField = view.findViewById(R.id.emailField);
        mPwdField = view.findViewById(R.id.PwdField);
        mSignInBtn = view.findViewById(R.id.signInBtn);
        emailIL = view.findViewById(R.id.emailInputLayout);
        pwdIL = view.findViewById(R.id.pwdInputLayout);
        progress = new ProgressDialog(getContext());

        //Google Sign In Button
        gSignInBtn = view.findViewById(R.id.gSingIn);
        gSignInBtn.setSize(SignInButton.SIZE_WIDE);

        //Get Fire Auth Instance
        mAuth = FirebaseAuth.getInstance();

        //Configure Google Sign
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        //Configure Google Sign Client
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        //On click sign in button
        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This ConnectivityManager reference is used to check connectivity throughout the app.
                mConnMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

//                //Instantiate Broadcast receiver.
                mReceiver = new NetworkChangeReceiver();
//
//                //Intent Filter will receive System Broadcast.
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//
                  //Register Broadcast receiver with the filter.
//                //So that whenever a change is made in network onReceive method is called.
                getActivity().registerReceiver(mReceiver, filter);


                if (mConnMgr != null) {
                    NetworkInfo networkInfo = mConnMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        email = mEmailField.getText().toString();
                        checkEmail();
                        password = mPwdField.getText().toString();
                        checkPassword();
                        if (notEmpty) {
                            signIn(email, password);
                        }
                    } else {
                        Snackbar.make(getView(), "Please Connect To The Internet", Snackbar.LENGTH_LONG).show();
                    }

                }
            }
        });

        //Google sign_in onClickListener
        gSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.gSingIn:
                        signInUsingGoogle();
                        break;
                }

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // >> start up the account choose intent
    private void signInUsingGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // << end of intent

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "SignIn failed " , e);
                Toast.makeText(getActivity(), "Sign_in Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // [END onactivityresult]

    // >> exchange google account token id with firebase credentials to authenticate user with firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "***firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
                            //Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    // << end of exchange

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener((Executor) this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }


    //Email Verification
    protected void checkIfEmailVerified() {
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            if (mUser.isEmailVerified()) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            } else {
                Snackbar.make(getView(), "Please Verify Your Email First!", Snackbar.LENGTH_SHORT).show();
                mAuth.signOut();
            }
        }
    }

    //Password validation
    protected void checkPassword() {
        if (TextUtils.isEmpty(password)) {
            pwdIL.setError("Password Required");
            notEmpty = false;
        } else {
            notEmpty = true;
            pwdIL.setError(null);
        }
    }

    //Email validation
    protected void checkEmail() {
        if (TextUtils.isEmpty(email)) {
            emailIL.setError("Email Required");
            //mEmailField.setError("Email Required");
            focusView = mEmailField;
            notEmpty = false;
        } else if (!isEmailVerified(email)) {
            emailIL.setError("Invalid Email");
            //mEmailField.setError("Email not valid");
            focusView = mEmailField;
            notEmpty = false;
        } else {
            notEmpty = true;
            emailIL.setError(null);
        }
    }

    //Check email format method
    protected boolean isEmailVerified(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Firebase sign in method
    protected void signIn(String email, String password) {
        progress.setCancelable(false);
        progress.setTitle("Signing In");
        progress.setMessage("Please Wait...");
        progress.show();
        if (mAuth == null)
            Log.d("CHECK", "mAuth");

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progress.dismiss();
                    Log.d("Inside SignIn:", "Login Successful");
                    checkIfEmailVerified();
                } else {
                    progress.dismiss();
                    Log.d("Inside SignIn:", "Login Failed");
                    Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("CHECK", e.getMessage().toString());
            }
        });
    }

    //Check network status at RUNTIME using BroadcastReceiver.
    //onReceive method will be called whenever network changes.
    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get Active network status.
            NetworkInfo networkInfo = mConnMgr.getActiveNetworkInfo();

            if (networkInfo == null && !networkInfo.isConnected()) {

                Snackbar.make(getView(), "Please connect to the Internet", Snackbar.LENGTH_LONG).show();

                /*//Check if active network interface is wifi.
                boolean isWifiAvailable = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

                //Check if active network interface is gsm.
                boolean isGsmAvailable = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();

                if ((!isWifiAvailable) && (!isGsmAvailable)) {
                    Snackbar.make(getView(), "Please connect to the Internet", Snackbar.LENGTH_LONG).show();
                }*/
            }

        }
    }

}
