package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.weaboo.jurnalotaku.R;

import java.util.HashMap;

import session.Session;

public class AdminInsert extends AppCompatActivity {

    EditText title;
    EditText author;
    EditText image;
    EditText description;
    Uri imageData = null;
    private StorageReference mStorage;

    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageData = data.getData();
            Picasso pica = Picasso.get();
            pica.load(imageData).into((ImageView) findViewById(R.id.image_placeholder_insert));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_insert);
        mStorage = FirebaseStorage.getInstance().getReference("postingan");
        Button uploadImg = findViewById(R.id.buttonUpload_insert);

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });



        Button insertBtn = findViewById(R.id.button_submit);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkInsert()){
                    insert(title.getText().toString(),author.getText().toString(),"dummy",description.getText().toString());
                }

            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void loadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
        NontifcationHelper.displayNotif(this,"InsertContent","HALOO","HALOO");
    }

    private boolean checkInsert(){
            title = findViewById(R.id.title_insert);
            author = findViewById(R.id.author_insert);
            image = findViewById(R.id.imageUrl_insert);
            description = findViewById(R.id.description_insert);

            if(title.getText().equals(" ") || title.getText().equals(""))
                return false;
            if(author.getText().equals(" ") || author.getText().equals(""))
                return false;
            if(imageData == null)
                return false;
            if(description.getText().equals(" ") || description.getText().equals(""))
                return false;
        return true;
    }
    public void insert(final String title, final String author, final String image, final String description){
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        final DatabaseReference writerBase = firebase.getReference("Postingan");
        writerBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id;
                Log.d("Jumlah Child",dataSnapshot.getChildrenCount()+"");
                if(dataSnapshot.getChildrenCount() < 10){
                 id = "post00"+(dataSnapshot.getChildrenCount()+1);
                }else if(dataSnapshot.getChildrenCount() < 100){
                    id = "post0"+(dataSnapshot.getChildrenCount()+1);
                }else{
                    id = "post"+(dataSnapshot.getChildrenCount()+1);
                }

                final String finalId = id;
                final StorageReference filereReference = mStorage.child(id+"."+getFileExtension(imageData));
                filereReference.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),R.string.success,Toast.LENGTH_SHORT).show();
                        filereReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                post currentPost = new post(author,description,image,title);
                                writerBase.child(finalId).child("Author").setValue(author);
                                writerBase.child(finalId).child("DateCreated").setValue("1-12-1222");
                                writerBase.child(finalId).child("Description").setValue(description);
                                writerBase.child(finalId).child("ImageUri").setValue(uri.toString());
                                writerBase.child(finalId).child("Title").setValue(title);
                                writerBase.child(finalId).child("ViewCount").setValue(0);
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),R.string.failed,Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),R.string.uploading,Toast.LENGTH_SHORT).show();
                    }
                });


                 }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    class post{
        String Author;
        String DateCreated="1-12-9999";
        String Description;
        String ImageUri;
        String Title;
        Integer ViewCount =0;
        post(String author,String description,String imageUri,String title){
            Author = author;
            Description = description;
            ImageUri = imageUri;
            Title = title;
        }
    }
}

