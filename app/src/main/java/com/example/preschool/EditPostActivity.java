package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.Event.AddEventActivity;
import com.example.preschool.TimeLine.Posts;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EditPostActivity extends AppCompatActivity {

    private TextView userName;
    private EditText description;
    private ImageView avatar;
    private ViewPager postImages;
    private String currentUserId;
    private DatabaseReference PostRef,UserRef;
    private StorageReference PostsImagesRefrence;
    private ValueEventListener postEventListener,userEventListener;
    private String postKey,idClass;
    private Button btnSave;
    private Uri ImageUri;
    private Bitmap bitmap;
    private Boolean hasChangeImage=false;
    private static final int Gallery_Pick = 1;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadingBar = new ProgressDialog(this);

        userName=findViewById(R.id.post_user_name);
        description=findViewById(R.id.post_description);
        postImages=findViewById(R.id.post_image);
        avatar=findViewById(R.id.post_profile_image);
        btnSave=findViewById(R.id.btn_save_post);

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        postKey=getIntent().getStringExtra("POST_KEY");
        idClass=getIntent().getStringExtra("ID_CLASS");


        PostsImagesRefrence = FirebaseStorage.getInstance().getReference().child(idClass);
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        PostRef= FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Posts").child(postKey);

        loadData();

        postImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                OpenGallery();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEditPost();

            }
        });

    }
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
//            ImageUri = data.getData();
//            Picasso.get().load(ImageUri).resize(1440,0).into(postImages);
//            hasChangeImage=true;
////            SelectPostImage.setImageURI(ImageUri);
//        }
    }
    private void uploadImageToFirebase(){

        //Image khi load lên ImageButton đã được resize bằng Picasso
        // nên upload bitmap từ ImageButton giúp giảm dung lượng ảnh
//        bitmap=((BitmapDrawable) postImages.getDrawable()).getBitmap();
//        ByteArrayOutputStream bao = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bao);
//        byte[] data = bao.toByteArray();
//
//
//        StorageReference filePath = PostsImagesRefrence.child("Post Images").child(postKey + ".jpg");
//        filePath.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if (task.isSuccessful()) {
//                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
//                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            final String downloadUrl = uri.toString();
//                            //lưu hình ảnh lên posts
//                            PostRef.child("postimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        loadingBar.dismiss();
//                                        finish();
//                                    } else {
//                                        loadingBar.dismiss();
//                                        Toast.makeText(EditPostActivity.this, "Không thể đổi ảnh", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                    });
//
//
//                } else {
//                    String message = task.getException().getMessage();
//                    Toast.makeText(EditPostActivity.this, "Không tải ảnh lên được:" + message, Toast.LENGTH_SHORT).show();
//                    loadingBar.cancel();
//                }
//            }
//        });


    }

    private void saveEditPost() {
        PostRef.child("description").setValue(description.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(hasChangeImage){
                    loadingBar.setMessage("Đang tải ảnh lên");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(false);
//                    uploadImageToFirebase();

                }else finish();
            }
        });
    }

    private void loadData() {
        // Load avatar and username
        userEventListener=UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.child("fullnameteacher").getValue().toString());
                Picasso.get()
                        .load(dataSnapshot.child("profileimage").getValue().toString())
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .resize(70,0)
                        .into(avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Load decription and image
        postEventListener=PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Posts posts=dataSnapshot.getValue(Posts.class);
                description.setText(posts.getDescription());
                AdapterImagePost adapter = new AdapterImagePost(EditPostActivity.this, posts.getPostimage());
                postImages.setAdapter(adapter);
//                description.setText(dataSnapshot.child("description").getValue().toString());
//                // Show images
//                AdapterImagePost adapter = new AdapterImagePost(this, dataSnapshot.child("postimage").getValue(ArrayList.class));
//                postImages.setAdapter(adapter);
//                Picasso.get()
//                        .load(dataSnapshot.child("postimage").getValue().toString())
//                        .networkPolicy(NetworkPolicy.NO_CACHE)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE)
//                        .resize(600,0)
//                        .into(postImages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserRef.removeEventListener(userEventListener);
        PostRef.removeEventListener(postEventListener);
    }
}
