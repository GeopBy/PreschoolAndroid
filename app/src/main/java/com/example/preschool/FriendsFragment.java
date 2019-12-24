package com.example.preschool;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.Chats.MessageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FriendsFragment extends Fragment {
    private RecyclerView myFriendList;
    private DatabaseReference UserStateRef, UsersRef;
    private ValueEventListener FiendListener, UsersListener;
    private FirebaseAuth mAuth;
    private String current_user_id, idClass, idTeacher, idFriend;
    private Bundle bundle;
    private FirebaseRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        // Nhận idClass từ Main
        bundle = new Bundle();
        bundle = getArguments();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher = bundle.getString("ID_TEACHER");
        }

        UserStateRef = FirebaseDatabase.getInstance().getReference("UserState");
//        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Friends").child(current_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myFriendList = view.findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);
        showAllFriend();
        return view;
    }

    // tao user state tren firebase
    public void updateUserStatus(String state) {
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        Map currentStateMap = new HashMap();
        currentStateMap.put("time", saveCurrentTime);
        currentStateMap.put("date", saveCurrentDate);
        currentStateMap.put("type", state);

        UserStateRef.child(current_user_id)
                .updateChildren(currentStateMap);
    }

    @Override
    public void onStart() {
        super.onStart();
//        DisplayAllFriends();
//        updateUserStatus("online");
    }

    @Override
    public void onResume() {
        super.onResume();

//        DisplayAllFriends();
        updateUserStatus("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (UsersListener != null) {
            UsersRef.removeEventListener(UsersListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        updateUserStatus("offline");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        updateUserStatus("offline");
    }

    private void showAllFriend() {
        Query showAllFriendsQuery = UsersRef.orderByChild("username").startAt("").endAt(""+"\uf8ff");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().
                setQuery(showAllFriendsQuery, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int i, @NonNull final User user) {
                String key = getRef(i).getKey();
//                friendsViewHolder.setIsRecyclable(false);
                boolean view = false;
                if (user.getRole().equals("Teacher")) {
                    if (user.getIdclass().equals(idClass) && key.equals(idTeacher)) {

                        friendsViewHolder.isTeacher.setVisibility(View.VISIBLE);
                        friendsViewHolder.kid_name.setVisibility(View.GONE);
                        friendsViewHolder.user_name.setText(user.getFullnameteacher());
                        view = true;


                    }

                }
                if (user.getRole().equals("Parent")) {
                    if(user.getUsername()!=null){
                        friendsViewHolder.user_name.setText(user.getUsername());
                    }
                    ArrayList<String> temp = user.getMyclass();
                    for (String node : temp) {
                        if (node.equals(idClass)) {
                            UsersRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try {
                                        String kidname=dataSnapshot.child("mychildren")
                                                .child(idClass)
                                                .child("name")
                                                .getValue(String.class);
                                        if(kidname!=null){
                                            friendsViewHolder.kid_name
                                                    .setText("Bé " + kidname);
                                        }
                                    } catch (Exception e) {
                                        friendsViewHolder.kid_name
                                                .setText("");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            view = true;
                            break;
                        }
                    }

                }
                if (view == true) {
                    if (user.getProfileimage() != null) {
                        friendsViewHolder.setProfileImage(user.getProfileimage());
                    }
                    UserStateRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.child("type").getValue().toString().equals("online")) {
                                    friendsViewHolder.online.setVisibility(View.VISIBLE);
                                } else {
                                    friendsViewHolder.online.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                friendsViewHolder.online.setVisibility(View.GONE);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    final String visit_user_id = getRef(i).getKey();
                    friendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "Thông tin của " + friendsViewHolder.user_name.getText().toString(),
                                    "Gửi tin nhắn"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        Intent profileIntent = new Intent(getContext(), PersonProfileActivity.class);
                                        bundle.putString("VISIT_USER_ID", visit_user_id);
                                        profileIntent.putExtras(bundle);
                                        startActivity(profileIntent);
                                    }
                                    if (which == 1) {
                                        if (visit_user_id.equals(current_user_id)) {
                                            Toast.makeText(getContext(), "Không thể nhắn tin cho chính bạn", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent chatintent = new Intent(getActivity(), MessageActivity.class);
                                            bundle.putString("VISIT_USER_ID", visit_user_id);
                                            chatintent.putExtras(bundle);
                                            startActivity(chatintent);
                                        }
                                    }
                                }
                            });
                            if(user.getProfileimage()!=null){
                                builder.show();
                            }

                        }
                    });
                } else {
                    friendsViewHolder.Layout_hide();
                }
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friend_display_layout, parent, false);

                FriendsViewHolder viewHolder = new FriendsViewHolder(view);
                return viewHolder;
            }
        };
        myFriendList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        private final androidx.constraintlayout.widget.ConstraintLayout friendlayout;
        final androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params;

        private TextView isTeacher;
        private ImageView user_image;
        private TextView user_name;
        private ImageView online;
        private TextView kid_name;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            friendlayout = itemView.findViewById(R.id.all_user_find_friend);
            params = new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);


            user_image = friendlayout.findViewById(R.id.all_users_profile_image);
            user_name = friendlayout.findViewById(R.id.all_users_profile_full_name);
            online = friendlayout.findViewById(R.id.online);
            kid_name = friendlayout.findViewById(R.id.all_users_profile_kid_name);
            isTeacher = friendlayout.findViewById(R.id.isTeacher);
        }

        public void setProfileImage(String profileimage) {
            Picasso.get().load(profileimage).placeholder(R.drawable.ic_person_black_50dp).resize(150, 0).into(user_image);

        }

        private void Layout_hide() {
            params.height = 0;
            //itemView.setLayoutParams(params); //This One.
            friendlayout.setLayoutParams(params);   //Or This one.

        }

    }
}
