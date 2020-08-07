package com.weaboo.jurnalotaku;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.annotation.Nullable;

import adapter.ImageAdapter;
import adapter.RecycleViewPostinganAdapter;
import database.Database;
import env.Env;
import models.Anime;
import models.Post;
import session.Session;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    Vector<Post> listPostingan = new Vector<>();
    Vector<Anime> listTopAnime = new Vector<>();
    FirebaseAuth fAuth;
    GoogleSignInAccount acct;
    FirebaseFirestore fStore;
    String userId;
    TextView profileUsername,profileEmail;
    ImageView profileImageView;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fAuth= FirebaseAuth.getInstance();
        acct = GoogleSignIn.getLastSignedInAccount(this);
        fStore=FirebaseFirestore.getInstance();
        userId=fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        View hView=navigationView.getHeaderView(0);
        profileUsername=hView.findViewById(R.id.home_profile_name);
        profileEmail=hView.findViewById(R.id.home_profile_email);
        profileImageView=hView.findViewById(R.id.home_profile_img);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    Intent toProfile=new Intent(Home.this,Profile.class);
                    startActivity(toProfile);
                } else if (id == R.id.nav_chat) {
                    Intent toChat=new Intent(Home.this,ChatPage.class);
                    startActivity(toChat);
                } else if (id == R.id.nav_friends) {
                    Intent toFriends=new Intent(Home.this,ViewFriendRequest.class);
//                    toFriends.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(toFriends);
                } else if (id == R.id.nav_catalog) {

                    Intent toJournal=new Intent(Home.this,CatalogJournal.class);
                    startActivity(toJournal);

                } else if (id == R.id.nav_slideshow) {

                } else if (id == R.id.nav_logout) {
                    Intent toLogin=new Intent(Home.this,Login.class);
                    toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    normalLogout();
                    if(acct!=null){
                        googleLogOut();
                    }
                    startActivity(toLogin);
                    finish();
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        DocumentReference documentReference=fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(Home.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null){
                    profileUsername.setText(documentSnapshot.getString("username"));
                    profileEmail.setText(documentSnapshot.getString("email"));
                }

            }
        });
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.



        othersCategory();
        generateTopAnime();
        getAllPostinganData();
        searchListener();
    }
    public void normalLogout(){
        FirebaseAuth.getInstance().signOut();
    }

    public void googleLogOut(){
        GoogleSignIn.getClient(this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();
    }

    private void othersCategory() {
        TextView tapForOther = findViewById(R.id.tap_for_others);
        tapForOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this,OtherPost.class);
                startActivity(i);
            }
        });
    }

    private void searchListener(){
        final EditText search  = findViewById(R.id.edit_text);
        search.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("dipencet",search.getText().toString());
                Session.setSession("search_jurnal",search.getText().toString());
                Intent intent = new Intent(Home.this, SearchPost.class);
                startActivity(intent);
                search.setText("");
                return true;
            }
        });
    }

    private void generateTopAnime(){
        final ImageView image1 = findViewById(R.id.topanime_1);
        final ImageView image2 = findViewById(R.id.topanime_2);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest object = new JsonObjectRequest(Request.Method.GET, Env.topAnimeApi, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final JSONArray array = response.getJSONArray("results");
                    for(int i =0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        if(i == 0){
                            Anime anime = new Anime();
                            anime.setId(object.getString("mal_id"));
                            anime.setTitle(object.getString("title"));
                            anime.setEpisodes(object.getInt("episodes")+" episodes");
                            anime.setRating(object.getInt("score")+" / 10");
                            anime.setSynopsis(object.getString("synopsis"));
                            listTopAnime.add(anime);
                            ImageAdapter.loadImageUrl(object.getString("image_url"), Home.this,image1);
                            image1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Home.this, AnimePost.class);
                                    Session.setSession("id_top_anime",listTopAnime.get(0).getId());
                                    startActivity(intent);
                                }
                            });
                        }
                        else if(i == 1){
                            Anime anime = new Anime();
                            anime.setId(object.getString("mal_id"));
                            anime.setTitle(object.getString("title"));
                            anime.setEpisodes(object.getInt("episodes")+" episodes");
                            anime.setRating(object.getInt("score")+" / 10");
                            anime.setSynopsis(object.getString("synopsis"));
                            listTopAnime.add(anime);
                            ImageAdapter.loadImageUrl(object.getString("image_url"), Home.this,image2);
                            image2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Home.this, AnimePost.class);
                                    Session.setSession("id_top_anime",listTopAnime.get(1).getId());

                                    startActivity(intent);
                                }
                            });
                        }
                    }
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
    private void generatePager() {
        ViewPager pager = findViewById(R.id.popular_slider);
        Vector<Post>tempPostingan = listPostingan;
        Collections.sort(tempPostingan, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o2.getViews()-o1.getViews();
            }
        });
        Vector<Post>top3 = new Vector<>();
        Log.d("HWEHWE",listPostingan.size()+" <-");
        top3.add(tempPostingan.get(0));
        top3.add(tempPostingan.get(1));
        top3.add(tempPostingan.get(2));

        ImageAdapter adapter = new ImageAdapter(this,listPostingan);
        pager.setAdapter(adapter);
    }

    private void getAllPostinganData(){
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
                    RecyclerView recyclerView = findViewById(R.id.RecycleViewPostingan);
                    RecycleViewPostinganAdapter adapter = new RecycleViewPostinganAdapter(listPostingan, Home.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Home.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
                generatePager();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err",databaseError.getMessage().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.mobile_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
