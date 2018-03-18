package com.evento.akay18.evento;


import android.app.ActivityOptions;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.SnapshotMetadata;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    public FeedFragment() {
        // Required empty public constructor
    }


    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    ArrayList <String> mFetchedList;
    ArrayAdapter<String> mAdapter;
    EventDetails mEvent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("event_details");
        mFetchedList = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,
                mFetchedList);
        mEvent = new EventDetails();


        final ListView eventList = view.findViewById(R.id.eventList);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mDS : dataSnapshot.getChildren())
                {
                    mEvent = mDS.getValue(EventDetails.class);
                    mFetchedList.add(mEvent.getTitle());
                }
                eventList.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("------ERROR", databaseError.getMessage());
            }
        });





        return view;
    }

}
