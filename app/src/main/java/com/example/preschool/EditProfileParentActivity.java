package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

public class EditProfileParentActivity extends AppCompatActivity {
    private EditText userName, userFatherName, userMotherName, userDOB, userParentOf, userPhoneNumber, userGender, userAddress;
    private RadioButton buttonMale, buttonFemale;
    private Button UpdateAccountSettingButton;
    private CircleImageView userProfImage;
    private ProgressDialog loadingBar;

    private DatabaseReference EditUserRef, ClassRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;
    private Uri resultUri;
    private String idClass, role;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
        }

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        EditUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        ClassRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass);

        userProfImage = findViewById(R.id.edit_profile_image);
        userName = findViewById(R.id.edit_username);
        userFatherName = findViewById(R.id.fathername);
        userMotherName = findViewById(R.id.mothername);

        userDOB = findViewById(R.id.edit_birthday);
//        userDOB.setEnabled(false);
        userParentOf = findViewById(R.id.edit_parentof);
        buttonMale = findViewById(R.id.radioButton_male);
        buttonFemale = findViewById(R.id.radioButton_female);
//        userGender = findViewById(R.id.sex);
        userPhoneNumber = findViewById(R.id.edit_phonenumber);
        userAddress = findViewById(R.id.address);

        UpdateAccountSettingButton = findViewById(R.id.update_account_settings_button);
        loadingBar = new ProgressDialog(this);

        //pick birthday
        userDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener mDatePickerDialog;
                mDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date;
                        if (month < 10) {
                            if (dayOfMonth < 10) {
                                date = "0" + dayOfMonth + "/0" + month + "/" + year;
                            } else date = dayOfMonth + "/0" + month + "/" + year;
                        } else {
                            if (dayOfMonth < 10) {
                                date = "0" + dayOfMonth + "/0" + month + "/" + year;
                            } else date = dayOfMonth + "/" + month + "/" + year;
                        }
                        userDOB.setText(date);
                    }
                };

                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                DatePickerDialog dialog = new DatePickerDialog(
                        EditProfileParentActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDatePickerDialog,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        EditUserRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild("role")) {
//                    role = dataSnapshot.child("role").getValue().toString();
//                    if (role.equals("Parent")) {
//                        userParentOf.setVisibility(View.VISIBLE);
//                        userDOB.setVisibility(View.VISIBLE);
//                        txtBirthday.setVisibility(View.VISIBLE);
//                        txtChildren.setVisibility(View.VISIBLE);
//                    } else {
//                        userParentOf.setVisibility(View.GONE);
//                        userDOB.setVisibility(View.GONE);
//                        txtBirthday.setVisibility(View.GONE);
//                        txtChildren.setVisibility(View.GONE);
//                    }
//                }
                try {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfImage);
                } catch (Exception e) {
//                    Picasso.get().load(R.drawable.ic_person_black_50dp).into(userProfImage);
                }


                if (dataSnapshot.hasChild("fullnamefather")) {
                    String myFatherName = dataSnapshot.child("fullnamefather").getValue().toString();
                    userFatherName.setText(myFatherName);
                }
                if (dataSnapshot.hasChild("fullnamemother")) {
                    String myMotherName = dataSnapshot.child("fullnamemother").getValue().toString();
                    userMotherName.setText(myMotherName);
                }
                if (dataSnapshot.hasChild("username")) {
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    userName.setText(myUserName);
                }
                if (dataSnapshot.hasChild("mychildren")) {
                    String myDOB = dataSnapshot.child("mychildren").child(idClass).child("birthday").getValue().toString();
                    userDOB.setText(myDOB);
                    String myParentOf = dataSnapshot.child("mychildren").child(idClass).child("name").getValue().toString();
                    userParentOf.setText(myParentOf);
                    String myGender = dataSnapshot.child("mychildren").child(idClass).child("sex").getValue().toString();
                    if (myGender.equals("Nam")) {
                        buttonMale.setChecked(true);
                    } else buttonFemale.setChecked(true);
//                    userGender.setText(myGender);
                }
                if (dataSnapshot.hasChild("phonenumber")) {
                    String myphone = dataSnapshot.child("phonenumber").getValue().toString();
                    userPhoneNumber.setText(myphone);
                }
                if (dataSnapshot.hasChild("address")) {
                    String myAddress = dataSnapshot.child("address").getValue().toString();
                    userAddress.setText(myAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UpdateAccountSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                userProfImage.setImageURI(resultUri);
            }
        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String userfathername = userFatherName.getText().toString();
        String usermothername = userMotherName.getText().toString();
        String userdob = userDOB.getText().toString();
        String userparentof = userParentOf.getText().toString();
        String userphonenumber = userPhoneNumber.getText().toString();
        String usergender;
        if (buttonMale.isChecked()) {
            usergender = "Nam";
        } else usergender = "Nữ";
//        String usergender = userGender.getText().toString();
        String useraddress = userAddress.getText().toString();

        if (TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(userfathername) ||
                TextUtils.isEmpty(usermothername) ||
                TextUtils.isEmpty(userdob) ||
                TextUtils.isEmpty(userparentof) ||
                TextUtils.isEmpty(userphonenumber) ||
                TextUtils.isEmpty(usergender) ||
                TextUtils.isEmpty(useraddress)) {
            Toast.makeText(EditProfileParentActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_LONG).show();
        } else {
            UpdateAccountInfo(username, userfathername, usermothername, userdob, userparentof, userphonenumber, usergender, useraddress);
        }


    }

    private void UpdateAccountInfo(final String username, final String userfathername, final String usermothername,
                                   final String userdob, final String userparentof, final String userphonenumber,
                                   final String usergender, final String useraddress) {


        loadingBar.setTitle("Cập nhật tài khoản");
        loadingBar.setMessage("Vui lòng chờ trong khi cập nhật");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        //

        final HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("fullnamefather", userfathername);
        userMap.put("fullnamemother", usermothername);
        userMap.put("address", useraddress);
        userMap.put("phonenumber", userphonenumber);

        StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");
        if (resultUri != null) {
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(EditProfileParentActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                userMap.put("profileimage", downloadUrl);

                                //lưu hình ảnh lên
                                EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            HashMap childrenMap = new HashMap();
                                            childrenMap.put("birthday", userdob);
                                            childrenMap.put("name", userparentof);
                                            childrenMap.put("sex", usergender);
                                            EditUserRef.child("mychildren").child(idClass).updateChildren(childrenMap);
                                            ClassRef.child("Children").child(currentUserId).updateChildren(childrenMap);
                                            loadingBar.dismiss();
//                                            finish();
                                            //Toast.makeText(EditProfileParentActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                            //finish();
                                        } else {
                                            //String message = task.getException().getMessage();
                                            //Toast.makeText(EditProfileParentActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        } else {
            EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        HashMap childrenMap = new HashMap();
                        childrenMap.put("birthday", userdob);
                        childrenMap.put("name", userparentof);
                        childrenMap.put("sex", usergender);
                        EditUserRef.child("mychildren").child(idClass).updateChildren(childrenMap);
                        ClassRef.child("Children").child(currentUserId).updateChildren(childrenMap);
                        loadingBar.dismiss();
                        //Toast.makeText(EditProfileParentActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                        //finish();
                    } else {
                        //String message = task.getException().getMessage();
                        //Toast.makeText(EditProfileParentActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            finish();
        }
//        userMap.put("username", username);
//        userMap.put("fullname", userfullname);
//        userMap.put("phonenumber", userphonenumber);
//
//        EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
//            @Override
//            public void onComplete(@NonNull Task task) {
//                if (task.isSuccessful()) {
//                    if (role.equals("Parent")) {
//                        final HashMap childrenMap = new HashMap();
//
//                        EditUserRef.child("mychildren").child(idClass).updateChildren(childrenMap);
//                        ClassRef.child("Children").child(currentUserId).setValue(childrenMap);
//                    }
//                    Toast.makeText(EditProfileParentActivity.this, "Updated Successful", Toast.LENGTH_SHORT).show();
//                    finish();
//                } else {
//                    Toast.makeText(EditProfileParentActivity.this, "Error", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
    }

}

