package com.evento.akay18.evento;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
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

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    private AHBottomNavigation bottomNavigation;
    private FirebaseAuth mAuth;
    private NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;
    private Activity activity;

    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    static Boolean filterState = false, doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/circular_std_book.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_main);

        activity = this;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        //Bottom Navigation Bar
        bottomNavigation = findViewById(R.id.bottomNavigation);
        AHBottomNavigationItem item_feed = new AHBottomNavigationItem(R.string.item_feed, R.drawable.ic_home_white_24dp, R.color.item_feed);
        AHBottomNavigationItem item_search = new AHBottomNavigationItem(R.string.item_search, R.drawable.ic_search_white_24dp, R.color.item_profile);
        AHBottomNavigationItem item_add = new AHBottomNavigationItem(R.string.item_addEvent, R.drawable.ic_add_circle_outline_white_24dp, R.color.item_profile);
        AHBottomNavigationItem item_profile = new AHBottomNavigationItem(R.string.item_profile, R.drawable.ic_person_white_24dp, R.color.item_feed);
        AHBottomNavigationItem item_set = new AHBottomNavigationItem(R.string.item_set, R.drawable.ic_settings_white_24dp, R.color.item_set);
        bottomNavigation.addItem(item_feed);
        bottomNavigation.addItem(item_search);
        bottomNavigation.addItem(item_add);
        bottomNavigation.addItem(item_profile);
        bottomNavigation.addItem(item_set);
        bottomNavigation.setCurrentItem(0);

        setBNBConditions();

        viewPager = findViewById(R.id.viewPager);
        viewPager.setPagingEnabled(false);

        Fragment feedFragment = new FeedFragment();
        Fragment profileFragment = new ProfileFragment();
        Fragment settingFragment = new SettingFragment();
        Fragment searchFragment = new SearchFragment();
        Fragment addEventFragment = new AddEventFragment();

        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());
        //TODO: Add fragments to pagerAdapter P.S : DONE
        pagerAdapter.addFragments(feedFragment);
        pagerAdapter.addFragments(searchFragment);
        pagerAdapter.addFragments(addEventFragment);
        pagerAdapter.addFragments(profileFragment);
        pagerAdapter.addFragments(settingFragment);
        viewPager.setAdapter(pagerAdapter);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!wasSelected) {
                    viewPager.setCurrentItem(position);
                }
                return true;
            }
        });

        setCustomTheme(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
    }


    private void setBNBConditions() {
        this.bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        // Disable the translation inside the CoordinatorLayout
        this.bottomNavigation.setBehaviorTranslationEnabled(true);
        // Enable the translation of the FloatingActionButton
        //this.bottomNavigation.manageFloatingActionButtonBehavior(MainFragment.getFab());
        // Change colors
        this.bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        this.bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        // Force to tint the drawable (useful for font with icon for example)
        this.bottomNavigation.setForceTint(true);
        // Use colored navigation with circle reveal effect
        this.bottomNavigation.setColored(true);
        // Set current item programmatically
        this.bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        // Force to tint the drawable (useful for font with icon for example)
        this.bottomNavigation.setForceTint(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void setCustomTheme(boolean isChecked) {
        if (isChecked) {
            setTheme(R.style.MyButton);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    //Handle Fragments at BNB
    public class BottomBarAdapter extends SmartFragmentStatePagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();

        public BottomBarAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragments(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter_event) {
            if (filterState == false) {
                Toast.makeText(this, "City Filter Turned ON", Toast.LENGTH_SHORT).show();
                editor.putBoolean("filterState", true).apply();
                filterState = preferences.getBoolean("filterState", true);
            } else {
                Toast.makeText(this, "City Filter Turned OFF", Toast.LENGTH_SHORT).show();
                editor.putBoolean("filterState", false).apply();
                filterState = preferences.getBoolean("filterState", false);
            }

            Log.i("-----FILTER STATE-----", filterState.toString());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }

        }, 2000);

    }
}