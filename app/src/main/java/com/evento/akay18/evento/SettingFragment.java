package com.evento.akay18.evento;


import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mAuth = FirebaseAuth.getInstance();
        //Configure Google Sign
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        //Configure Google Sign Client
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        Button btnSignOut = view.findViewById(R.id.signOutBtn);
        Switch themeSwitch = view.findViewById(R.id.themeSwitch);

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getActivity()).setCustomTheme(isChecked);
            }
        });


        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }


    public void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
    }


}
