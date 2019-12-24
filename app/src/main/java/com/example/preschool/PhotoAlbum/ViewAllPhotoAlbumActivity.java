package com.example.preschool.PhotoAlbum;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.preschool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewAllPhotoAlbumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseReference albumRef;
    private ValueEventListener albumEventListener;
    private Bundle bundle;
    private Album album;
    private String linkStorageRef;
    private ArrayList<String> arrayListUrl;
    private boolean isTeacher;

    private String idClass, current_user_id, idTeacher, positionAlbum;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // get bundle
        bundle=getIntent().getExtras();
        idClass=bundle.getString("ID_CLASS");
        idTeacher=bundle.getString("ID_TEACHER");
        positionAlbum=bundle.getString("POSITION_ALBUM");

        current_user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(current_user_id.equals(idTeacher))
            isTeacher=true;
        else isTeacher=false;

        albumRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Albums").child(positionAlbum);
        albumRef.keepSynced(true);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.hasFixedSize();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);


        albumEventListener= albumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                album= dataSnapshot.getValue(Album.class);
                arrayListUrl=album.getImageUrlList();
                getSupportActionBar().setTitle(album.getName());
                AdapterGripViewPhoto adapter = new AdapterGripViewPhoto(getApplicationContext(),arrayListUrl);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // button add photo
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        if(isTeacher){
            fab.setVisibility(View.INVISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add new photo
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        albumRef.removeEventListener(albumEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option_album, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_rename_album:
                if(isTeacher){
                    renameAlbum();
                }else Toast.makeText(ViewAllPhotoAlbumActivity.this,"Bạn không phải là giáo viên",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_save_on_device:
                Toast.makeText(ViewAllPhotoAlbumActivity.this,"Tính năng chưa hoàn thiện",Toast.LENGTH_SHORT).show();
//                saveOnDevice();
                break;
            case R.id.action_delete_album:
                if(isTeacher){
                    deleteAlbum();
                }else Toast.makeText(ViewAllPhotoAlbumActivity.this,"Bạn không phải là giáo viên",Toast.LENGTH_SHORT).show();
                break;


        }


        return true;
    }

    private void deleteAlbum() {
        final AlertDialog.Builder dialogDeleteAlbum=new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        dialogDeleteAlbum.setMessage("Bạn có chắc muốn xóa album này?");
        dialogDeleteAlbum.setCancelable(false);
        dialogDeleteAlbum.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete Album on CloudStorage
                StorageReference storageAlbum;
                for (int j=0;j<arrayListUrl.size();j++){
                    storageAlbum =FirebaseStorage.getInstance().getReferenceFromUrl(arrayListUrl.get(j));
                    storageAlbum.delete();
                }
                // Delete Album on FirebaseDatabase
                albumRef.removeEventListener(albumEventListener);
                albumRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
                dialogInterface.dismiss();
            }
        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogDeleteAlbum.show();

    }

    private void renameAlbum() {
        final Dialog dialogRenameAlbum=new Dialog(ViewAllPhotoAlbumActivity.this);
        dialogRenameAlbum.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRenameAlbum.setContentView(R.layout.dialog_rename_album);

        dialogRenameAlbum.setCancelable(true);

        final EditText txtNewName=dialogRenameAlbum.findViewById(R.id.txt_rename_album);
        Button btnRename=dialogRenameAlbum.findViewById(R.id.btn_rename_album);

        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtNewName.getText().toString().equals("")){
                    albumRef.child("name").setValue(txtNewName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            dialogRenameAlbum.dismiss();
                        }
                    });
                }
                else Toast.makeText(ViewAllPhotoAlbumActivity.this,"Nhập gì đi chứ man!!!",Toast.LENGTH_SHORT).show();

//                albumRef.child("name").setValue(txtNewName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        dialogRenameAlbum.cancel();
//                    }
//                });
            }
        });

        dialogRenameAlbum.show();

    }
}
