package com.evento.akay18.evento;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView userName, userEmail;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private DatabaseReference mRef, queryRef;
    static private String name, email;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();

        userName.setText(mUser.getDisplayName());
        if (TextUtils.isEmpty(userName.getText())) {
            mRef = mDatabase.getReference().child("userdata").child(mUser.getUid());

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    name = dataSnapshot.child("name").getValue().toString();
                    userName.setText(name);
                    //email = dataSnapshot.child("email").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("ERROR", databaseError.getMessage());
                }
            });
        }
        userEmail.setText(mUser.getEmail());

        queryRef = mDatabase.getReference().child("event_details");

        return view;
    }

}
