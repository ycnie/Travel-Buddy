package com.testapp.travel.ui.trips;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Weather");

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
        /*
        getDataFromDatabase(trip);

        tvCity.setText(cityName);

        Toast.makeText(this, startDateSt, Toast.LENGTH_SHORT).show();
        */
    }
}
/*
    public void getDataFromDatabase(Trip trip) {
        // Getting database references
        DatabaseReference mBeginDateReference= FirebaseUtil.getTripsRef().child(trip.getTripId()).child("beginDate");
        DatabaseReference mEndDateReference= FirebaseUtil.getTripsRef().child(trip.getTripId()).child("endDate");
        DatabaseReference mCityNameReference= FirebaseUtil.getTripsRef().child(trip.getTripId()).child("searchDestination").child("name");

        // Attach a listener to read the data at our posts references
        // Start date
        mBeginDateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try{
                    if (snapshot.getValue() != null) {
                        try {
                            startDateSt = (String) snapshot.getValue();
                            Log.e("FB", "" + snapshot.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("FB", "it's null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("onCancelled", " cancelled");
            }
        });

        // End Date
        mEndDateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try{
                    if (snapshot.getValue() != null) {
                        try {
                            endDateSt = (String) snapshot.getValue();
                            Log.e("FB", "" + snapshot.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("FB", "it's null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("onCancelled", " cancelled");
            }
        });

        // City Name
        mCityNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try{
                    if (snapshot.getValue() != null) {
                        try {
                            cityName = (String) snapshot.getValue();
                            Log.e("FB", "" + snapshot.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("FB", "it's null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("onCancelled", " cancelled");
            }
        });
    }
}
*/
