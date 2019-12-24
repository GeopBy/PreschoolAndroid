package com.example.preschool.Chats;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preschool.Chats.Model.Chat;
import com.example.preschool.Chats.Model.ChatList;
import com.example.preschool.Notifications.Token;
import com.example.preschool.R;
import com.example.preschool.ReturnMinute;
import com.example.preschool.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> mUsers;
    private FirebaseUser fuser;
    private DatabaseReference ChatListRef, UsersRef, MessRef, UserStateRef;
    private ChatList listIdChatUser;
    private String idClass;
    private String current_user_id;
    private FirebaseRecyclerAdapter adapter;
    private Bundle bundle;

    private ValueEventListener ChatListListener, UserListener, UserStateListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        // Nhận idClass từ Main
        bundle = getArguments();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = fuser.getUid();

        UserStateRef=FirebaseDatabase.getInstance().getReference("UserState");
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        ChatListRef = FirebaseDatabase.getInstance().getReference("ChatList").child(current_user_id);
        MessRef = FirebaseDatabase.getInstance().getReference("Messages");

        getChatList();

        return view;
    }

    private static class ChatListViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private TextView time;
        private ImageView profile_image;
        private ImageView is_online;
        private TextView last_msg;
        private TextView sender;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            is_online = itemView.findViewById(R.id.online);
            last_msg = itemView.findViewById(R.id.last_msg);
            sender = itemView.findViewById(R.id.sender);
            time=itemView.findViewById(R.id.all_messages_time);
        }
    }



    // Return IdChat of both
    private String idChat(String A, String B) {
        if (A.compareTo(B) > 0) {
            return A + B;
        } else return B + A;
    }

    private void getChatList() {
        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>().
                setQuery(ChatListRef.child("uidList"), String.class).build();
        adapter = new FirebaseRecyclerAdapter<String, ChatListViewHolder>(options) {
            @NonNull
            @Override
            public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_chat, parent, false);
                return new ChatListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatListViewHolder chatListViewHolder, int i, @NonNull final String s) {
                chatListViewHolder.setIsRecyclable(false);
                UsersRef.child(s).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        User user = dataSnapshot.getValue(User.class);
                        chatListViewHolder.username.setText(user.getUsername());
                        Picasso.get().load(user.getProfileimage()).into(chatListViewHolder.profile_image);

                        // online/offline
                        UserStateListener= UserStateRef.child(s).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    if(dataSnapshot.child("type").getValue().equals("online"))
                                        chatListViewHolder.is_online.setVisibility(View.VISIBLE);
                                    else chatListViewHolder.is_online.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//                        if (user.getUserState().getType().equals("online"))
//                            chatListViewHolder.is_online.setVisibility(View.VISIBLE);
//                        else chatListViewHolder.is_online.setVisibility(View.GONE);

                        // Get Last Mess
                        Query query = MessRef.child(idChat(user.userid, current_user_id)).orderByKey().limitToLast(1);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot child: dataSnapshot.getChildren()){

                                    //set thoi gian lan cuoi nhan tin
                                    try{
                                        setLastTimeChat(child.child("time").getValue(String.class),chatListViewHolder.time);
                                    }catch (Exception e){

                                    }
                                    chatListViewHolder.last_msg.setText(child.child("message").getValue().toString());
                                    if(child.child("sender").getValue().toString().equals(current_user_id)){
                                        chatListViewHolder.sender.setVisibility(View.VISIBLE);
                                        chatListViewHolder.sender.setWidth(100);
                                        setTextLastMess(child.child("message").getValue().toString()
                                                ,chatListViewHolder.last_msg,true);
                                    }
                                    else {
                                        chatListViewHolder.sender.setVisibility(View.GONE);
                                        chatListViewHolder.sender.setWidth(1);
                                        setTextLastMess(child.child("message").getValue().toString()
                                                ,chatListViewHolder.last_msg,child.child("isseen").getValue(Boolean.class));
                                    }

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                chatListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chatIntent = new Intent(getActivity(), MessageActivity.class);
                        bundle.putString("VISIT_USER_ID", s);
                        chatIntent.putExtras(bundle);
                        startActivity(chatIntent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void setLastTimeChat(String lastTimeChat,TextView time){
        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String nowTime = currentTime.format(calFordTime.getTime());
        ReturnMinute returnMinute = new ReturnMinute();
        long a = returnMinute.getMinute(lastTimeChat, nowTime);
        if (a <= 60) {
            time.setText((String.valueOf(a) + " phút"));
        } else {
            if (a > 1440) time.setText((String.valueOf(a / 1440) + " ngày"));
            else time.setText((String.valueOf(a / 60) + " giờ"));
        }

    }

    private void setTextLastMess(String theLastMessage, TextView last_msg, Boolean seen) {

        if (seen) {
            last_msg.setTextColor(Color.parseColor("#A9A9A9"));
            last_msg.setTypeface(Typeface.create(last_msg.getTypeface(), Typeface.NORMAL));

        } else {
            last_msg.setTextColor(getResources().getColor(android.R.color.black));
            last_msg.setTypeface(null, Typeface.BOLD);
        }
        try {// Có  dấu Enter
            if (theLastMessage.indexOf("\n") < 20)
                last_msg.setText(theLastMessage.substring(0, theLastMessage.indexOf("\n")) + "...");
            else last_msg.setText(theLastMessage.substring(0, 20) + "...");
        } catch (Exception e) {// Không có dấu Enter
            if (theLastMessage.length() > 20) {
                last_msg.setText(theLastMessage.substring(0, 20) + "...");
            } else last_msg.setText(theLastMessage);
        }
    }

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

        UserStateRef.child(fuser.getUid())
                .updateChildren(currentStateMap);
    }

    @Override
    public void onStart() {
        super.onStart();
//        updateUserStatus("online");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserStatus("online");
    }

    @Override
    public void onStop() {
        super.onStop();
        updateUserStatus("offline");
        if(UserStateListener!=null){
                UserStateRef.removeEventListener(UserStateListener);
//            }
//            if(ChatListRef!=null){
//                ChatListRef.removeEventListener(ChatListListener);
        }

    }
}
