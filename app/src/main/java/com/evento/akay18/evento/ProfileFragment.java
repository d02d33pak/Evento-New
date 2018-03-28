package com.evento.akay18.evento;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private DatabaseReference mRef, queryRef;

    private TextView userName, userEmail;
    static private String name, email;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<EventDetails> mEventList;


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

        // Working with recycler view
        mRecyclerView = view.findViewById(R.id.userRecycler);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Needs an adapter to inflate view
        mAdapter = new MyAdapter(mEventList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        // Ends here

        // Firebase Instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();

        // User profile data
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
        // END

        // Running a query at firebase to fetch user uploaded events
        String currentUid = mUser.getUid();
        queryRef = mDatabase.getReference();
        Query query = queryRef.child("event_details").orderByChild("uid").equalTo(currentUid);

        // Just attach the data from query to our adapter
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mEventList = new ArrayList<EventDetails>();
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    EventDetails value = dataSnapshot1.getValue(EventDetails.class);
                    EventDetails eDetails = new EventDetails();
                    String eventName = value.getTitle();
                    String eventOrg = value.getOrganiser();
                    String eventDesc = value.getDescription();
                    String eventLoc = value.getLocation();
                    String eventDate = value.getDate();
                    String eventTime = value.getTime();
                    eDetails.setTitle(eventName);
                    eDetails.setOrganiser(eventOrg);
                    eDetails.setDescription(eventDesc);
                    eDetails.setLocation(eventLoc);
                    eDetails.setDate(eventDate);
                    eDetails.setTime(eventTime);
                    mEventList.add(eDetails);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
