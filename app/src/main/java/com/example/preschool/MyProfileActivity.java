package com.example.preschool;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {

    private TextView userName, parentFullName, className, father, mother, childrenName, birthday, phoneNumber, gender, address;
    private TextView phoneNumberTeacher;
    private LinearLayout layoutParent;
    private CircleImageView userProfileImage;
    private DatabaseReference UsersRef;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String current_user_id, idClass, idTeacher;
    private ProgressDialog loadingBar;

    private Bundle bundle;

    private Boolean isTeacher = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher = bundle.getString("ID_TEACHER");
        }


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        current_user_id = firebaseUser.getUid();
        if (current_user_id.equals(idTeacher)) {
            isTeacher = true;
        }
        addControlls();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit_profile: {

                Intent intent;
                if(isTeacher){
                    intent = new Intent(MyProfileActivity.this, EditProfileTeacherActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    intent = new Intent(MyProfileActivity.this, EditProfileParentActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
//                Intent intent = new Intent(MyProfileActivity.this, EditProfileParentActivity.class);

                break;
            }

            case R.id.action_change_pass:
                changePassWord();
                break;
        }
        return true;
    }

    private void changePassWord() {
        final Dialog dialogChangePassWord = new Dialog(MyProfileActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        dialogChangePassWord.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangePassWord.setContentView(R.layout.dialog_change_password);
        dialogChangePassWord.setCancelable(true);
        dialogChangePassWord.show();

        // Controlls
        final EditText txtOldPass = dialogChangePassWord.findViewById(R.id.old_pass);
        final EditText txtNewPass = dialogChangePassWord.findViewById(R.id.new_pass);
        final EditText txtReNewPass = dialogChangePassWord.findViewById(R.id.renew_pass);
        Button btnChangePass = dialogChangePassWord.findViewById(R.id.btn_change_pass);


        // Verify Password
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String oldPass = txtOldPass.getText().toString();
                String newPass = txtNewPass.getText().toString();
                String reNewPass = txtReNewPass.getText().toString();

                passWordVerify(oldPass, newPass, reNewPass);

            }

            private void passWordVerify(String oldPass, final String newPass, final String reNewPass) {
                if (oldPass.length() >= 6) {
                    // Test login to check right Password
                    mAuth.signInWithEmailAndPassword(getMyEmail(), oldPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (newPass.length() >= 6) {
                                    if (newPass.equals(reNewPass)) {
                                        loadingBar = new ProgressDialog(MyProfileActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                                        loadingBar.setTitle("Đổi Mật Khẩu");
                                        loadingBar.setMessage("Xin chờ quá trình đổi mật khẩu thành công");
                                        loadingBar.setCanceledOnTouchOutside(false);
                                        loadingBar.show();
                                        mAuth.getCurrentUser().updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dialogChangePassWord.dismiss();
                                                    loadingBar.dismiss();
                                                    Toast.makeText(MyProfileActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    dialogChangePassWord.dismiss();
                                                    loadingBar.dismiss();
                                                    Toast.makeText(MyProfileActivity.this, "Không thể đổi mật khẩu", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else
                                        Toast.makeText(MyProfileActivity.this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(MyProfileActivity.this, "Mật khẩu phải hơn 6 ký tự", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(MyProfileActivity.this, "Sai Mật khẩu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else
                    Toast.makeText(MyProfileActivity.this, "Mật khẩu phải hơn 6 ký tự", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getMyEmail() {
        return firebaseUser.getEmail();
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

}
