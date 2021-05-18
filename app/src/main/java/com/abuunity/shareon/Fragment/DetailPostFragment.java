package com.abuunity.shareon.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abuunity.shareon.Adapter.PostAdapter;
import com.abuunity.shareon.Model.Posts;
import com.abuunity.shareon.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailPostFragment extends Fragment {

    private String postId;
    private RecyclerView recyclerViewDetail;
    private PostAdapter postAdapter;
    private List<Posts> postsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_post, container, false);

        postId = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postId", "none");

        recyclerViewDetail = view.findViewById(R.id.rv_posts);
        recyclerViewDetail.setHasFixedSize(true);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(getContext()));

        postsList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postsList);
        recyclerViewDetail.setAdapter(postAdapter);

        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                postsList.add(snapshot.getValue(Posts.class));

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}