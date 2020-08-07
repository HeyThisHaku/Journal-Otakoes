package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentManageOther extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_manage_other,container,false);


//        final EditText title = view.findViewById(R.id.another_post_title);
//        final EditText url = view.findViewById(R.id.another_posturl);
//        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
//        final DatabaseReference writerBase = firebase.getReference("Other");
//        writerBase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String id;
//                Log.d("Jumlah Child",dataSnapshot.getChildrenCount()+"");
//                id = "oth"+(dataSnapshot.getChildrenCount()+1);
//                writerBase.child(id).child("title").setValue(title.getText());
//                writerBase.child(id).child("url_video").setValue(url.getText());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }
}
