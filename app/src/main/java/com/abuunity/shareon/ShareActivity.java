package com.abuunity.shareon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.HashMap;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView close;
    private ImageView imageAdd;
    private TextView post;
    private String imageUrl;
    private SocialAutoCompleteTextView description;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Posting");
        dialog.setCancelable(false);

        close = findViewById(R.id.close);
        imageAdd = findViewById(R.id.image_add);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        firebaseAuth = FirebaseAuth.getInstance();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShareActivity.this, MainActivity.class));
                finish();
            }
        });

        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25 || resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
            imageAdd.setImageURI(imageUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        ArrayAdapter<Hashtag> hashtagArrayAdapter = new HashtagArrayAdapter<>(getApplicationContext());

        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    hashtagArrayAdapter.add(new Hashtag(snapshot1.getKey(), (int) snapshot.getChildrenCount()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        description.setHashtagAdapter(hashtagArrayAdapter);
    }

    private void upload() {
        dialog.show();
        if(imageUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            StorageTask uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    Uri downdoadUri = task.getResult();
                    imageUrl = downdoadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = ref.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("postid", postId);
                    map.put("imageurl", imageUrl);
                    map.put("description", description.getText().toString());
                    map.put("publisher", firebaseAuth.getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

                    DatabaseReference mHasTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hasTags = description.getHashtags();
                    if(!hasTags.isEmpty()) {
                        for(String tag : hasTags) {
                            map.clear();
                            map.put("tag", tag.toLowerCase());
                            map.put("postid", postId);

                            mHasTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }
                    }

                    dialog.dismiss();
                    startActivity(new Intent(ShareActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShareActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            imageUrl = "No Image";

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            String postId = ref.push().getKey();

            HashMap<String, Object> map = new HashMap<>();
            map.put("postid", postId);
            map.put("imageurl", imageUrl);
            map.put("description", description.getText().toString());
            map.put("publisher", firebaseAuth.getCurrentUser().getUid());

            ref.child(postId).setValue(map);

            DatabaseReference mHasTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
            List<String> hasTags = description.getHashtags();
            if(!hasTags.isEmpty()) {
                for(String tag : hasTags) {
                    map.clear();
                    map.put("tag", tag.toLowerCase());
                    map.put("postid", postId);

                    mHasTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                }
            }

            dialog.dismiss();
            startActivity(new Intent(ShareActivity.this, MainActivity.class));
        }
    }
}
