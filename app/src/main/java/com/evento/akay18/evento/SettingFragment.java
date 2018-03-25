package com.evento.akay18.evento;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_setting, container, false);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getContext(), SignInActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        Button btnSignOut = view.findViewById(R.id.signOutBtn);
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
    }

}
