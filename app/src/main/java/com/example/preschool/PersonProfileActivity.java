package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.appcenter.ingestion.Ingestion;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.MissingFormatArgumentException;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, parentFullName, className, father, mother, childrenName, birthday, phoneNumber, gender, address;
    private TextView phoneNumberTeacher;
    private LinearLayout layoutParent;
    private CircleImageView userProfileImage;
    private DatabaseReference UsersRef;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String idClass, idTeacher, visitUserId;
    private ProgressDialog loadingBar;

    private Bundle bundle;

    private Boolean isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thông tin tài khoản");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bundle = getIntent().getExtras();
        if (bundle != null) {
            visitUserId = bundle.getString("VISIT_USER_ID");
            idClass = bundle.getString("ID_CLASS");
            idTeacher=bundle.getString("ID_TEACHER");
        }

//        mAuth = FirebaseAuth.getInstance();
//        firebaseUser = mAuth.getCurrentUser();
//        current_user_id = firebaseUser.getUid();
        if (visitUserId.equals(idTeacher)) {
            isTeacher = true;
        }
//        mAuth = FirebaseAuth.getInstance();
//        current_user_id = mAuth.getCurrentUser().getUid();




        // Khai báo các thành phần giao diện
        addControlls();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(visitUserId);


        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {

                        //get default values
                        String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                        String myUserName = dataSnapshot.child("username").getValue().toString();
                        String myAddress = dataSnapshot.child("address").getValue().toString();
                        String myClass = dataSnapshot.child("classname").getValue().toString();
                        String myphoneNumber = dataSnapshot.child("phonenumber").getValue().toString();
                        String fullName;

                        //set default values
                        Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfileImage);
                        userName.setText(myUserName);
                        className.setText("Lớp học: "+myClass);
                        address.setText("Địa chỉ: "+myAddress);

                        if (isTeacher) {
                            phoneNumberTeacher.setVisibility(View.VISIBLE);
                            phoneNumberTeacher.setText("Sdt: "+myphoneNumber);

                            fullName=dataSnapshot.child("fullnameteacher").getValue(String.class);
                            parentFullName.setText(fullName);
                        } else {
                            String fatherName=dataSnapshot.child("fullnamefather").getValue(String.class);
                            String motherName=dataSnapshot.child("fullnamemother").getValue(String.class);
                            String kidName=dataSnapshot.child("mychildren").child(idClass).child("name").getValue(String.class);
                            String birthDay=dataSnapshot.child("mychildren").child(idClass).child("birthday").getValue(String.class);
                            String sex=dataSnapshot.child("mychildren").child(idClass).child("sex").getValue(String.class);
                            phoneNumber.setText("Sdt: "+myphoneNumber);
                            try{
                                parentFullName.setText(fatherName);
                                father.setText("Cha: "+fatherName);
                                mother.setText("Mẹ: "+motherName);
                            }catch (Exception e){

                            }

                            childrenName.setText("Tên bé: "+kidName);
                            birthday.setText("Ngày sinh bé: "+birthDay);
                            gender.setText("Giới tính: "+sex);
                        }

                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addControlls() {
        userProfileImage = findViewById(R.id.person_profile_pic);
        parentFullName = findViewById(R.id.person_full_name);
        className = findViewById(R.id.class_name);
        userName = findViewById(R.id.person_username);
        address = findViewById(R.id.address);
        if (isTeacher) {
            phoneNumberTeacher = findViewById(R.id.phonenumberteacher);
            phoneNumberTeacher.setVisibility(View.VISIBLE);
        } else {
            layoutParent = findViewById(R.id.layout_parent);
            layoutParent.setVisibility(View.VISIBLE);

            father = findViewById(R.id.father);
            mother = findViewById(R.id.mother);
            childrenName = findViewById(R.id.kidname);
            birthday = findViewById(R.id.person_birthday);
            phoneNumber = findViewById(R.id.phonenumber);
            gender = findViewById(R.id.sex);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        UsersRef.child(current_user_id).removeEventListener(UserListener);
    }
}
