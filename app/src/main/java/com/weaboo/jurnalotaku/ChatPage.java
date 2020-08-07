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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import adapter.AllFriendAdapter;
import layoutrecycle.ExistChatListener;
import layoutrecycle.TaskListener;
import models.Chat;

public class ChatPage extends AppCompatActivity {
    ImageView backBtn;
    TextView noChat;
    FloatingActionButton floatingActionButton;
    RecyclerView friendRecView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    AllFriendAdapter allFriendAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        noChat=findViewById(R.id.chat_not_exists);
        friendRecView=findViewById(R.id.list_of_last_messages);
        friendRecView.setLayoutManager(new LinearLayoutManager(this));
        allFriendAdapter=new AllFriendAdapter(ChatPage.this,null);
        friendRecView.setAdapter(allFriendAdapter);
        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        fStore.collection("users").document(fAuth.getCurrentUser().getUid()).
                collection("friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(int i=0;i<task.getResult().size();i++){
                        fStore.collection("users").document(fAuth.getCurrentUser().getUid()).
                                collection("friends").document(task.getResult().getDocuments().get(i).get("id").toString())
                                .collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w("Listen", "Listen failed.", e);
                                    return;
                                }
                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    Log.d("Listen", "Current data: " + queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1).get("messageText"));
                                    noChat.setVisibility(View.GONE);
                                    friendRecView.setVisibility(View.VISIBLE);
                                    getAllFriendList();
//                                            new ExistChatListener() {
//                                        @Override
//                                        public void callBack(ArrayList<Chat> val) {
//                                            Log.d("omg1","ini size req "+val.size());
//                                            if(!val.isEmpty()){
//                                                Log.d("omg","ini size req "+val.size());
//                                                allFriendAdapter=new AllFriendAdapter(ChatPage.this,val);
//                                                friendRecView.setAdapter(allFriendAdapter);
//
//                                            }
//                                        }
//                                    });

                                }else{
                                    Log.d("Listen", "Current data: null");
                                }
                            }
                        });
                    }
                }
            }
        });
        floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile=new Intent(ChatPage.this, StartNewConversation.class);
                toProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toProfile);
            }
        });
        backBtn=findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile=new Intent(ChatPage.this, Home.class);
                toProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toProfile);
            }
        });
    }
    private void getAllFriendList(){
        final ArrayList<Chat> updatedChat=new ArrayList<>();
//        pendingRequests.add(new PendingRequest("abc","orlando",null,acceptBtn,declineBtn));
        fAuth= FirebaseAuth.getInstance();
        String id=fAuth.getCurrentUser().getUid();
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        fStore.collection("users").document(id).collection("friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Log.d("Hasil", document.getId() + " => " + document.getData());
                        Map<String, Object> pr=document.getData();
                        final String id=pr.get("id").toString();
                        final String name=pr.get("name").toString();
                        final String email=pr.get("email").toString();
                        Log.d("Hasil", id);
                        Log.d("Hasil", name);
//                        String msg=pr.get
                        fStore.collection("users").document(fAuth.getCurrentUser().getUid()).
                                collection("friends").document(id).
                                collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot document = task.getResult();
                                    if (document != null && !document.isEmpty()) {
                                        Log.d("Have message only", "DocumentSnapshot data: " + document.getDocuments().get(document.size()-1).get("messageText"));
                                        updatedChat.add(new Chat(name,id,"",null,email));
//                                        Collections.sort(updatedChat, Collections.<Chat>reverseOrder());
                                        allFriendAdapter.setChats(updatedChat);
                                        allFriendAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d("No msg", "No such document");
                                    }
                                }
//                                listener.callBack(updatedChat);

                            }
                        });
//

                    }

//                    Log.d("Have message only", "DocumentSnapshot data: " + updatedChat.get(0).getUsername());
//                    allFriendAdapter.setChats(updatedChat);
//                    Log.d("Have message only", "DocumentSnapshot data: " + allFriendAdapter.getItemCount());
//                    allFriendAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d("Error","Not found");
                }
            }
        });
    }
}
