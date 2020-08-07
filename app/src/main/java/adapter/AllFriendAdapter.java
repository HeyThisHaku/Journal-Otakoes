package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.weaboo.jurnalotaku.AllFriends;
import com.weaboo.jurnalotaku.ChatRoom;
import com.weaboo.jurnalotaku.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import layoutrecycle.AllFriendHolder;
import layoutrecycle.MenuClickListener;
import layoutrecycle.PendingRequestHolder;
import models.Chat;
import models.ChatMessage;

public class AllFriendAdapter extends RecyclerView.Adapter<AllFriendHolder> {
    private Context c;
    private ArrayList<Chat> chats;
    private StorageReference storageReference;
    private boolean showLastMsg=true;
    public AllFriendAdapter(Context c, ArrayList<Chat> chats) {
        this.c = c;
        this.chats = chats;
    }
    public AllFriendAdapter(Context c, ArrayList<Chat> chats,Boolean showLastMsg) {
        this.c = c;
        this.chats = chats;
        this.showLastMsg=showLastMsg;
    }
    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public AllFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friend_rows,parent,false);
        return new AllFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AllFriendHolder holder, int position) {
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        final FirebaseAuth fAuth=FirebaseAuth.getInstance();
        final ArrayList<ChatMessage> chatMessages=new ArrayList<>();
        if(!this.showLastMsg){
            holder.message.setVisibility(View.GONE);
        }
        else{
            holder.message.setVisibility(View.VISIBLE);
        }
        holder.username.setText(chats.get(position).getUsername());
        holder.email.setText(chats.get(position).getEmail());
        holder.profileImg.setImageResource(R.drawable.edit_profile);
//        holder.message.setText();
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profileRef = storageReference.child("users/"+chats.get(position).getUserID()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.profileImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        fStore.collection("users").document(fAuth.getCurrentUser().getUid()).collection("friends").
                document(chats.get(position).getUserID()).collection("messages").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("chat", document.getId() + " => " + document.getData());
                                Map<String, Object> pr = document.getData();
                                final String idGet = pr.get("messageUser").toString();
                                final String msg = pr.get("messageText").toString();
                                final long time = (long) pr.get("messageTime");
                                final String friendId = pr.get("messageReceiver").toString();
                                chatMessages.add(new ChatMessage(msg, idGet, time, friendId));
                            }
                            Collections.sort(chatMessages, new Comparator<ChatMessage>() {
                                @Override
                                public int compare(ChatMessage o1, ChatMessage o2) {
                                    return (int) (o1.getMessageTime() - o2.getMessageTime());
                                }
                            });
                            if(!chatMessages.isEmpty()){
                                holder.message.setText(chatMessages.get(chatMessages.size()-1).getMessageText());

                            }
                        }
                    }
                });
        holder.setMenuClickListener(new MenuClickListener() {
            @Override
            public void onMenuClickListener(View v, int position) {
                Intent i = new Intent(v.getContext(), ChatRoom.class);
                i.putExtra("username",chats.get(position).getUsername());
                i.putExtra("id",chats.get(position).getUserID());
                c.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(chats!=null){
            return chats.size();
        }
        return 0;
    }
}
