package com.example.preschool.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.Children.Children;
import com.example.preschool.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity {
    private EditText userName, userFullName, userFather, userMother, userPhone,userAddress;
    private Button UpdateAccountSettingButton;
    private CircleImageView userProfImage;
    private ProgressDialog loadingBar;

    private DatabaseReference EditUserRef, ClassRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;
    private Uri resultUri;
    private Spinner classNameSpinner, roleSpinner;
    private String editRole, editClass;
    private int classChoose = 0;
    private int roleChoose = 0;
    private RecyclerView recyclerView;
    final ArrayList<String> role = new ArrayList<>();
    private final ArrayList<String> className = new ArrayList<>();
    private final ArrayList<String> classId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        EditUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(getIntent().getStringExtra("USER_ID"));
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        ClassRef = FirebaseDatabase.getInstance().getReference().child("Class");

        userProfImage = findViewById(R.id.edit_profile_image);
        userName = findViewById(R.id.edit_username);
        userFullName = findViewById(R.id.edit_fullname);
        userPhone = findViewById(R.id.edit_phonenumber);
        userAddress=findViewById(R.id.edit_address);
        userFather=findViewById(R.id.edit_fullnamefather);
        userMother=findViewById(R.id.edit_fullnamemother);
        UpdateAccountSettingButton = findViewById(R.id.update_account_settings_button);
        loadingBar = new ProgressDialog(this);

        recyclerView = findViewById(R.id.recycler_info_kid);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setVisibility(View.GONE);

        //spinner
        roleSpinner = findViewById(R.id.roleSniper);
        classNameSpinner = findViewById(R.id.classNameSpinner);
        roleSpinner.setEnabled(false);

        role.add("Choose Role...");
        role.add("Parent");
        role.add("Teacher");
        role.add("Admin");
        final ArrayAdapter<String> roleAdapter = new ArrayAdapter<String>(EditAccountActivity.this, android.R.layout.simple_spinner_item, role) {
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
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        className.add("Choose Class...");
        classId.add("");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Class").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                    String classname = suggestionSnapshot.child("classname").getValue(String.class);
                    String classid = suggestionSnapshot.getKey();
                    className.add(classname);
                    classId.add(classid);
                }
                EditUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("profileimage")) {
                            String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                            Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfImage);
                        }
                        if (dataSnapshot.hasChild("phonenumber")) {
                            String phone = dataSnapshot.child("phonenumber").getValue().toString();
                            userPhone.setText(phone);
                        }
                        if (dataSnapshot.hasChild("username")) {
                            String myUserName = dataSnapshot.child("username").getValue().toString();
                            userName.setText(myUserName);
                        }
                        if (dataSnapshot.hasChild("address")) {
                            String myUserName = dataSnapshot.child("address").getValue().toString();
                            userAddress.setText(myUserName);
                        }
                        if (dataSnapshot.hasChild("fullnamefather")) {
                            String myUserName = dataSnapshot.child("fullnamefather").getValue().toString();
                            userFather.setText(myUserName);
                        }
                        if (dataSnapshot.hasChild("fullnamemother")) {
                            String myUserName = dataSnapshot.child("fullnamemother").getValue().toString();
                            userMother.setText(myUserName);
                        }

                        editRole = dataSnapshot.child("role").getValue().toString();
                        if(editRole.equals("Admin")){
                            if (dataSnapshot.hasChild("fullnameadmin")) {
                                String myProfileName = dataSnapshot.child("fullnameadmin").getValue().toString();
                                userFullName.setText(myProfileName);
                            }
                        }
                        if(editRole.equals("Teacher")){
                            if (dataSnapshot.hasChild("fullnameteacher")) {
                                String myProfileName = dataSnapshot.child("fullnameteacher").getValue().toString();
                                userFullName.setText(myProfileName);
                            }
                        }
                        if (editRole.equals("Parent")) {
                            //////////////////////////////////////////////////
                            recyclerView.setVisibility(View.VISIBLE);
                            Query mychildrenQuery = EditUserRef.child("mychildren");
                            final FirebaseRecyclerOptions<Children> options = new FirebaseRecyclerOptions.Builder<Children>().setQuery(mychildrenQuery, Children.class).build();
                            FirebaseRecyclerAdapter adapter;
                            adapter = new FirebaseRecyclerAdapter<Children, EditAccountActivity.ChildrenViewHolder>(options) {

                                @Override
                                protected void onBindViewHolder(@NonNull final ChildrenViewHolder childrenViewHolder, int i, @NonNull Children children) {
                                    String classKey = getRef(i).getKey();
                                    final String[] className = new String[1];
                                    ClassRef.child(classKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            childrenViewHolder.txtClass.setText("Lớp: " + dataSnapshot.child("classname").getValue(String.class));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    childrenViewHolder.txtName.setText("Bé: " + children.getName());
                                    childrenViewHolder.txtBirthday.setText("Sinh nhật: " + children.getBirthday());
                                }

                                @NonNull
                                @Override
                                public ChildrenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view;
                                    // create a new view

                                    view = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.info_kid_items, parent, false);
                                    return new ChildrenViewHolder(view);
                                }


                            };
                            adapter.startListening();
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            classNameSpinner.setVisibility(View.GONE);
                            ///////////////////////////////////////////////////

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            classNameSpinner.setVisibility(View.VISIBLE);
                        }
                        int vitri = 0;
                        for (int i = 1; i < role.size(); i++) {
                            if (role.get(i).equals(editRole)) {
                                vitri = i;
                            }
                        }
                        roleSpinner.setSelection(vitri);
                        if (dataSnapshot.hasChild("idclass")) {
                            editClass = dataSnapshot.child("idclass").getValue().toString();
                            if (editClass.equals("")) {
                                vitri = 0;
                            } else {
                                for (int i = 1; i < classId.size(); i++) {
                                    if (classId.get(i).equals(editClass)) {
                                        vitri = i;
                                    }
                                }
                            }
                            classNameSpinner.setSelection(vitri);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(EditAccountActivity.this, android.R.layout.simple_spinner_item, className) {
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
        autoComplete.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoComplete.notifyDataSetChanged();
        classNameSpinner.setAdapter(autoComplete);
        classNameSpinner.setVisibility(View.GONE);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    classNameSpinner.setVisibility(View.VISIBLE);
                    classNameSpinner.setEnabled(false);
                    userFullName.setVisibility(View.VISIBLE);
                    userMother.setVisibility(View.GONE);
                    userFather.setVisibility(View.GONE);
                    userAddress.setVisibility(View.VISIBLE);
                    userPhone.setVisibility(View.VISIBLE);
                } else if (position == 3 ) {
                    classNameSpinner.setVisibility(View.GONE);
                    userFullName.setVisibility(View.VISIBLE);
                    userMother.setVisibility(View.GONE);
                    userFather.setVisibility(View.GONE);
                    userAddress.setVisibility(View.VISIBLE);
                    userPhone.setVisibility(View.VISIBLE);
                }
                else if(position==1){
                    userFullName.setVisibility(View.GONE);
                    userMother.setVisibility(View.VISIBLE);
                    userFather.setVisibility(View.VISIBLE);
                    userAddress.setVisibility(View.VISIBLE);
                    userPhone.setVisibility(View.VISIBLE);
                    classNameSpinner.setVisibility(View.GONE);
                }
                roleChoose = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classChoose = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
        UpdateAccountSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                userProfImage.setImageURI(resultUri);
            }
        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String userfullname = userFullName.getText().toString();
        String userphone = userPhone.getText().toString();
        String father = userFather.getText().toString();
        String mother = userMother.getText().toString();
        String address = userAddress.getText().toString();

        loadingBar.setTitle("Profile Update");
        loadingBar.setMessage("Please wait, while we updating your profile...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        UpdateAccountInfo(username, userfullname, userphone, role.get(roleChoose),
                classId.get(classChoose), className.get(classChoose),
                father,mother,address);
    }

    private void UpdateAccountInfo(final String username, final String userfullname,
                                   final String userphone, final String role, final String idclass,
                                   final String classname,  final String father,  final String mother,
                                   final String address) {
        StorageReference filePath = UserProfileImageRef.child(getIntent().getStringExtra("USER_ID") + ".jpg");
        if (resultUri != null) {
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditAccountActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                //lưu hình ảnh lên
                                EditUserRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            HashMap userMap = new HashMap();
                                            if(role.equals("Parent")){
                                                userMap.put("fullnamefather", father);
                                                userMap.put("fullnamemother", mother);
                                            }
                                            if(role.equals("Teacher")){
                                                userMap.put("fullnameteacher", userfullname);
                                            }
                                            if(role.equals("Admin")){
                                                userMap.put("fullnameadmin", userfullname);
                                            }
                                            userMap.put("username", username);
                                            userMap.put("phonenumber", userphone);
                                            userMap.put("address", address);
                                            userMap.put("role", role);


                                            EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {

                                                        loadingBar.dismiss();
                                                        Toast.makeText(EditAccountActivity.this, "Updated Successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(EditAccountActivity.this, ViewAccountActivity.class);
                                                        intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                                                        startActivity(intent);

                                                    } else {
                                                        Toast.makeText(EditAccountActivity.this, "Error", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        });

                    }
                }
            });
        }
        else{
            HashMap userMap = new HashMap();
            if(role.equals("Parent")){
                userMap.put("fullnamefather", father);
                userMap.put("fullnamemother", mother);
            }
            if(role.equals("Teacher")){
                userMap.put("fullnameteacher", userfullname);
            }
            if(role.equals("Admin")){
                userMap.put("fullnameadmin", userfullname);
            }
            userMap.put("username", username);
            userMap.put("phonenumber", userphone);
            userMap.put("address", address);
            userMap.put("role", role);

            EditUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        loadingBar.dismiss();
                        Toast.makeText(EditAccountActivity.this, "Updated Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditAccountActivity.this, ViewAccountActivity.class);
                        intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
                        startActivity(intent);

                    } else {
                        Toast.makeText(EditAccountActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                }
            });
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
        Intent intent = new Intent(EditAccountActivity.this, ManageUserActivity.class);
        startActivity(intent);
    }

    public class ChildrenViewHolder extends RecyclerView.ViewHolder {

        TextView txtClass, txtName, txtBirthday;

        public ChildrenViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClass = itemView.findViewById(R.id.person_class);
            txtName = itemView.findViewById(R.id.relationship_with_children);
            txtBirthday = itemView.findViewById(R.id.person_birthday);
        }
    }
}