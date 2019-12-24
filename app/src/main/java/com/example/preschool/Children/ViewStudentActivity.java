package com.example.preschool.Children;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.preschool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewStudentActivity extends AppCompatActivity {

    private TextView nameBe, DoBbe, Sex, Dad, Mom, DiaChi, Phone;
    private String idClass;
    private Bundle bundle;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private String id_children_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thông tin trẻ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bundle = getIntent().getExtras();
        if (bundle != null) {
            id_children_user = bundle.getString("ID_CHILDREN");
            idClass = bundle.getString("ID_CLASS");
            bundle.remove("ID_CHILDREN");
        }

        nameBe = findViewById(R.id.nameBe);
        DoBbe = findViewById(R.id.DoBBe);
        Sex = findViewById(R.id.sex);
        Dad = findViewById(R.id.DadBe);
        Mom = findViewById(R.id.MomBe);
        DiaChi = findViewById(R.id.AddrBe);
        Phone = findViewById(R.id.PhoneBe);
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        ShowStudent();
    }


    public void ShowStudent() {
        UsersRef.child(id_children_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        Phone.setText(dataSnapshot.child("phonenumber").getValue().toString());
                        nameBe.setText(dataSnapshot.child("mychildren").child(idClass).child("name").getValue().toString());
                        DoBbe.setText(dataSnapshot.child("mychildren").child(idClass).child("birthday").getValue().toString());
                        Sex.setText(dataSnapshot.child("mychildren").child(idClass).child("sex").getValue().toString());
                        Dad.setText(dataSnapshot.child("fullnamefather").getValue().toString());
                        Mom.setText(dataSnapshot.child("fullnamemother").getValue().toString());
                        DiaChi.setText(dataSnapshot.child("address").getValue().toString());
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
