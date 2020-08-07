package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

import adapter.JournalAdapter;
import adapter.RecycleViewCommentAdapter;
import database.Database;
import models.CommentModels;
import models.JournalModel;
import session.Session;

public class CatalogJournal extends AppCompatActivity {

    Vector<JournalModel> listJournal = new Vector<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_journal);
        getJournalData();
    }

    private void getJournalData() {
        final Database database = Database.getInstance();
        database.setRefer("Journal");
        database.getRefer().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    String id = data.getKey().toString();
                    String url = data.getValue().toString();
                    Log.d("Result journal",id);
                    JournalModel jm = new JournalModel();
                    jm.setId(id);
                    jm.setLink(url);
                    listJournal.add(jm);
                    RecyclerView recyclerView = findViewById(R.id.result_journal);
                    JournalAdapter adapter = new JournalAdapter(listJournal, CatalogJournal.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CatalogJournal.this);
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
