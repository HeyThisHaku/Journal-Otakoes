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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import adapter.AllFriendAdapter;
import adapter.FriendRequestAdapter;
import adapter.PendingRequestAdapter;
import models.Chat;
import models.FriendRequest;
import models.PendingRequest;

public class ViewFriendRequest extends AppCompatActivity {

    Button acceptBtn,declineBtn;
    PendingRequestAdapter pendingRequestAdapter;
    ImageButton backBtn,addBtn;
    RecyclerView recView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    LinearLayout showRequests,showFriends;
    Boolean allReqClick,allFriendClick;
    RecyclerView friendRecView;
    AllFriendAdapter allFriendAdapter;
    ImageView reqUp,reqDown,frUp,frDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend_request);
        backBtn=findViewById(R.id.back_btn);
        addBtn=findViewById(R.id.add_friend_btn);
        showRequests=findViewById(R.id.show_all_request);
        showFriends=findViewById(R.id.show_all_friend);
        recView=findViewById(R.id.pending_request);
        reqUp=findViewById(R.id.req_up_arrow);
        reqDown=findViewById(R.id.req_down_arrow);
        frUp=findViewById(R.id.friend_up_arrow);
        frDown=findViewById(R.id.friend_down_arrow);
        allReqClick=true;
        allFriendClick=true;
        recView.setLayoutManager(new LinearLayoutManager(this));
//        pendingRequestAdapter=new PendingRequestAdapter(this,getList());
        pendingRequestAdapter=new PendingRequestAdapter(ViewFriendRequest.this,null);

        friendRecView=findViewById(R.id.all_friends);
        friendRecView.setLayoutManager(new LinearLayoutManager(this));
        allFriendAdapter=new AllFriendAdapter(ViewFriendRequest.this,null,false);
        friendRecView.setAdapter(allFriendAdapter);
        recView.setAdapter(pendingRequestAdapter);
        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen", "Listen failed.", e);
                    return;
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    Log.d("Listen", "Current data: " + queryDocumentSnapshots.getDocuments());
                    getAllFriendList();

                } else {
                    Log.d("Listen", "Current data: null");
                    removeList();
                }
            }
        });
        fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("pending_request").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen", "Listen failed.", e);

                    return;
                }
//                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                    switch (dc.getType()) {
//                        case ADDED:
//                            Log.d("Inserted", "New city: " + dc.getDocument().getData());
//                            break;
//                        case MODIFIED:
//                            Log.d("Updated", "Modified city: " + dc.getDocument().getData());
//                            break;
//                        case REMOVED:
//                            Log.d("Deleted", "Removed city: " + dc.getDocument().getData());
//                            return;
//                    }
//                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    Log.d("Listen", "Current data: " + queryDocumentSnapshots.getDocuments());
                    getList();

                } else {
                    Log.d("Listen", "Current data: null");
                    removeList();
                }
            }
        });
//
//        getList();

//        pendingRequestAdapter.notifyDataSetChanged();
//        ArrayList<PendingRequest>temp=getList();
//        Log.d("Hasil",temp.size()+"");



        acceptBtn=findViewById(R.id.req_accept_btn);
        declineBtn=findViewById(R.id.req_decline_btn);
        showRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent toShowFriend=new Intent(ViewFriendRequest.this,AllFriends.class);
////                startActivity(toShowFriend);
                if(!allReqClick){
                    recView.setVisibility(View.VISIBLE);
                    reqDown.setVisibility(View.VISIBLE);
                    reqUp.setVisibility(View.GONE);
                    allReqClick=true;
                }
                else{
                    recView.setVisibility(View.GONE);
                    reqUp.setVisibility(View.VISIBLE);
                    reqDown.setVisibility(View.GONE);
                    allReqClick=false;
                }

            }
        });
        showFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allFriendClick){
                    friendRecView.setVisibility(View.VISIBLE);
                    frDown.setVisibility(View.VISIBLE);
                    frUp.setVisibility(View.GONE);
                    allFriendClick=true;
                }
                else{
                    friendRecView.setVisibility(View.GONE);
                    frUp.setVisibility(View.VISIBLE);
                    frDown.setVisibility(View.GONE);
                    allFriendClick=false;
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHome=new Intent(ViewFriendRequest.this,Home.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toHome);
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddFriend=new Intent(ViewFriendRequest.this,AddFriend.class);
                toAddFriend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toAddFriend);
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
    private void removeList(){
        pendingRequestAdapter.setPendingRequests(null);
        pendingRequestAdapter.notifyDataSetChanged();
    }
    private void getList(){
        final ArrayList<PendingRequest> pendingRequests=new ArrayList<>();
//        pendingRequests.add(new PendingRequest("abc","orlando",null,acceptBtn,declineBtn));
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("pending_request").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task!=null){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Log.d("Hasil", document.getId() + " => " + document.getData());
                        Map<String, Object> pr=document.getData();
                        String id=pr.get("from").toString();
                        String name=pr.get("name").toString();
                        pendingRequests.add(new PendingRequest(name,id,null,acceptBtn,declineBtn));
                        Log.d("Hasil", id);
                        Log.d("Hasil", name);

                    }
                    Log.d("Hasil", pendingRequests.size()+"");
                    pendingRequestAdapter.setPendingRequests(pendingRequests);
                    pendingRequestAdapter.notifyDataSetChanged();

//
                }
                else{
                    Log.d("Hasil", "gagal");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Error", e.getMessage());
            }
        });
//        return pendingRequests;
    }

}
