package com.abuunity.shareon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private final Context mContext;
    private List<Users> usersList;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UsersAdapter(Context mContext, List<Users> usersList, boolean isFragment) {
        this.mContext = mContext;
        this.usersList = usersList;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_users, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Users users = usersList.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.username.setText(users.getUsername());
        holder.fullName.setText(users.getName());

        Picasso.get().load(users.getImageUrl()).placeholder(R.drawable.ic_person).into(holder.imageProfil);

        isFollowed(users.getId(), holder.btnFollow);

        if(users.getId().equals(firebaseUser.getUid())) {
            holder.btnFollow.setVisibility(View.GONE);
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btnFollow.getText().toString().toLowerCase().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(firebaseUser.getUid()).child("following").child(users.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(users.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(firebaseUser.getUid()).child("following").child(users.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(users.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });


    }

    private void isFollowed(final String id, final Button btnFollow) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists()) {
                    btnFollow.setText("following");
                } else {
                    btnFollow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfil;
        public TextView username;
        public TextView fullName;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfil = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.full_name);
            btnFollow = itemView.findViewById(R.id.btn_follow);

        }
    }
}
