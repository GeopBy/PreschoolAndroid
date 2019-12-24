package com.example.preschool.NghiPhep;
/**
 * Activity này dành cho giáo viên của lớp, có thể xem toàn bộ
 * đơn xin nghỉ phép của phụ huynh trong lớp
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.preschool.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DonNghiPhepFullViewActivity extends AppCompatActivity {

    private DatabaseReference DonNghiPhepRef;
    private RecyclerView recyclerView;
    private ArrayList<DonNghiPhep> donNghiPhepArrayList = new ArrayList<DonNghiPhep>();
    private DonNghiPhepFullViewAdapter adapter;
    private String idClass;
    private ValueEventListener donNghiPhepListener;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_nghi_phep_full_view);

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

        // Get Bundle
        bundle = getIntent().getExtras();
        idClass = bundle.getString("ID_CLASS");


        recyclerView = findViewById(R.id.recycler_view_donnghiphep_full);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DonNghiPhepRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("DonNghiPhep");
        donNghiPhepListener = DonNghiPhepRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    donNghiPhepArrayList.add(ds.getValue(DonNghiPhep.class));
                }
                adapter = new DonNghiPhepFullViewAdapter(donNghiPhepArrayList);
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
