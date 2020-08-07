package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText profileFullName,profileEmail;
    ImageView profileImageView;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    ImageButton backBtn;
    GoogleSignInAccount acct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Intent data = getIntent();
        final String username = data.getStringExtra("username");
        String email = data.getStringExtra("email");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        profileFullName = findViewById(R.id.profileFullName);
        profileEmail = findViewById(R.id.profileEmailAddress);
        profileImageView = findViewById(R.id.profileImageView);
        saveBtn = findViewById(R.id.saveProfileInfo);
        backBtn=findViewById(R.id.back_btn);
//        acct = GoogleSignIn.getLastSignedInAccount(this);
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile=new Intent(EditProfile.this,ManageProfile.class);
                toProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toProfile);
            }
        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileFullName.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, R.string.edit_username_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(profileEmail.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, R.string.edit_email_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(acct!=null){
//
//                }
//                else{
                openDialog();
//                }
            }
        });

        profileEmail.setText(email);
        profileFullName.setText(username);
        Log.d(TAG, "onCreate: " + username + " " + email);
    }

    private void openDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(EditProfile.this);
        View view= getLayoutInflater().inflate(R.layout.confirm_edit_profile,null);
        final EditText pass=(EditText) view.findViewById(R.id.edit_profile_pass);
        builder.setView(view).setTitle(R.string.confirm_pass).setPositiveButton(R.string.confirm_changes, null)
                .setNegativeButton(R.string.cancel_add_friend, new DialogInterface.OnClickListener() {
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
                        String pwd=pass.getText().toString();
                        if(pwd.isEmpty()){
                            Toast.makeText(EditProfile.this, R.string.edit_pass_failed, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            AuthCredential authCredential= EmailAuthProvider.getCredential(user.getEmail(),pwd);
                            user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        final String email = profileEmail.getText().toString();
                                        user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DocumentReference docRef = fStore.collection("users").document(user.getUid());
                                                Map<String,Object> edited = new HashMap<>();
                                                edited.put("email",email);
                                                edited.put("username",profileFullName.getText().toString());
                                                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(EditProfile.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(),ManageProfile.class));
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                });
//                        Toast.makeText(EditProfile.this, "Email is changed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditProfile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Toast.makeText(EditProfile.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(EditProfile.this, R.string.edit_wrong_pass, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

//                profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);


            }
        }

    }
    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
