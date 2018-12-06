package com.testapp.travel.ui.files;

import android.app.FragmentManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testapp.travel.R;
import com.testapp.travel.data.model.Friend;
import com.testapp.travel.data.model.Message;
import com.testapp.travel.data.model.User;
import com.testapp.travel.ui.userProfile.RatingBarFragment;
import com.testapp.travel.ui.userProfile.userProfileFragment;
import com.testapp.travel.utils.StaticConfig;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static com.testapp.travel.utils.FirebaseUtil.getCurrentUserId;

public class FilesFragment extends Fragment implements selectGenderFragment.onClickRadioButtonListener , AddressFragment.onClickDoneListener,
        LanguageFragment.onClickItemListener,WorkFragment.onClickDoneListener {
    private CircleImageView photoUrl;
    private TextView userName;
    private TextView gender;
    private TextView address;
    private TextView language;
    private TextView work;

    private ImageView editGender;
    private  ImageView editAddress;
    private ImageView editLanguage;
    private  ImageView editWork;


    public static FilesFragment newInstance() {
        return new FilesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_files, container, false);
        userName = (TextView)rootView.findViewById(R.id.user_name);
        photoUrl = (CircleImageView) rootView.findViewById(R.id.photo_url);
        gender = (TextView) rootView.findViewById(R.id.gender);

        editGender = (ImageView) rootView.findViewById(R.id.gender_ic);
        editGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                selectGenderFragment fragment = new selectGenderFragment();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("curGender", gender.getText());
                fragment.setArguments(bundle);
                // SETS the target fragment for use later when sending results
                fragment.setTargetFragment(FilesFragment.this, 300);
                fragment.show(fm, "selectGenderFragment");
            }
        });
        address =(TextView) rootView.findViewById(R.id.location);

        editAddress = (ImageView) rootView.findViewById(R.id.location_ic);
        editAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                AddressFragment fragment = new AddressFragment();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("curAddress", address.getText());
                fragment.setArguments(bundle);
                // SETS the target fragment for use later when sending results
                fragment.setTargetFragment(FilesFragment.this, 300);
                fragment.show(fm, "addressFragment");
            }
        });
        language = (TextView) rootView.findViewById(R.id.language);

        editLanguage = (ImageView) rootView.findViewById(R.id.language_ic);
        editLanguage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                LanguageFragment fragment = new LanguageFragment();
                // SETS the target fragment for use later when sending results
                fragment.setTargetFragment(FilesFragment.this, 300);
                fragment.show(fm, "languageFragment");
            }
        });
        work = (TextView) rootView.findViewById(R.id.work);

        editWork = (ImageView) rootView.findViewById(R.id.work_ic);
        editWork.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                WorkFragment fragment = new WorkFragment();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("curWork", work.getText());
                fragment.setArguments(bundle);
                // SETS the target fragment for use later when sending results
                fragment.setTargetFragment(FilesFragment.this, 300);
                fragment.show(fm, "workFragment");
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String uid = getCurrentUserId();
        if (uid == null) {
            return;
        }
        else{
            getCurUserInfo(uid);
        }

    }
    @Override
    public void onItemSelected(String callback) {
        gender.setText(callback.toCharArray(), 0, callback.length());
        FirebaseDatabase.getInstance().getReference().child("users/" + StaticConfig.UID).child("gender").setValue(callback);
    }

    @Override
    public void saveAddress(CharSequence addr){
        address.setText(addr);
        FirebaseDatabase.getInstance().getReference().child("users/" + StaticConfig.UID).child("address").setValue(String.valueOf(addr));
    }

    @Override
    public void saveLanguage(String l) {
        language.setText(l.toCharArray(), 0, l.length());
        FirebaseDatabase.getInstance().getReference().child("users/" + StaticConfig.UID).child("language").setValue((l));
    }

    @Override
    public void saveWork(CharSequence w) {
        work.setText(w);
        FirebaseDatabase.getInstance().getReference().child("users/" + StaticConfig.UID).child("work").setValue(String.valueOf(w));
    }

    private void getCurUserInfo(String uid) {
        FirebaseDatabase.getInstance().getReference().child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
                    userName.setText((String) mapUserInfo.get("displayName"));
                    if((String) mapUserInfo.get("profileImageUrl") != null){
                        Glide.with(getActivity())
                                .load((String) mapUserInfo.get("profileImageUrl"))
                                .into(photoUrl);
                    }
                    if(mapUserInfo.containsKey("gender")) {
                        gender.setText((String)mapUserInfo.get("gender"));

                    }
                    if(mapUserInfo.containsKey("address")) {
                        address.setText((String)mapUserInfo.get("address"));
                    }
                    if(mapUserInfo.containsKey("language")) {
                        language.setText((String)mapUserInfo.get("language"));
                    }
                    if(mapUserInfo.containsKey("work")) {
                        work.setText((String)mapUserInfo.get("work"));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
