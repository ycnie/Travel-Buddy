package com.testapp.travel.ui.trips;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.testapp.travel.R;
import com.testapp.travel.data.model.Trip;
import com.testapp.travel.ui.helpers.CardScaleHelper;
import com.testapp.travel.utils.BlurBitmapUtils;
import com.testapp.travel.utils.ViewSwitchUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DisplayWeatherActivity extends AppCompatActivity {

    private TextView weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);

        weatherId=(TextView) findViewById(R.id.weatherID);
        weatherId.setText("-20 F");

    }






}
