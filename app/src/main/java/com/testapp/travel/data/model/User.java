package com.testapp.travel.data.model;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@IgnoreExtraProperties
public class User {

    public String displayName;
    public String profileImageUrl;
    public String userId;
    public Message message;
    public float rating;
    public int noReviews;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String displayName, Uri profileImageUrl) {
        this.displayName = displayName;
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl.toString();
        }
        message = new Message();
    }

    public User(String displayName, String profileImageUrl) {
        this.displayName = displayName;
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
        message = new Message();

    }


}