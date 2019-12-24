package com.example.preschool.Notifications;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.preschool.CommentsActivity;
import com.example.preschool.R;
import com.example.preschool.TimeLine.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView myNotificationList;
    private DatabaseReference PostsRef, UsersRef;
    private FirebaseAuth mAuth;
    private Bundle bundle;
    private String current_user_id, idClass,idTeacher;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_notification, container, false);

        bundle = getArguments();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher=bundle.getString("ID_TEACHER");
        }
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostsRef= FirebaseDatabase.getInstance().getReference().child("Class").child(idClass).child("Posts");

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myNotificationList = view.findViewById(R.id.notification_list);
        myNotificationList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myNotificationList.setLayoutManager(linearLayoutManager);
        DisplayAllNotification();

        return view;
    }

    private void DisplayAllNotification() {
        Query SortPostsInDecendingOrder = PostsRef;
        FirebaseRecyclerOptions<Posts> options=new FirebaseRecyclerOptions.Builder<Posts>().
                setQuery(SortPostsInDecendingOrder, Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> adapterMessages=new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder postsViewHolder, final int position, @NonNull Posts posts) {
//                final Intent chatIntent=new Intent(getActivity(), NewsFeedFragment.class);

                postsViewHolder.setIsRecyclable(false);

                String idUserPost=posts.getUid();
                UsersRef.child(idUserPost).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postsViewHolder.setFullname("Giáo viên");
                        postsViewHolder.setProfileImage(dataSnapshot.child("profileimage").getValue().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final String PostKey = getRef(position).getKey();
                postsViewHolder.setContent(" đã thêm một bài viết mới");
                postsViewHolder.setTime(posts.getTime());

                postsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntent=new Intent(getActivity(), CommentsActivity.class);
                        bundle.putString("KEY_POST",PostKey);
                        commentsIntent.putExtras(bundle);
                        startActivity(commentsIntent);
                    }
                });
            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View messagesView= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_notification_display_layout,parent,false);

                PostsViewHolder viewHolder=new PostsViewHolder(messagesView);
                return viewHolder;
            }
        };
        /////////////////////////////////////////////////////////////////////////////////////////
        myNotificationList.setAdapter(adapterMessages);
        adapterMessages.startListening();
    }

    @Override
    public void onRefresh() {

    }
    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView timePost;
        TextView userFullName;
        TextView postContent;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            timePost=mView.findViewById(R.id.all_notification_time);
            userFullName = mView.findViewById(R.id.all_notification_full_name);
            postContent=mView.findViewById(R.id.all_notification_content);
        }
        public void setContent(String content){
            postContent.setText(content);
        }

        public void setProfileImage(String profileimage) {
            CircleImageView userImage = mView.findViewById(R.id.all_notification_profile_image);
            Picasso.get().load(profileimage).resize(150,0).placeholder(R.drawable.ic_person_black_50dp).into(userImage);

        }

        public void setFullname(String fullname) {
            userFullName.setText(fullname);
        }

        public void setTime(String time) {
            timePost.setText(time);
        }
    }
}
