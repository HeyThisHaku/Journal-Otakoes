package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Vector;

import adapter.RecycleViewPostinganAdapter;
import database.Database;
import env.Env;
import models.Anime;
import models.Post;
import session.Session;

public class SearchPost extends AppCompatActivity {

    Vector<Post>listPost = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);
        TextView text = findViewById(R.id.text_result_search);
        text.setText(text.getText()+Session.getData("search_jurnal"));
        generateData();
        getFromApi();
    }

    private void getFromApi(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest array = new JsonArrayRequest(Request.Method.GET, Env.jurnalAPI + Session.getData("search_jurnal").replace(" ","+"), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i =0; i<response.length(); i++){
                    try {
                        Log.d("Hasill",response.getJSONObject(i).getString("img ").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Post post = new Post();
                        post.setViews(0);
                        Log.d("iniHasil",object.getString("img "));
                        post.setImageUrl(object.getString("img "));
                        post.setId("null");
                        post.setTitle(object.getString("title"));
                        post.setAuthor("Jurnal-Otaku");
                        listPost.add(post);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                RecyclerView recyclerView = findViewById(R.id.search_rcv);
                RecycleViewPostinganAdapter adapter = new RecycleViewPostinganAdapter(listPost,SearchPost.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchPost.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(array);
    }

    private void generateData(){
        final Database database = Database.getInstance();
        database.setRefer("Postingan");
        database.getRefer().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPost.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    if(data.child("Title").getValue().toString().equalsIgnoreCase(Session.getData("search_jurnal"))) {
                        Post currentPost = new Post();
                        currentPost.setTitle(data.child("Title").getValue().toString());
                        currentPost.setAuthor(data.child("Author").getValue().toString());
                        currentPost.setId(data.getKey());
                        currentPost.setViews(Integer.parseInt(data.child("ViewCount").getValue().toString()));
                        currentPost.setImageUrl(data.child("ImageUri").getValue().toString());
                        currentPost.setPost(data.child("Description").getValue().toString());
                        listPost.add(currentPost);
                        RecyclerView recyclerView = findViewById(R.id.search_rcv);
                        RecycleViewPostinganAdapter adapter = new RecycleViewPostinganAdapter(listPost, SearchPost.this);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchPost.this);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err",databaseError.getMessage().toString());
            }
        });



    }
}
