package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import adapter.AllFriendAdapter;
import models.Chat;

public class StartNewConversation extends AppCompatActivity {
    RecyclerView recView;
    AllFriendAdapter allFriendAdapter;
    FirebaseAuth fAuth;
    ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_new_conversation);
        recView=findViewById(R.id.all_friends);
        recView.setLayoutManager(new LinearLayoutManager(this));
        allFriendAdapter=new AllFriendAdapter(StartNewConversation.this,null,false);
        recView.setAdapter(allFriendAdapter);
        backBtn=findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHome=new Intent(StartNewConversation.this,ChatPage.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toHome);
            }
        });
        getList();

    }
    private void getList(){
        final ArrayList<Chat> updatedChat=new ArrayList<>();
//        pendingRequests.add(new PendingRequest("abc","orlando",null,acceptBtn,declineBtn));
        fAuth= FirebaseAuth.getInstance();
        String id=fAuth.getCurrentUser().getUid();
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        fStore.collection("users").document(id).collection("friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task!=null){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Log.d("Hasil", document.getId() + " => " + document.getData());
                        Map<String, Object> pr=document.getData();
                        String id=pr.get("id").toString();
                        String name=pr.get("name").toString();
                        String email=pr.get("email").toString();
                        Log.d("Hasil", id);
                        Log.d("Hasil", name);
//                        String msg=pr.get
                        updatedChat.add(new Chat(name,id,"",null,email));
                    }
                    allFriendAdapter.setChats(updatedChat);
                    allFriendAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
