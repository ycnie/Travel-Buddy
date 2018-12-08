package com.testapp.travel.ui.files;


import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.testapp.travel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class selectGenderFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener{

    private RadioButton male;
    private RadioButton female;
    private RadioButton other;


    public selectGenderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_gender, container, false);
        male = (RadioButton) rootView.findViewById(R.id.male);
        male.setOnClickListener(this);
        female = (RadioButton) rootView.findViewById(R.id.female);
        female.setOnClickListener(this);
        other = (RadioButton) rootView.findViewById(R.id.other);
        other.setOnClickListener(this);
        CharSequence charSequence = getArguments().getCharSequence("curGender");
        switch (charSequence.toString()){
            case "male":
                male.setChecked(true);
                break;
            case "female":
                female.setChecked(true);
                break;
            case "other":
                other.setChecked(true);
                break;
        }
        return  rootView;
    }

    public interface onClickRadioButtonListener {
        void onItemSelected(String gender);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        onClickRadioButtonListener activity = (onClickRadioButtonListener) getTargetFragment();
        switch(id) {
            case R.id.male:
                activity.onItemSelected("male");
                dismiss();
                break;
            case R.id.female:
                activity.onItemSelected("female");
                dismiss();
                break;
            case R.id.other:
                activity.onItemSelected("other");
                dismiss();
                break;
        }
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }
}
