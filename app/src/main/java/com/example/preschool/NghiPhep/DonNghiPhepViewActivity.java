package com.example.preschool.NghiPhep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.preschool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DonNghiPhepViewActivity extends AppCompatActivity {

    private DatabaseReference DonNghiPhepRef;
    private ValueEventListener donNghiPhepListener;
    private RecyclerView recyclerView;
    private ArrayList<DonNghiPhep> donNghiPhepArrayList = new ArrayList<DonNghiPhep>();
    private DonNghiPhepViewAdapter adapter;
    private String userId;
    private String idClass;
    private  Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_nghi_phep_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đơn xin phép");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get bundle
        bundle=getIntent().getExtras();
        if(bundle!=null){
            idClass=bundle.getString("ID_CLASS");
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recycler_view_donnghiphep);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DonNghiPhepRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("DonNghiPhep");
        donNghiPhepListener= DonNghiPhepRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(DonNghiPhep.class).getUserId().equals(userId)) {
                        donNghiPhepArrayList.add(ds.getValue(DonNghiPhep.class));
                    }
                }
                adapter = new DonNghiPhepViewAdapter(donNghiPhepArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        DonNghiPhepRef.removeEventListener(donNghiPhepListener);
    }
}
