package com.testapp.travel.ui.places;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.testapp.travel.R;

public class VisitedFragment extends Fragment {

    public static VisitedFragment newInstance() {
        return new VisitedFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places_visited, container, false);
        return rootView;



    }
}
