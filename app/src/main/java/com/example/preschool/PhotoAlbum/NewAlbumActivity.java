package com.example.preschool.PhotoAlbum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewAlbumActivity extends AppCompatActivity {

    private static final int PICK_IMG = 1;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private EditText mEditTextAlbumName;
    private ImageView mImageView;
    private TextView addPhotos;
    private ProgressBar mProgressBar;
    private ProgressDialog loadingBar;
    private String date;
    private Uri mImageUri;
    private int uploads = 0;

    private ArrayList<Uri> uriList = new ArrayList<Uri>();
    private ArrayList<String> imageUrlList = new ArrayList<String>();

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;
    ///////////////////////////////////
    private String idClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_album);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tạo album mới");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Khai bao controls
        mButtonChooseImage = findViewById(R.id.select_photos);
        mButtonUpload = findViewById(R.id.upload_image);
        mEditTextAlbumName = findViewById(R.id.add_title_album);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        addPhotos = findViewById(R.id.add_photos);
        loadingBar=new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("dd-MM-yyyy");
        date = currentTime.format(calendar.getTime());

        /**
         * quăng id class vô chổ này classtest1
         *
         */
        ////////////////////////////////////////////////////
        idClass=getIntent().getExtras().get("idClass").toString();
        mStorageRef = FirebaseStorage.getInstance().getReference().child(idClass).child("Albums");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Albums");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriList.clear();
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(NewAlbumActivity.this, "Image in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if(!mEditTextAlbumName.getText().toString().equals("")){
                        if(uriList.size()!=0){
                            uploadFile();
                        }
                        else Toast.makeText(NewAlbumActivity.this,"Bạn chưa chọn ảnh",Toast.LENGTH_SHORT).show();

                    }else Toast.makeText(NewAlbumActivity.this,"Bạn chưa nhập tên Album",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void openFileChooser() {
        //we will pick images
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);// Buộc phải chọn 2 hình trở lên
        startActivityForResult(intent, PICK_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        uriList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }
                    addPhotos.setText("Bạn đã chọn  " + uriList.size() + " ảnh");
                    mImageUri = data.getClipData().getItemAt(0).getUri();
                    Picasso.get().load(mImageUri).into(mImageView);

                }

            }else Toast.makeText(NewAlbumActivity.this,"Bạn phải chọn từ 2 ảnh trở lên",Toast.LENGTH_SHORT).show();

        }else Toast.makeText(NewAlbumActivity.this,"Bạn phải chọn từ 2 ảnh trở lên 1",Toast.LENGTH_SHORT).show();


    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void getDate(){

    }

    public void uploadFile() {
        loadingBar.setTitle("Tải lên");
        loadingBar.setMessage("Đang tải ảnh lên vui lòng chờ");
        loadingBar.setCanceledOnTouchOutside(false);

        final Album album = new Album();
        if (uriList != null) {
            loadingBar.show();
            for (int i = 0; i < uriList.size(); i++) {

                StorageReference fileReference = mStorageRef.child(mEditTextAlbumName.getText().toString()).child(System.currentTimeMillis() + "." +
                        getFileExtension(uriList.get(i)));

                mUploadTask = fileReference.putFile(uriList.get(i))

                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            private static final String TAG = "ViewAllAlbumActivity";

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setProgress(0);
                                    }
                                }, 500);
//                                Toast.makeText(NewAlbumActivity.this, "Image Successful", Toast.LENGTH_LONG).show();
//                                addPhotos.setText("Uploaded " + i/uriList.size() + " Pictures");
//***************************************************************************************************************
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();

                                Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString());

                                imageUrlList.add(downloadUrl.toString());

                                album.setDate(date);
                                album.setName(mEditTextAlbumName.getText().toString());
                                album.setImageUrlList(imageUrlList);
                                if (imageUrlList.size()==uriList.size()) {
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(album).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            imageUrlList.clear();
                                            finish();
                                        }
                                    });

                                }

//**************************************************************************************************************
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewAlbumActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                                mProgressBar.setProgress((int) progress);
                            }
                        });

            }

        } else {
            loadingBar.dismiss();
            Toast.makeText(this, "NO File Selected", Toast.LENGTH_SHORT).show();
        }
    }
//    private void openImagesActivity() {
//        Intent intent = new Intent(this, ImagesActivity.class);
//        startActivity(intent);
//    }
}
