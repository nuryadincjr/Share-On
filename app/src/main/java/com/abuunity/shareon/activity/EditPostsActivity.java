package com.abuunity.shareon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abuunity.shareon.Interface.ItemClickListener;
import com.abuunity.shareon.Interface.OnEditTextChanged;
import com.abuunity.shareon.MainActivity;
import com.abuunity.shareon.R;
import com.abuunity.shareon.api.CommentsRepository;
import com.abuunity.shareon.api.HashtagRepository;
import com.abuunity.shareon.api.PostRepository;
import com.abuunity.shareon.adapter.StepsAdapter;
import com.abuunity.shareon.adapter.ToolsAdapter;
import com.abuunity.shareon.fragment.HomeFragment;
import com.abuunity.shareon.pojo.Posts;
import com.abuunity.shareon.pojo.Steps;
import com.abuunity.shareon.pojo.Tools;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditPostsActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView close;
    private ImageView imageAdd;
    private TextView post;
    private SocialAutoCompleteTextView title;
    private SocialAutoCompleteTextView description;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;

    private RecyclerView recyclerViewTools;
    private List<Tools> toolsList;
    private ToolsAdapter toolsAdapter;
    private Tools tools;

    private RecyclerView recyclerViewSteps;
    private List<Steps> stepsList;
    private StepsAdapter stepsAdapter;
    private ImageView imageSteps;
    private Steps steps;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private StorageTask uploadTask;

    private String postid, titles, descriptions, images, publishers;
    private List<String> hasTagsOld;

    public EditPostsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_posts);

        recyclerViewSteps = findViewById(R.id.rv_steps);
        stepsList = new ArrayList<>();
        steps = new Steps("Add your steps");
        stepsList.add(steps);
        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback2);
        itemTouchHelper.attachToRecyclerView(recyclerViewSteps);
        stepsAdapter = new StepsAdapter(stepsList);
        recyclerViewSteps.setAdapter(stepsAdapter);

        recyclerViewTools = findViewById(R.id.rv_tools);
        toolsList = new ArrayList<>();
        tools = new Tools("Add your tools");
        toolsList.add(tools);
        recyclerViewTools.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleCallback);
        itemTouchHelper2.attachToRecyclerView(recyclerViewTools);
        toolsAdapter = new ToolsAdapter(toolsList);
        recyclerViewTools.setAdapter(toolsAdapter);

        storageReference = FirebaseStorage.getInstance().getReference().child("posts");
        firebaseFirestore = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Posting");
        dialog.setCancelable(false);

        close = findViewById(R.id.close);
        imageAdd = findViewById(R.id.image_add);
        post = findViewById(R.id.post);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        firebaseAuth = FirebaseAuth.getInstance();
        imageSteps = findViewById(R.id.image_steps);

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        titles = intent.getStringExtra("title");
        descriptions = intent.getStringExtra("descriptions");
        images = intent.getStringExtra("image");
        publishers = intent.getStringExtra("publisher");

        title.setText(titles);
        description.setText(descriptions);
        Picasso.get().load(images).into(imageAdd);
        hasTagsOld = description.getHashtags();


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditPostsActivity.this, MainActivity.class));
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

        toolsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                openMenuEditTools(view,position);
            }
        });

        stepsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                openMenuEditSteps(view,position);
            }
        });
    }

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25 && resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
            imageAdd.setImageURI(imageUri);

        } else if (requestCode == 26 && resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
            imageSteps.setImageURI(imageUri);
        }else {
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

        String uid = firebaseAuth.getCurrentUser().getUid();
        List<String> hasTags = description.getHashtags();
        List<String> tools = new ArrayList<>();
        for(int i=0; i < toolsAdapter.getItemCount(); i++){
            ToolsAdapter.ViewHolder viewHolder = (ToolsAdapter.ViewHolder) recyclerViewTools.findViewHolderForAdapterPosition(i);
            EditText editText =viewHolder.inputTools;
            System.out.println(editText.getText().toString());
            tools.add(editText.getText().toString());
        }

        List<String> steps = new ArrayList<>();
        for(int i=0; i < stepsAdapter.getItemCount(); i++){
            StepsAdapter.ViewHolder viewHolder = (StepsAdapter.ViewHolder) recyclerViewSteps.findViewHolderForAdapterPosition(i);
            EditText editText =viewHolder.inputTools;
            System.out.println(editText.getText().toString());
            steps.add(editText.getText().toString());
        }

        Posts posts = new Posts(postid, title.getText().toString(), description.getText().toString(), images, publishers, tools, steps);

        if(imageUri != null) {
            if (imageUri==null || !imageUri.equals("default")){
                storageReference.child(postid+"/"+HomeFragment.getImagPath(images)).delete();
            }

            StorageReference filePath = storageReference.child(postid+"/"+System.currentTimeMillis()+ "." + getFileExtension(imageUri));
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

                    posts.setImageurl(url);
                    new PostRepository().editPosts(posts);
                    new HashtagRepository().deleteHashtags(hasTagsOld, hasTags, postid, publishers);

                    dialog.dismiss();
                    startActivity(new Intent(EditPostsActivity.this, MainActivity.class));
                }
            });
        } else {

            new PostRepository().editPosts(posts);
            new HashtagRepository().deleteHashtags(hasTagsOld, hasTags, postid, publishers);

            dialog.dismiss();
            startActivity(new Intent(EditPostsActivity.this, MainActivity.class));
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(toolsList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            recyclerView.getAdapter().notifyDataSetChanged();
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    ItemTouchHelper.SimpleCallback simpleCallback2 = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(stepsList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            recyclerView.getAdapter().notifyDataSetChanged();
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };


    public void openMenuEditTools(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_adder, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.add:
                    toolsList.add(tools);
                    toolsAdapter.notifyItemInserted(position);
                    toolsAdapter.notifyItemRangeChanged(position, toolsList.size());
                    toolsAdapter.notifyDataSetChanged();

                    break;
                case R.id.delete:
                    toolsList.remove(position);
                    toolsAdapter.notifyItemRemoved(position);
                    toolsAdapter.notifyItemRangeChanged(position, toolsList.size());
                    toolsAdapter.notifyDataSetChanged();
                    break;
            }
            return true;
        });
        popupMenu.show();
    }

    public void openMenuEditSteps(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_adder, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.add:
                    stepsList.add(steps);
                    stepsAdapter.notifyItemInserted(position);
                    stepsAdapter.notifyItemRangeChanged(position, stepsList.size());
                    stepsAdapter.notifyDataSetChanged();

                    break;
                case R.id.delete:
                    stepsList.remove(position);
                    stepsAdapter.notifyItemRemoved(position);
                    stepsAdapter.notifyItemRangeChanged(position, stepsList.size());
                    stepsAdapter.notifyDataSetChanged();
                    break;
            }
            return true;
        });
        popupMenu.show();
    }
}
