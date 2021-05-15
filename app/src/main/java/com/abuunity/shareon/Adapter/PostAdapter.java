package com.abuunity.shareon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abuunity.shareon.CommentActivity;
import com.abuunity.shareon.Model.Posts;
import com.abuunity.shareon.Model.Users;
import com.abuunity.shareon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Posts> postsList;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Posts> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Posts posts = postsList.get(position);
        Picasso.get().load(posts.getImageurl()).into(holder.imagePost);
        System.out.println(posts.getImageurl());
        holder.description.setText(posts.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(posts.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if(users.getImageUrl().equals("default")) {
                    holder.imageProfile.setImageResource(R.drawable.ic_person);
                } else {
                    Picasso.get().load(users.getImageUrl()).into(holder.imageProfile);
                }

                holder.username.setText(users.getUsername());
                holder.author.setText(users.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        isLiked(posts.getPostid(), holder.imageLike);
        countLike(posts.getPostid(), holder.likeCount);
        countComment(posts.getPostid(), holder.commentCount);

        holder.imageLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.imageLike.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(posts.getPostid()).child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(posts.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.imageComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", posts.getPostid());
                intent.putExtra("authorId", posts.getPublisher());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile;
        public ImageView imagePost;
        public ImageView imageLike;
        public ImageView imageComment;
        public ImageView imageSave;
        public ImageView imageMore;

        public TextView username;
        public TextView likeCount;
        public TextView author;
        public TextView commentCount;
        public SocialTextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            imagePost = itemView.findViewById(R.id.image_post);
            imageLike = itemView.findViewById(R.id.like_post);
            imageComment = itemView.findViewById(R.id.comment_post);
            imageSave = itemView.findViewById(R.id.save_post);
            imageMore = itemView.findViewById(R.id.image_more);

            username = itemView.findViewById(R.id.username);
            likeCount = itemView.findViewById(R.id.like_count);
            author = itemView.findViewById(R.id.title_post);
            commentCount = itemView.findViewById(R.id.comment_count);
            description = itemView.findViewById(R.id.description_post);

        }
    }

    private void isLiked(String postid, ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_love_fill);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_love);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countLike(String posId, final TextView textView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(posId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countComment(String posId, final TextView textView) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(posId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}