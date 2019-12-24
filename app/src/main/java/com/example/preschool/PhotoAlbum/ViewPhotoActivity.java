package com.example.preschool.PhotoAlbum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.preschool.Admin.ViewPagerFixed;
import com.example.preschool.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class ViewPhotoActivity extends AppCompatActivity {

    private RecyclerView myRecycleView;
    private ViewPagerFixed viewPager;
    private DatabaseReference mPhotosRef;
    private String positionAlbum;
    private TextView nameAlbum;
    private Album mAlbum = new Album();
    private AdapterImageView adapterImageView;
    private String idClass, idTeacher;

    private Bundle bundle;
    private ArrayList<String> uriList;
    private int position;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo_album);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // get uirList
        uriList = getIntent().getStringArrayListExtra("IMAGE_LINK");
        position=getIntent().getIntExtra("POSITION",0);
        getSupportActionBar().setTitle("");

//        title=findViewById(R.id.position_photo);
//        title.setText("1/4");
//        title.setText(String.valueOf(uriList.size()));

        viewPager=findViewById(R.id.view_pager);
        AdapterImageView adapter = new AdapterImageView(ViewPhotoActivity.this, uriList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.clearOnPageChangeListeners();

    }

}
