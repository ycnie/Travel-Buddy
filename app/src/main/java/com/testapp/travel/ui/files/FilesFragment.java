package com.testapp.travel.ui.files;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testapp.travel.R;

public class FilesFragment extends Fragment {
    public static FilesFragment newInstance() {
        return new FilesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_files, container, false);
        return rootView;
    }
}
