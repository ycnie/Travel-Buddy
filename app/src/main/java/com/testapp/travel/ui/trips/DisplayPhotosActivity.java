package com.testapp.travel.ui.trips;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.testapp.travel.R;
import com.testapp.travel.data.model.Photo;
import com.testapp.travel.data.model.Trip;
import com.testapp.travel.ui.helpers.CardScaleHelper;
import com.testapp.travel.utils.BlurBitmapUtils;
import com.testapp.travel.utils.FirebaseUtil;
import com.testapp.travel.utils.ViewSwitchUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DisplayPhotosActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageView mBlurView;
//    private TextView tvLocation;
    private TextView tvComment;
    private List<Integer> mPhotos = new ArrayList<>();
    private List<Bitmap> mPhotosBitmaps = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
   // private List<String> mTexts = new ArrayList<>();
    private List<String> mComments = new ArrayList<>();
    private CardScaleHelper mCardScaleHelper = null;
    private Runnable mBlurRunnable;
    private int mLastPos = -1;
    private ImageView ivAddPhoto;
    private Trip trip;

    private int TAKE_PHTOT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        trip=new Trip();
        trip=(Trip) Parcels.unwrap(getIntent()
                .getParcelableExtra("Trip"));
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void init() {
        DatabaseReference mTripPhotoRef = FirebaseUtil.getTripsRef().child(trip.getTripId()).child("photos");
        mTripPhotoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPhotosBitmaps.clear();
                mComments.clear();
                for (DataSnapshot photoIdDs : dataSnapshot.getChildren()) {
                    String photoId = photoIdDs.getKey();
                    DatabaseReference mPhotoRef = FirebaseUtil.getPhotosRef().child(photoId);
                    mPhotoRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Photo photo = dataSnapshot.getValue(Photo.class);
                            if (photo != null) {
                                byte[] imageArray = Base64.decode(photo.getImage(), Base64.DEFAULT);
                                Bitmap photoBitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
                                mPhotosBitmaps.add(photoBitmap);
                                mComments.add(photo.getComment());
                                mAdapter.notifyDataSetChanged();
                                notifyBackgroundChange();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewPhoto);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new CardAdapter(mPhotosBitmaps);
        mRecyclerView.setAdapter(mAdapter);
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(2);
        mCardScaleHelper.attachToRecyclerView(mRecyclerView);
//        tvLocation=(TextView)findViewById(R.id.tvLocation) ;
        tvComment=(TextView)findViewById(R.id.tvComment) ;
        ivAddPhoto=(ImageView)findViewById(R.id.ivAddPhoto);
        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DisplayPhotosActivity.this,AddPhotoActivity.class)
                        .putExtra("Trip", Parcels.wrap(trip));
                startActivity(intent);
//                startActivityForResult(intent, TAKE_PHTOT);
            }
        });

        initBlurBackground();
    }

    private void initBlurBackground() {
        mBlurView = (ImageView) findViewById(R.id.blurView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange();
                }
            }
        });

    }

    private void notifyBackgroundChange() {
        if (mLastPos == mCardScaleHelper.getCurrentItemPos()) return;
        mLastPos = mCardScaleHelper.getCurrentItemPos();
        if (mLastPos >= mPhotosBitmaps.size() || mLastPos >= mComments.size()) return;
        mBlurView.removeCallbacks(mBlurRunnable);
        mBlurRunnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = mPhotosBitmaps.get(mLastPos);
                ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), bitmap, 15));
            }
        };

       // tvLocation.setText(mTexts.get(mLastPos));
        tvComment.setText(mComments.get(mLastPos));
        mBlurView.postDelayed(mBlurRunnable, 500);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == TAKE_PHTOT) {
//            Toast.makeText(getApplicationContext(), "New photo added!", Toast.LENGTH_LONG).show();
//        }
//    }
}
