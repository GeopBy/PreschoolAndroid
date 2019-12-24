package com.example.preschool.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.R;
import com.example.preschool.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageUserActivity extends AppCompatActivity {
    private RecyclerView myAccountList;
    private FirebaseAuth mAuth;
    private EditText UserEmail;
    private FloatingActionButton CreateAccountButton;
    private ProgressDialog loadingBar;
    private Spinner classNameSpinner, roleSpinner;
    private DatabaseReference UsersRef, ClassRef;
    private int classChoose = 0;
    private int roleChoose = 0;
    private String userIdJustAdd;
    private ListView listClass;
    private ValueEventListener ClassEventListener, UsersEventListener, updateClass;
    final ArrayList<String> classId = new ArrayList<>();
    private CheckBox classCheckBox;
    final ArrayList<String> checkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        mAuth = FirebaseAuth.getInstance();
        UserEmail = findViewById(R.id.setup_email);
        CreateAccountButton = findViewById(R.id.create_button);
        classNameSpinner = findViewById(R.id.classNameSpinner);
        roleSpinner = findViewById(R.id.roleSniper);
        ClassRef = FirebaseDatabase.getInstance().getReference().child("Class");
        listClass = findViewById(R.id.listClassListView);
        listClass.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        classCheckBox = findViewById(R.id.listClassCheckbox);
        final ArrayList<String> role = new ArrayList<>();
        role.add("Choose Role...");
        role.add("Parent");
        role.add("Teacher");
        role.add("Admin");
        final ArrayAdapter<String> roleAdapter = new ArrayAdapter<String>(ManageUserActivity.this, android.R.layout.simple_spinner_item, role) {
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
        roleAdapter.notifyDataSetChanged();
        roleSpinner.setAdapter(roleAdapter);

        final ArrayList<String> className = new ArrayList<>();

        className.add("Choose Class...");
        classId.add("");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        ClassEventListener = database.child("Class").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                    int count = (int) dataSnapshot.getChildrenCount() + 1;
                    if (className.size() <= count
                            && classId.size() <= count) {
                        String classname = suggestionSnapshot.child("classname").getValue(String.class);
                        String classid = suggestionSnapshot.getKey();
                        className.add(classname);
                        classId.add(classid);
                        if (classId.size() == count) classId.add("");
                    }
                }
                for (int i = 0; i < classId.size(); i++) {
                    checkList.add(i, "0");
                }
                listClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckedTextView v = (CheckedTextView) view;
                        if (v.isChecked()) {
                            checkList.set(position, "1");
                        } else {
                            checkList.set(position, "0");
                        }
//                        Toast.makeText(ManageUserActivity.this, checkList.get(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(ManageUserActivity.this, android.R.layout.simple_spinner_item, className) {
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
        classCheckBox.setVisibility(View.GONE);
        listClass.setVisibility(View.GONE);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1 || position == 2) {
                    if (position == 1) {
                        classCheckBox.setVisibility(View.VISIBLE);
                    } else {
                        classCheckBox.setVisibility(View.GONE);
                    }
                    classNameSpinner.setVisibility(View.VISIBLE);
                    if (classChoose != 0)
                        LoadAccountClass(role.get(position), classId.get(classChoose));
                    else LoadAccountClass(role.get(position), classId.get(0));
                } else if (position == 3) {
                    classNameSpinner.setVisibility(View.GONE);
                    LoadAccountClass(role.get(position), classId.get(0));
                    classCheckBox.setVisibility(View.GONE);
                } else LoadAccountClass(role.get(position), classId.get(0));
                roleChoose = position;
                classNameSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ManageUserActivity.this, android.R.layout.simple_list_item_checked, className) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(getResources().getColor(R.color.hintcolor));
                }
                return view;
            }
        };
        listClass.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        classCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classCheckBox.isChecked()) {
                    classNameSpinner.setVisibility(View.GONE);
                    myAccountList.setVisibility(View.GONE);
                    listClass.setVisibility(View.VISIBLE);
                } else {
                    classNameSpinner.setVisibility(View.VISIBLE);
                    myAccountList.setVisibility(View.VISIBLE);
                    listClass.setVisibility(View.GONE);
                }
            }
        });


        loadingBar = new ProgressDialog(this);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myAccountList = findViewById(R.id.list_account);
        myAccountList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageUserActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myAccountList.setLayoutManager(linearLayoutManager);

        classNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classChoose = position;
                LoadAccountClass(role.get(roleChoose), classId.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = UserEmail.getText().toString();
                String password = "123456";
                int temp = 0;
                for (int i = 0; i < checkList.size(); i++) {
                    if (checkList.get(i).equals("1")) {
                        temp = 1;
                        break;
                    }
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ManageUserActivity.this, "Please write email...", Toast.LENGTH_SHORT).show();
                } else if (roleChoose == 0) {
                    Toast.makeText(ManageUserActivity.this, "Please choose role...", Toast.LENGTH_SHORT).show();
                } else if (roleChoose != 0 && classChoose == 0 && roleChoose != 3 && classCheckBox.isChecked() == false) {
                    Toast.makeText(ManageUserActivity.this, "Please choose class...", Toast.LENGTH_SHORT).show();
                } else if (classCheckBox.isChecked() == true && temp == 0) {
                    Toast.makeText(ManageUserActivity.this, "Please choose class...", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please wait, while we are creating your new Account...");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.dismiss();
                    final String mail = mAuth.getCurrentUser().getEmail();
                    final int finalTemp = temp;
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UserEmail.setText("");
                                        userIdJustAdd = task.getResult().getUser().getUid();
                                        final HashMap userMap = new HashMap();
                                        userMap.put("email", email);
                                        userMap.put("role", role.get(roleChoose));
                                        if (roleChoose == 1 || roleChoose == 2) {
                                            if (roleChoose == 1) {
                                                if (classCheckBox.isChecked() == true) {
                                                    for (int i = 0; i < checkList.size(); i++) {
                                                        if (checkList.get(i).equals("1")) {
                                                            userMap.put("classname", className.get(i));
                                                            userMap.put("idclass", classId.get(i));
                                                            break;
                                                        }
                                                    }
                                                    ArrayList<String> temp = new ArrayList<>();
                                                    for (int i = 0; i < checkList.size(); i++) {
                                                        if (checkList.get(i).equals("1")) {
                                                            temp.add(classId.get(i));
                                                        }
                                                    }
                                                    userMap.put("myclass", temp);
                                                } else {
                                                    userMap.put("classname", className.get(classChoose));
                                                    userMap.put("idclass", classId.get(classChoose));
                                                    ArrayList<String> temp = new ArrayList<>();
                                                    temp.add(classId.get(classChoose));
                                                    userMap.put("myclass", temp);
                                                }
                                            } else {
                                                userMap.put("classname", className.get(classChoose));
                                                userMap.put("idclass", classId.get(classChoose));
                                            }
                                        }
                                        UsersRef.child(userIdJustAdd).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    if (roleChoose == 2) {
                                                        ClassRef.child(classId.get(classChoose)).child("teacher").setValue(userIdJustAdd);
                                                    }
                                                    Toast.makeText(ManageUserActivity.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                    mAuth.signInWithEmailAndPassword(mail, "123456")
                                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    Intent intent = new Intent(ManageUserActivity.this, ManageUserActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                    loadingBar.dismiss();
                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(ManageUserActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthUserCollisionException existEmail) {
                                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                                            UsersEventListener = ref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (final DataSnapshot children : dataSnapshot.getChildren()) {
                                                            //xác định nếu ko có role thì đã xóa
                                                            if (children.child("email").getValue().toString().equals(UserEmail.getText().toString())) {
                                                                //nếu email tạo trùng với email đã xóa thì chỉ cần thêm dữ liệu, ko cần tạo lại user
                                                                if (!children.hasChild("role")) {
                                                                    UserEmail.setText("");
                                                                    final HashMap userMap = new HashMap();
                                                                    userMap.put("role", role.get(roleChoose));
                                                                    if (roleChoose == 1 || roleChoose == 2) {
                                                                        if (roleChoose == 1) {
                                                                            if (classCheckBox.isChecked() == true) {
                                                                                for (int i = 0; i < checkList.size(); i++) {
                                                                                    if (checkList.get(i).equals("1")) {
                                                                                        userMap.put("classname", className.get(i));
                                                                                        userMap.put("idclass", classId.get(i));
                                                                                        break;
                                                                                    }
                                                                                }
                                                                                ArrayList<String> temp = new ArrayList<>();
                                                                                for (int i = 0; i < checkList.size(); i++) {
                                                                                    if (checkList.get(i).equals("1")) {
                                                                                        temp.add(classId.get(i));
                                                                                    }
                                                                                }
                                                                                userMap.put("myclass", temp);
                                                                            } else {
                                                                                userMap.put("classname", className.get(classChoose));
                                                                                userMap.put("idclass", classId.get(classChoose));
                                                                                ArrayList<String> temp = new ArrayList<>();
                                                                                temp.add(classId.get(classChoose));
                                                                                userMap.put("myclass", temp);
                                                                            }
                                                                        } else {
                                                                            userMap.put("classname", className.get(classChoose));
                                                                            userMap.put("idclass", classId.get(classChoose));
                                                                        }
                                                                    }
                                                                    ref.child(children.getKey()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task task) {
                                                                            if (task.isSuccessful()) {
                                                                                final String key = children.getKey();
                                                                                if (roleChoose == 2) {
                                                                                    ClassRef.child(classId.get(classChoose)).child("teacher").setValue(key);
                                                                                }
                                                                                //recreate();
                                                                                Toast.makeText(ManageUserActivity.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
                                                                                loadingBar.dismiss();
                                                                            } else {
                                                                                String message = task.getException().getMessage();
                                                                                Toast.makeText(ManageUserActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                                                loadingBar.dismiss();
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    Toast.makeText(ManageUserActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                                                    UserEmail.setText("");
                                                                }
                                                            }

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            Toast.makeText(ManageUserActivity.this, "Error Occured: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                }

                            });
                }
            }

        });
    }

    private void LoadAccountClass(final String roleChoose, final String idClassChoose) {
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().
                setQuery(UsersRef, User.class).build();
        FirebaseRecyclerAdapter<User, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder usersViewHolder, final int position, @NonNull final User model) {
                usersViewHolder.setIsRecyclable(false);
                //nếu chọn role thì sắp xếp theo role
                if (!roleChoose.equals("Choose Role...")) {
                    //nếu ko chọn class thì sẽ hiển thị theo role
                    if (idClassChoose.equals("")) {
                        if (model.getRole() != null && model.getRole().equals(roleChoose)) {
                            usersViewHolder.setFullname(model.getUsername());
                            usersViewHolder.setProfileImage(model.getProfileimage());
                            usersViewHolder.setEmail(model.getEmail());
                        } else usersViewHolder.Layout_hide();
                    }
                    //nếu vừa chọn role vừa chọn class thì hiển theo role rồi hiển thị theo class
                    else {
                        boolean view = false;
                        if (model.getRole() != null
                                && model.getIdclass() != null
                                && model.getRole().equals(roleChoose)) {
                            if (model.getRole().equals("Teacher")) {
                                if (model.getIdclass().equals(idClassChoose))
                                    view = true;
                            } else {
                                ArrayList<String> temp = model.getMyclass();
                                for (String node : temp) {
                                    if (node.equals(idClassChoose)) {
                                        view = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (view == true) {
                            usersViewHolder.setFullname(model.getUsername());
                            usersViewHolder.setProfileImage(model.getProfileimage());
                            usersViewHolder.setEmail(model.getEmail());
                        } else usersViewHolder.Layout_hide();
                    }
                }
                //ngược lại hiển thị toàn bộ user
                else {
                    if (model.getRole() != null) {
                        usersViewHolder.setFullname(model.getUsername());
                        usersViewHolder.setProfileImage(model.getProfileimage());
                        usersViewHolder.setEmail(model.getEmail());
                    } else usersViewHolder.Layout_hide();
                }
                final String usersIDs = getRef(position).getKey();
                usersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Detail User",
                                "Edit User",
                                "Delete User"
                        };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserActivity.this);
                        builder.setTitle("Select Option");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 2) {
                                    final String userID = getRef(position).getKey();
                                    String currentUser = mAuth.getCurrentUser().getUid();
                                    if (userID.equals(currentUser)) {
                                        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(ManageUserActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                                        dialogDelete.setMessage("Không thể xóa tài khoản của bạn");
                                        dialogDelete.setCancelable(false);
                                        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        dialogDelete.show();
                                    } else {
                                        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(ManageUserActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                                        dialogDelete.setMessage("Bạn có chắc muốn xóa user này?");
                                        dialogDelete.setCancelable(false);
                                        dialogDelete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                UsersRef.child(userID).removeValue();
                                                UsersRef.child(userID).child("email").setValue(model.getEmail());
                                                //xóa user
                                                ClassRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                                                if (children.hasChild("teacher") && children.child("teacher").getValue().toString().equals(userID)) {
                                                                    ClassRef.child(children.getKey()).child("teacher").setValue("");
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                dialogInterface.dismiss();
                                            }
                                        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        dialogDelete.show();
                                    }

                                }
                                if (which == 1) {
                                    Intent intent = new Intent(ManageUserActivity.this, EditAccountActivity.class);
                                    intent.putExtra("USER_ID", usersIDs);
                                    startActivity(intent);
                                }
                                if (which == 0) {
                                    Intent intent = new Intent(ManageUserActivity.this, ViewAccountActivity.class);
                                    intent.putExtra("USER_ID", usersIDs);
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
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_account_display_layout, parent, false);

                UsersViewHolder viewHolder = new UsersViewHolder(view);
                return viewHolder;
            }
        };
        myAccountList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_image;
        private TextView user_name;
        private TextView user_email;
        private final androidx.constraintlayout.widget.ConstraintLayout layout;
        final LinearLayout.LayoutParams params;

        public UsersViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.all_account_class_layout);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            user_image = layout.findViewById(R.id.all_account_profile_image);
            user_name = layout.findViewById(R.id.all_account_full_name);
            user_email = layout.findViewById(R.id.all_account_email);

        }

        public void setProfileImage(String profileimage) {
            Picasso.get().load(profileimage).placeholder(R.drawable.ic_person_black_50dp).into(user_image);

        }

        public void setFullname(String fullname) {
            user_name.setText(fullname);
        }

        public void setEmail(String student) {
            user_email.setText(student);
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
        Intent intent = new Intent(ManageUserActivity.this, AdminActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ClassEventListener != null) {
            ClassRef.removeEventListener(ClassEventListener);
        }
        if (UsersEventListener != null) {
            UsersRef.removeEventListener(UsersEventListener);
        }
        if (updateClass != null) {
            ClassRef.child(classId.get(classChoose)).removeEventListener(updateClass);
        }
        finish();
    }

}
