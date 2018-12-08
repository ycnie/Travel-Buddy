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
    private Trip trip;
    private String beginDateStr;
    private long beginDateUnix;
    private String endDateStr;
    private long endDateUnix;
    private String destinationStr;
    private Double latitudeStr;
    private Double longitudeStr;
    private int numOfDays;
    private double tripLowTemp;
    private double tripHighTemp;
    private boolean rain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);

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

        // Get information from trip object
        beginDateStr = trip.getBeginDate();
        endDateStr = trip.getEndDate();
        destinationStr = trip.getSearchDestination().getName();
        latitudeStr = trip.getSearchDestination().getLatitude();
        longitudeStr = trip.getSearchDestination().getLongitude();

        // Get information about the trip
        datesToUnixTimestamp();
        getNumDays();

        // Setting UI elements
        tvCityName.setText(destinationStr);
        tvTripDays.setText("Your trip is " + Integer.toString(numOfDays) + " days");

        // More information button
        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // [0] = month; [1] = day; [2] = year
                String dateSplit[] = dateSplitter(beginDateStr);
                String dsDate = dateSplit[2] + "-" + dateSplit[0] + "-" + dateSplit[1];
                String url = "https://darksky.net/details/" + Double.toString(latitudeStr) + "," + Double.toString(longitudeStr) + "/" + dsDate + "/us12/en";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getBaseContext(), Uri.parse(url));
            }
        });

    }
    /*
     * dateSplitter(String date)
     * @params:     date: the date in string type in the format MM-DD-YYYY
     * @returns:    Array of strings in the format: [0]=MM; [1]=DD; [2]=YYYY
     */
    private String[] dateSplitter(String date){
        return date.split("-");
    }

    /*
     * datesToUnixTimestamp()
     * Converts beginDateStr and endDateStr Stings to unix timestamps of type long
     */
    private void datesToUnixTimestamp() {
        String splitStartDate[] = dateSplitter(beginDateStr);
        String splitEndDate[] = dateSplitter(endDateStr);

        // Converting beginDateStr to beginDateUinx
        int newStartMonth = Integer.parseInt(splitStartDate[0]) - 1970;
        int newStartDay = Integer.parseInt(splitStartDate[1]) - 1;
        int newStartYear = Integer.parseInt(splitStartDate[2]) - 1;
        beginDateUnix = (newStartYear * 31556926) + (newStartMonth * 2629743) + (newStartDay * 86400);

        // Converting endDateStr to endDateUnix
        int newEndMonth = Integer.parseInt(splitEndDate[0]) - 1970;
        int newEndDay = Integer.parseInt(splitEndDate[1]) - 1;
        int newEndYear = Integer.parseInt(splitEndDate[2]) - 1;
        endDateUnix = (newEndYear * 31556926) + (newEndMonth * 2629743) + (newEndDay * 86400);
    }

    /*
     * getNumDays()
     * Uses beginDate and endDate to calculate the total number of days in the trip and saves the
     * value to the global int numOfDays
     */
    private void getNumDays() {
        numOfDays = 0;
        for (long currentUnix = beginDateUnix; currentUnix <= endDateUnix; currentUnix += 86400) {
            numOfDays++;
        }
    }
}
