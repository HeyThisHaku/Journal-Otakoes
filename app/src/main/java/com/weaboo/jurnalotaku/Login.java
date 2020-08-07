package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shobhitpuri.custombuttons.GoogleSignInButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    public static final int GOOGLE_SIGN_IN_CODE = 10005;
    EditText etUsername,etPass;
    Button loginBtn;
    TextView toRegister;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    GoogleSignInButton signInButton;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername=findViewById(R.id.et_username);
        etPass=findViewById(R.id.et_password);
        loginBtn=findViewById(R.id.btn_login);
        toRegister=findViewById(R.id.toRegister);
        progressBar=findViewById(R.id.progress_bar);
        fAuth=FirebaseAuth.getInstance();
        signInButton=findViewById(R.id.google_sign_in_btn);
        fStore=FirebaseFirestore.getInstance();
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken("665831426839-9h78cbj4jeljal8s0bb59eq3g1681j6m.apps.googleusercontent.com").build();
        gsc= GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount signInAccount=GoogleSignIn.getLastSignedInAccount(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        if(fAuth.getCurrentUser()!=null||signInAccount!=null){
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleLogin=gsc.getSignInIntent();
                startActivityForResult(googleLogin,GOOGLE_SIGN_IN_CODE);
            }
        });
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register=new Intent(Login.this,Register.class);
                register.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(register);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username=etUsername.getText().toString();
                String password=etPass.getText().toString();
                if(username.isEmpty()){
                    Toast.makeText(Login.this,R.string.login_email_empty,Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(password.isEmpty()){

                    Toast.makeText(Login.this,R.string.login_pass_empty,Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if(username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")){
                    Toast.makeText(Login.this,R.string.success_login,Toast.LENGTH_SHORT).show();
                    Intent toHome=new Intent(Login.this,AdminHome.class);
                    toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(toHome);
                    finish();
                }
                fAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,getApplicationContext().getString(R.string.success_login),Toast.LENGTH_SHORT).show();
                            Intent toHome=new Intent(Login.this,Home.class);
                            toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(toHome);
                        }
                        else{
                            try {
                                throw task.getException();
                            }  catch (FirebaseAuthWeakPasswordException e){
                                Toast.makeText(Login.this,R.string.login_pass_less_6,Toast.LENGTH_SHORT).show();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                Toast.makeText(Login.this,R.string.login_failed,Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(Login.this,R.string.login_error,Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                Log.e("Error",e.getMessage());
                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GOOGLE_SIGN_IN_CODE){
            Task<GoogleSignInAccount> signInTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount signInAccount=signInTask.getResult(ApiException.class);
                AuthCredential authCredential= GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
                fAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final String userID=fAuth.getCurrentUser().getUid();
                        fStore.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.success_login_google), Toast.LENGTH_SHORT).show();
                                    Intent toHome=new Intent(Login.this,Home.class);
                                    toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(toHome);
                                }
                                else{
                                    DocumentReference documentReference=fStore.collection("users").document(userID);
                                    Map<String,Object> user=new HashMap<>();
                                    user.put("id",userID);
                                    user.put("username",signInAccount.getDisplayName());
                                    user.put("email",signInAccount.getEmail());

                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Success google","user profile is created with uuid"+userID);
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.success_login_google), Toast.LENGTH_SHORT).show();
                                    Intent toHome=new Intent(Login.this,Home.class);
                                    toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(toHome);
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}
