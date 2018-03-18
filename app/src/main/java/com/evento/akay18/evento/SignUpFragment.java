package com.evento.akay18.evento;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private EditText mEmailField, mPwdField, mCnfPwdField, mNameField, mMobField;
    private Button mSignUpBtn;
    private String email, password, cnfPwd, fullName, mob, userID;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private boolean validName = true, validEmail = true, validPwd = true, validMob = true, validCnf = true ;
    private View focusView = null;
    private TextInputLayout emailIL, pwdIL, nameIL, cnfPwdIL, mobIL;
    private ProgressDialog progress;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //Get Firebase Auth Instance
        mAuth = FirebaseAuth.getInstance();
        //Get Firebase Database Instance
        mDatabase = FirebaseDatabase.getInstance();
        //Get Database Reference
        mRef = mDatabase.getReference("userdata");

        //Instantiate views
        mNameField = view.findViewById(R.id.nameField);
        mEmailField = view.findViewById(R.id.emailField);
        mPwdField = view.findViewById(R.id.pwdField);
        mSignUpBtn = view.findViewById(R.id.signUpBtn);
        mCnfPwdField = view.findViewById(R.id.cnfPwdField);
        mMobField = view.findViewById(R.id.mobField);
        nameIL = view.findViewById(R.id.nameInputField);
        emailIL = view.findViewById(R.id.emailInputLayout);
        pwdIL = view.findViewById(R.id.pwdInputLayout);
        cnfPwdIL = view.findViewById(R.id.cnfPwdInputField);
        mobIL = view.findViewById(R.id.mobInputField);

        //Progress Dialog
        progress = new ProgressDialog(getContext());

        //On click sign up
        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName = mNameField.getText().toString();
                email = mEmailField.getText().toString();
                password = mPwdField.getText().toString();
                cnfPwd = mCnfPwdField.getText().toString();
                mob = mMobField.getText().toString();
                checkName();
                checkEmail();
                checkPassword();
                checkCnfPwd();
                checkMob();
                if(validName && validEmail && validPwd && validCnf && validMob){
                    createAccount(email, password);
                    autoSignIn(email, password);
                    signOut();
                }
            }
        });
        return view;
    }

    //Name Validation
    private void checkName() {
        if (TextUtils.isEmpty(fullName)) {
            focusView = nameIL;
            nameIL.setError("Name Required");
            validName = false;
        } else {
            validName = true;
            nameIL.setError(null);
        }
    }

    //Email Validation
    private void checkEmail() {
        if (TextUtils.isEmpty(email)) {
            emailIL.setError("Email Required");
            focusView = mEmailField;
            validEmail = false;
        } else if (!isEmailVerified(email)) {
            emailIL.setError("Email not valid");
            focusView = mEmailField;
            validEmail = false;
        } else {
            validEmail = true;
            emailIL.setError(null);
        }
    }

    //Password validation
    protected void checkPassword(){
        if (TextUtils.isEmpty(password)) {
            focusView = pwdIL;
            pwdIL.setError("Password Required");
            validPwd = false;
        }else if (!isPasswordVerified(password)){
            focusView = pwdIL;
            pwdIL.setError("Password too short");
            validPwd = false;
        } else {
            validPwd = true;
            pwdIL.setError(null);
        }
    }

    //Check Confirm Password
    private void checkCnfPwd(){
        if(password.equals(cnfPwd)){
            validCnf = true;
            cnfPwdIL.setError(null);
        } else {
            focusView = cnfPwdIL;
            cnfPwdIL.setError("Password does not match");
            validCnf = false;
        }
    }

    //Mobile Validation
    private void checkMob(){
        if(TextUtils.isEmpty(mob)){
            focusView = mobIL;
            mobIL.setError("Mobile number required");
            validMob = false;
        } else if (mob.length() < 10){
            focusView = mobIL;
            mobIL.setError("Invalid mobile number");
            validMob = false;
        } else {
            validMob = true;
            mobIL.setError(null);
        }
    }


    private boolean isPasswordVerified(String password) {
        if (password.length() < 8)
            return false;
        else
            return true;
    }

    private boolean isEmailVerified(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Create account method
    private void createAccount(String email, String password) {
        progress.setTitle("Signing Up");
        progress.setMessage("Please Wait...");
        progress.setCancelable(false);
        progress.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("Inside Create Account:" , "Sign up successful");
                    progress.dismiss();
                } else {
                    Log.i("Inside Create Account:" , "Sign up failed");
                    progress.dismiss();
                }
            }
        });
    }

    //Auto sign in to save data at firebase database
    private void autoSignIn(final String email, String password) {
        progress.setTitle("Sending verification email");
        progress.setMessage("Please Wait...");
        progress.setCancelable(false);
        progress.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progress.dismiss();
                    mUser = mAuth.getCurrentUser();
                    mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Snackbar.make(getView(), "Verification link sent. Please confirm!", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                    saveUserData(mUser);
                } else {
                    progress.dismiss();
                    Snackbar.make(getView(), "Something went wrong. Please try again!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    //TODO: A method to save user data at firebase database
    private void saveUserData(FirebaseUser currentUser){
        userID = currentUser.getUid();
        User user = new User(fullName, email, mob);
        mRef.child(userID).setValue(user);
    }

    //Sign out method
    private void signOut(){
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
    }
}
