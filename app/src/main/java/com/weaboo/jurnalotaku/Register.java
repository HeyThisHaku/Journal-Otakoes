package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText etUsername,etEmail,etPass,etConfPass;
    Button registerBtn;
    TextView toLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etUsername=findViewById(R.id.et_username);
        etEmail=findViewById(R.id.et_email);
        etPass=findViewById(R.id.et_password);
        etConfPass=findViewById(R.id.et_confirm_password);
        registerBtn=findViewById(R.id.btn_register);
        toLogin=findViewById(R.id.toLogin);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login=new Intent(Register.this,Login.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=etUsername.getText().toString();
                final String email=etEmail.getText().toString();
                String pass=etPass.getText().toString();
                String confPass=etConfPass.getText().toString();

                if(username.isEmpty()){
                    Toast.makeText(Register.this,R.string.register_usr_failed,Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty()){
                    Toast.makeText(Register.this,R.string.register_email_failed,Toast.LENGTH_SHORT).show();
                }
                else if(!emailValidation(email)){
                    Toast.makeText(Register.this,R.string.register_email_format_failed,Toast.LENGTH_SHORT).show();
                }
                else if(pass.isEmpty()){

                    Toast.makeText(Register.this,R.string.register_pass_failed,Toast.LENGTH_SHORT).show();
                }
                else if(!validateAlphaNum(pass)){
                    Toast.makeText(Register.this,R.string.register_pass_alphanum,Toast.LENGTH_SHORT).show();
                }
                else if(!confPass.equals(pass)){
                    Toast.makeText(Register.this,R.string.register_conf_pass_failed,Toast.LENGTH_SHORT).show();
                }
                else{
                    fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this,R.string.success_register,Toast.LENGTH_SHORT).show();
                                final String userID=fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference=fStore.collection("users").document(userID);
                                Map<String,Object> user=new HashMap<>();
                                user.put("id",userID);
                                user.put("username",username);
                                user.put("email",email);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"user profile is created with uuid"+userID);
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(),Home.class));
                                finish();
                            }
                            else{
                                Toast.makeText(Register.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
    private boolean emailValidation(String email) {
        int count=0;
        if(email.startsWith("@")){
            System.out.println("Email must not start with @");
            return false;
        }
        else if(!email.endsWith(".com")){
            System.out.println("Email must end with .com");
            return false;
        }
        else{
            for (int i = 0; i < email.length(); i++) {
                if(email.charAt(i)=='@'){
                    count++;
                    if(email.charAt(i+1)=='.'){
                        System.out.println("After @ should not be .");
                        return false;
                    }
                }

            }
            if(count!=1){
                System.out.println("Email must contain only one @");
                return false;

            }
        }
        return true;
    }
    private boolean validateAlphaNum(String pass){
        boolean alpha=false;
        boolean numeric=false;
        for (int i=0;i<pass.length();i++){
            if(Character.isAlphabetic(pass.charAt(i))){
                alpha=true;

            }
            else if(Character.isDigit(pass.charAt(i))){
                numeric=true;
            }
            if(alpha&&numeric){
                return true;
            }
        }
        return false;
    }
}
