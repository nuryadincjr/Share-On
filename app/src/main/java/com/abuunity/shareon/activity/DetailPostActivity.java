package com.abuunity.shareon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.abuunity.shareon.R;
import com.abuunity.shareon.adapter.DetailPostAdapter;
import com.abuunity.shareon.pojo.Posts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailPostActivity extends AppCompatActivity {

    private String postId;
    private String authorId;

    private RecyclerView recyclerViewDetail;
    private DetailPostAdapter detailPostAdapter;
    private List<Posts> postsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");
        authorId = intent.getStringExtra("authorId");
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

        recyclerViewDetail = findViewById(R.id.rv_posts);
        recyclerViewDetail.setHasFixedSize(true);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(this));

        postsList = new ArrayList<>();
        detailPostAdapter = new DetailPostAdapter(this, postsList);
        recyclerViewDetail.setAdapter(detailPostAdapter);
        getPost();
    }

    private void getPost() {

    }
}