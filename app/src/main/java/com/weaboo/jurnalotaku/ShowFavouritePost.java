package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import adapter.RecycleViewPostinganAdapter;
import models.Post;

public class ShowFavouritePost extends AppCompatActivity {
    RecyclerView recView;
    FirebaseAuth fAuth;
    RecycleViewPostinganAdapter adapter;
    ImageButton backBtn;
    TextView noFav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favourite_post);
        noFav=findViewById(R.id.fav_post_not_exists);
        recView=findViewById(R.id.all_favourite);
        recView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new RecycleViewPostinganAdapter(null,ShowFavouritePost.this);
        recView.setAdapter(adapter);
        backBtn=findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHome=new Intent(ShowFavouritePost.this,Profile.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toHome);
            }
        });
        fAuth= FirebaseAuth.getInstance();
        String id=fAuth.getCurrentUser().getUid();
        FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        fStore.collection("users").document(id).collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen", "Listen failed.", e);
                    return;
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    Log.d("Listen", "Current data: " + queryDocumentSnapshots.getDocuments());
                    noFav.setVisibility(View.GONE);
                    getList();

                } else {
                    Log.d("Listen", "Current data: null");
                    getList();
                    noFav.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    private void getList(){
        final Vector<Post> posts=new Vector<>();
        fAuth= FirebaseAuth.getInstance();
        String id=fAuth.getCurrentUser().getUid();
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        fStore.collection("users").document(id).
                collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Map<String, Object> pr=document.getData();
                        Post post=new Post();
                        post.setId(pr.get("id").toString());
                        post.setAuthor(pr.get("author").toString());
                        post.setImageUrl(pr.get("imageUrl").toString());
                        post.setTitle(pr.get("title").toString());
                        post.setViews(Integer.parseInt(pr.get("views").toString()));
                        post.setPost(pr.get("post").toString());
                        posts.add(post);
                    }
                    adapter.setDataList(posts);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
