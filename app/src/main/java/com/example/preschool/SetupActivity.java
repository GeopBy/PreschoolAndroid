package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.preschool.MainActivity;
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

public class SetupActivity extends AppCompatActivity {
    private EditText UserName, FullNameTeacher, FullNameFather, FullNameMother, PhoneNumberFather, PhoneNumberMother, PhoneNumberTeacher, address;

    private LinearLayout layoutTeacher, layoutParent;
    private Button SaveInformationbuttion;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;

    private Boolean isTeacher;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference ClassRef;
    private StorageReference UserProfileImageRef;
    private Uri resultUri;

    final static int Gallery_Pick = 1;

    String currentUserID, role;
    String myClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        ClassRef = FirebaseDatabase.getInstance().getReference().child("Class");
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        UserName = findViewById(R.id.setup_username);
        address = findViewById(R.id.setup_address);
        SaveInformationbuttion = findViewById(R.id.setup_information_button);
        ProfileImage = findViewById(R.id.setup_profile_image);

        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("role").getValue(String.class).equals("Teacher")) {
                    layoutTeacher = findViewById(R.id.layout_teacher);
                    layoutTeacher.setVisibility(View.VISIBLE);
                    FullNameTeacher = findViewById(R.id.setup_fullnameteacher);
                    PhoneNumberTeacher = findViewById(R.id.setup_phoneteacher);
                    isTeacher = true;
                } else {
                    layoutParent = findViewById(R.id.layout_parent);
                    layoutParent.setVisibility(View.VISIBLE);
                    FullNameFather = findViewById(R.id.setup_fullnameFather);
                    PhoneNumberFather = findViewById(R.id.setup_phonenumberFather);
                    FullNameMother = findViewById(R.id.setup_fullnameMother);
                    PhoneNumberMother = findViewById(R.id.setup_phonenumberMother);
                    isTeacher = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        loadingBar = new ProgressDialog(this);

        SaveInformationbuttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTeacher) {
                    SaveInfoTeacher();
                } else SaveInfoParent();
//                SaveAccountSetupInformation();
            }
        });
        //chọn ảnh đại diện
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(SetupActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                    if(dataSnapshot.hasChild("role")){
//                        role=dataSnapshot.child("role").getValue().toString();
//                        if(role.equals("Parent")){
//                            ParentOf.setVisibility(View.VISIBLE);
//                            BirthDay.setVisibility(View.VISIBLE);
//                        }
//                        else{
//                            ParentOf.setVisibility(View.GONE);
//                            BirthDay.setVisibility(View.GONE);
//                        }
//                    }
                    if (dataSnapshot.hasChild("idclass")) {
                        myClass = dataSnapshot.child("idclass").getValue().toString();
                        //Toast.makeText(SetupActivity.this, "id la"+myClass, Toast.LENGTH_SHORT).show();
                    }
//                    if (dataSnapshot.hasChild("profileimage")) {
//                        String image = dataSnapshot.child("profileimage").getValue().toString();
//
//                        Picasso.get()
//                                .load(image)
//                                .placeholder(R.drawable.ic_person_black_50dp)
//                                .into(ProfileImage);
//
//                    } else {
////                        Toast.makeText(SetupActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
//                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SaveInfoTeacher() {
        final String username = UserName.getText().toString();
        final String fullnameteacher = FullNameTeacher.getText().toString();
        final String addr = address.getText().toString();
        final String phonenumber = PhoneNumberTeacher.getText().toString();
//        final String[] classname = new String[1];

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Chưa nhập tên người dùng", Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(fullnameteacher)) {
                Toast.makeText(this, "Chưa nhập họ tên", Toast.LENGTH_SHORT).show();
            } else {
                if (TextUtils.isEmpty(addr)) {
                    Toast.makeText(this, "Chưa nhập địa chỉ của bạn", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(phonenumber)) {
                        Toast.makeText(this, "Chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    } else {
                        loadingBar.setTitle("Đang lưu thông tin");
                        loadingBar.setMessage("vui lòng chờ cập nhật thông tin tài khoản");
                        loadingBar.show();
                        loadingBar.setCanceledOnTouchOutside(false);
                        StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                        if (resultUri != null) {
                            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String downloadUrl = uri.toString();

                                                final HashMap userMap = new HashMap();
                                                userMap.put("username", username);
                                                userMap.put("fullnameteacher", fullnameteacher);
                                                userMap.put("phonenumber", phonenumber);
                                                userMap.put("userid", currentUserID);
                                                userMap.put("address", addr);
                                                userMap.put("profileimage", downloadUrl);

                                                UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                                    public void onComplete(@NonNull Task task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SetupActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_LONG).show();
                                                            SendUserToMainActivity();
                                                        } else {
                                                            String message = task.getException().getMessage();
                                                            Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SetupActivity.this, "Bạn chưa chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                }
            }


        }
    }

    private void SaveInfoParent() {
        final String username = UserName.getText().toString();
        final String fullnamefather = FullNameFather.getText().toString();
        final String fullnamemother = FullNameMother.getText().toString();
        final String addr = address.getText().toString();
        final String phonenumber = PhoneNumberFather.getText().toString() + "-" + PhoneNumberMother.getText().toString();
        final String[] classname = new String[1];

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Chưa nhập tên người dùng", Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(fullnamefather) && TextUtils.isEmpty(fullnamemother)) {
                Toast.makeText(this, "Chưa nhập tên của phụ huynh", Toast.LENGTH_SHORT).show();
            } else {
                if (TextUtils.isEmpty(addr)) {
                    Toast.makeText(this, "Chưa nhập địa chỉ của bạn", Toast.LENGTH_SHORT).show();
                } else {
                    if (phonenumber.equals("-")) {
                        Toast.makeText(this, "Chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    } else {
                        loadingBar.setTitle("Đang lưu thông tin");
                        loadingBar.setMessage("vui lòng chờ cập nhật thông tin tài khoản");
                        loadingBar.show();
                        loadingBar.setCanceledOnTouchOutside(false);
                        StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                        if (resultUri != null) {
                            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {

//                            Toast.makeText(SetupActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String downloadUrl = uri.toString();

                                                final HashMap userMap = new HashMap();
                                                userMap.put("username", username);
                                                userMap.put("fullnamefather", fullnamefather);
                                                userMap.put("fullnamemother", fullnamemother);
                                                userMap.put("phonenumber", phonenumber);
                                                userMap.put("address", addr);
                                                userMap.put("userid", currentUserID);
                                                userMap.put("profileimage", downloadUrl);

                                                UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                                    public void onComplete(@NonNull Task task) {
                                                        if (task.isSuccessful()) {
//                                if(role.equals("Parent")){
//                                    final HashMap childrenMap=new HashMap();
////                                    childrenMap.put("parentof", parentof);
////                                    childrenMap.put("birthday", birthday);
//                                    ClassRef.child(myClass).child("Children").child(currentUserID).updateChildren(childrenMap);
//                                }

                                                            Toast.makeText(SetupActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_LONG).show();
//                                                loadingBar.dismiss();
                                                            SendUserToMainActivity();
                                                        } else {
                                                            String message = task.getException().getMessage();
                                                            Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        }
                                                    }
                                                });
//                                    UsersRef.child("profileimage").setValue(downloadUrl)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
////                                                        Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
//                                                        //finish();
//                                                    } else {
//                                                        String message = task.getException().getMessage();
//                                                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            });
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SetupActivity.this, "Bạn chưa chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
//            final HashMap userMap = new HashMap();
//            userMap.put("username", username);
//            userMap.put("fullname", fullname);
//            userMap.put("phonenumber", phonenumber);
//
//            userMap.put("userid", currentUserID);
////            if(role.equals("Parent")){
////                userMap.put("parentof", parentof);
//                userMap.put("birthday", birthday);
//            }


//            ClassRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                    UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
//
//                        public void onComplete(@NonNull Task task) {
//                            if (task.isSuccessful()) {
//                                SendUserToMainActivity();
//                                Toast.makeText(SetupActivity.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
//                                loadingBar.dismiss();
//                            } else {
//                                String message = task.getException().getMessage();
//                                Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
//                                loadingBar.dismiss();
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

                    }

                }
            }

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
//        this.finish();
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
                ProfileImage.setImageURI(resultUri);

            }
        }
    }

//    private void SaveAccountSetupInformation() {
//
//        final String username = UserName.getText().toString();
//
//        final String fullnamefather = FullNameFather.getText().toString();
//        final String fullnamemother = FullNameMother.getText().toString();
//        String addr = address.getText().toString();
//        final String phonenumber = PhoneNumberFather.getText().toString() + "-" + PhoneNumberFather.getText().toString();
//        final String[] classname = new String[1];
////        if(role.equals("Parent")){
////            if (TextUtils.isEmpty(parentof)) {
////                Toast.makeText(this, "Please write children name...", Toast.LENGTH_SHORT).show();
////            }
////            if (TextUtils.isEmpty(birthday)) {
////                Toast.makeText(this, "Please write birthday of your child...", Toast.LENGTH_SHORT).show();
////            }
////        }
//        if (TextUtils.isEmpty(username)) {
//            Toast.makeText(this, "Chưa nhập tên người dùng", Toast.LENGTH_SHORT).show();
//        }
//        if (TextUtils.isEmpty(fullnamefather) && TextUtils.isEmpty(fullnamemother)) {
//            Toast.makeText(this, "Chưa nhập tên của phụ huynh", Toast.LENGTH_SHORT).show();
//        }
//        if (phonenumber.equals("-")) {
//            Toast.makeText(this, "Chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
//        } else {
//            loadingBar.setTitle("Đang lưu thông tin");
//            loadingBar.setMessage("vui lòng chờ cập nhật thông tin tài khoản");
//            loadingBar.show();
//            loadingBar.setCanceledOnTouchOutside(false);
//            StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
//
//            if (resultUri != null) {
//                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//
////                            Toast.makeText(SetupActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
//
//                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
//
//                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    final String downloadUrl = uri.toString();
//
//                                    final HashMap userMap = new HashMap();
//                                    userMap.put("username", username);
//                                    userMap.put("fullnamefather", fullnamefather);
//                                    userMap.put("fullnamemother", fullnamemother);
//                                    userMap.put("phonenumber", phonenumber);
//                                    userMap.put("userid", currentUserID);
//                                    userMap.put("profileimage", downloadUrl);
//
//                                    UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
//                                        public void onComplete(@NonNull Task task) {
//                                            if (task.isSuccessful()) {
////                                if(role.equals("Parent")){
////                                    final HashMap childrenMap=new HashMap();
//////                                    childrenMap.put("parentof", parentof);
//////                                    childrenMap.put("birthday", birthday);
////                                    ClassRef.child(myClass).child("Children").child(currentUserID).updateChildren(childrenMap);
////                                }
//
//                                                Toast.makeText(SetupActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_LONG).show();
////                                                loadingBar.dismiss();
//                                                SendUserToMainActivity();
//                                            } else {
//                                                String message = task.getException().getMessage();
//                                                Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
//                                                loadingBar.dismiss();
//                                            }
//                                        }
//                                    });
////                                    UsersRef.child("profileimage").setValue(downloadUrl)
////                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                @Override
////                                                public void onComplete(@NonNull Task<Void> task) {
////                                                    if (task.isSuccessful()) {
//////                                                        Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
////                                                        //finish();
////                                                    } else {
////                                                        String message = task.getException().getMessage();
////                                                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
////                                                    }
////                                                }
////                                            });
//                                }
//                            });
//                        }
//                    }
//                });
//            }
////            final HashMap userMap = new HashMap();
////            userMap.put("username", username);
////            userMap.put("fullname", fullname);
////            userMap.put("phonenumber", phonenumber);
////
////            userMap.put("userid", currentUserID);
//////            if(role.equals("Parent")){
//////                userMap.put("parentof", parentof);
////                userMap.put("birthday", birthday);
////            }
//
//
////            ClassRef.addValueEventListener(new ValueEventListener() {
////                @Override
////                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////
////
////                    UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
////
////                        public void onComplete(@NonNull Task task) {
////                            if (task.isSuccessful()) {
////                                SendUserToMainActivity();
////                                Toast.makeText(SetupActivity.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
////                                loadingBar.dismiss();
////                            } else {
////                                String message = task.getException().getMessage();
////                                Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
////                                loadingBar.dismiss();
////                            }
////                        }
////                    });
////                }
////
////                @Override
////                public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                }
////            });
//
//        }
//    }

    private void SendUserToMainActivity() {
        //////////////////////////////////////////////////////
        String currentUserID = mAuth.getCurrentUser().getUid();
        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference ClassRef = FirebaseDatabase.getInstance().getReference().child("Class");
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String idClass = dataSnapshot.child("idclass").getValue().toString();
                ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String idTeacher = dataSnapshot.child(idClass).child("teacher").getValue().toString();
                        String className = dataSnapshot.child(idClass).child("classname").getValue().toString();
                        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("ID_CLASS", idClass);
                        bundle.putString("CLASS_NAME", className);
                        bundle.putString("ID_TEACHER", idTeacher);
                        mainIntent.putExtras(bundle);
                        finish();
                        //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
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
        Intent intent = new Intent(SetupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
