package com.testapp.travel.ui.places;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testapp.travel.R;
import com.testapp.travel.utils.FirebaseUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsPlacesVisited extends FragmentActivity implements OnMapReadyCallback
{
        private GoogleMap mMap;
        private EditText mSearchText;
        private FloatingActionButton fab;
        //private FloatingActionButton fab2;
        private RelativeLayout innerlay;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps_places_visited);

            mSearchText = (EditText) findViewById(R.id.input_search);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            //fab2 = (FloatingActionButton) findViewById(R.id.fab2);
            innerlay = (RelativeLayout) findViewById(R.id.innerrellayout);

            innerlay.setVisibility(View.INVISIBLE);

            fab.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    innerlay.setVisibility(View.VISIBLE);
                }
            });
/*
        fab2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mMap.OnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

                })
            }
        });*/

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            init();
        }

        private void init(){
            Log.i("INIT","initialising");
            mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == event.ACTION_DOWN
                            || event.getAction() == event.KEYCODE_ENTER){
                        geolocate();
                    }

                    return false;
                }
            });
            DatabaseReference mVisitedRef = FirebaseDatabase.getInstance().getReference("visitedLOC/"+FirebaseUtil.getCurrentUserId());
            mVisitedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String locationName = ds.child("locationName").getValue(String.class);
                        Double longitude = ds.child("position").child("longitude").getValue(Double.class);
                        Double latitude = ds.child("position").child("latitude").getValue(Double.class);
                        LatLng position = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(position).title(locationName).icon(BitmapDescriptorFactory.fromResource(R.drawable.latestmarker)));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void geolocate(){
            String searchString = mSearchText.getText().toString();
            Geocoder geocoder = new Geocoder(MapsPlacesVisited.this);
            List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(searchString,1);
            } catch (IOException e){
                Log.e("TAG", "geolocate: ");
            }

            if(list.size() > 0) {
                Address address = list.get(0);
                Log.d("GEO","Geolocate : " + address.toString());
                LatLng location = new LatLng(address.getLatitude(),address.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mMap.addMarker(new MarkerOptions().position(location).title(searchString).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.latestmarker)));
                addloctoDB(searchString,location);
            }

            mSearchText.setText("");
            innerlay.setVisibility(View.INVISIBLE);


        }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
        }

        public void addloctoDB(String name, LatLng pos){
            String currentuserid = FirebaseUtil.getCurrentUserId() ;
//            FirebaseDatabase.getInstance().getReference().child("visitedLOC").child(currentuserid).child("locationName").setValue(name);
//            FirebaseDatabase.getInstance().getReference().child("visitedLOC").child(currentuserid).child("position").setValue(pos);
            String key = FirebaseDatabase.getInstance().getReference().child("visitedLOC").child(currentuserid).push().getKey();
            FirebaseDatabase.getInstance().getReference().child("visitedLOC").child(currentuserid).child(key).child("locationName").setValue(name);
            FirebaseDatabase.getInstance().getReference().child("visitedLOC").child(currentuserid).child(key).child("position").setValue(pos);
//            FirebaseDatabase.getInstance().getReference().child("visitedLOC").child(currentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    dataSnapshot.child("").getValue()
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        }
}
