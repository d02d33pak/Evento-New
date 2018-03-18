package com.evento.akay18.evento;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;

public class AddEventActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mClient;
    private EditText titleView, orgView, descView, phNumView;
    private String date, time, location, uid;
    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        titleView = findViewById(R.id.titleView);
        orgView = findViewById(R.id.orgView);
        descView = findViewById(R.id.descView);
        phNumView = findViewById(R.id.numView);
    }

    //API Connection
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("onConnected", "Success");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("onConnected", "Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnected", "Failure");
    }

    //Location Listener
    public void addPlaceListener(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesNotAvailableException e){
            Log.e("TEST", "Failed");
        } catch (GooglePlayServicesRepairableException e){
            Log.e("TEST", "Failed");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                location = String.format("Place: %s", place.getName());
            }
        }
    }

    //DATE PICKER
    public void addDateListener(View view){
        new DatePickerDialog(AddEventActivity.this, dateListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
        String format = "DD/MM/YYYY";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        date = sdf.format(myCalendar.getTime());
    }

    //TIME PICKER
    public void addTimeListener(View view) {
        new TimePickerDialog(AddEventActivity.this, timeListener, myCalendar.get(Calendar.HOUR),myCalendar.get(Calendar.MINUTE), true).show();
    }

    //Time Picker Listener
    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            myCalendar.set(Calendar.HOUR,selectedHour);
            myCalendar.set(Calendar.MINUTE, selectedMinute);
            saveTime();
        }
    };

    private void saveTime() {
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        time = sdf.format(myCalendar.getTime());
    }

    //Firebase upload event data function
    public void uploadEventData(View view) {
        mUser = mAuth.getCurrentUser();
        mRef = mDatabase.getReference("event_details/"+getRandomEID(10));
        EventDetails ed = new EventDetails(mUser.getUid().toString(), titleView.getText().toString(), orgView.getText().toString(), descView.getText().toString(), date, time, location, phNumView.getText().toString());
        mRef.setValue(ed);
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
}
