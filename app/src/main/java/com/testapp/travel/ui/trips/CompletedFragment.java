package com.testapp.travel.ui.trips;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testapp.travel.R;

public class CompletedFragment extends Fragment {

    public static CompletedFragment newInstance() {
        return new CompletedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places_liked, container, false);
        return rootView;
    }
}
