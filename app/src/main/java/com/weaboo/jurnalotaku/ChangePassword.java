package com.weaboo.jurnalotaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePassword extends AppCompatActivity {
    ImageButton backBtn;
    EditText oldPass,confPass,newPass;
    Button save;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        backBtn=findViewById(R.id.back_btn);
        oldPass=findViewById(R.id.old_pass);
        confPass=findViewById(R.id.confirm_pass);
        newPass=findViewById(R.id.newPass);
        save=findViewById(R.id.savePassword);
        fAuth= FirebaseAuth.getInstance();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_pass=oldPass.getText().toString();
                String conf_pass=confPass.getText().toString();
                final String new_pass=newPass.getText().toString();
                if(old_pass.isEmpty()){
                    Toast.makeText(ChangePassword.this,R.string.old_pass_failed,Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(new_pass.isEmpty()){

                    Toast.makeText(ChangePassword.this,R.string.new_pass_failed,Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(new_pass.length()<6){
                    Toast.makeText(ChangePassword.this,R.string.new_pass_6_char,Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!validateAlphaNum(new_pass)){
                    Toast.makeText(ChangePassword.this,R.string.new_pass_alphanum,Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!conf_pass.equals(new_pass)){
                    Toast.makeText(ChangePassword.this,R.string.conf_pass_failed,Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    AuthCredential credential = EmailAuthProvider.getCredential(fAuth.getCurrentUser().getEmail(),old_pass);
                    fAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                fAuth.getCurrentUser().updatePassword(new_pass);
                                Toast.makeText(ChangePassword.this,R.string.success_update_pass,Toast.LENGTH_SHORT).show();
                                Intent toProfile=new Intent(ChangePassword.this,ManageProfile.class);
                                toProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(toProfile);
                            } else {
                                // Password is incorrect
                                Toast.makeText(ChangePassword.this,R.string.failed_update_pass,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile=new Intent(ChangePassword.this,ManageProfile.class);
                toProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toProfile);
            }
        });
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
