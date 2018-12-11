package com.testapp.travel.ui.trips;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.json.JSONException;
import org.json.JSONObject;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayWeatherActivity extends AppCompatActivity {

    // Finals
    private final double ColdWeatherThresh = 50.0;
    private final double WarmWeatherThresh = 70.0;
    private final double RainThresh = 0.35;

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
    String TAG = "WTH";
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
    private ArrayList<String> weather;
    private ArrayList<ArrayList<Double>> temps;
    private ArrayList<Integer> dayType;
    private ArrayList<Integer> precipitation;
    private int coldDays;
    private int moderateDays;
    private int warmDays;
    private boolean rain;
    private boolean snow;
    private boolean otherPrecip;
    private boolean precipData;
    private String descriptioStr;

    // API
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);

        // Allow network on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        // Initializing other global variables
        weather = new ArrayList<String>();
        temps = new ArrayList<ArrayList<Double>>();
        dayType = new ArrayList<Integer>();     // MAP: 0 = cold, 1 = moderate, 2 = warm
        precipitation = new ArrayList<Integer>();     // MAP: 0 = none, 1 = rain, 2 = snow, 3 = other
        coldDays = 0;
        moderateDays = 0;
        warmDays = 0;
        rain = false;
        snow = false;
        otherPrecip = false;
        precipData = false;

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
        getAllWeather();
        getDayType();
        descriptionGenerator();

        // Setting UI elements
        tvCityName.setText(destinationStr);
        tvTripDays.setText("Your trip is " + Integer.toString(numOfDays) + " days");
        tvHighTemp.setText(Double.toString(round(tripHighTemp, 1)) + "\u00b0 F");
        tvLowTemp.setText(Double.toString(round(tripLowTemp, 1)) + "\u00b0 F");
        tvColdDays.setText("x" + Integer.toString(coldDays));
        tvModerateDays.setText("x" + Integer.toString(moderateDays));
        tvWarmDays.setText("x" + Integer.toString(warmDays));
        tvPackingDescription.setText(descriptioStr);

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
                customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                customTabsIntent.launchUrl(getBaseContext(), Uri.parse(url));
            }
        });
    }

    private void descriptionGenerator() {
        String outputString = "You should pack for ";
        outputString = outputString + Integer.toString(warmDays) + " warm days, ";
        outputString = outputString + Integer.toString(moderateDays) + " moderate days, ";
        if (precipData) {
            outputString = outputString + Integer.toString(coldDays) + " cold days, ";
            outputString = outputString + "and it looks like ";
            if (!otherPrecip) {
                if (rain && snow) {
                    outputString = outputString + "it's going to rain and snow";
                } else if (rain) {
                    outputString = outputString + "it's going to rain";
                } else if (snow) {
                    outputString = outputString + "it's going to snow";
                } else outputString = outputString + "it's not going to precipitate";
            } else {
                outputString = outputString + "it's going to precipitate";
            }
        } else {
            outputString = outputString + "and " + Integer.toString(coldDays) + " cold days, ";
        }
        descriptioStr = outputString;
    }

    private void getDayType() {
        for (int i = 0; i < numOfDays; i++) {
            Double avgDayTemp = (temps.get(i).get(0) + temps.get(i).get(1)) / 2;
            if (avgDayTemp < ColdWeatherThresh) {
                dayType.add(0);
                coldDays++;
            }
            else if (avgDayTemp < WarmWeatherThresh) {
                dayType.add(1);
                moderateDays++;
            }
            else {
                dayType.add(2);
                warmDays++;
            }
        }
    }

    private void getAllWeather() {
        for (int i = 0; i < numOfDays; i++) {
            String url = "https://api.darksky.net/forecast/" + getString(R.string.darksky_spi_key) + "/" + Double.toString(round(latitudeStr, 4)) + "," + Double.toString(round(longitudeStr, 4)) + "," + String.valueOf(beginDateUnix + (i * 86400) + "?exclude=currently,minutely,hourly,alerts,flags");
            try {
                int num = numOfDays;
                weather.add(getOneWeather(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        parseJSONString();
    }

    /*
     * Gets the weather for one day of the trip
     */
    String getOneWeather(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try  {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error getting weather information", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /*
     * parseJSONString()
     * Parses the JSON strings in the global variable weather for the high and low temperatures of
     * the day, and saves them in the global variable temps in the format [dayNum][0 = low temp; 1
     * = high temp]
     */
    private void parseJSONString() {
        for (int i = 0; i < numOfDays; i++) {
            Double tempLow = Double.parseDouble((weather.get(i).split("\"apparentTemperatureLow\":"))[1].split(",")[0]);
            Double tempHigh = Double.parseDouble((weather.get(i).split("\"apparentTemperatureLow\":"))[1].split(",")[0]);
            if (weather.get(i).contains("\"precipProbability\":") && weather.get(i).contains("\"precipType\":")) {
                precipData = true;
                if ((Double.parseDouble(weather.get(i).split("\"precipProbability\":")[1].split(",")[0])) > RainThresh) {
                    if (weather.get(i).split("\"precipType\":")[1].split(",")[0].toLowerCase().contains("rain")) {
                        precipitation.add(1);
                        rain = true;
                    } else if (weather.get(i).split("\"precipType\":")[1].split(",")[0].toLowerCase().contains("snow")) {
                        precipitation.add(2);
                        snow = true;
                    } else {
                        precipitation.add(2);
                        otherPrecip = true;
                    }
                } else {
                    precipitation.add(0);
                }
            }
            ArrayList<Double> tempAL = new ArrayList<Double>();
            tempAL.add(tempLow);
            tempAL.add(tempHigh);
            temps.add(tempAL);
            if (i == 0) {
                tripLowTemp = temps.get(i).get(0);
                tripHighTemp = temps.get(i).get(1);
            } else {
                if (temps.get(i).get(0) < tripLowTemp) {
                    tripLowTemp = temps.get(i).get(0);
                }
                if (temps.get(i).get(1) > tripHighTemp) {
                    tripHighTemp = temps.get(i).get(1);
                }
            }
        }
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
     * Converts beginDateStr and endDateStr Stings to unix timestamps of type long, in the timezone
     * of the destination
     */
    private void datesToUnixTimestamp() {
        String splitStartDate[] = dateSplitter(beginDateStr);
        String splitEndDate[] = dateSplitter(endDateStr);

        // Converting beginDateStr to beginDateUinx
        int newStartMonth = Integer.parseInt(splitStartDate[0]) - 1;
        int newStartDay = Integer.parseInt(splitStartDate[1]) - 1;
        int newStartYear = Integer.parseInt(splitStartDate[2]) - 1970;
        beginDateUnix = (newStartYear * 31556926) + (newStartMonth * 2629743) + (newStartDay * 86400);

        // Converting endDateStr to endDateUnix
        int newEndMonth = Integer.parseInt(splitEndDate[0]) - 1;
        int newEndDay = Integer.parseInt(splitEndDate[1]) - 1;
        int newEndYear = Integer.parseInt(splitEndDate[2]) - 1970;
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

    /*
     * round(double value, int precision)
     * Rounds a number to a certain number of decimal places
     * @params:     value: the value to be rounded
     *              precision: The number of decimal places to round to
     * @returns:    The rounded double
     */
    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
