package adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.weaboo.jurnalotaku.R;
import com.weaboo.jurnalotaku.ViewFriendRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import layoutrecycle.FriendRequestHolder;
import models.FriendRequest;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestHolder> implements Filterable {

    private Context c;
    private ArrayList<FriendRequest> friendRequests;
    private ArrayList<FriendRequest> friendRequestsAll;
    StorageReference storageReference;
    public FriendRequestAdapter(Context c, ArrayList<FriendRequest> friendRequests) {
        this.c = c;
        this.friendRequests = friendRequests;
        this.friendRequestsAll=new ArrayList<>(friendRequests);
//        Log.d("Aiyo", "ini size normal "+this.friendRequests.size()+"");
//        Log.d("Aiyo", "ini size all "+this.friendRequestsAll.size()+"");
//        for (int i = 0; i < friendRequestsAll.size(); i++) {
//            Log.d("Aiyo", friendRequestsAll.get(i).getUsername()+"");
//        }
    }

    @NonNull
    @Override
    public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_rows,parent,false);
        return new FriendRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendRequestHolder holder, final int position) {
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        final FirebaseAuth fAuth=FirebaseAuth.getInstance();
        holder.username.setText(friendRequests.get(position).getUsername());
        holder.email.setText(friendRequests.get(position).getEmail());
        holder.profileImg.setImageResource(R.drawable.edit_profile);

        storageReference = FirebaseStorage.getInstance().getReference();
//        holder.profileImg.setImageResource(friendRequests.get(position).getImg());
        final StorageReference profileRef = storageReference.child("users/"+friendRequests.get(position).getUserID()+"/profile.jpg");
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
        fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("sent_request").document(friendRequests.get(position).getUserID())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d("Requested",task.getResult().get("name")+" "+task.getResult().get("to"));
                        holder.addFriend.setVisibility(View.GONE);
                        holder.cancelRequest.setVisibility(View.VISIBLE);
                        holder.alreadyFriends.setVisibility(View.GONE);
                    } else {
                        Log.d("Not Requested", "No such document");
                    }

                }
            }
        });
        fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("friends").document(friendRequests.get(position).getUserID())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d("Already",task.getResult().get("name")+" "+task.getResult().get("id"));
                        holder.addFriend.setVisibility(View.GONE);
                        holder.cancelRequest.setVisibility(View.GONE);
                        holder.alreadyFriends.setVisibility(View.VISIBLE);
                    }
                    else{
                        Log.d("Not Already", "No such document");
                    }

                }
            }
        });
//        holder.addFriend.setVisibility(View.VISIBLE);
        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c,R.string.friend_req,Toast.LENGTH_SHORT).show();
                holder.cancelRequest.setVisibility(View.VISIBLE);
                holder.addFriend.setVisibility(View.GONE);
                holder.alreadyFriends.setVisibility(View.GONE);
                final String id=fAuth.getCurrentUser().getUid();
                fStore.collection("users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot!=null){
                            final Map<String,Object>pending_request=new HashMap<>();
                            pending_request.put("from",id);
                            pending_request.put("name",documentSnapshot.get("username"));
                            pending_request.put("email",documentSnapshot.get("email"));
                            Log.d("name",pending_request.get("name")+"");
                            final Map<String,Object>sent_request=new HashMap<>();
                            sent_request.put("to",friendRequests.get(position).getUserID());
                            sent_request.put("name",friendRequests.get(position).getUsername());
                            sent_request.put("email",friendRequests.get(position).getEmail());
//                            Log.d("name",documentSnapshot.get("username")+"");
                            fStore.collection("users").document(friendRequests.get(position).getUserID()).collection("pending_request").document(id).set(pending_request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("behazil","Friend req send from "+id+" to"+friendRequests.get(position).getUserID());
                                }
                            });
                            fStore.collection("users").document(id).collection("sent_request").document(friendRequests.get(position).getUserID()).set(sent_request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("behazil","Pending req put to "+id);
                                }
                            });
                        }
                    }
                });


            }
        });
        holder.cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c,R.string.cancel_friend_req,Toast.LENGTH_SHORT).show();
                holder.cancelRequest.setVisibility(View.GONE);
                holder.addFriend.setVisibility(View.VISIBLE);
                holder.alreadyFriends.setVisibility(View.GONE);
                final String id=fAuth.getCurrentUser().getUid();
                fStore.collection("users").document(id).collection("sent_request").document(friendRequests.get(position).getUserID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("deleted","Sent req deleted "+friendRequests.get(position).getUserID());
                    }
                });
                fStore.collection("users").document(friendRequests.get(position).getUserID()).collection("pending_request").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("deleted","Pending req deleted "+id);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    @Override
    public Filter getFilter() {
        Log.d("Aiyo", "Get filter");
        return filter;
    }
    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FriendRequest>filteredList=new ArrayList<>();
//            Log.d("Aiyo", "Fileter Friend req "+friendRequests.size()+"");
//            Log.d("Aiyo", "Fileter Friend req all"+friendRequestsAll.size()+"");
            if(constraint.toString().isEmpty()){
//                Log.d("Aiyo", "none");
                filteredList.addAll(friendRequestsAll);
            }
            else{
//                Log.d("Aiyo", friendRequests.size()+"");
                for(FriendRequest fr:friendRequestsAll){
//                    Log.d("Aiyo", fr.getUsername().toLowerCase()+""+constraint.toString().toLowerCase().trim());
                    if(fr.getEmail().toLowerCase().startsWith(constraint.toString().toLowerCase().trim())){
                        filteredList.add(fr);
                    }
                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            friendRequests.clear();
            friendRequests.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
