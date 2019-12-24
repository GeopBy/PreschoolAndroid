package com.example.preschool.Children;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preschool.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentActivity extends AppCompatActivity {
    private String idClass;
    private Bundle bundle;
    private DatabaseReference UsersRef, ChildrenRef;
    private RecyclerView childrenList;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh sách trẻ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        gioitinh=findViewById(R.id.gioitinh);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
//            idTeacher = bundle.getString("ID_TEACHER");
        }
        ChildrenRef = FirebaseDatabase.getInstance().getReference("Class").child(idClass).child("Children");
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
//        ChildrenRef=ClassRef.child(idClass).child("Children");


        childrenList = findViewById(R.id.childrenRecycleView);
        childrenList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StudentActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        childrenList.setLayoutManager(linearLayoutManager);


        showAllChildren();


    }


    private void showAllChildren() {

        FirebaseRecyclerOptions<Children> options = new FirebaseRecyclerOptions.Builder<Children>().
                setQuery(ChildrenRef, Children.class).build();
        FirebaseRecyclerAdapter<Children, ChildrensViewHolder> adapter = new FirebaseRecyclerAdapter<Children, ChildrensViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChildrensViewHolder childrensViewHolder, int i, @NonNull Children children) {
                childrensViewHolder.txtFullName.setText(children.getName());
                childrensViewHolder.txtngaysinh.setText(children.getBirthday());
                final String id_children = getRef(i).getKey();
                childrensViewHolder.txtFullName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StudentActivity.this, ViewStudentActivity.class);
                        bundle.putString("ID_CHILDREN", id_children);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                });


            }

            @NonNull
            @Override
            public ChildrensViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_children_layout, parent, false);

                ChildrensViewHolder viewHolder = new ChildrensViewHolder(view);
                return viewHolder;
            }
        };

        childrenList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    public static class ChildrensViewHolder extends RecyclerView.ViewHolder {
        private TextView txtFullName;
        private TextView txtngaysinh;

        public ChildrensViewHolder(View itemView) {
            super(itemView);

            txtFullName = itemView.findViewById(R.id.txtChildrenFullname);
            txtngaysinh = itemView.findViewById(R.id.gioitinh);

        }
    }

}
