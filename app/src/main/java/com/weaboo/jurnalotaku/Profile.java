package com.weaboo.jurnalotaku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.Vector;

import adapter.MenuAdapter;
import models.ProfileMenu;

public class Profile extends AppCompatActivity {

    RecyclerView recView;
    MenuAdapter menuAdapter;
    ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        recView=findViewById(R.id.recyclerView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter=new MenuAdapter(this,getList());
        recView.setAdapter(menuAdapter);
        backBtn=findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHome=new Intent(Profile.this,Home.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toHome);
            }
        });
    }
    private Vector<ProfileMenu> getList(){
        Vector<ProfileMenu> menus=new Vector<>();
        menus.add(new ProfileMenu("Edit profile",R.drawable.edit_profile));
        menus.add(new ProfileMenu("Language",R.drawable.ic_favorite_black_24dp));
//        menus.add(new ProfileMenu("Test",R.drawable.background));
        return menus;
    }
}
