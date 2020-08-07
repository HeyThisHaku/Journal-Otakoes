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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import adapter.ChatMessageAdapter;
import models.Chat;
import models.ChatMessage;

public class ChatRoom extends AppCompatActivity {
    ImageView backBtn;
    TextView name;
    FloatingActionButton fab;
    RecyclerView recView;
    FirebaseAuth fAuth;
    ChatMessageAdapter chatMessageAdapter;
    FirebaseFirestore fStore;
    String friendId;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_chat_room);
        Intent data=getIntent();
        username = data.getStringExtra("username");
        friendId=data.getStringExtra("id");
        name=findViewById(R.id.chat_profile_name);
        name.setText(username);
        fab=findViewById(R.id.fab);
        recView=findViewById(R.id.list_of_messages);
        recView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recView.setLayoutManager(linearLayoutManager);
        chatMessageAdapter=new ChatMessageAdapter(ChatRoom.this,null);
        recView.setAdapter(chatMessageAdapter);
        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        String id=fAuth.getCurrentUser().getUid();
        fStore.collection("users").document(id).collection("friends").document(friendId).
                collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("Dengar", "Listen failed.", e);
                    return;
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    Log.d("Dengar", "Current data: " + queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1).get("messageText"));
                    getList();

                } else {
                    Log.d("Dengar", "Current data: null");

                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseFirestore fStore=FirebaseFirestore.getInstance();
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                String id=firebaseAuth.getCurrentUser().getUid();
                ChatMessage chatMessage=new ChatMessage(input.getText().toString(),id,friendId);
                fStore.collection("users").document(id).collection("friends").
                        document(friendId).collection("messages").add(chatMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        getList();
                    }
                });
                fStore.collection("users").document(friendId).collection("friends").
                        document(id).collection("messages").add(chatMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                });

                input.setText("");
            }
        });
        backBtn=findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile=new Intent(ChatRoom.this, ChatPage.class);
                toProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toProfile);
            }
        });
    }
    private void getList(){
        final ArrayList<ChatMessage> chatMessages=new ArrayList<>();
//        pendingRequests.add(new PendingRequest("abc","orlando",null,acceptBtn,declineBtn));
        fAuth= FirebaseAuth.getInstance();
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        final String id=fAuth.getCurrentUser().getUid();
        fStore.collection("users").document(id).collection("friends").
                document(friendId).collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Log.d("chat", document.getId() + " => " + document.getData());
                        Map<String, Object> pr=document.getData();
                        final String idGet=pr.get("messageUser").toString();
                        final String msg=pr.get("messageText").toString();
                        final long time= (long) pr.get("messageTime");
                        final String friendId=pr.get("messageReceiver").toString();
//                        fStore.collection("users").document(idGet).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if(task!=null){
//
//                                }
//
//                            }
//                        });
//                        chatMessages.add(new ChatMessage(msg,username,time));
                        chatMessages.add(new ChatMessage(msg,idGet,time,friendId));
                    }
                    Collections.sort(chatMessages, new Comparator<ChatMessage>() {
                        @Override
                        public int compare(ChatMessage o1, ChatMessage o2) {
                            return (int) (o1.getMessageTime()-o2.getMessageTime());
                        }
                    });
                    Log.d("chat", chatMessages.size()+"");
                    chatMessageAdapter.setChats(chatMessages);
                    chatMessageAdapter.notifyDataSetChanged();
                }
            }
        });
//        fStore.collection("users").document(friendId).collection("friends").
//                document(id).collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task!=null){
//                    for(QueryDocumentSnapshot document: task.getResult()){
//                        Log.d("chat", document.getId() + " => " + document.getData());
//                        Map<String, Object> pr=document.getData();
//                        String idGet=pr.get("messageUser").toString();
//                        final String msg=pr.get("messageText").toString();
//                        final long time= (long) pr.get("messageTime");
//                        final String friendId=pr.get("messageReceiver").toString();
//                        fStore.collection("users").document(idGet).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if(task!=null){
//                                    chatMessages.add(new ChatMessage(msg,task.getResult().get("username").toString(),time,friendId));
//                                }
//
//                            }
//                        });
//
//
//                    }
//                    Log.d("chat", chatMessages.size()+"");
//
//                    chatMessageAdapter.setChats(chatMessages);
//                    chatMessageAdapter.notifyDataSetChanged();
//                }
//            }
//        });
    }
}
