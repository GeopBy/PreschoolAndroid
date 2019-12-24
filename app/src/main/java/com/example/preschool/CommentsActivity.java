package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preschool.TimeLine.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private ImageButton PostOptionButton;
    private EditText CommentInputText;
    private DatabaseReference UsersRef, PostsRef,LikesRef;
    private ValueEventListener commentListener;
    private ViewPager PostImage;
    private TextView PostDescription, PostName;
    private CircleImageView PostProfileImage;
    private TextView PostTime, PostDate;
    private TextView LikeButton, CommentButton;
    private LinearLayout linearLayoutLike, linearLayoutCmt;
    private ImageView imageViewLike;

    private DatabaseReference clickPostRef, CommentsRef;

    private String Post_Key, current_user_id, idClass, idTeacher;
    private FirebaseAuth mAuth;
    private String postimage, profileimage, description, time, date, postname;
    Boolean LikeChecker = false;

    private TextView DisplayNoOfLikes, DisplayNoOfComments;
    int countLikes, countComments;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bình luận");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Get Bundle
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher = bundle.getString("ID_TEACHER");
            Post_Key = bundle.getString("KEY_POST");
            bundle.remove("KEY_POST");
        }


        PostName = findViewById(R.id.post_user_name);
        PostImage = findViewById(R.id.post_image);
        PostDescription = findViewById(R.id.post_description);
        PostProfileImage = findViewById(R.id.post_profile_image);
        PostTime = findViewById(R.id.post_time);
        PostDate = findViewById(R.id.post_date);
        LikeButton = findViewById(R.id.like_button);
        CommentButton = findViewById(R.id.comment_button);
        linearLayoutLike=findViewById(R.id.linearLayoutLike);
        linearLayoutCmt=findViewById(R.id.linearLayoutCmt);
        DisplayNoOfLikes = findViewById(R.id.display_no_of_likes);
        imageViewLike=findViewById(R.id.img_like);
        PostOptionButton=findViewById(R.id.post_option_button);
        PostOptionButton.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        PostsRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Posts").child(Post_Key).child("Comments");

//        PostsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
////                    long a=dataSnapshot.getChildrenCount();
////                    int b=(int) a;
//                    CommentsList.scrollToPosition((int)dataSnapshot.getChildrenCount()+1);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        clickPostRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Posts").child(Post_Key);
        LikesRef=FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Posts").child(Post_Key).child("Likes");

        CommentsList = findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(false);
        CommentsList.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        CommentInputText = findViewById(R.id.comment_input);
        PostCommentButton = findViewById(R.id.post_comment_btn);

        DisplayNoOfComments = findViewById(R.id.display_no_of_comments);

        CommentsRef = FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Posts");

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentListener = UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userName = dataSnapshot.child("username").getValue().toString();
                            String urlAvatar = dataSnapshot.child("profileimage").getValue().toString();
                            ValidateComment(userName, urlAvatar);
                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        clickPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setLikeButtonStatus(Post_Key);
                    setCommentPostButtonStatus(Post_Key);
                    setImagePost(CommentsActivity.this, dataSnapshot.getValue(Posts.class).getPostimage());
                    description = dataSnapshot.child("description").getValue().toString();

//                    postimage = dataSnapshot.child("postimage").getValue().toString();
                    time = dataSnapshot.child("time").getValue().toString();
                    date = dataSnapshot.child("date").getValue().toString();


                    PostDescription.setText(description);
//                    Picasso.get().load(postimage).resize(600, 0).into(PostImage);

//                    PostImage.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Dialog dialogImage = new Dialog(CommentsActivity.this, R.style.DialogViewImage);
//                            dialogImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            dialogImage.setCancelable(true);
//                            dialogImage.setContentView(R.layout.dialog_show_image_post);
//                            ImageView imageView = dialogImage.findViewById(R.id.image_post_view);
//
//                            Picasso.get().load(postimage).networkPolicy(NetworkPolicy.NO_CACHE)
//                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                    .placeholder(PostImage.getDrawable())
//                                    .into(imageView);
//                            dialogImage.show();
//                        }
//                    });

                    UsersRef.child(idTeacher).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final String image = dataSnapshot.child("profileimage").getValue().toString();
                                final String name = dataSnapshot.child("fullnameteacher").getValue().toString();
                                PostName.setText(name);
                                Picasso.get().load(image).resize(100, 0).into(PostProfileImage);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    PostTime.setText(time);
                    PostDate.setText(date);
                    linearLayoutCmt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommentInputText.requestFocus();
                        }
                    });
                    linearLayoutLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LikeChecker = true;
                            LikesRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (LikeChecker.equals(true)) {
                                        if (dataSnapshot.hasChild(current_user_id)) {
                                            LikesRef.child(current_user_id).removeValue();
                                            LikeChecker = false;
                                        } else {
                                            LikesRef.child(current_user_id).setValue(true);
                                            LikeChecker = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setImagePost(Context context, ArrayList<String> postimage) {
        AdapterImagePost adapter = new AdapterImagePost(context, postimage);
        PostImage.setAdapter(adapter);
    }

    private void setCommentPostButtonStatus(final String PostKey) {
        CommentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(PostKey).child("Comments").hasChild(current_user_id)) {
                    countComments = (int) dataSnapshot.child(PostKey).child("Comments").getChildrenCount();
                    CommentsList.scrollToPosition(countComments - 1);
                    DisplayNoOfComments.setText((Integer.toString(countComments) + " Comments"));
                } else {
                    countComments = (int) dataSnapshot.child(PostKey).child("Comments").getChildrenCount();
                    CommentsList.scrollToPosition(countComments - 1);
                    DisplayNoOfComments.setText((Integer.toString(countComments) + " Comments"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLikeButtonStatus(final String PostKey) {

        LikesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(current_user_id)) {
                    countLikes = (int) dataSnapshot.getChildrenCount();
                    imageViewLike.setImageResource(R.drawable.ic_favorite_black_25dp);
                    DisplayNoOfLikes.setText((Integer.toString(countLikes) + " Likes"));
                    LikeButton.setTextColor(Color.parseColor("#FF5722"));
                } else {
                    countLikes = (int) dataSnapshot.getChildrenCount();
                    imageViewLike.setImageResource(R.drawable.ic_favorite_border_black_25dp);
                    DisplayNoOfLikes.setText((Integer.toString(countLikes) + " Likes"));
                    LikeButton.setTextColor(Color.parseColor("#959292"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(PostsRef, Comments.class).build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder commentsViewHolder, int position, @NonNull Comments comments) {
                commentsViewHolder.setUsername(comments.getUsername());
                commentsViewHolder.setComment(comments.getComment());
                commentsViewHolder.setDate(comments.getDate());
                commentsViewHolder.setTime(comments.getDate()+" "+comments.getTime());
                commentsViewHolder.setAvatar(comments.getAvatar());

            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comments_layout, parent, false);
                return new CommentsViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
//        firebaseRecyclerAdapter.notifyDataSetChanged();
        CommentsList.setAdapter(firebaseRecyclerAdapter);

    }

    private void showComment(){

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setAvatar(String urlAvatar) {
            ImageView avatar = mView.findViewById(R.id.avatar_comment);
            Picasso.get().load(urlAvatar).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).resize(50, 0).placeholder(R.drawable.ic_person_black_24dp).into(avatar);
        }

        public void setComment(String comment) {
            TextView myComment = mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }


        public void setDate(String date) {
            TextView myDate = mView.findViewById(R.id.comment_date);
            myDate.setText(date);
        }


        public void setTime(String time) {
            TextView myTime = mView.findViewById(R.id.comment_time);
            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String nowTime = currentTime.format(calFordTime.getTime());
            ReturnMinute returnMinute = new ReturnMinute();
            long a = returnMinute.getMinute(time, nowTime);
            if (a <= 60) {
                myTime.setText((String.valueOf(a) + " phút trước"));
            } else {
                if (a > 1440) myTime.setText((String.valueOf(a / 1440) + " ngày trước"));
                else myTime.setText((String.valueOf(a / 60) + " giờ trước"));
            }
        }

        public void setUsername(String username) {
            TextView myUserName = mView.findViewById(R.id.comment_username);
            myUserName.setText(username);
        }
    }

    private void ValidateComment(String userName, String urlAvatar) {
        String commentText = CommentInputText.getText().toString();
        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "please write text to comment...", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            String RandomKey = FirebaseDatabase.getInstance().getReference().push().getKey();
            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", current_user_id);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", userName);
            commentsMap.put("avatar", urlAvatar);
            PostsRef.child(RandomKey).setValue(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(CommentsActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommentsActivity.this, "Không thể bình luận, kiểm tra đường truyền mạng của bạn", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (commentListener != null)
            UsersRef.child(current_user_id).removeEventListener(commentListener);
    }
}
