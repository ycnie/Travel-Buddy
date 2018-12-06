
package com.testapp.travel.ui.trips;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testapp.travel.R;
import com.testapp.travel.data.model.Place;
import com.testapp.travel.data.model.Trip;
import com.testapp.travel.ui.MainActivity;
import com.testapp.travel.utils.FirebaseUtil;
import com.testapp.travel.utils.MapUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditTripActivity extends AppCompatActivity
        implements OnMapReadyCallback,View.OnClickListener{

    private GoogleMap mMap;
    private Trip trip;
    int HOTEL_PICKER_REQUEST = 1;

   /* @BindView(R.id.tvTripName)
    TextView tvTripName;*/
    @BindView(R.id.lyLocation)
    LinearLayout lyLocation;
    @BindView(R.id.lyAirbnb)
    LinearLayout lyAirbnb;
    @BindView(R.id.lyCompanion)
    LinearLayout lyCompanion;
    @BindView(R.id.lyFlight)
    LinearLayout lyFlight;
    @BindView(R.id.lyPhotos)
    LinearLayout lyPhotos;
    @BindView(R.id.lyWeather)
    LinearLayout lyWeather;
    @BindView(R.id.tvDone)
    TextView tvDone;
    @BindView(R.id.tvPageTitle)
    TextView tvPageTitle;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvDeleteTrip)
    TextView tvDeleteTrip;
    ArrayList<Place> destinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trip=new Trip();
        trip=(Trip) Parcels.unwrap(getIntent()
                .getParcelableExtra("Trip"));
        tvPageTitle.setText("Edit "+ trip.getTripName());
        tvDate.setText(trip.getBeginDate());
        //tvTripName.setText("Edit"trip.getTripName());
        lyCompanion.setOnClickListener(this);
        lyFlight.setOnClickListener(this);
        lyAirbnb.setOnClickListener(this);
        lyLocation.setOnClickListener(this);
        lyPhotos.setOnClickListener(this);
        lyWeather.setOnClickListener(this);
        tvDone.setOnClickListener(this);
        tvDeleteTrip.setOnClickListener(this);

        DatabaseReference mListItemRef= FirebaseUtil.getTripsRef().child(trip.getTripId()).child("destinations");//.getRef().child("KXQ5mRVEbFxYDPoLtiA");
        destinations=new ArrayList<Place>();
        mListItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String destinationId = postSnapshot.getKey().toString();
                    FirebaseUtil.getPlacesRef().child(destinationId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Place place=snapshot.getValue(Place.class);
                            destinations.add(place);
                            LatLng destination = new LatLng(place.getLatitude(), place.getLongitude());
                            addMarker(place.getName(),destination);
                            MapUtil.focusPoints(destinations,mMap);
                        }
                        @Override public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng destination = new LatLng(trip.getSearchDestination().getLatitude(), trip.getSearchDestination().getLongitude());
        addMarker(trip.getSearchDestination().getName(),destination);
        destinations.add(trip.getSearchDestination());
        MapUtil.focusPoints(destinations,mMap);
    }

    private void addMarker(String name,LatLng destination){
        mMap.addMarker(new MarkerOptions().position(destination).title(name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id) {
            case R.id.lyLocation:
                Intent intentLocation=new Intent(this, EditDestinationActivity.class)
                        .putExtra("Trip", Parcels.wrap(trip));
                intentLocation.putExtra("Destinations", destinations);
                startActivity(intentLocation);
                break;

            case R.id.lyCompanion:
                Intent intentCompanion=new Intent(this, AddCompanionActivity.class)
                        .putExtra("Trip", Parcels.wrap(trip));;
                startActivity(intentCompanion);
                break;

            case R.id.lyPhotos:
                Intent intentPhotos=new Intent(this,DisplayPhotosActivity.class)
                        .putExtra("Trip", Parcels.wrap(trip));
                startActivity(intentPhotos);
                break;
            case R.id.lyFlight:
                Intent intentFlight=new Intent(this,flightPricesActivity.class)
                        .putExtra("Trip", Parcels.wrap(trip));
                startActivity(intentFlight);
                break;

            case R.id.lyWeather:
                Intent intentWeather=new Intent(this,DisplayWeatherActivity.class)
                        .putExtra("Trip", Parcels.wrap(trip));
                startActivity(intentWeather);
                break;

            case R.id.lyAirbnb:

                Intent airbnbIntent=new Intent(this,AirbnbActivity.class)
                        .putExtra("Trip", Parcels.wrap(trip));
                startActivity(airbnbIntent);

                break;
            case R.id.tvDone:
                Intent intentDone=new Intent(this,MainActivity.class);
                startActivity(intentDone);
                finish();
                break;

            case R.id.tvDeleteTrip:
                String tripId = trip.getTripId();
                FirebaseUtil.getTripsRef().child(tripId).removeValue();
                FirebaseUtil.getUsersRef().child(FirebaseUtil.getCurrentUserId()).child("trips").child(tripId).removeValue();
                finish();
        }

    }
}
