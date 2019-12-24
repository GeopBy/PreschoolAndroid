package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class PostActivity extends AppCompatActivity {
    private ProgressDialog loadingBar;

    private ImageView SelectPostImage, img1, img2, img3, img4;
    private Button PostButton;
    private EditText PostDescription;
    private Uri ImageUri;
    private String Description;
    private ArrayList<String> arrayListImage;
    private static final int Gallery_Pick = 1, GALLERY = 1;

    private StorageReference PostsImagesRefrence;
    private DatabaseReference UsersRef, PostsRef;
    private ValueEventListener usersListener, postListener;
    private FirebaseAuth mAuth;

    private String idClass;

    private String saveCurrentDate, saveCurrentTime, downloadUrl, current_user_id;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Get Bundle
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm Bảng Tin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostsImagesRefrence = FirebaseStorage.getInstance().getReference().child(idClass);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Posts");


        SelectPostImage = findViewById(R.id.select_post_image);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        PostButton = findViewById(R.id.post_button);
        PostDescription = findViewById(R.id.post_description);
        loadingBar = new ProgressDialog(this);

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        PostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });


    }

    private void OpenGallery() {
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(galleryIntent, GALLERY);

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            Picasso.get().load(ImageUri).resize(1440, 0).into(SelectPostImage);
            if (img1.getDrawable() == null) {
                Picasso.get().load(ImageUri).resize(1440, 0).into(img1);
            } else {
                if (img2.getDrawable() == null) {
                    Picasso.get().load(ImageUri).resize(1440, 0).into(img2);
                } else {
                    if (img3.getDrawable() == null) {
                        Picasso.get().load(ImageUri).resize(1440, 0).into(img3);
                    } else Picasso.get().load(ImageUri).resize(1440, 0).into(img4);
                }
            }

//            SelectPostImage.setImageURI(ImageUri);
        }
    }

    private void ValidatePostInfo() {
        Description = PostDescription.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(PostActivity.this, "Hãy chọn hình ảnh", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(PostActivity.this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Tạo Bài Đăng");
            loadingBar.setMessage("Vui lòng chờ trong giây lát");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            createPost();
        }
    }

//    private int countImage(){
//
//
//    }

    private void createPost() {
//        data=new byte[];

        arrayListImage = new ArrayList<>();
        final ByteArrayOutputStream bao = new ByteArrayOutputStream();

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        final String childString = PostsRef.push().getKey();

        Bitmap bitmap1 = ((BitmapDrawable) img1.getDrawable()).getBitmap();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 70, bao);
        final byte[] data1;
        data1 = bao.toByteArray();
        final StorageReference filePath = PostsImagesRefrence.child("Post Images").child(childString);

        filePath.child(childString + "1" + ".jpg").putBytes(data1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    data1.clone();
                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            arrayListImage.add(downloadUrl);
                            try {
                                Bitmap bitmap2 = ((BitmapDrawable) img2.getDrawable()).getBitmap();
                                ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
                                bitmap2.compress(Bitmap.CompressFormat.JPEG, 70, bao2);
                                final byte[] data2;
                                data2 = bao2.toByteArray();

                                filePath.child(childString + "2" + ".jpg").putBytes(data2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            data2.clone();
                                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadUrl = uri.toString();
                                                    arrayListImage.add(downloadUrl);
                                                    try {
                                                        Bitmap bitmap3 = ((BitmapDrawable) img3.getDrawable()).getBitmap();
                                                        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
                                                        bitmap3.compress(Bitmap.CompressFormat.JPEG, 70, bao3);
                                                        final byte[] data3;
                                                        data3 = bao3.toByteArray();

                                                        filePath.child(childString + "3" + ".jpg").putBytes(data3).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    data3.clone();
                                                                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {
                                                                            String downloadUrl = uri.toString();
                                                                            arrayListImage.add(downloadUrl);
                                                                            try {
                                                                                Bitmap bitmap4 = ((BitmapDrawable) img4.getDrawable()).getBitmap();
                                                                                ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
                                                                                bitmap4.compress(Bitmap.CompressFormat.JPEG, 70, bao4);
                                                                                final byte[] data4;
                                                                                data4 = bao4.toByteArray();

                                                                                filePath.child(childString + "4" + ".jpg").putBytes(data4).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            data4.clone();
                                                                                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                                                                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                                @Override
                                                                                                public void onSuccess(Uri uri) {
                                                                                                    String downloadUrl = uri.toString();
                                                                                                    arrayListImage.add(downloadUrl);
                                                                                                    putPostToFireBase(childString);
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                });

                                                                            } catch (Exception e) {
                                                                                putPostToFireBase(childString);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });

                                                    } catch (Exception e) {
                                                        putPostToFireBase(childString);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                putPostToFireBase(childString);
                            }

                        }
                    });

                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        for(byte[] temp:arrayListData){
//            StorageReference filePath = PostsImagesRefrence.child("Post Images").child(childString).child(childString + + ".jpg");
//            filePath.putBytes(temp).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
//                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                final String downloadUrl = uri.toString();
//                                arrayListImage.add(downloadUrl);
//                                PostsRef.child(childString).child("postimage").setValue(arrayListImage).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            Toast.makeText(PostActivity.this,"Tải ảnh lên thành công",Toast.LENGTH_SHORT).show();
//                                        }
//                                        else Toast.makeText(PostActivity.this,"Không thể tải ảnh lên",Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        });
//
//                    } else {
//                        String message = task.getException().getMessage();
//                        Toast.makeText(PostActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }


        // Upload tối đa 4 ảnh cho 1 Post
//        for (int i = 0; i < 4; i++) {
//            if (i == 0) {
//                bitmap = ((BitmapDrawable) img1.getDrawable()).getBitmap();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bao);
//                byte[] data = bao.toByteArray();
//
//                uploadImage(data, i, childString);
//            } else {
//                if (i == 1) {
//                    try {
//                        bitmap = ((BitmapDrawable) img2.getDrawable()).getBitmap();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bao);
//                        byte[] data = bao.toByteArray();
//
//                        uploadImage(data, i, childString);
//                    } catch (Exception e) {
//                        SendUserToNewsFeedActivity();
//                        break;
//                    }
//                } else {
//                    if (i == 2) {
//                        try {
//                            bitmap = ((BitmapDrawable) img3.getDrawable()).getBitmap();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bao);
//                            byte[] data = bao.toByteArray();
//
//                            uploadImage(data, i, childString);
//                        } catch (Exception e) {
//                            SendUserToNewsFeedActivity();
//                            break;
//                        }
//
//                    }else {
//                        try {
//                            bitmap = ((BitmapDrawable) img4.getDrawable()).getBitmap();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bao);
//                            byte[] data = bao.toByteArray();
//
//                            uploadImage(data, i, childString);
//                            SendUserToNewsFeedActivity();
//                        } catch (Exception e) {
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        putPostToFireBase(childString);
    }

    private void putPostToFireBase(final String childString) {
        usersListener = UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("postimage", arrayListImage);

                    PostsRef.child(childString).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PostActivity.this, "Tạo bài đăng thành công", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        SendUserToNewsFeedActivity();
                                    } else {
                                        Toast.makeText(PostActivity.this, "Lỗi rồi.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        SendUserToNewsFeedActivity();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void uploadImage(byte[] data, int i, final String childString) {
//        StorageReference filePath = PostsImagesRefrence.child("Post Images").child(childString).child(childString + i + ".jpg");
//        filePath.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if (task.isSuccessful()) {
//                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
//                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            final String downloadUrl = uri.toString();
//                            arrayListImage.add(downloadUrl);
//                            PostsRef.child(childString).child("postimage").setValue(arrayListImage).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        Toast.makeText(PostActivity.this,"Tải ảnh lên thành công",Toast.LENGTH_SHORT).show();
//                                    }
//                                    else Toast.makeText(PostActivity.this,"Không thể tải ảnh lên",Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    });
//
//                } else {
//                    String message = task.getException().getMessage();
//                    Toast.makeText(PostActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }

    private void SendUserToNewsFeedActivity() {
        finish();
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
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        UsersRef.removeEventListener(usersListener);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//    }


}
