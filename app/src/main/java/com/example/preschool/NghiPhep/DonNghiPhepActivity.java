package com.example.preschool.NghiPhep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.preschool.Notifications.Data;
import com.example.preschool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DonNghiPhepActivity extends AppCompatActivity {

    /**
     * DonXinPhep này đặt trong Class->idClass
     * Sau khi tạo đơn mới, setValue lên Firebase ok
     */

    private EditText ngayNghi;
    private EditText soNgayNghi;
    private EditText lyDo;
    private EditText nguoiViet;
    private Button btnGui;
    private DatabaseReference DonXinPhepRef, UserRef, ChildrenRef;
    private DatePickerDialog.OnDateSetListener mDatePickerDialog;
    private String userId;
    private String mParentName, idClass;
    private String mKidName;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_xin_phep);
        // Get bundle from Main
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
        }

        ngayNghi = findViewById(R.id.ngay_nghi);
        soNgayNghi = findViewById(R.id.so_ngay_nghi);
        lyDo = findViewById(R.id.ly_do);
        btnGui = findViewById(R.id.gui_don);
        nguoiViet=findViewById(R.id.parent);
        // Lấy User ID hiện tại đang đăng nhập
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DonXinPhepRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("DonNghiPhep");


        // get parent name
//        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
//        UserRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mParentName = dataSnapshot.child("fullname").getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        // get children name
        ChildrenRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("mychildren").child(idClass);
        ChildrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mKidName = dataSnapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date;
                if (month < 10) {
                    date = dayOfMonth + "/0" + month + "/" + year;
                } else {
                    date = dayOfMonth + "/" + month + "/" + year;
                }
                ngayNghi.setText(date);
            }
        };
    }

    public void ShowDanhSach(View view) {
        Intent intent = new Intent(DonNghiPhepActivity.this, DonNghiPhepViewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void guiDonXinPhep(View view) {
        String mNgayNghi = ngayNghi.getText().toString();
        String mSoNgay = soNgayNghi.getText().toString();
        String mLyDo = lyDo.getText().toString();
        String mNguoiViet=nguoiViet.getText().toString();

        btnGui.setEnabled(false);

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = calFordDate.getTime();

        if (!TextUtils.isEmpty(mNgayNghi) && !TextUtils.isEmpty(mSoNgay) && !TextUtils.isEmpty(mLyDo) && !TextUtils.isEmpty(mNguoiViet)) {
            try {
                Date dayoff = simpleDateFormat.parse(mNgayNghi);
                if (dayoff.after(date)) {
                    String id = DonXinPhepRef.push().getKey();
                    DonNghiPhep donNghiPhep = new DonNghiPhep(userId, mNguoiViet, mKidName, mNgayNghi, mSoNgay, mLyDo);
                    DonXinPhepRef.child(id).setValue(donNghiPhep, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(DonNghiPhepActivity.this, "Đã nộp đơn", Toast.LENGTH_LONG).show();
                            ngayNghi.setText("");
                            soNgayNghi.setText("");
                            lyDo.setText("");
                            nguoiViet.setText("");
                            btnGui.setEnabled(true);
                        }
                    });

                } else{
                    btnGui.setEnabled(true);
                    Toast.makeText(DonNghiPhepActivity.this, "Bạn không thể tạo đơn trước, trùng ngày hiện tại", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {

            }
            String id = DonXinPhepRef.push().getKey();


        } else {
            Toast.makeText(this, "Bạn phải điền đủ nội dung", Toast.LENGTH_SHORT).show();
            btnGui.setEnabled(true);
        }

    }

    public void pickNgayNghi(View view) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(
                DonNghiPhepActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDatePickerDialog,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}
