package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChangeClassActivity extends AppCompatActivity {
    private String idClass, idTeacher, current_user_id, nameClass;
    private Bundle bundle;
    private DatabaseReference usersRef, classRef;
    private Button btnChangeClass;
    private Spinner spinner;
    private List listNameClass;
    private List listIdClass;
    private FirebaseAuth mAuth;
    private ValueEventListener userGetMyClassListener;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.change_join_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get Bundle
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher = bundle.getString("ID_TEACHER");
        }

        listIdClass = new ArrayList<String>();
        listNameClass = new ArrayList<String>();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();


        spinner = findViewById(R.id.spinner);
        btnChangeClass = findViewById(R.id.btn_change_class);
        btnChangeClass.setEnabled(false);
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        // get list idclass
        userGetMyClassListener = usersRef.child("myclass").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btnChangeClass.setEnabled(true);
                    for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                        listIdClass.add(suggestionSnapshot.getValue(String.class));
                        //get Class Name
                        classRef.child(suggestionSnapshot.getValue(String.class)).child("classname").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                listNameClass.add(dataSnapshot.getValue(String.class));
                                adapter = new ArrayAdapter<String>(ChangeClassActivity.this, android.R.layout.simple_list_item_1, listNameClass);
                                spinner.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }
                }
//                else btnChangeClass.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        btnChangeClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String idClassChoised = listIdClass.get(spinner.getSelectedItemPosition()).toString();
                String classNameChosed = spinner.getSelectedItem().toString();

                usersRef.child("idclass").setValue(idClassChoised);
                usersRef.child("classname").setValue(classNameChosed);
                Intent intent = new Intent(ChangeClassActivity.this, StartActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userGetMyClassListener != null)
            usersRef.child("myclass").removeEventListener(userGetMyClassListener);
    }
}

