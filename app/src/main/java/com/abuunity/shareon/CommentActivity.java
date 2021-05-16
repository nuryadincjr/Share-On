package com.abuunity.shareon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.abuunity.shareon.Adapter.CommentAdapter;
import com.abuunity.shareon.Model.Comments;
import com.abuunity.shareon.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    RecyclerView recyclerViewComment;
    private CommentAdapter commentAdapter;
    private List<Comments> commentsList;

    private EditText addComment;
    private ImageView postComment;
    private CircleImageView imageProfile;

    private String postId;
    private String authorId;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        recyclerViewComment = findViewById(R.id.rv_comments);
        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));
        commentsList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentsList, postId);
        recyclerViewComment.setAdapter(commentAdapter);

        addComment = findViewById(R.id.comment_add);
        imageProfile = findViewById(R.id.image_profile);
        postComment = findViewById(R.id.comment_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getUserImage();

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString())) {
                    Toast.makeText(CommentActivity.this, "No comment added", Toast.LENGTH_SHORT).show();
                } else {
                    putComments();
                }
            }
        });

        getComment();
    }

    private void getComment() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comments comments = dataSnapshot.getValue(Comments.class);
                    commentsList.add(comments);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComments() {
        HashMap<String, Object> map = new HashMap<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String id = reference.push().getKey();

        map.put("id", id);
        map.put("comment", addComment.getText().toString());
        map.put("publisher", firebaseUser.getUid());

        reference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    addComment.setText("");
                    Toast.makeText(CommentActivity.this, "Comment added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if(users.getImageUrl().equals("default")) {
                    imageProfile.setImageResource(R.drawable.ic_person);
                } else {
                    Picasso.get().load(users.getImageUrl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}