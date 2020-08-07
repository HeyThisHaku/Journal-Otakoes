package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Vector;

import adapter.FriendRequestAdapter;
import layoutrecycle.TaskListener;
import models.FriendRequest;

public class AddFriend extends AppCompatActivity {
    Button addFriend,cancelFriend;
    RecyclerView recView;
    FriendRequestAdapter friendRequestAdapter;
    ImageButton backBtn;
    SearchView searchView;
    FirebaseAuth fAuth;
    ArrayList<FriendRequest> friendRequests;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        addFriend=findViewById(R.id.req_add_friend_btn);
        cancelFriend=findViewById(R.id.cancel_add_friend_btn);
        recView=findViewById(R.id.friend_request);
        recView.setLayoutManager(new LinearLayoutManager(this));
        fStore=FirebaseFirestore.getInstance();

        friendRequests=getList(new TaskListener() {
            @Override
            public void callBack(ArrayList<FriendRequest> val) {
                if(!val.isEmpty()){
                    Log.d("omg","ini size req "+val.size());
                    friendRequestAdapter=new FriendRequestAdapter(AddFriend.this,val);
                    recView.setAdapter(friendRequestAdapter);
                }
                searchView=findViewById(R.id.search_user);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String username) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        friendRequestAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
            }
        });
        Log.d("Tes", "ini size req "+friendRequests.size()+"");
        fAuth= FirebaseAuth.getInstance();
        backBtn=findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHome=new Intent(AddFriend.this,ViewFriendRequest.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toHome);
            }
        });

    }

    private ArrayList<FriendRequest> getList(final TaskListener listener){
        final ArrayList<FriendRequest> friendReq=new ArrayList<>();
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fStore.collection("users").whereLessThan("id",fAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (final QueryDocumentSnapshot document: task.getResult()){
                        Log.d("Hasil", document.getId() + " => " + document.getData());
                        final FriendRequest fr=document.toObject(FriendRequest.class);
//
                        fr.setUserID(document.getId());
                        fr.setAddBtn(addFriend);
                        fr.setCancelBtn(cancelFriend);
                        friendReq.add(fr);
//
                    }
                    listener.callBack(friendReq);
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
        fStore.collection("users").whereGreaterThan("id",fAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document: task.getResult()){
                        Log.d("Hasil", document.getId() + " => " + document.getData());
                        FriendRequest fr=document.toObject(FriendRequest.class);
//                        fr.setImg();
                        fr.setUserID(document.getId());
                        fr.setAddBtn(addFriend);
                        fr.setCancelBtn(cancelFriend);
                        friendReq.add(fr);
                    }
                    listener.callBack(friendReq);
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

        return friendReq;
    }
}
