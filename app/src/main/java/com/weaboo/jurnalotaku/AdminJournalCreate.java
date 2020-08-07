package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import adapter.RecycleViewPostinganAdapter;
import env.Env;
import models.Post;
import session.Session;

public class AdminJournalCreate extends AppCompatActivity {

    Vector<String> listPdf = new Vector<>();
    TextView zipped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_journal_create);
        // kita harus ubah journal nya ke html semua dahulu terus ke pdf in
        int index = 2;
        String listHtmlTemp="";
        for (final Post currentPost: Session.getJournalQueue()) {
                    final Post finalCurrentPost = currentPost;
                    final String hit = Env.jurnalToHtml;
                    Log.d("Title Html nya",finalCurrentPost.getTitle());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hitApi(hit,finalCurrentPost); // jadiin html // karena pdf nya ada masalah terus di android akhirnya save journal dengan html
                        }
                    },5000*index);
                    index++;
                    listHtmlTemp=listHtmlTemp+"file[]="+finalCurrentPost.getTitle()+".html.pdf&";
                    if(Session.getJournalQueue().lastElement().getId().equals(currentPost.getId())){
                        listHtmlTemp = listHtmlTemp.substring(0,listHtmlTemp.length()-1);
                        Button zipper = findViewById(R.id.button_zip);
                        final String finalListHtmlTemp = listHtmlTemp;
                        zipper.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    makeZip(Env.mergerToZip, finalListHtmlTemp);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                Session.clearJournal();
                            }
                        });
                    }
        }

    }

    private void makeZip(String mergerToZip, String listHtmlGet) throws UnsupportedEncodingException {
        RequestQueue queue = Volley.newRequestQueue(this);
        Log.d("Sebelum masuk",mergerToZip+"?"+listHtmlGet);
        Toast.makeText(this,mergerToZip+"?"+listHtmlGet,Toast.LENGTH_SHORT).show();
        JsonObjectRequest array = new JsonObjectRequest(Request.Method.GET, mergerToZip+"?"+listHtmlGet, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            zipped = findViewById(R.id.zip_message);
                            Log.d("Hasil",response.getString("file"));
                            zipped.setText("Success zipId:"+response.get("file")+ " insert db success");
                            insertDb(response.getString("file"));
                            return;
                        } catch (JSONException e) {
                            Log.d("Ada error22","Ada Error");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Oalah kena error22","Error : "+error.getMessage());
            }
        });
        queue.add(array);

    }

    private void insertDb(final String file) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        final DatabaseReference writerBase = firebase.getReference("Journal");
        writerBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id;
                Date today = new Date();
                Calendar cal  =Calendar.getInstance();

                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                id = dayOfMonth+"-"+month+"-"+year;
                writerBase.child(id).setValue(file);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void makePdf(String url,final String htmlName) throws UnsupportedEncodingException {
        RequestQueue queue = Volley.newRequestQueue(this);
        Log.d("Sebelum masuk",url+"?filename=" +htmlName);
        JsonObjectRequest array = new JsonObjectRequest(Request.Method.GET, url + "?filename=" + htmlName, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Hasil",response.getString("filename"));
                            listPdf.add(response.getString("filename"));
                        } catch (JSONException e) {
                            Log.d("Ada error","Ada Error");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Oalah kena error","Error : "+error.getMessage());
            }
        });
        queue.add(array);
//        String res = executeCommand("curl "+url+"?filename=" + URLEncoder.encode(htmlName, "UTF-8"));
//        Log.d("Ini hasil curl",res);
    }

    private void hitApi(String url, final Post data){
        Log.d("Masuk","Masuk"+ url);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //Log.d("Response", response);
                        try {
                            makePdf(Env.htmlToPdf,data.getTitle()+".html");//jadiin pdf karnea gak bisa jadiin zip langsung
                        } catch (UnsupportedEncodingException e) {
                            Log.d("Ada error2","Ada Error2");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("title", data.getTitle());
                params.put("photo", data.getImageUrl());
                params.put("description",data.getPost());
                return params;
            }
        };
        queue.add(postRequest);
    }


}
