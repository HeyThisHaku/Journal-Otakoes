package com.weaboo.jurnalotaku;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import adapter.ImageAdapter;
import env.Env;
import session.Session;

public class AnimePost extends AppCompatActivity {
    private ImageView image;
    private  TextView desc;
    private  TextView title;
    private  TextView score;
    private  TextView episode;
    private String linkUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_post);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        image = findViewById(R.id.anime_detail_image);
        desc = findViewById(R.id.anime_detail_description);
        title = findViewById(R.id.anime_detail_title);
        score = findViewById(R.id.anime_detail_score);
        episode = findViewById(R.id.anime_detail_episode);
        getWindow().setLayout((int) (width * .9), (int) (height * .9));
        ImageView shareButton = findViewById(R.id.shareButtonAnimeDetail);

        try {
            String id = Session.getData("id_top_anime");
            getData(Integer.parseInt(id));
        } catch (Exception e) {
            Log.d("Error boys","Error");
            Intent intent = new Intent(AnimePost.this, Home.class);
            startActivity(intent);
        }

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share anime!");
                intent.putExtra(Intent.EXTRA_TEXT, "Best Anime!" + linkUrl);
                startActivity(Intent.createChooser(intent, "Share using"));
            }
        });
    }

    private void getData(Integer id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest object = new JsonObjectRequest(Request.Method.GET, Env.animeDetailApi + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ImageAdapter.loadImageUrl(response.getString("image_url"), AnimePost.this,image);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    desc.setText(response.getString("synopsis"));
                    title.setText(response.getString("title"));
                    score.setText(response.getInt("score")+"/10");
                    episode.setText(response.getInt("episodes")+" episodes");
                    linkUrl = response.getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(object);
    }
}
