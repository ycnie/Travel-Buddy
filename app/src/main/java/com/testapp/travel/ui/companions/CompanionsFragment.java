package com.testapp.travel.ui.companions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testapp.travel.R;
import com.testapp.travel.data.model.Friend;
import com.testapp.travel.data.model.ListFriend;
import com.testapp.travel.data.model.Message;
import com.testapp.travel.data.model.User;
import com.testapp.travel.utils.FirebaseUtil;
import com.google.firebase.database.Query;
import com.testapp.travel.utils.StaticConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static com.testapp.travel.utils.FirebaseUtil.getCurrentUserId;

public class CompanionsFragment extends Fragment {

    public static final String TAG = "CompanionsFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;


    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private int mRecyclerViewPosition = 0;
    private OnCompanionSelectedListener mListener;

    private ArrayList<String> listFriendId = new ArrayList<>();
    private ListFriend dataListFriend = new ListFriend();
    public static int ACTION_START_CHAT = 1;


    public static CompanionsFragment newInstance() {
        return new CompanionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_companions, container, false);
        rootView.setTag(TAG);

        //show the list of my companions
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        if (dataListFriend.getListFriend() == null || dataListFriend.getListFriend().size() == 0) {
            Log.i(TAG, "getListFriendUId");
            getListFriendUId();
            Log.i(TAG, String.valueOf(listFriendId.size()));
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String uid = getCurrentUserId();
        if (uid == null) {
            return;
        }

        //GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mRecyclerViewPosition = (int) savedInstanceState
                    .getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
            // TODO: RecyclerView only restores position properly for some tabs.
        }

        Timber.d("Restoring recycler view position: %d", mRecyclerViewPosition);
//        Query companionsQuery = FirebaseUtil.getCompanionsRef();
//        mAdapter = getFirebaseRecyclerAdapter(companionsQuery);
//        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                // TODO: Refresh feed view.
//            }
//        });

        mAdapter = new ListFriendsAdapter(getActivity().getApplicationContext(), dataListFriend, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        //mRecyclerView.setAdapter(mAdapter);
    }


//    private FirebaseRecyclerAdapter<User, CompanionViewHolder> getFirebaseRecyclerAdapter(Query query) {
////
////        String uid = getCurrentUserId();
////
////        return new FirebaseIndexRecyclerAdapter<User, CompanionViewHolder>(
////                User.class,
////                R.layout.widget_cardview_companion,
////                CompanionViewHolder.class,
////                FirebaseUtil.getCompanionsRef().child(uid),
////                FirebaseUtil.getUsersRef()) {
////            @Override
////            protected void populateViewHolder(CompanionViewHolder viewHolder, User model, int position) {
////                String key = this.getRef(position).getKey();
////                Timber.d("position %d key %s", position, key);
////                viewHolder.setAuthor(model.displayName, key);
////                viewHolder.setIcon(model.profileImageUrl, key);
////            }
////        };
////    }

    private void getListFriendUId() {
        Log.i(TAG, StaticConfig.UID);
        FirebaseDatabase.getInstance().getReference().child("companions/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        listFriendId.add(data.getKey());
                        Log.i("friendlist", data.getKey());
                    }
                    getAllFriendInfo(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAllFriendInfo(final int index) {
        if (index == listFriendId.size()) {
            //save list friend
            mAdapter.notifyDataSetChanged();
            // mSwipeRefreshLayout.setRefreshing(false);

        } else {
            final String id = listFriendId.get(index);
            FirebaseDatabase.getInstance().getReference().child("users/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Friend friend = new Friend();
                        HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
                        friend.displayName = (String) mapUserInfo.get("displayName");
                        friend.profileImageUrl = (String) mapUserInfo.get("profileImageUrl");
                        friend.id = id;
                        Message message = new Message();
                        HashMap<String, Object> map = (HashMap<String, Object>) mapUserInfo.get("message");
                        message.idReceiver = (String)map.get("idReceiver");
                        message.idSender = (String)map.get("idSender");
                        message.text = (String)map.get("text");
                        message.timestamp = (Long) map.get("timestamp");
                        friend.message = message;
                        friend.idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
                        dataListFriend.getListFriend().add(friend);

                    }
                    getAllFriendInfo(index + 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null && mAdapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTION_START_CHAT == requestCode && data != null && ListFriendsAdapter.mapMark != null) {
            ListFriendsAdapter.mapMark.put(data.getStringExtra("idFriend"), false);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        int recyclerViewScrollPosition = getRecyclerViewScrollPosition();
        Timber.d("Recycler view scroll position: %d", recyclerViewScrollPosition);
        savedInstanceState.putSerializable(KEY_LAYOUT_POSITION, recyclerViewScrollPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    private int getRecyclerViewScrollPosition() {
        int scrollPosition = 0;
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        return scrollPosition;
    }

    public interface OnCompanionSelectedListener {
        void onCompanionRemoved(String companionKey);

        void onCompanionAdded(String companionKey);

        void onCompanionSelected(String companionKey);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCompanionSelectedListener) {
            mListener = (OnCompanionSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCompanionSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

class ListFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ListFriend listFriend;
    private Context context;
    private Activity activity;
    public static Map<String, Query> mapQuery;
    public static Map<String, ChildEventListener> mapChildListener;
    public static Map<String, ChildEventListener> mapChildListenerOnline;
    public static Map<String, Boolean> mapMark;


    public ListFriendsAdapter(Context context, ListFriend listFriend, Activity activity) {
        this.listFriend = listFriend;
        this.context = context;
        this.activity = activity;
        mapQuery = new HashMap<>();
        mapChildListener = new HashMap<>();
        mapMark = new HashMap<>();
        mapChildListenerOnline = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new  ItemFriendViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final String name = listFriend.getListFriend().get(position).displayName;
        final String id = listFriend.getListFriend().get(position).id;
        final String idRoom = listFriend.getListFriend().get(position).idRoom;
        final String avata = listFriend.getListFriend().get(position).profileImageUrl;
        ((ItemFriendViewHolder) holder).txtName.setText(name);

        ((View) ((ItemFriendViewHolder) holder).txtName.getParent().getParent().getParent())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT);
                        ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT);
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, name);
                        ArrayList<CharSequence> idFriend = new ArrayList<>();
                        idFriend.add(id);
                        intent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriend);
                        intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, idRoom);

                        ChatActivity.bitmapAvataFriend = new HashMap<>();
                        if (avata != null) {
                            ChatActivity.bitmapAvataFriend.put(id,avata);
                        } else {
                            ChatActivity.bitmapAvataFriend.put(id, null);
                        }
                        mapMark.put(id, null);
                        activity.startActivityForResult(intent, CompanionsFragment.ACTION_START_CHAT);
                    }
                });
        //todo: delete friend



        if (listFriend.getListFriend().get(position).message.text.length() > 0) {
            ((ItemFriendViewHolder) holder).txtMessage.setVisibility(View.VISIBLE);
            ((ItemFriendViewHolder) holder).txtTime.setVisibility(View.VISIBLE);
            if (!listFriend.getListFriend().get(position).message.text.startsWith(id)) {
                ((ItemFriendViewHolder) holder).txtMessage.setText(listFriend.getListFriend().get(position).message.text);
                ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT);
                ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT);
            } else {
                ((ItemFriendViewHolder) holder).txtMessage.setText(listFriend.getListFriend().get(position).message.text.substring((id + "").length()));
                ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT_BOLD);
                ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT_BOLD);
            }
            String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(listFriend.getListFriend().get(position).message.timestamp));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
            if (today.equals(time)) {
                ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("HH:mm").format(new Date(listFriend.getListFriend().get(position).message.timestamp)));
            } else {
                ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("MMM d").format(new Date(listFriend.getListFriend().get(position).message.timestamp)));
            }
        } else {
            ((ItemFriendViewHolder) holder).txtMessage.setVisibility(View.GONE);
            ((ItemFriendViewHolder) holder).txtTime.setVisibility(View.GONE);
            if (mapQuery.get(id) == null && mapChildListener.get(id) == null) {
                mapQuery.put(id, FirebaseDatabase.getInstance().getReference().child("message/" + idRoom).limitToLast(1));
                mapChildListener.put(id, new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        if (mapMark.get(id) != null) {
                            if (!mapMark.get(id)) {
                                listFriend.getListFriend().get(position).message.text = id + mapMessage.get("text");
                            } else {
                                listFriend.getListFriend().get(position).message.text = (String) mapMessage.get("text");
                            }
                            notifyDataSetChanged();
                            mapMark.put(id, false);
                        } else {
                            listFriend.getListFriend().get(position).message.text = (String) mapMessage.get("text");
                            notifyDataSetChanged();
                        }
                        listFriend.getListFriend().get(position).message.timestamp = (long) mapMessage.get("timestamp");
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                mapMark.put(id, true);
            } else {
                mapQuery.get(id).removeEventListener(mapChildListener.get(id));
                mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                mapMark.put(id, true);
            }
        }
        if(listFriend.getListFriend().get(position).profileImageUrl == null) {
            ((ItemFriendViewHolder) holder).avata.setImageResource(R.drawable.default_avata);
        }
        else {
            Glide.with(context)
                    .load(listFriend.getListFriend().get(position).profileImageUrl)
                    .into(((ItemFriendViewHolder) holder).avata);
        }
    }

    @Override
    public int getItemCount() {
        return listFriend.getListFriend() != null ? listFriend.getListFriend().size() : 0;
    }


}

class ItemFriendViewHolder extends RecyclerView.ViewHolder{
    public CircleImageView avata;
    public TextView txtName, txtTime, txtMessage;
    private Context context;

    ItemFriendViewHolder(Context context, View itemView) {
        super(itemView);
        avata = (CircleImageView) itemView.findViewById(R.id.icon_avata);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtTime = (TextView) itemView.findViewById(R.id.txtTime);
        txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
        this.context = context;
    }
}


