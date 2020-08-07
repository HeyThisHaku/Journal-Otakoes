package com.weaboo.jurnalotaku;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Vector;

import adapter.RecycleViewCommentAdapter;
import adapter.RecycleViewOtherAdapter;
import database.Database;
import session.Session;

public class OtherPost extends YouTubeBaseActivity {

    Vector<models.OtherPost> listOther = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        getData();
    }

    public void getData(){
        final Database database = Database.getInstance();
        database.setRefer("Other");
        database.getRefer().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    String id = data.getKey().toString();
                    String title = data.child("title").getValue().toString();
                    String url = data.child("url_video").getValue().toString();
                    url = url.split("v=")[1];
                    models.OtherPost other = new models.OtherPost();
                    other.setId(id);
                    other.setTitle(title);
                    other.setVideo_url(url);
                    listOther.add(other);
                }
                RecyclerView recyclerView = findViewById(R.id.RecycleViewOther);
                RecycleViewOtherAdapter adapter = new RecycleViewOtherAdapter(listOther, OtherPost.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OtherPost.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
