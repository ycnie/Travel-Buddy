package com.testapp.travel.ui.trips;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.testapp.travel.R;
import com.testapp.travel.data.model.Trip;

import org.parceler.Parcels;

public class AirbnbActivity extends AppCompatActivity {

    Trip trip;
    private WebView webviewFlight ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airbnb);
        trip = new Trip();
        trip = (Trip) Parcels.unwrap(getIntent()
                .getParcelableExtra("Trip"));
        String destination = trip.getSearchDestination().getName();
//12-05-2018
        String startDate = trip.getBeginDate();
        String[] splitStartDate = startDate.split("-");
        startDate=splitStartDate[2]+"-"+splitStartDate[0]+"-"+splitStartDate[1];

        String endDate = trip.getEndDate();
        String[] splitEndDate = endDate.split("-");
        endDate=splitEndDate[2]+"-"+splitEndDate[0]+"-"+splitEndDate[1];


        String url = "https://www.airbnb.com/s/homes?checkin="+startDate+"&checkout="+endDate+"&adults=1&children=0&infants=0&guests=1&query="+destination;

        webviewFlight = (WebView) findViewById(R.id.webviewAirbnb);
        WebSettings webSettings = webviewFlight.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webviewFlight.loadUrl(url);

    }

}
