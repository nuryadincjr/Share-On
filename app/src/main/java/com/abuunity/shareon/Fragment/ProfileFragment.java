package com.abuunity.shareon.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.abuunity.shareon.EditProfileActivity;
import com.abuunity.shareon.Model.Posts;
import com.abuunity.shareon.Model.Users;
import com.abuunity.shareon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView imageProfil;
    private ImageView options;
    private ImageButton postsApp;
    private ImageButton saveApp;

    private TextView followers;
    private TextView following;
    private TextView posts;
    private TextView name;
    private TextView bio;
    private TextView username;

    private RecyclerView recyclerViewPosts;
    private RecyclerView recyclerViewSave;

    private FirebaseUser firebaseUser;
    private String profilid;
    private Button editProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profilid = firebaseUser.getUid();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if(data.equals("none")) {
            profilid = firebaseUser.getUid();
        } else{
            profilid = data;
        }

        imageProfil = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options_bar);
        postsApp = view.findViewById(R.id.post_bar);
        saveApp = view.findViewById(R.id.save_bar);

        followers = view.findViewById(R.id.followers_counts);
        following = view.findViewById(R.id.following_counts);
        posts = view.findViewById(R.id.post_counts);
        name = view.findViewById(R.id.full_name);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);

        recyclerViewPosts = view.findViewById(R.id.rv_my_posts);
        recyclerViewSave = view.findViewById(R.id.rv_my_save);
        editProfile = view.findViewById(R.id.edit_profile);

        userInfo();
        countsFollowersIng();
        countPosts();

        if(profilid.equals(firebaseUser.getUid())) {
            editProfile.setText("Edit profile");
        } else {
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();

                if(btnText.equals("Edit profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else {
                    if(btnText.equals("follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profilid).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profilid)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profilid).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profilid)
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }
            }
        });

        return view;
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profilid).exists()) {
                    editProfile.setText("following");
                }else{
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Posts posts1 = dataSnapshot.getValue(Posts.class);

                    if(posts1.getPublisher().equals(profilid)) counter++;

                }
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countsFollowersIng() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profilid);

        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profilid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if(users.getImageUrl().equals("default")) {
                    imageProfil.setImageResource(R.drawable.ic_person);
                } else {
                    Picasso.get().load(users.getImageUrl()).into(imageProfil);
                }

                username.setText(users.getUsername());
                name.setText(users.getName());
                bio.setText(users.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}