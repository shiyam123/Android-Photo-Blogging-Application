package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginemailtext;
    private EditText loginpasstest;
    private Button loginbtn;
    private Button loginreg;
    private ProgressBar progbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        loginemailtext= (EditText) findViewById(R.id.reg_email);
        loginpasstest= (EditText) findViewById(R.id.reg_password);
        loginbtn= (Button) findViewById(R.id.button);
        loginreg= (Button) findViewById(R.id.button2);
        //comment;
        progbar= (ProgressBar) findViewById(R.id.progressBar);
        loginreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(reg);
                finish();
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lmail=loginemailtext.getText().toString();
                String pass=loginpasstest.getText().toString();
                if(!TextUtils.isEmpty(lmail) && !TextUtils.isEmpty(pass))
                {
                    progbar.setVisibility(View.VISIBLE);
                }
                mAuth.signInWithEmailAndPassword(lmail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            sendToMain();
                        }
                        else
                        {
                            String err="Sorry wrong credentials";
                            System.out.println(lmail);
                            System.out.println(pass);
                            Toast.makeText(LoginActivity.this,err,Toast.LENGTH_LONG).show();
                            progbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
            sendToMain();
        }



    }

    private void sendToMain() {
        Intent mainintent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }
}