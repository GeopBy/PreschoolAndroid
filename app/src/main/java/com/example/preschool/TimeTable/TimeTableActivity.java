package com.example.preschool.TimeTable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.preschool.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.HashMap;

public class TimeTableActivity extends AppCompatActivity implements DatePickerListener {
    private EditText EdtDiscription;
    private TextView TxtStart;
    private TextView TxtEnd;
    private ImageButton BtnSave, BtnEdit;
    private DatabaseReference TimeTableRef;
    private String dateSelect, idClass, idTeacher;
    private RecyclerView myTimeTableList;
    private HorizontalPicker picker;
    private LinearLayout linearLayout;
    private FirebaseRecyclerAdapter adapter;
    private Boolean isTeacher=false;

    private Bundle bundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thời khóa biểu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher = bundle.getString("ID_TEACHER");
        }

        addControlls();
        TimeTableRef = FirebaseDatabase.getInstance().getReference("Class").child(idClass).child("TimeTable");
        BtnEdit.setVisibility(View.GONE);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(idTeacher)) {
            isTeacher=true;
            linearLayout.setVisibility(View.VISIBLE);
        } else linearLayout.setVisibility(View.GONE);


        addEvents();


    }

    private void addEvents() {
        TxtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TimeTableActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedMinute < 10) {
                            TxtStart.setText(selectedHour + ":" + "0" + selectedMinute);
                        } else {
                            TxtStart.setText(selectedHour + ":" + selectedMinute);
                        }

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Chọn giờ");
                mTimePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mTimePicker.show();

            }
        });
        TxtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TimeTableActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if (selectedMinute < 10) {
                            TxtEnd.setText(selectedHour + ":" + "0" + selectedMinute);
                        } else {
                            TxtEnd.setText(selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Chọn giờ");
                mTimePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mTimePicker.show();

            }
        });

        BtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSchedule();

            }
        });
    }

    private void addControlls() {

        linearLayout = findViewById(R.id.layout_add_time_table);
        myTimeTableList = findViewById(R.id.timetable_list);
        myTimeTableList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TimeTableActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myTimeTableList.setLayoutManager(linearLayoutManager);

//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat date = new SimpleDateFormat("ddMMyyyy");
//        dateSelect = date.format(cal.getTime());


        EdtDiscription = findViewById(R.id.edtDiscription);
        TxtStart = findViewById(R.id.nStart);
        TxtEnd = findViewById(R.id.nEnd);
        BtnSave = findViewById(R.id.save);
        BtnEdit=findViewById(R.id.edit);

        picker = findViewById(R.id.datePicker);
        picker.setListener(this)
                .setDays(30)
                .setOffset(3)
                .setDateSelectedColor(Color.DKGRAY)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.DKGRAY)
                .setTodayButtonTextColor(getResources().getColor(R.color.colorPrimary))
                .setTodayDateTextColor(getResources().getColor(R.color.colorPrimary))
                .setTodayDateBackgroundColor(Color.GRAY)
                .setUnselectedDayTextColor(Color.DKGRAY)
                .setDayOfWeekTextColor(Color.DKGRAY)
                .setUnselectedDayTextColor(getResources().getColor(R.color.colorPrimary))
                .showTodayButton(false)
                .init();
        picker.setBackgroundColor(Color.WHITE);
        picker.setDate(new DateTime().plusDays(0));
    }

    public void CreateSchedule() {
        String timestart = TxtStart.getText().toString();
        final String timeend = TxtEnd.getText().toString();
        String description = EdtDiscription.getText().toString();
        if (!TextUtils.isEmpty(timestart) && !TextUtils.isEmpty(timeend) && !TextUtils.isEmpty(description)) {
            String id = TimeTableRef.push().getKey();
            TimeTable off = new TimeTable(timestart, timeend, description);
            TimeTableRef.child(dateSelect).child(id).setValue(off).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(TimeTableActivity.this, "Bạn đã thêm thành công", Toast.LENGTH_SHORT).show();
                    EdtDiscription.setText("");
                    TxtStart.setText(timeend);
                    TxtEnd.setText("00:00");

                    //DisplayAllTimeTable(dateSelect);
                }
            });
        } else {
            Toast.makeText(this, "Bạn phải điền đủ nội dung", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        dateSelect = dateSelected.toString("ddMMyyyy");
        cleanData();
        DisplayAllTimeTable(dateSelect);
    }

    private void cleanData() {
        TxtStart.setText("00:00");
        TxtEnd.setText("00:00");
        EdtDiscription.setText("");
    }

    private void DisplayAllTimeTable(final String dateSelect) {
        Query query=TimeTableRef.child(dateSelect).orderByKey();
        FirebaseRecyclerOptions<TimeTable> options = new FirebaseRecyclerOptions.Builder<TimeTable>().setQuery(query
                ,TimeTable.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<TimeTable, TimeTableViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TimeTableViewHolder timeTableViewHolder, final int position, @NonNull TimeTable timeTable) {
                timeTableViewHolder.timeStart.setText(timeTable.getTimeStart());
                timeTableViewHolder.timeEnd.setText(timeTable.getTimeEnd());
                timeTableViewHolder.description.setText(timeTable.getDescription());
                timeTableViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    String id= getRef(position).getKey();
                    @Override
                    public void onClick(View v) {
                        if(isTeacher){
                            CharSequence options[] = new CharSequence[]{
                                    "Sửa",
                                    "Xóa"
                            };
                            final AlertDialog.Builder builder = new AlertDialog.Builder(TimeTableActivity.this);
                            builder.setTitle("Tùy chọn");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==1){
                                        TimeTableRef.child(dateSelect).child(id).removeValue();
                                    }
                                    if(which==0){
                                        TimeTableRef.child(dateSelect).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    TxtStart.setText(dataSnapshot.child("timeStart").getValue().toString());
                                                    TxtEnd.setText(dataSnapshot.child("timeEnd").getValue().toString());
                                                    EdtDiscription.setText(dataSnapshot.child("description").getValue().toString());
                                                }
                                                BtnSave.setVisibility(View.GONE);
                                                BtnEdit.setVisibility(View.VISIBLE);
                                                BtnEdit.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String timestart = TxtStart.getText().toString();
                                                        String timeend = TxtEnd.getText().toString();
                                                        String description = EdtDiscription.getText().toString();
                                                        final TimeTable off = new TimeTable(timestart, timeend, description);
                                                        final HashMap timetable=new HashMap();
                                                        timetable.put("timeStart",timestart);
                                                        timetable.put("timeEnd",timeend);
                                                        timetable.put("description",description);
                                                        TimeTableRef.child(dateSelect).child(id).updateChildren(timetable).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                EdtDiscription.setText("");
                                                                TxtStart.setText("00:00");
                                                                TxtEnd.setText("00:00");
                                                                BtnEdit.setVisibility(View.GONE);
                                                                BtnSave.setVisibility(View.VISIBLE);
                                                            }
                                                        });

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            });
                            builder.show();
                        }

                    }
                });
            }

            @NonNull
            @Override
            public TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timetable_layout, parent, false);
                return new TimeTableViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        myTimeTableList.setAdapter(adapter);

    }


    public static class TimeTableViewHolder extends RecyclerView.ViewHolder {

        private TextView timeStart;
        private TextView timeEnd;
        private TextView description;

        public TimeTableViewHolder(View itemView) {
            super(itemView);
            timeStart = itemView.findViewById(R.id.timeStart);
            timeEnd = itemView.findViewById(R.id.timeEnd);
            description = itemView.findViewById(R.id.description);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}
