package com.testapp.travel.ui.trips;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.testapp.travel.R;
import com.testapp.travel.data.model.Trip;
import com.testapp.travel.utils.FirebaseUtil;

import org.parceler.Parcels;

public class DisplayWeatherActivity extends AppCompatActivity {

    // UI
    private TextView tvCityName;
    private TextView tvHighTemp;
    private TextView tvLowTemp;
    private TextView tvTripDays;
    private TextView tvWarmDays;
    private TextView tvModerateDays;
    private TextView tvColdDays;
    private TextView tvPackingDescription;
    private Button btnMoreInfo;

    // Info
    Trip trip;
    String beginDateStr;
    String endDateStr;
    String destinationStr;
    Double latitudeStr;
    Double longitudeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Weather");

        // UI
        tvCityName = (TextView) findViewById(R.id.tvCityName);
        tvHighTemp = (TextView) findViewById(R.id.tvHighTemp);
        tvLowTemp = (TextView) findViewById(R.id.tvLowTemp);
        tvTripDays = (TextView) findViewById(R.id.tvTripDays);
        tvWarmDays = (TextView) findViewById(R.id.tvWarmDays);
        tvModerateDays = (TextView) findViewById(R.id.tvModerateDays);
        tvColdDays = (TextView) findViewById(R.id.tvColdDays);
        tvPackingDescription = (TextView) findViewById(R.id.tvPackingDescription);
        btnMoreInfo = (Button) findViewById(R.id.btnMoreInfo);

        trip = new Trip();
        trip = (Trip) Parcels.unwrap(getIntent()
                .getParcelableExtra("Trip"));

        // Get information from trip
        beginDateStr = trip.getBeginDate();
        endDateStr = trip.getEndDate();
        destinationStr = trip.getSearchDestination().getName();
        latitudeStr = trip.getSearchDestination().getLatitude();
        longitudeStr = trip.getSearchDestination().getLongitude();

        // Setting UI elements
        tvCityName.setText(destinationStr);

        // More information button
        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // [0] = month; [1] = day; [2] = year
                String dateSplit[] = beginDateStr.split("-");
                String dsDate = dateSplit[2] + "-" + dateSplit[0] + "-" + dateSplit[1];
                String url = "https://darksky.net/details/" + Double.toString(latitudeStr) + "," + Double.toString(longitudeStr) + "/" + dsDate + "/us12/en";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getBaseContext(), Uri.parse(url));
            }
        });

    }
}
