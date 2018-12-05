package com.testapp.travel.ui.trips;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.testapp.travel.BuildConfig;
import com.testapp.travel.R;
import com.testapp.travel.data.model.Place;
import com.testapp.travel.data.model.Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import timber.log.Timber;

public class flightPricesActivity extends AppCompatActivity {
    Trip trip;
    private WebView webviewFlight ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_prices);
        trip = new Trip();
        trip = (Trip) Parcels.unwrap(getIntent()
                .getParcelableExtra("Trip"));
        String destination = trip.getSearchDestination().getAirport();

        String startDate = trip.getBeginDate();
        String[] splitStartDate = startDate.split("-");
        startDate=splitStartDate[2].substring(2,4)+splitStartDate[0]+splitStartDate[1];

        String endDate = trip.getEndDate();
        String[] splitEndDate = endDate.split("-");
        endDate=splitEndDate[2].substring(2,4)+splitEndDate[0]+splitEndDate[1];


        String url = "https://www.skyscanner.net/transport/flights/bos/"+destination+"/"+startDate+"/"+endDate+"/?adults=1&children=0&adultsv2=1&childrenv2=&infants=0" +
                "&cabinclass=economy&rtn=1&preferdirects=false&outboundaltsenabled=true&inboundaltsenabled=true&ref=home#results";

        webviewFlight = (WebView) findViewById(R.id.webviewFlight);
        WebSettings webSettings = webviewFlight.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webviewFlight.loadUrl(url);

    }


}
