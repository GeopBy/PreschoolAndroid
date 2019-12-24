//package com.example.preschool.Event;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import com.example.preschool.R;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//
//public class AddEventActivity extends AppCompatActivity {
//
//    private Button btnOK, btnCancel;
//    private EditText txtTitle, txtLocal, txtDecription;
//    private Switch isAllDay;
//    private TextView txtStart, txtEnd;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_event);
//
//        addControlls();
//        addEvents();
//    }
//
//    private void addControlls() {
//        btnCancel=findViewById(R.id.event_cancel);
//        btnOK=findViewById(R.id.event_ok);
//        txtTitle=findViewById(R.id.event_title);
//        txtLocal=findViewById(R.id.event_location);
//        txtDecription=findViewById(R.id.event_decription);
//        isAllDay=findViewById(R.id.is_all_day);
//        txtStart=findViewById(R.id.event_start);
//        txtEnd=findViewById(R.id.event_end);
//    }
//
//    private void addEvents() {
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//
//        txtStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isAllDay.isChecked()){
////                    DatePicker.
//
//                }
//
//            }
//        });
//
//    }
//}


package com.example.preschool.Event;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.preschool.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.view.View.*;

public class AddEventActivity extends AppCompatActivity {

    private Button btnOK, btnCancel;
    private EditText txtTitle, txtLocal, txtDecription;
    private Switch isAllDay;
    private TextView TimeStart, TimeEnd;
    private TextView DateStart, DateEnd;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private DatabaseReference EventRef;
    private Bundle bundle;
    private String idClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        // Get Bundle
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
        }
        addControlls();
        addEvents();

    }

    private void addControlls() {
        btnCancel = findViewById(R.id.event_cancel);
        btnOK = findViewById(R.id.event_ok);
        txtTitle = findViewById(R.id.event_title);
        txtLocal = findViewById(R.id.event_location);
        txtDecription = findViewById(R.id.event_decription);
        isAllDay = findViewById(R.id.is_all_day);
        TimeStart = findViewById(R.id.event_timestart);
        TimeEnd = findViewById(R.id.event_timeend);
        DateStart = findViewById(R.id.event_datestart);
        DateEnd = findViewById(R.id.event_dateend);
        EventRef = FirebaseDatabase.getInstance().getReference("Class").child(idClass).child("Events");

    }

    private void addEvents() {

        isAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TimeStart.setVisibility(TextView.INVISIBLE);
                    TimeStart.setText("Cả ngày");
                    TimeEnd.setVisibility(TextView.INVISIBLE);
                    TimeEnd.setText("Cả ngày");
                } else {
                    TimeStart.setText("00:00");
                    TimeStart.setVisibility(TextView.VISIBLE);
                    TimeEnd.setVisibility(TextView.VISIBLE);
                    TimeEnd.setText("00:00");

                }
            }

        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TimeStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                TimeStart.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        DateStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                if ((monthOfYear + 1) < 10) {
                                    DateStart.setText(dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year);
                                } else {
                                    DateStart.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });
        TimeEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                TimeEnd.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        DateEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                if ((monthOfYear + 1) < 10) {
                                    DateEnd.setText(dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year);
                                } else {
                                    DateEnd.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String timestart, timeend, nameevent, description, position;
                nameevent = txtTitle.getText().toString();
                timestart = TimeStart.getText().toString();
                timeend = TimeEnd.getText().toString();
                description = txtDecription.getText().toString();
                position = txtLocal.getText().toString();
                Event event = new Event(nameevent, timestart, timeend, position, description);
                Calendar d1 = Calendar.getInstance();// calendar c
                Calendar d2 = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {

                    Date date1 = sdf.parse(DateStart.getText().toString()); //ngay start
                    Date date2 = sdf.parse(DateEnd.getText().toString());

                    d1.setTime(date1);
                    d2.setTime(date2);
                    if (!TextUtils.isEmpty(nameevent) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(position)) {
                        while (!d1.getTime().after(d2.getTime())) {
                            String x = sdf.format(d1.getTime());
//                            EventRef.child(x).setValue(event);
                            final String childString = EventRef.child(x).push().getKey();
                            EventRef.child(x).child(childString).setValue(event);
                            d1.add(Calendar.DATE, 1);
                            Toast.makeText(AddEventActivity.this, "Them thanh cong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddEventActivity.this, "Ban phai dien du noi dung", Toast.LENGTH_SHORT).show();

                    }
                    finish();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

    }


}
