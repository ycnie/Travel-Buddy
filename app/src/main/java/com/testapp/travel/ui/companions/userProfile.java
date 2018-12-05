package com.testapp.travel.ui.companions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testapp.travel.R;
import com.testapp.travel.data.model.Friend;
import com.testapp.travel.data.model.Message;
import com.testapp.travel.data.model.User;
import com.testapp.travel.utils.StaticConfig;

import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfile extends AppCompatActivity {

    private CircleImageView photoUrl;
    private TextView userName;
    private TextView userEmail;
    private CharSequence friendId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
//        photoUrl = (CircleImageView)findViewById(R.id.photot_url);
//        userName = (TextView)findViewById(R.id.user_name);
//        userEmail = (TextView)findViewById(R.id.user_email);
//        Intent intentData = getIntent();
//        friendId = intentData.getStringExtra("FriendID");
//        FirebaseDatabase.getInstance().getReference().child("users/" + friendId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
//                    userName.setText((String) mapUserInfo.get("displayName"));
//
//                    if((String) mapUserInfo.get("profileImageUrl") != null){
//                        Glide.with(getApplicationContext())
//                                .load((String) mapUserInfo.get("profileImageUrl"))
//                                .into(photoUrl);
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
