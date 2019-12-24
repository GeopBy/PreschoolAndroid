package com.example.preschool.Chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.Chats.Model.Chat;
import com.example.preschool.Chats.Model.ChatList;
import com.example.preschool.MainActivity;
import com.example.preschool.Notifications.Client;
import com.example.preschool.Notifications.Data;
import com.example.preschool.Notifications.MyResponse;
import com.example.preschool.Notifications.Sender;
import com.example.preschool.Notifications.Token;
import com.example.preschool.PersonProfileActivity;
import com.example.preschool.R;
import com.example.preschool.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;
    private TextView lastseen;
    private DatabaseReference UsersRef, MessagesRef, UserStateRef, ChatListRef;
    private ImageButton btn_send;
    private ImageButton info_user;
    private EditText text_send;
    private MessageAdapter messageAdapter;
    private List<Chat> mchat;
    private ChatList chatList;
    private RecyclerView recyclerView;
    private Intent intent;
    private ValueEventListener seenListener;
    private String idReciver, current_user_id;
    private String saveCurrentDate, saveCurrentTime;

    private APIService apiService;

    private boolean notify = false;
    private Bundle bundle;
    private String childToChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            idReciver = bundle.getString("VISIT_USER_ID");
        }

        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        childToChat = idChat(current_user_id, idReciver);

        UserStateRef = FirebaseDatabase.getInstance().getReference("UserState");
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        ChatListRef = FirebaseDatabase.getInstance().getReference("ChatList");
        MessagesRef = FirebaseDatabase.getInstance().getReference("Messages");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        lastseen = findViewById(R.id.last_seen);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        info_user = findViewById(R.id.info_user);

//        fuser = FirebaseAuth.getInstance().getCurrentUser();

        info_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MessageActivity.this, PersonProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
//                    notify = true;
                    sendMessage(current_user_id, idReciver, msg);
                    setChatList(current_user_id, idReciver);
                }
                text_send.setText("");
            }
        });


        UsersRef.child(idReciver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getProfileimage().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //and this
                    Picasso.get().load(user.getProfileimage()).resize(70, 0).into(profile_image);
                }
                UserStateRef.child(idReciver).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String state = dataSnapshot.child("type").getValue().toString();
                            if (state.equals("online") || state.equals("chatting"))
                                lastseen.setText("online");
                            else
                                lastseen.setText(dataSnapshot.child("time").getValue().toString());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                readMesagges(user.getProfileimage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(idReciver);
    }

    private String idChat(String myId, String yourId) {
        if (myId.compareTo(yourId) > 0) {
            return myId + yourId;
        } else return yourId + myId;
    }

    private void setChatList(final String current_user_id, final String idReciver) {
        // add user to chat fragment
        chatList = new ChatList();
        ChatListRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list = new ArrayList<String>();
                if (!dataSnapshot.exists()) {
                    list.add(idReciver);
                    chatList.setUidList(list);
                    ChatListRef.child(current_user_id).setValue(chatList);
                } else {
                    chatList.setUidList(dataSnapshot.getValue(ChatList.class).getUidList());
                    list.add(idReciver);
                    for (String s : chatList.getUidList()) {
                        if (!s.equals(idReciver))
                            list.add(s);
                    }
                    chatList.setUidList(list);
                    ChatListRef.child(current_user_id).setValue(chatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ChatListRef.child(idReciver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list = new ArrayList<String>();
                if (!dataSnapshot.exists()) {
                    list.add(current_user_id);
                    chatList.setUidList(list);
                    ChatListRef.child(idReciver).setValue(chatList);
                } else {
                    chatList.setUidList(dataSnapshot.getValue(ChatList.class).getUidList());
                    list.add(current_user_id);
                    for (String s : chatList.getUidList()) {
                        if (!s.equals(current_user_id))
                            list.add(s);
                    }

                    chatList.setUidList(list);
                    ChatListRef.child(idReciver).setValue(chatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(final String idReciver) {
        seenListener = MessagesRef.child(childToChat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(current_user_id) && chat.getSender().equals(idReciver)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message) {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        saveCurrentTime = currentDate.format(calFordDate.getTime());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message.trim());
        hashMap.put("isseen", false);
        hashMap.put("time", saveCurrentTime);

        final String msg = message.trim();

        MessagesRef.child(childToChat).push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Query query = MessagesRef.child(childToChat).orderByKey().limitToLast(1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String data= dataSnapshot.toString();
                                if(data.contains("isseen=false")){
                                    try{
                                        UsersRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);

                                                sendNotifiaction(receiver, user.getUsername(), msg);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }catch (Exception e){

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }, 2000);

            }
        });




    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(current_user_id, R.mipmap.ic_launcher, username + ": " + message, "Tin Nhắn Mới",
                            idReciver);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
//                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Show all message
    private void readMesagges(final String imageurl) {
        mchat = new ArrayList<>();
        Query query = MessagesRef.child(childToChat).orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    mchat.add(chat);
                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserStatus(String state) {
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
                .setValue(currentStateMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessagesRef.child(childToChat).removeEventListener(seenListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUserStatus("offline");
    }
}


