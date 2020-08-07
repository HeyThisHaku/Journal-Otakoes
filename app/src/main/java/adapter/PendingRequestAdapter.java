package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.weaboo.jurnalotaku.R;
import com.weaboo.jurnalotaku.ViewFriendRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import layoutrecycle.FriendRequestHolder;
import layoutrecycle.PendingRequestHolder;
import models.PendingRequest;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestHolder> {
    private Context c;
    private ArrayList<PendingRequest> pendingRequests;

    public PendingRequestAdapter(Context c, ArrayList<PendingRequest> pendingRequests) {
        this.c = c;
        this.pendingRequests = pendingRequests;

    }

    public ArrayList<PendingRequest> getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(ArrayList<PendingRequest> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    @NonNull
    @Override
    public PendingRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_request_rows,parent,false);
        return new PendingRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRequestHolder holder, final int position) {
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        final FirebaseAuth fAuth=FirebaseAuth.getInstance();
//        Log.d("Delete",pendingRequests.get(position).getUserID()+" "+pendingRequests.size());
        holder.username.setText(pendingRequests.get(position).getUsername());
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id=fAuth.getCurrentUser().getUid();

                fStore.collection("users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot!=null){
                            Map<String,Object>externalFriend=new HashMap<>();
                            externalFriend.put("id",id);
                            externalFriend.put("name",documentSnapshot.get("username"));
                            externalFriend.put("email",documentSnapshot.get("email"));
                            fStore.collection("users").document(pendingRequests.get(position).getUserID()).collection("friends").document(fAuth.getCurrentUser().getUid()).set(externalFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                    Log.d("External",pendingRequests.get(position).getUserID()+"");
                                }
                            });
                        }
                    }
                });
                fStore.collection("users").document(pendingRequests.get(position).
                        getUserID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot!=null){
                            Map<String,Object>internalFriend=new HashMap<>();
                            internalFriend.put("id",pendingRequests.get(position).getUserID());
                            internalFriend.put("name",documentSnapshot.get("username"));
                            internalFriend.put("email",documentSnapshot.get("email"));
                            Toast.makeText(c,R.string.added_successfull+pendingRequests.get(position).getUsername(),Toast.LENGTH_SHORT).show();

                            fStore.collection("users").document(id).collection("friends").document(pendingRequests.get(position).getUserID()).set(internalFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                    Log.d("External",pendingRequests.get(position).getUserID()+"");
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

                fStore.collection("users").document(pendingRequests.get(position).getUserID()).
                        collection("sent_request").document(fAuth.getCurrentUser().getUid()).delete().
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                        Log.d("Delete",pendingRequests.get(position).getUserID()+"");
                            }
                        });
                fStore.collection("users").document(fAuth.getCurrentUser().getUid()).
                        collection("pending_request").document(pendingRequests.get(position).getUserID()).delete().
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                        Log.d("Delete",pendingRequests.get(position).getUserID()+"");
                            }
                        });




            }
        });
        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(c);
                View view= LayoutInflater.from(c).inflate(R.layout.cancel_add_friend,null);
                builder.setView(view).setTitle(R.string.confirm_changes).setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                final AlertDialog alertDialog=builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button pos=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        pos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fStore.collection("users").document(pendingRequests.get(position).getUserID()).
                                        collection("sent_request").document(fAuth.getCurrentUser().getUid()).delete().
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });
                                fStore.collection("users").document(fAuth.getCurrentUser().getUid()).
                                        collection("pending_request").
                                        document(pendingRequests.get(position).getUserID()).delete().
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(c,R.string.cancel_friend_req,Toast.LENGTH_SHORT).show();
                                                notifyDataSetChanged();
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        });
                    }
                });
                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        if(pendingRequests!=null){
            return pendingRequests.size();
        }
        return 0;
    }
}
