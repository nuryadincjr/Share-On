package com.abuunity.shareon.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abuunity.shareon.Adapter.TagAdapter;
import com.abuunity.shareon.Adapter.UsersAdapter;
import com.abuunity.shareon.Model.Users;
import com.abuunity.shareon.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerViewUsers;
    private SocialAutoCompleteTextView searchBar;
    private List<Users> usersList;
    private UsersAdapter usersAdapter;
    private RecyclerView recyclerViewTags;
    private List<String> tagList;
    private List<String> countList;
    private TagAdapter tagAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchBar = view.findViewById(R.id.search_bar);

        recyclerViewUsers = view.findViewById(R.id.rv_users);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsers.setHasFixedSize(true);
        usersList = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), usersList, true);
        recyclerViewUsers.setAdapter(usersAdapter);

        recyclerViewTags = view.findViewById(R.id.rv_tags);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTags.setHasFixedSize(true);
        tagList = new ArrayList<>();
        countList = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(), tagList, countList);
        recyclerViewTags.setAdapter(tagAdapter);

        readUsers();
        readTag();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }


    private void readTag() {
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tagList.clear();
                countList.clear();

                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    tagList.add(snapshot1.getKey());
                    countList.add(snapshot1.getChildrenCount() + "");
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(searchBar.getText().toString())) {
                    usersList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Users users = dataSnapshot.getValue(Users.class);
                        usersList.add(users);
                    }
                    usersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersList.add(users);
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text) {
        List<String> searchTag = new ArrayList<>();
        List<String> searchCount = new ArrayList<>();

        for(String s: tagList) {
            if(s.toLowerCase().contains(text.toLowerCase())) {
                searchTag.add(s);
                searchCount.add(countList.get(tagList.indexOf(s)));
            }
        }
        tagAdapter.filter(searchTag, searchCount);
    }
}