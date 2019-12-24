package com.example.preschool;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.preschool.Chats.ChatsFragment;
import com.example.preschool.Children.StudentActivity;
import com.example.preschool.Event.EventsActivity;
import com.example.preschool.Help.HelpActivity;
import com.example.preschool.Menu.MenuActivity;
import com.example.preschool.Menu.ViewMenuActivity;
import com.example.preschool.NghiPhep.DonNghiPhepActivity;
import com.example.preschool.NghiPhep.DonNghiPhepFullViewActivity;
import com.example.preschool.Notifications.NotificationFragment;
import com.example.preschool.Notifications.Token;
import com.example.preschool.PhotoAlbum.ViewAllAlbumActivity;
import com.example.preschool.Setting.SettingActivity;
import com.example.preschool.TimeTable.TimeTableActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private CircleImageView imageViewUser;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,ClassRef,UserStateRef;
    private TextView txtclassName;
    private String currentUserID;
    private String teacherName,year,room;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private Bundle bundle;
    private int countExit=0;
    private Boolean isTeacher=false;

    private String idClass, idTeacher, className;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nhận bundle
        bundle = getIntent().getExtras();
        if(bundle!=null){
            idClass= bundle.getString("ID_CLASS");
            className=bundle.getString("CLASS_NAME");
            idTeacher=bundle.getString("ID_TEACHER");
        }

        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();
        if(currentUserID.equals(idTeacher)){
            isTeacher=true;
        }
        // Update token
        updateToken(FirebaseInstanceId.getInstance().getToken());

        UserStateRef=FirebaseDatabase.getInstance().getReference("UserState");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        ClassRef=FirebaseDatabase.getInstance().getReference().child("Class").child(idClass);

        // setup thông tin của trẻ cho lớp học
        UsersRef.child("mychildren").child(idClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists() && !isTeacher&& !currentUserID.equals("9nTFWm43IpbpSHzbucpL2zSgoZ43")){
                    setupChildren();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




//        updateUserStatus("online");
        AppCenter.start(getApplication(), "74bc89c2-9212-4cc3-9b55-6fc10baf76bb", Analytics.class, Crashes.class);



        try{
            ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    year=dataSnapshot.child("year").getValue(String.class);
                    room=dataSnapshot.child("room").getValue(String.class);

                    DatabaseReference TeacherRef= FirebaseDatabase.getInstance().getReference("Users").child(idTeacher);
                    TeacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            teacherName= dataSnapshot.child("fullnameteacher").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){

        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageViewUser=findViewById(R.id.icon_user);
        UsersRef.child("profileimage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue(String.class)).resize(70,0).into(imageViewUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imageViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,MyProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        txtclassName=findViewById(R.id.class_name);
        txtclassName.setText(className);
        txtclassName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogDetailClass =new Dialog(MainActivity.this);
                dialogDetailClass.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogDetailClass.setCancelable(true);
                dialogDetailClass.setContentView(R.layout.detail_class);
                dialogDetailClass.getWindow().setBackgroundDrawableResource(android.R.color.white);

                final TextView txtClassName, txtNameTeacher, txtYear, txtRoom;
                txtClassName=dialogDetailClass.findViewById(R.id.class_name);
                txtNameTeacher=dialogDetailClass.findViewById(R.id.teacher_name);
                txtRoom=dialogDetailClass.findViewById(R.id.room);
                txtYear=dialogDetailClass.findViewById(R.id.year);

                txtClassName.setText(className);
                txtNameTeacher.setText(teacherName);
                txtRoom.setText(room);
                txtYear.setText(year);
                dialogDetailClass.show();

            }
        });

        mViewPager = findViewById(R.id.viewPager);
        final TabLayout tabLayout = findViewById(R.id.tablayout);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),bundle);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);



        // Set icon for tabItem
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_contact);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_timeline);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_tab_message);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_tab_notification);

        /**
         * đổi màu icon tab_selected
         *
         */
        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#a8a8a8"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                } else {
                    drawer.openDrawer(Gravity.RIGHT);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setupChildren() {



        final Dialog dialogSetupChildren=new Dialog(MainActivity.this);
        dialogSetupChildren.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSetupChildren.setCancelable(false);
        dialogSetupChildren.setContentView(R.layout.setup_children_dialog);
        dialogSetupChildren.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialogSetupChildren.show();

        TextView txtLabel=dialogSetupChildren.findViewById(R.id.textView);
        final EditText childrenNam=dialogSetupChildren.findViewById(R.id.txtChildrenFullname);
        final EditText childrenBirthday =dialogSetupChildren.findViewById(R.id.txtChildrenBirth);
        final Button btnsave=dialogSetupChildren.findViewById(R.id.btnSave);
        final RadioButton radioButtonMale=dialogSetupChildren.findViewById(R.id.radioButton_male);
        final RadioButton radioButtonFemale=dialogSetupChildren.findViewById(R.id.radioButton_female);



        final DatePickerDialog.OnDateSetListener mDatePickerDialog;
        mDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date;
                if (month < 10) {
                    if (dayOfMonth < 10) {
                        date = "0" + dayOfMonth + "/0" + month + "/" + year;
                    } else date = dayOfMonth + "/0" + month + "/" + year;
                } else {
                    if (dayOfMonth < 10) {
                        date = "0" + dayOfMonth + "/0" + month + "/" + year;
                    } else date = dayOfMonth + "/" + month + "/" + year;
                }
                childrenBirthday.setText(date);
            }
        };

        Button btnChildrenBirthday=dialogSetupChildren.findViewById(R.id.btnChildrenBirthday);
        btnChildrenBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH);
                    int year = cal.get(Calendar.YEAR);
                    DatePickerDialog dialog = new DatePickerDialog(
                            MainActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDatePickerDialog,
                            year, month, day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
            }
        });

        txtLabel.setText("Cập nhật thông tin của bé cho lớp: "+className);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnsave.setEnabled(false);
                if(!childrenNam.getText().toString().equals("")){
                    if(!childrenBirthday.getText().toString().equals("")){
                        final HashMap childrenMap = new HashMap();
                        if(radioButtonMale.isChecked()){
                            childrenMap.put("sex","Nam");
                        }else childrenMap.put("sex","Nữ");
                        childrenMap.put("birthday", childrenBirthday.getText().toString());
                        childrenMap.put("name", childrenNam.getText().toString());

                        UsersRef.child("mychildren").child(idClass).updateChildren(childrenMap)
                                .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                ClassRef.child("Children").child(currentUserID).updateChildren(childrenMap)
                                        .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        dialogSetupChildren.dismiss();
                                    }
                                });
                            }
                        });
                    }else Toast.makeText(MainActivity.this,"Bạn chưa nhập sinh nhật cho bé",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(MainActivity.this,"Bạn chưa nhập tên cho bé",Toast.LENGTH_SHORT).show();
            }
        });


    }


    /**
     * Check xem user đã có full name hay chưa, nếu chưa gửi sang trang SetupActivity
     */

//    private void SendUserToSetupActivity() {
//        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
//        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(setupIntent);
//        finish();
//    }


//    private void SendUserToLoginActivity() {
//        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
//        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        finish();
//        startActivity(loginIntent);
//
//    }

    @Override
    public void onBackPressed() {
        countExit++;
        if(countExit==1){
            Toast.makeText(MainActivity.this,"Nhấn lần nữa để thoát",Toast.LENGTH_SHORT).show();
        }
        else finish();

//        drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
//                    drawer.closeDrawer(Gravity.RIGHT);
//                } else {
//                    drawer.openDrawer(Gravity.RIGHT);
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");
//        Toast.makeText(this,"Main Start",Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        countExit=0;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUserStatus("offline");
//        Toast.makeText(this,"Main Stop",Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final Intent intent;

        switch (id){
            case R.id.photoalbum:
                intent=new Intent(MainActivity.this, ViewAllAlbumActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.permissonform:
                if(isTeacher){
                    intent=new Intent(MainActivity.this, DonNghiPhepFullViewActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                }
                else {
                    intent=new Intent(MainActivity.this, DonNghiPhepActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                }
            case R.id.timetable:
                intent=new Intent(MainActivity.this, TimeTableActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.student:
                intent=new Intent(MainActivity.this, StudentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.menu:
                if(isTeacher){
                    intent=new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    intent=new Intent(MainActivity.this, ViewMenuActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.profile:
                intent=new Intent(MainActivity.this, MyProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.event:
                intent= new Intent(MainActivity.this, EventsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
//            case R.id.knowledge:
//                break;
            case R.id.changeclass:
                if(!isTeacher){
                    intent= new Intent(MainActivity.this, ChangeClassActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"Giáo viên không thể đổi lớp",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.help:
                intent=new Intent(MainActivity.this, HelpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.setting:
                intent=new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.logout:
                AlertDialog.Builder dialogLogout=new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                dialogLogout.setMessage("Bạn có chắc muốn đăng xuất");
                dialogLogout.setCancelable(false);
                dialogLogout.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(MainActivity.this, StartActivity.class);
                        mAuth.signOut();
                        startActivity(intent);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialogLogout.show();

                break;
        }
        return true;
    }

    // Update token to send Notifications
    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentUserID).setValue(token1);
    }
    public void updateUserStatus(String state){
        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calForTime.getTime());

        Map currentStateMap=new HashMap();
        currentStateMap.put("time",saveCurrentTime);
        currentStateMap.put("date",saveCurrentDate);
        currentStateMap.put("type",state);

        UserStateRef.child(currentUserID)
                .setValue(currentStateMap);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ////////////////////////////////////////////
        Bundle bundle;
        public SectionsPagerAdapter(FragmentManager fm,Bundle bundle) {
            super(fm);
            this.bundle=bundle;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            switch (position){
                case 0:
                    fragment=new FriendsFragment();
                    break;
                case 1:
                    fragment=new NewsFeedFragment();
                    break;
                case 2:
                    fragment=new ChatsFragment();
                    break;
                case 3:
                    fragment=new NotificationFragment();
                    break;
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

}
