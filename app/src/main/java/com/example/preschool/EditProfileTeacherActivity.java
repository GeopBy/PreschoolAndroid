package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileTeacherActivity extends AppCompatActivity {

    private EditText userName, userFullName, userPhoneNumber, userAddress;
    private Button UpdateAccountSettingButton;
    private CircleImageView userProfImage;
    private ProgressDialog loadingBar;

    private DatabaseReference EditUserRef, ClassRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;
    private Uri resultUri;
    private String idClass;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_teacher);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
        }
//
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        EditUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        ClassRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass);

        userProfImage = findViewById(R.id.edit_profile_image);
        userName = findViewById(R.id.edit_username);
        userFullName = findViewById(R.id.edit_fullname);
        userPhoneNumber = findViewById(R.id.sdt);
        userAddress = findViewById(R.id.address);
        UpdateAccountSettingButton = findViewById(R.id.update_account_settings_button);
        loadingBar = new ProgressDialog(this);
//
        EditUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                try {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfImage);
                } catch (Exception e) {
//                    Picasso.get().load(R.drawable.ic_person_black_50dp).into(userProfImage);
                }

                if (dataSnapshot.hasChild("fullnameteacher")) {
                    String myProfileName = dataSnapshot.child("fullnameteacher").getValue().toString();
                    userFullName.setText(myProfileName);
                }
                if (dataSnapshot.hasChild("username")) {
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    userName.setText(myUserName);
                }
                if (dataSnapshot.hasChild("phonenumber")) {
                    String myphone = dataSnapshot.child("phonenumber").getValue().toString();
                    userPhoneNumber.setText(myphone);
                }
                if (dataSnapshot.hasChild("address")) {
                    String myphone = dataSnapshot.child("address").getValue().toString();
                    userAddress.setText(myphone);
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
        String userfullname = userFullName.getText().toString();
        String userphonenumber = userPhoneNumber.getText().toString();
        String useraddress = userAddress.getText().toString();

        if (TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(userfullname) ||
                TextUtils.isEmpty(userphonenumber) ||
                TextUtils.isEmpty(useraddress)) {
            Toast.makeText(EditProfileTeacherActivity.this,"Chưa nhập đủ thông tin",Toast.LENGTH_LONG).show();
        }else UpdateAccountInfo(username, userfullname, userphonenumber, useraddress);

//        loadingBar.dismiss();
    }

    private void UpdateAccountInfo(final String username, final String userfullname, final String userphonenumber, final String useraddress) {

        loadingBar.setTitle("Cập nhật tài khoản");
        loadingBar.setMessage("Vui lòng chờ cập nhật thông tin");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("fullnameteacher", userfullname);
        userMap.put("phonenumber", userphonenumber);
        userMap.put("address",useraddress);

        final StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");
        if (resultUri != null) {
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(EditProfileTeacherActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                userMap.put("profileimage",uri.toString());


                                EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditProfileTeacherActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(EditProfileTeacherActivity.this, "Error", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        } else {
            EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileTeacherActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileTeacherActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

}



