package com.example.preschool.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.R;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewMenuActivity extends AppCompatActivity implements DatePickerListener {

    private TextView txtSang;
    private TextView txtTrua;
    private TextView txtXe;
    private DatabaseReference MenuRef;
    private String daysele;

    private String idClass, idTeacher;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menu);

        // Get Bundle
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
//            idTeacher=bundle.getString("ID_TEACHER");
        }

        //Khai báo đối tượng
        txtSang = findViewById(R.id.isang);
        txtTrua = findViewById(R.id.itrua);
        txtXe = findViewById(R.id.ixe);
        MenuRef = FirebaseDatabase.getInstance().getReference("Class").child(idClass).child("Menu");
        //Calendar
        HorizontalPicker picker = (HorizontalPicker) findViewById(R.id.datePicker);
        picker.setListener(this)
                .setDays(365)
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
        picker.setBackgroundColor(getResources().getColor(R.color.bgr));
        picker.setDate(new DateTime().plusDays(0));

    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        if (dateSelected != null) {
            daysele = dateSelected.toString("ddMMyyyy");

            try {
                MenuRef.child(daysele).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Menu menu = dataSnapshot.getValue(Menu.class);
                        if(menu!=null){
                            txtSang.setText(menu.getiSang());
                            txtTrua.setText(menu.getiTrua());
                            txtXe.setText(menu.getiChieu());
                        }
                        else {
                            txtSang.setText("Chưa có thông tin");
                            txtTrua.setText("Chưa có thông tin");
                            txtXe.setText("Chưa có thông tin");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                txtSang.setText("");
                txtTrua.setText("");
                txtXe.setText("");
            }
        }
        else {
            txtSang.setText("Chưa có thông tin");
            txtTrua.setText("Chưa có thông tin");
            txtXe.setText("Chưa có thông tin");
        }

    }
}
