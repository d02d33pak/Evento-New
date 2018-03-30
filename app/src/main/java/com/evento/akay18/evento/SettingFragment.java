package com.evento.akay18.evento;


import android.*;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    int PLACE_PICKER_REQUEST = 1;
    Geocoder geocoder;
    static String city;
    Button btnChngCity;

    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);

        geocoder = new Geocoder(getContext());

        mAuth = FirebaseAuth.getInstance();
        //Configure Google Sign
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        //Configure Google Sign Client
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();


        Button btnSignOut = view.findViewById(R.id.signOutBtn);
        btnChngCity = view.findViewById(R.id.chngCity);
        Switch themeSwitch = view.findViewById(R.id.themeSwitch);

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getActivity()).setCustomTheme(isChecked);
            }
        });

        btnChngCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
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

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        if (!TextUtils.isEmpty(preferences.getString("MyCity", "")))
            btnChngCity.setText("Current City : " + preferences.getString("MyCity", ""));

        return view;
    }

    //Location Picker
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                try {
                    List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    city = addresses.get(0).getLocality();
                    editor.putString("MyCity", city);
                    editor.apply();
                    btnChngCity.setText("Current City : " + preferences.getString("MyCity", ""));
                } catch (IOException e) {
                    Log.i("City Error", e.getMessage());
                }
            }
        }
    }


    public void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
    }


}
