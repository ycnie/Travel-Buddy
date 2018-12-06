package com.testapp.travel.ui.userProfile;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testapp.travel.R;
import com.testapp.travel.utils.FirebaseUtil;
import com.testapp.travel.utils.StaticConfig;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */

//https://stackoverflow.com/questions/32605774/cannot-resolve-method-showandroid-support-v4-app-fragmentmanager-java-lang-stri
    //need to keep consistent with fragmentManger in version selection
public class userProfileFragment extends DialogFragment implements View.OnClickListener, RatingBarFragment.RatingDialogListener {
    private CircleImageView photoUrl;
    private TextView userName;
    private TextView mFriendsCount;
    private TextView mTripsCount;
    private TextView mBucketListCount;
    private CharSequence friendId;
    private Button addFriend;
    private Button rateFriend;
    private TextView ratingTxt;
    private Float sumOfRating = 5f;
    private Long numOfPeople = 1L;


    public userProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        rootView.setTag("userProfile");
        photoUrl = (CircleImageView)rootView.findViewById(R.id.photo_url);
        userName = (TextView)rootView.findViewById(R.id.user_name);
        mFriendsCount = (TextView) rootView.findViewById(R.id.userFriendsCount);
        mTripsCount = (TextView) rootView.findViewById(R.id.userTripCount);
        mBucketListCount = (TextView) rootView.findViewById(R.id.userBucketListCount);
        addFriend = (Button) rootView.findViewById(R.id.add_friend_button);
        rateFriend = (Button) rootView.findViewById(R.id.rating_friend_button);
        ratingTxt = (TextView) rootView.findViewById(R.id.avg_rating);
        rateFriend.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        friendId = getArguments().getCharSequence("FriendID");
        FirebaseDatabase.getInstance().getReference().child("users/" + friendId).addListenerForSingleValueEvent(new ValueEventListener() {
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


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference userInforef = FirebaseUtil.getUsersRef().child(String.valueOf(friendId));
        //Get number of trips
        userInforef.child("trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mTripsCount != null) {
                    mTripsCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        //Get number of bucketlist
        userInforef.child("places").child("bucketlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mBucketListCount != null) {
                    mBucketListCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Get number of companions
        FirebaseUtil.getCompanionsRef().child(String.valueOf(friendId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mFriendsCount != null) {
                    mFriendsCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Get rating
        FirebaseDatabase.getInstance().getReference().child("rating/" + String.valueOf(friendId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    sumOfRating = Float.parseFloat((String)dataSnapshot.child("sumRating").getValue());
                    numOfPeople = (Long)dataSnapshot.child("numOfPeople").getValue();
                    ratingTxt.setText(String.valueOf(sumOfRating / numOfPeople));

                    Log.i("Rating", String.valueOf(dataSnapshot.child("sumRating").getValue()));
                    Log.i("Rating", String.valueOf(dataSnapshot.child("numOfPeople").getValue()));
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("rating/" + String.valueOf(friendId) + "/sumRating").setValue(String.valueOf(sumOfRating));
                    FirebaseDatabase.getInstance().getReference().child("rating/" + String.valueOf(friendId)+ "/numOfPeople" ).setValue(1);
                    ratingTxt.setText("5.0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //check if is already companion
        //https://stackoverflow.com/questions/38948905/how-can-i-check-if-a-value-exists-already-in-a-firebase-data-class-android
        FirebaseUtil.getCompanionsRef().child(StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(String.valueOf(friendId)).exists()) {
                    addFriend.setVisibility(View.GONE);
                }
                else {
                    rateFriend.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        //check if rated the friend
        FirebaseDatabase.getInstance().getReference().child("rating/" + String.valueOf(friendId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(StaticConfig.UID).exists()) {
                    rateFriend.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
           case R.id.rating_friend_button:
               ratingFriend();
               break;
            case R.id.add_friend_button:
                addingFriend();
                break;
        }
    }

    @Override
    public void onFinishEditDialog(float rating) {
        Log.i("rating", String.valueOf(rating));
        FirebaseDatabase.getInstance().getReference().child("rating/" + String.valueOf(friendId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    sumOfRating = Float.parseFloat((String)dataSnapshot.child("sumRating").getValue());
                    numOfPeople = (Long)dataSnapshot.child("numOfPeople").getValue();
                    ratingTxt.setText(String.valueOf(sumOfRating / numOfPeople));
                    if(dataSnapshot.child(StaticConfig.UID).exists()) {
                        rateFriend.setVisibility(View.GONE);
                    }
                    Log.i("Rating", String.valueOf(dataSnapshot.child("sumRating").getValue()));
                    Log.i("Rating", String.valueOf(dataSnapshot.child("numOfPeople").getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(rating != Float.MAX_EXPONENT) {
            sumOfRating += rating;
            numOfPeople++;
            FirebaseDatabase.getInstance().getReference().child("rating/" + String.valueOf(friendId)+ "/sumRating").setValue(String.valueOf(sumOfRating));
            FirebaseDatabase.getInstance().getReference().child("rating/" + String.valueOf(friendId)+ "/numOfPeople").setValue(numOfPeople);
            FirebaseDatabase.getInstance().getReference().child("rating/" + String.valueOf(friendId)).child(StaticConfig.UID).setValue(true);
        }

    }

    public void ratingFriend() {
        FragmentManager fm = getFragmentManager();
        RatingBarFragment ratingBarFragment = new RatingBarFragment();
        // SETS the target fragment for use later when sending results
        ratingBarFragment.setTargetFragment(userProfileFragment.this, 300);
        ratingBarFragment.show(fm, "ratingBarFragment");


    }

    public void addingFriend() {

    }








}
