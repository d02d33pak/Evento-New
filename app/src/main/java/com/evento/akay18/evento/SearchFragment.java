package com.evento.akay18.evento;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<EventDetails> mEventList;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("event_details");



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        mRecyclerView = view.findViewById(R.id.mRecycler);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(mEventList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
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
                    mRecyclerView.setAdapter(mAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

        return view;
    }

}
