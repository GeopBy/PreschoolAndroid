package com.example.preschool.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.preschool.Children.Children;
import com.example.preschool.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAccountActivity extends AppCompatActivity {
    private TextView userPhoneTeacher, userName, userProfName, userRole, userEmail, userAddress;
    private TextView userFather, userMother,userPhoneParent;
    private TextView userClass;
    private LinearLayout layoutParent;
    private RecyclerView recyclerView;
    private CircleImageView userProfileImage;

    private Button EditButton;

    private DatabaseReference UsersRef, ClassRef;
    private FirebaseAuth mAuth;
    private String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        userid = getIntent().getStringExtra("USER_ID");
        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        ClassRef = FirebaseDatabase.getInstance().getReference("Class");

        userProfileImage = findViewById(R.id.person_profile_pic);
        userName = findViewById(R.id.person_username);
        userEmail = findViewById(R.id.email);
        userProfName = findViewById(R.id.person_full_name);
        userClass = findViewById(R.id.class_name);
        userRole = findViewById(R.id.role);
        userPhoneTeacher = findViewById(R.id.phoneteacher);
        userPhoneParent=findViewById(R.id.phonenumber);
        userAddress = findViewById(R.id.address);
        EditButton = findViewById(R.id.edit_account);

        userFather = findViewById(R.id.father);
        userMother = findViewById(R.id.mother);

        layoutParent = findViewById(R.id.layout_parent);

        recyclerView = findViewById(R.id.recycler_info_kid);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        //show info user

        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("role")) {
                    //show on recyclerview
                    String myRole = dataSnapshot.child("role").getValue().toString();
                    if (myRole.equals("Teacher")) {
                        userClass.setVisibility(View.VISIBLE);
                        try{
                            userProfName.setText(dataSnapshot.child("fullnameteacher").getValue().toString());
                        }catch (Exception e){userProfName.setText("Họ tên giáo viên");}
                        try{
                            userName.setText(dataSnapshot.child("username").getValue().toString());
                        }catch (Exception e){userName.setText("Username Giáo viên");}

                        String myClass = dataSnapshot.child("classname").getValue().toString();
                        userClass.setText("Lớp: " + myClass);

                        userPhoneTeacher.setVisibility(View.VISIBLE);
                        userPhoneTeacher.setText("Sdt: "+dataSnapshot.child("phonenumber").getValue().toString());

                        userRole.setText("Quyền: Giáo viên");
                    } else {
                        if (myRole.equals("Parent")) {
                            layoutParent.setVisibility(View.VISIBLE);
                            try{
                                userProfName.setText(dataSnapshot.child("username").getValue().toString());
                            }catch (Exception e){}
                            userName.setVisibility(View.GONE);
                            try {
                                userFather.setText("Tên cha: "+dataSnapshot.child("fullnamefather").getValue().toString());
                            } catch (Exception e) {
                            }
                            try {
                                userMother.setText("Tên mẹ: "+dataSnapshot.child("fullnamemother").getValue().toString());
                            } catch (Exception e) {
                            }
                            try {
                                userPhoneParent.setText("Sdt: "+dataSnapshot.child("phonenumber").getValue().toString());
                            } catch (Exception e) {
                            }
                            userRole.setText("Quyền: Phụ huynh");

                            try {
                                Query mychildrenQuery = UsersRef.child("mychildren");
                                final FirebaseRecyclerOptions<Children> options = new FirebaseRecyclerOptions.Builder<Children>().setQuery(mychildrenQuery, Children.class).build();
                                FirebaseRecyclerAdapter adapter;
                                adapter = new FirebaseRecyclerAdapter<Children, ViewAccountActivity.ChildrenViewHolder>(options) {

                                    @NonNull
                                    @Override
                                    public ChildrenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                        View view;
                                        // create a new view

                                        view = LayoutInflater.from(parent.getContext())
                                                .inflate(R.layout.info_kid_items, parent, false);
                                        return new ViewAccountActivity.ChildrenViewHolder(view);
                                    }

                                    @Override
                                    protected void onBindViewHolder(@NonNull final ChildrenViewHolder childrenViewHolder, int i, @NonNull Children children) {
                                        String classKey = getRef(i).getKey();
                                        final String[] className = new String[1];
                                        ClassRef.child(classKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                childrenViewHolder.txtClass.setText("Lớp: " + dataSnapshot.child("classname").getValue(String.class));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        childrenViewHolder.txtName.setText("Bé: " + children.getName());
                                        childrenViewHolder.txtBirthday.setText("Sinh nhật: " + children.getBirthday());
                                        childrenViewHolder.txtGender.setText("Giới tính: " + children.getSex());
                                    }
                                };
                                adapter.startListening();
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                            } catch (Exception e) {
                            }

                        }else {
                            userRole.setText("Quyền: Quản trị viên");
                            if(dataSnapshot.hasChild("username")){
                                userName.setText(dataSnapshot.child("username").getValue().toString());
                            }
                            if(dataSnapshot.hasChild("fullnameadmin")){
                                userProfName.setText(dataSnapshot.child("fullnameadmin").getValue().toString());

                            }
                            if(dataSnapshot.hasChild("phonenumber")){
                                userPhoneTeacher.setText(dataSnapshot.child("phonenumber").getValue().toString());

                            }

                            userPhoneTeacher.setVisibility(View.VISIBLE);
                        }
                    }

                }


                try{
                    userEmail.setText(dataSnapshot.child("email").getValue().toString());
                }catch (Exception e){

                }
                try{
                    userAddress.setText("Địa chỉ: "+dataSnapshot.child("address").getValue().toString());
                }catch (Exception e){}
                try{
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfileImage);
                }catch (Exception e){}


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAccountActivity.this, EditAccountActivity.class);
                intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewAccountActivity.this, ManageUserActivity.class);
        startActivity(intent);
    }


    public class ChildrenViewHolder extends RecyclerView.ViewHolder {

        TextView txtClass, txtName, txtBirthday, txtGender;

        public ChildrenViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClass = itemView.findViewById(R.id.person_class);
            txtName = itemView.findViewById(R.id.relationship_with_children);
            txtBirthday = itemView.findViewById(R.id.person_birthday);
            txtGender = itemView.findViewById(R.id.person_gender);
        }
    }
}
