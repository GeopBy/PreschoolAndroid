package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.preschool.Admin.AdminActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private String currentUserID;
    private Intent intent;
    private ValueEventListener valueEventListener;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        progressBar=findViewById(R.id.progress_bar_start);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserID = mAuth.getCurrentUser().getUid();
            valueEventListener=UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //xác nhận tài khoản đã bị xóa, hủy tài khoản
                    if (!dataSnapshot.hasChild("role")) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        UsersRef.child(currentUserID).removeValue();
                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential(currentUser.getEmail(), "123456");
                                        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            startActivity(new Intent(StartActivity.this, LoginActivity.class));
                                                            finish();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(StartActivity.this, "that bai", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                }
                            }
                        };
                        AlertDialog.Builder ab = new AlertDialog.Builder(StartActivity.this);
                        ab.setMessage("Tài khoản của bạn đã hết hạn. Vui lòng liên hệ với nhà trường để được " +
                                "cấp tài khoản mới ").setPositiveButton("Yes", dialogClickListener)
                                .show();

                    }
                    //ngược lại đăng nhập vào
                    else
                        {
                            if(dataSnapshot.child("role").getValue().toString().equals("Admin")){
                                intent = new Intent(StartActivity.this, AdminActivity.class);
                                startActivity(intent);
                            }
                            else{
                                    final String idClass;
                                    idClass = dataSnapshot.child("idclass").getValue().toString();
                                    if(idClass.equals("")) {
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                                                        finish();
                                                }
                                            }
                                        };
                                        AlertDialog.Builder ab = new AlertDialog.Builder(StartActivity.this);
                                        ab.setMessage("Bạn đang không thuộc lớp nào cả. Vui lòng liên hệ nhà trường để thêm lớp cho bạn ").setPositiveButton("Yes", dialogClickListener)
                                                .show();
                                    }
                                    else {
                                        if (!dataSnapshot.hasChild("username")) {
                                            intent = new Intent(StartActivity.this, SetupActivity.class);
                                            startActivity(intent);
                                        }
                                        else{

                                                DatabaseReference ClassRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass);
                                                ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String idTeacher = dataSnapshot.child("teacher").getValue().toString();
                                                        String className = dataSnapshot.child("classname").getValue().toString();
                                                        Bundle bundleStart = new Bundle();
                                                        intent = new Intent(StartActivity.this, MainActivity.class);
                                                        // Đóng gói dữ liệu vào bundle
                                                        bundleStart.putString("ID_CLASS", idClass);
                                                        bundleStart.putString("CLASS_NAME", className);
                                                        bundleStart.putString("ID_TEACHER", idTeacher);
                                                        intent.putExtras(bundleStart);

                                                        startActivity(intent);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                        }
                                }
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }


//    private void SendUserToMainActivity() {
//        String currentUserID = mAuth.getCurrentUser().getUid();
//        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        final DatabaseReference ClassRef = FirebaseDatabase.getInstance().getReference().child("Class");
//        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final String idClass;
//                idClass = dataSnapshot.child("idclass").getValue().toString();
//                ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String idTeacher = dataSnapshot.child(idClass).child("teacher").getValue().toString();
//                        String className = dataSnapshot.child(idClass).child("classname").getValue().toString();
//                        Bundle bundleStart = new Bundle();
//
//                        // Đóng gói dữ liệu vào bundle
//                        bundleStart.putString("ID_CLASS", idClass);
//                        bundleStart.putString("CLASS_NAME", className);
//                        bundleStart.putString("ID_TEACHER", idTeacher);
//                        intentMain.putExtras(bundleStart);
//                        startActivity(intentMain);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if(valueEventListener!=null){

            UsersRef.removeEventListener(valueEventListener);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        finish();
    }
}
