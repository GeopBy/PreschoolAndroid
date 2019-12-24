package com.example.preschool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.Admin.AdminActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private EditText txtUsername, txtPass;
    private Button btnFogotPass, btnSignIn;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, ClassRef;
    private ValueEventListener UsersEventListener, ClasEventListener;
    private Intent intent;
    private String currentUserID, idClass, idTeacher;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addControls();
        addEvents();
    }

    private void addEvents() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowingUserToLogin();

            }
        });

        // Click FogotPass
        btnFogotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogFogotPass = new Dialog(LoginActivity.this);
                dialogFogotPass.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogFogotPass.setCancelable(false);
                dialogFogotPass.setContentView(R.layout.forgot_password_dialog);
                dialogFogotPass.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                final EditText txtMailFogotPass = dialogFogotPass.findViewById(R.id.txtMailForgotPass);
                final TextView txtNotAMail = dialogFogotPass.findViewById(R.id.txtNotAMail);
                Button btnSendForgotPass = dialogFogotPass.findViewById(R.id.btnSendFogotPass);
                Button btnBackSendPass = dialogFogotPass.findViewById(R.id.btnBackSendPass);

                btnSendForgotPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtNotAMail.setText("");
                        loadingBar.setTitle("Loading");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        sendResetPassword();
                    }

                    /**
                     * send mail reset pass to current mail
                     */
                    private void sendResetPassword() {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String emailAddress = txtMailFogotPass.getText().toString().trim();

                        if (isMail(emailAddress)) {
                            auth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                                Toast.makeText(LoginActivity.this,
                                                        getString(R.string.reset_pass_successful),
                                                        Toast.LENGTH_LONG).show();
                                                dialogFogotPass.cancel();
                                                loadingBar.dismiss();
                                            } else {
                                                Toast.makeText(LoginActivity.this,
                                                        "Mail chưa đăng ký",
                                                        Toast.LENGTH_LONG).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        } else {
                            txtNotAMail.setText(getString(R.string.not_a_mail));
                            loadingBar.dismiss();
                        }
                    }

                    /**
                     * Check is Mail
                     * @param email
                     * @return
                     */
                    private boolean isMail(String email) {
                        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
                        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
                        java.util.regex.Matcher m = p.matcher(email);
                        return m.matches();
                    }
                });

                btnBackSendPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogFogotPass.cancel();
                    }
                });

                dialogFogotPass.show();
            }
        });
    }

    private void AllowingUserToLogin() {
        final String email = txtUsername.getText().toString();
        String password = txtPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.insert_email, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.insert_pass, Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle(R.string.login);
            loadingBar.setMessage(getString(R.string.message_login_successfully));
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            // Sign With Email and Password by Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                loadingBar.dismiss();
                                final FirebaseUser currentUser = mAuth.getCurrentUser();
                                currentUserID = mAuth.getCurrentUser().getUid();
                                UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                UsersEventListener =
                                        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
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
                                                                                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                                                                                finish();
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(LoginActivity.this, "that bai", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                    }
                                                }
                                            };
                                            AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
                                            ab.setMessage("Tài khoản của bạn đã hết hạn. Vui lòng liên hệ với nhà trường để được " +
                                                    "cấp tài khoản mới ").setPositiveButton("Yes", dialogClickListener)
                                                    .show();
                                        }
                                        //ngược lại đăng nhập vào
                                        else {
                                            if(dataSnapshot.child("role").getValue().toString().equals("Admin")){
                                                intent = new Intent(LoginActivity.this, AdminActivity.class);
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
                                                                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                                                                    finish();
                                                            }
                                                        }
                                                    };
                                                    AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
                                                    ab.setMessage("Bạn đang không thuộc lớp nào cả. Vui lòng liên hệ nhà trường để thêm lớp cho bạn ").setPositiveButton("Yes", dialogClickListener)
                                                            .show();
                                                }
                                                else {
                                                    if (!dataSnapshot.hasChild("username")) {
                                                        intent = new Intent(LoginActivity.this, SetupActivity.class);
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
                                                                intent = new Intent(LoginActivity.this, MainActivity.class);
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
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, R.string.error_occured + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }


    private void addControls() {
        txtUsername = findViewById(R.id.login_email);
        txtPass = findViewById(R.id.login_password);
        btnFogotPass = findViewById(R.id.fogot_password);
        btnSignIn = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        moveTaskToBack(true);
//        finish();
        boolean sentAppToBackground = moveTaskToBack(true);

        if(!sentAppToBackground){
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ClasEventListener!=null){
            ClassRef.removeEventListener(ClasEventListener);
        }
        if(UsersEventListener!=null){
            UsersRef.removeEventListener(UsersEventListener);
        }
        finish();
    }
}
