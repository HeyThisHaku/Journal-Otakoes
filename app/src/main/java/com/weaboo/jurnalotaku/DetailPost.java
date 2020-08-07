package com.weaboo.jurnalotaku;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.annotation.Nullable;

import adapter.ImageAdapter;
import adapter.RecycleViewCommentAdapter;
import database.Database;
import models.CommentModels;
import models.Post;
import session.Session;

public class DetailPost extends AppCompatActivity {

    private Post postingan = new Post();
    private Vector<CommentModels> listComment = new Vector<>();
    private ImageView imageCover;
    private TextView title;
    private TextView authorAndView;
    private Boolean hasWrite = false;
    private Boolean asc = false;
    private TextView post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        imageCover = findViewById(R.id.photo_cover);
        title = findViewById(R.id.title_text);
        post = findViewById(R.id.post);
        authorAndView = findViewById(R.id.authorandcount_detail);
        if(Session.getData("id_postingan") != null){
            Log.d("Masuk","Masuk1");
            getAllPostinganData();
            setShareLinkButton();
            setFavouriteButton();
            setFavouriteEnabledButton();
            ImageView sort = findViewById(R.id.sortComment);
            sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sortComment();
                }
            });
            final ScrollView scrollView = findViewById(R.id.scroll_detail);
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    int scrollY = scrollView.getScrollY();
                    if(scrollY >= 1000) {
                        if (!hasWrite) {
                            FirebaseDatabase firebase = FirebaseDatabase.getInstance();
                            DatabaseReference writerBase = firebase.getReference("Postingan");
                            writerBase.child(Session.getData("id_postingan")
                                    .toString())
                                    .child("ViewCount")
                                    .setValue(Integer.parseInt(postingan.getViews().toString()) + 1);
                            hasWrite = true;
                        }
                    }
                }
            });

        }
        else{
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
    }
    private void sortComment(){
        Vector<CommentModels> tempComment = listComment;

        if(!asc) {
            Collections.sort(tempComment, new Comparator<CommentModels>() {
                @Override
                public int compare(CommentModels o1, CommentModels o2) {
                    return o1.getLiked() - o2.getLiked();
                }
            });
            asc = true;
        }
        else{
            Collections.sort(tempComment, new Comparator<CommentModels>() {
                @Override
                public int compare(CommentModels o1, CommentModels o2) {
                    return o2.getLiked() - o1.getLiked();
                }
            });
            asc = false;
        }
        listComment = tempComment;
        RecyclerView recyclerView = findViewById(R.id.RecycleViewComment);
        RecycleViewCommentAdapter adapter = new RecycleViewCommentAdapter(listComment, DetailPost.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailPost.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    private void getAllComment(){
        //write
        final Database database = Database.getInstance();
        database.setRefer("Komentar");
        database.getRefer().addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    String id = data.child("IdPostingan").getValue().toString();
                    try {
                        if(!id.equalsIgnoreCase(Session.getData("id_postingan")))
                            continue;
                    }catch (Exception e){

                    }
                    CommentModels comment = new CommentModels();
                    comment.setName(data.child("Name").getValue().toString());
                    comment.setComment(data.child("Comment").getValue().toString());
                    comment.setLiked(Integer.parseInt(data.child("Liked").getValue().toString()));
                    listComment.add(comment);
                    RecyclerView recyclerView = findViewById(R.id.RecycleViewComment);
                    RecycleViewCommentAdapter adapter = new RecycleViewCommentAdapter(listComment, DetailPost.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailPost.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err",databaseError.getMessage().toString());
            }
        });

        //set post
        ImageView postComment = findViewById(R.id.postComment);
        final EditText commentInput = findViewById(R.id.input_comment);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postComment(commentInput.getText().toString());
                commentInput.setText("");

            }
        });
    }

    private void postComment(final String comment) {
        if(!comment.equals("") && !comment.equals(" ") && !comment.isEmpty()){
            FirebaseDatabase firebase = FirebaseDatabase.getInstance();
            final DatabaseReference writerBase = firebase.getReference("Komentar");
            writerBase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id="";
                    Log.d("Jumlah Child",dataSnapshot.getChildrenCount()+"");
                    if(dataSnapshot.getChildrenCount() < 10){
                        id = "comment00"+(dataSnapshot.getChildrenCount()+1);
                    }else if(dataSnapshot.getChildrenCount() < 100){
                        id = "comment0"+(dataSnapshot.getChildrenCount()+1);
                    }else{
                        id = "comment"+(dataSnapshot.getChildrenCount()+1);
                    }


                    final CommentModels model = new CommentModels();
                    FirebaseAuth fAuth;
                    GoogleSignInAccount acct;
                    FirebaseFirestore fStore;
                    StorageReference storageReference;
                    String userId;
                    fAuth= FirebaseAuth.getInstance();
                    acct = GoogleSignIn.getLastSignedInAccount(DetailPost.this);
                    fStore=FirebaseFirestore.getInstance();
                    userId=fAuth.getCurrentUser().getUid();
                    storageReference = FirebaseStorage.getInstance().getReference();
                    fStore=FirebaseFirestore.getInstance();
                    DocumentReference documentReference=fStore.collection("users").document(userId);
                    final String finalId = id;
                    documentReference.addSnapshotListener(DetailPost.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(documentSnapshot!=null){
                                model.setName(documentSnapshot.getString("username"));
                                model.setComment(comment);
                                model.setLiked(0);
                                writerBase.child(finalId).child("Comment").setValue(model.getComment());
                                writerBase.child(finalId).child("IdPostingan").setValue(Session.getData("id_postingan").toString());
                                writerBase.child(finalId).child("Liked").setValue(model.getLiked());
                                writerBase.child(finalId).child("Name").setValue(model.getName());
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void setFavouriteEnabledButton(){
        final ImageView favouriteEnabled=findViewById(R.id.favorite_btn_on);
        favouriteEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Database database = Database.getInstance();
                database.setRefer("Postingan");
                database.getRefer().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("Masuk2", Session.getData("id_postingan").toString());
                        DataSnapshot data = dataSnapshot.child(Session.getData("id_postingan"));
                        Log.d("Masuk3",data.child("Author").getValue().toString());
                        Log.d("Masuk4",data.child("ImageUri").getValue().toString());
                        final Post currentPost = new Post();
                        currentPost.setTitle(data.child("Title").getValue().toString());
                        currentPost.setAuthor(data.child("Author").getValue().toString());
                        currentPost.setId(data.getKey());

                        currentPost.setViews(Integer.parseInt(data.child("ViewCount").getValue().toString()));
                        currentPost.setImageUrl(data.child("ImageUri").getValue().toString());
                        currentPost.setPost(data.child("Description").getValue().toString());
                        FirebaseAuth fAuth=FirebaseAuth.getInstance();
                        FirebaseFirestore fStore=FirebaseFirestore.getInstance();
                        fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                                .collection("posts").document(currentPost.getId()).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("Post Store",currentPost.getId()+"");
                                        Toast.makeText(DetailPost.this,"Removet post from favourite post",Toast.LENGTH_SHORT).show();
                                        favouriteEnabled.setVisibility(View.GONE);
                                        ImageView button=findViewById(R.id.favorite_btn);
                                        button.setVisibility(View.VISIBLE);
                                    }
                                });
//                        postingan = currentPost;
//                        title.setText(postingan.getTitle());
//                        authorAndView.setText("Author: "+currentPost.getAuthor()+" Viewed: "+currentPost.getViews());
//                        ImageAdapter.loadImageUrl(postingan.getImageUrl(), DetailPost.this,imageCover);
//                        post.setText(postingan.getPost());
//                        getAllComment();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("err",databaseError.getMessage().toString());
                    }
                });
            }
        });
    }
    private void setFavouriteButton(){
        final ImageView button=findViewById(R.id.favorite_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Database database = Database.getInstance();
                database.setRefer("Postingan");
                database.getRefer().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("Masuk2", Session.getData("id_postingan").toString());
                        DataSnapshot data = dataSnapshot.child(Session.getData("id_postingan"));
                        Log.d("Masuk3",data.child("Author").getValue().toString());
                        Log.d("Masuk4",data.child("ImageUri").getValue().toString());
                        final Post currentPost = new Post();
                        currentPost.setTitle(data.child("Title").getValue().toString());
                        currentPost.setAuthor(data.child("Author").getValue().toString());
                        currentPost.setId(data.getKey());

                        currentPost.setViews(Integer.parseInt(data.child("ViewCount").getValue().toString()));
                        currentPost.setImageUrl(data.child("ImageUri").getValue().toString());
                        currentPost.setPost(data.child("Description").getValue().toString());
                        FirebaseAuth fAuth=FirebaseAuth.getInstance();
                        FirebaseFirestore fStore=FirebaseFirestore.getInstance();
                        fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                                .collection("posts").document(currentPost.getId()).set(currentPost)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("Post Store",currentPost.getId()+"");
                                        Toast.makeText(DetailPost.this,"Successfully added to favourite post",Toast.LENGTH_SHORT).show();
                                        button.setVisibility(View.GONE);
                                        ImageView favouriteEnabled=findViewById(R.id.favorite_btn_on);
                                        favouriteEnabled.setVisibility(View.VISIBLE);

                                    }
                                });
//                        postingan = currentPost;
//                        title.setText(postingan.getTitle());
//                        authorAndView.setText("Author: "+currentPost.getAuthor()+" Viewed: "+currentPost.getViews());
//                        ImageAdapter.loadImageUrl(postingan.getImageUrl(), DetailPost.this,imageCover);
//                        post.setText(postingan.getPost());
//                        getAllComment();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("err",databaseError.getMessage().toString());
                    }
                });
            }
        });
    }
    private void setShareLinkButton(){
        ImageView button = this.findViewById(R.id.shareButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,postingan.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT,postingan.getPost());
                startActivity(Intent.createChooser(intent,"Share using"));
            }
        });
    }

    private void getAllPostinganData(){
        final Database database = Database.getInstance();
        database.setRefer("Postingan");
        database.getRefer().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Masuk2", Session.getData("id_postingan").toString());
                    DataSnapshot data = dataSnapshot.child(Session.getData("id_postingan"));
                Log.d("Masuk3",data.child("Author").getValue().toString());
                Log.d("Masuk4",data.child("ImageUri").getValue().toString());
                    Post currentPost = new Post();
                    currentPost.setTitle(data.child("Title").getValue().toString());
                    currentPost.setAuthor(data.child("Author").getValue().toString());
                    currentPost.setId(data.getKey());

                    currentPost.setViews(Integer.parseInt(data.child("ViewCount").getValue().toString()));
                    currentPost.setImageUrl(data.child("ImageUri").getValue().toString());
                    currentPost.setPost(data.child("Description").getValue().toString());
                    postingan = currentPost;
                    title.setText(postingan.getTitle());
                    authorAndView.setText("Author: "+currentPost.getAuthor()+" Viewed: "+currentPost.getViews());
                    ImageAdapter.loadImageUrl(postingan.getImageUrl(), DetailPost.this,imageCover);
                    post.setText(postingan.getPost());
                    FirebaseAuth fAuth=FirebaseAuth.getInstance();
                    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
                    fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                            .collection("posts").document(currentPost.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    Log.d("Res", "DocumentSnapshot data: " + document.getData());
                                    ImageView button=findViewById(R.id.favorite_btn);
                                    button.setVisibility(View.GONE);
                                    ImageView favouriteEnabled=findViewById(R.id.favorite_btn_on);
                                    favouriteEnabled.setVisibility(View.VISIBLE);
                                } else {
                                    Log.d("Res", "No such document");
                                }
                            }

                        }
                    });
                    getAllComment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err",databaseError.getMessage().toString());
            }
        });
    }
}
