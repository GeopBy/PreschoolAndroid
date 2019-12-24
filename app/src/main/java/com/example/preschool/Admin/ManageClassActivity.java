package com.example.preschool.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageClassActivity extends AppCompatActivity {
    private RecyclerView classList;
    private FirebaseAuth mAuth;
    private EditText NameClass,Year,Room;
    private FloatingActionButton CreateClassButton;
    private ProgressDialog loadingBar;
    private DatabaseReference ClassRef, UserRef;
    private ValueEventListener userEventListener;
    private ArrayList<String> arrYear=new ArrayList<>();
    private int positionChoose = 0;
    private Spinner yearSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_class);
        mAuth = FirebaseAuth.getInstance();
        NameClass = findViewById(R.id.setup_name_class);
        Room=findViewById(R.id.setup_phong);
        CreateClassButton = findViewById(R.id.create_button);
        loadingBar = new ProgressDialog(this);
        ClassRef = FirebaseDatabase.getInstance().getReference().child("Class");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        yearSpinner=findViewById(R.id.yearSniper);
        classList = findViewById(R.id.list_class);
        classList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageClassActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        classList.setLayoutManager(linearLayoutManager);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        int temp=2015;
        arrYear.add("Chọn niên khóa");
        for(int i=0;i<7;i++){
            String year=temp+"-"+(temp+1);
            arrYear.add(year);
            temp++;
        }
        final ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(ManageClassActivity.this, android.R.layout.simple_spinner_item, arrYear) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(getResources().getColor(R.color.hintcolor));
                } else {
                    tv.setTextColor(Color.WHITE);
                }
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                return view;
            }
        };
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.notifyDataSetChanged();
        yearSpinner.setAdapter(yearAdapter);
        CreateClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = NameClass.getText().toString();
                String room = Room.getText().toString();
                String year= arrYear.get(yearSpinner.getSelectedItemPosition());
                if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(room)) {
                    String id = ClassRef.push().getKey();
                    HashMap hashMap=new HashMap();
                    hashMap.put("classname",name);
                    hashMap.put("room",room);
                    hashMap.put("year",year);
                    ClassRef.child(id).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ManageClassActivity.this, "Bạn đã thêm thành công", Toast.LENGTH_SHORT).show();
                            NameClass.setText("");
                            yearSpinner.setSelection(0);
                            Room.setText("");
                        }
                    });

                } else {
                    Toast.makeText(ManageClassActivity.this, "Bạn phải điền đủ nội dung", Toast.LENGTH_SHORT).show();
                }
            }

        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LoadAllClass(arrYear.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void LoadAllClass(final String yearChoose) {
        FirebaseRecyclerOptions<Class> options = new FirebaseRecyclerOptions.Builder<Class>().
                setQuery(ClassRef, Class.class).build();
        FirebaseRecyclerAdapter<Class, ClassViewHolder> adapter = new FirebaseRecyclerAdapter<Class, ClassViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ClassViewHolder classViewHolder, final int position, @NonNull final Class model) {
                classViewHolder.setIsRecyclable(false);
                //Nếu không chọn niên khóa thì view hết
                if(!yearChoose.equals("Chọn niên khóa")){
                    if(model.getYear().equals(yearChoose)){
                        classViewHolder.setClassName(model.getClassname());
                        classViewHolder.setYear(model.getYear());
                        String idTeacher = model.getTeacher();
                        if (idTeacher != null) {
                            UserRef.child(idTeacher).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("fullnameteacher"))
                                        classViewHolder.setTeacherName(dataSnapshot.child("fullnameteacher").getValue().toString());
                                    else
                                        classViewHolder.setTeacherName("Don't Setup");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            classViewHolder.setTeacherName("null");
                        }
                    }
                    else{
                        classViewHolder.Layout_hide();
                    }
                }
                else{
                        classViewHolder.setClassName(model.getClassname());
                        classViewHolder.setYear(model.getYear());
                        String idTeacher = model.getTeacher();
                        if (idTeacher != null) {
                            UserRef.child(idTeacher).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("fullnameteacher"))
                                        classViewHolder.setTeacherName(dataSnapshot.child("fullnameteacher").getValue().toString());
                                    else
                                        classViewHolder.setTeacherName("Don't Setup");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            classViewHolder.setTeacherName("null");
                        }
                }
                classViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Detail Class",
                                "Edit Class",
                                "Delete Class"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageClassActivity.this);
                        builder.setTitle("Select Option");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //xóa class
                                final String classID = getRef(position).getKey();
                                if (which == 2) {
                                    ClassRef.child(classID).removeValue();
                                    userEventListener = UserRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot children : dataSnapshot.getChildren()) {
                                                    if (children.hasChild("idclass") && children.child("idclass").getValue().toString().equals(classID)) {
                                                        UserRef.child(children.getKey()).child("idclass").setValue("");
                                                        UserRef.child(children.getKey()).child("classname").setValue("");
                                                    }
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                if (which == 0) {
                                    Intent intent = new Intent(ManageClassActivity.this, ViewClassActivity.class);
                                    intent.putExtra("CLASS_ID", classID);
                                    startActivity(intent);
                                }
                                if (which == 1) {
                                    Intent intent = new Intent(ManageClassActivity.this, EditClassActivity.class);
                                    intent.putExtra("CLASS_ID", classID);
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_class_display_layout, parent, false);

                ClassViewHolder viewHolder = new ClassViewHolder(view);
                return viewHolder;
            }
        };
        classList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView class_name;
        private TextView teacher;
        private TextView year;
        private final androidx.constraintlayout.widget.ConstraintLayout layout;
        final LinearLayout.LayoutParams params;

        public ClassViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.all_class_layout);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            class_name = layout.findViewById(R.id.all_class_name);
            teacher = layout.findViewById(R.id.all_teacher_name);
            year=layout.findViewById(R.id.all_nienkhoa);
        }

        public void setClassName(String className) {
            class_name.setText(className);
        }
        public void setYear(String nienkhoa) {
            year.setText(nienkhoa);
        }
        public void setTeacherName(String teacherName) {
            teacher.setText(teacherName);
        }

        private void Layout_hide() {
            params.height = 0;
            layout.setLayoutParams(params);
        }
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
        Intent intent = new Intent(ManageClassActivity.this, AdminActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userEventListener != null) {
            UserRef.removeEventListener(userEventListener);
        }

    }
}
