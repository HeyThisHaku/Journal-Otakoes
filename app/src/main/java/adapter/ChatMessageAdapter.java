package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.weaboo.jurnalotaku.R;

import java.util.ArrayList;
import java.util.Vector;

import layoutrecycle.ChatBoxHolder;
import models.ChatMessage;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatBoxHolder> {
    private Context c;
    private ArrayList<ChatMessage> chats;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    StorageReference storageReference;
    FirebaseUser fUser;
    public ChatMessageAdapter(Context c, ArrayList<ChatMessage> chats) {
        this.c = c;
        this.chats = chats;
    }

    public ArrayList<ChatMessage> getChats() {
        return chats;
    }

    public void setChats(ArrayList<ChatMessage> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatBoxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_LEFT){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left,parent,false);
            return new ChatBoxHolder(view);
        }
        else{
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right,parent,false);
            return new ChatBoxHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ChatBoxHolder holder, int position) {
        storageReference = FirebaseStorage.getInstance().getReference();
        holder.profileImg.setImageResource(R.drawable.edit_profile);
        holder.text.setText(chats.get(position).getMessageText());
        holder.time.setText(DateFormat.format("HH:mm",chats.get(position).getMessageTime()));
        final StorageReference profileRef = storageReference.child("users/"+
                (chats.get(position).getMessageUser().equals(fUser.getUid())?chats.get(position).getMessageReceiver():chats.get(position).getMessageUser())+"/profile.jpg");
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
    }

    @Override
    public int getItemViewType(int position) {
        fUser=FirebaseAuth.getInstance().getCurrentUser();
        if(chats.get(position).getMessageUser().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        if(chats!=null){
            return chats.size();
        }
        return 0;
    }
}
