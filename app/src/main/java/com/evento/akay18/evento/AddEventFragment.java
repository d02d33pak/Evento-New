package com.evento.akay18.evento;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private DatabaseReference mRef;

    private String date, time, location;
    private boolean isTitleEmpty, isOrgEmpty, isDescEmpty, isNumEmpty;

    private TextInputLayout titleIL, orgIL, descIL, numIL;
    private EditText titleView, orgView, descView, phNumView;
    private ImageButton addDateBtn, addTimeBtn, addLocationBtn;
    private Button uploadBtn;

    int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mGoogleClient;
    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        //Google Location API
        mGoogleClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(),this)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        titleView = view.findViewById(R.id.titleView);
        orgView = view.findViewById(R.id.orgView);
        descView = view.findViewById(R.id.descView);
        phNumView = view.findViewById(R.id.numView);
        titleIL = view.findViewById(R.id.titleIL);
        orgIL = view.findViewById(R.id.orgIL);
        descIL = view.findViewById(R.id.descIL);
        numIL = view.findViewById(R.id.numIL);
        addDateBtn = view.findViewById(R.id.addDateBtn);
        addTimeBtn = view.findViewById(R.id.addTimeBtn);
        addLocationBtn = view.findViewById(R.id.addLocBtn);
        uploadBtn = view.findViewById(R.id.uploadBtn);

        addDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dateListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), timeListener, myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Location Listener
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e("TEST", "Failed");
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e("TEST", "Failed");
                }

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser = mAuth.getCurrentUser();
                mRef = mDatabase.getReference("event_details/"+getRandomEID(10));
                checkIfFieldsEmpty();
                if(!isTitleEmpty || !isOrgEmpty || !isDescEmpty || !isNumEmpty){
                    EventDetails ed = new EventDetails(mUser.getUid(), titleView.getText().toString(), orgView.getText().toString(), descView.getText().toString(), date, time, location, phNumView.getText().toString());
                    mRef.setValue(ed);
                }
            }
        });

        return view;
    }

    //Date Picker Listener
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            saveDate();
        }
    };

    private void saveDate() {
        String format = "dd/MM/YYYY";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        date = sdf.format(myCalendar.getTime());
    }

    //Time Picker Listener
    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            myCalendar.set(Calendar.HOUR, selectedHour);
            myCalendar.set(Calendar.MINUTE, selectedMinute);
            saveTime();
        }
    };

    private void saveTime() {
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        time = sdf.format(myCalendar.getTime());
    }

    //Location Picker
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(),data);
                location = String.format("Place: %s", place.getName());
            }
        }
    }

    //Creating random event_id
    public static String getRandomEID(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- >= 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleClient.stopAutoManage(getActivity());
        mGoogleClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Form Validation
    private void checkIfFieldsEmpty(){
        if(titleView.getText().toString().isEmpty()){
            titleIL.setError("Title required!");
            isTitleEmpty = true;
        } else {
            titleIL.setError(null);
            isTitleEmpty = false;
        }

        if(orgView.getText().toString().isEmpty()){
            orgIL.setError("Organiser required!");
            isOrgEmpty = true;
        } else{
            orgIL.setError(null);
            isOrgEmpty = false;
        }

        if(descView.getText().toString().isEmpty()){
            descIL.setError("Description required!");
            isDescEmpty = true;
        } else{
            descIL.setError(null);
            isDescEmpty = false;
        }

        if(phNumView.getText().toString().isEmpty()){
            numIL.setError("Phone number required!");
            isNumEmpty = true;
        } else{
            numIL.setError(null);
            isNumEmpty = false;
        }

    }

}
