package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

import adapter.RecycleViewPostinganAdapter;
import adapter.RecycleViewPostinganAdminAdapter;
import database.Database;
import models.Post;

public class FragmentManagePost extends Fragment {

    Vector<Post> listPostingan = new Vector<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_manage_post,container,false);


        //nanti di isi disini code nya
        getAllPostinganData(view,container.getContext());
        Button insertBtn=view.findViewById(R.id.insert_btn);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AdminInsert.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getAllPostinganData(final View view, final Context containerContext){
        final Database database = Database.getInstance();
        database.setRefer("Postingan");
        database.getRefer().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPostingan.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    Post currentPost = new Post();
                    currentPost.setTitle(data.child("Title").getValue().toString());
                    currentPost.setAuthor(data.child("Author").getValue().toString());
                    currentPost.setId(data.getKey());
                    currentPost.setViews(Integer.parseInt(data.child("ViewCount").getValue().toString()));
                    currentPost.setImageUrl(data.child("ImageUri").getValue().toString());
                    currentPost.setPost(data.child("Description").getValue().toString());
                    listPostingan.add(currentPost);
                    RecyclerView recyclerView = view.findViewById(R.id.list_post);
                    RecycleViewPostinganAdminAdapter adapter = new RecycleViewPostinganAdminAdapter(listPostingan, containerContext);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(containerContext);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err",databaseError.getMessage().toString());
            }
        });
    }

}
