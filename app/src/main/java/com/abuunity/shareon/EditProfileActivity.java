package com.abuunity.shareon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.abuunity.shareon.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView closeBtn;
    private TextView savesBtn;
    private CircleImageView imageProfile;
    private ImageButton browsersBtn;
    private TextView name;
    private TextView usernanme;
    private TextView bio;

    private Uri imageUri;
    private FirebaseUser firebaseUser;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Posting");
        dialog.setCancelable(false);

        closeBtn = findViewById(R.id.close);
        savesBtn = findViewById(R.id.save);
        imageProfile = findViewById(R.id.image_profile);
        browsersBtn = findViewById(R.id.btn_browseres);
        name = findViewById(R.id.name);
        usernanme = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profiles");
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                name.setText(users.getName());
                usernanme.setText(users.getUsername());
                bio.setText(users.getBio());

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

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        browsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });

        savesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        dialog.show();

        if(imageUri != null) {
            StorageReference filePath = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    Uri downdoadUri = (Uri) task.getResult();
                    String url = downdoadUri.toString();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", name.getText().toString());
                    map.put("username", usernanme.getText().toString());
                    map.put("bio", bio.getText().toString());
                    map.put("imageUrl", url);

                    FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);
                }
            });
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", name.getText().toString());
            map.put("username", usernanme.getText().toString());
            map.put("bio", bio.getText().toString());

            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);
        }

        dialog.dismiss();
    }

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25 || resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
            imageProfile.setImageURI(imageUri);
        }
    }
}