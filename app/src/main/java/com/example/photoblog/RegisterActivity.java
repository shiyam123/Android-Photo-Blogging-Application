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

public class RegisterActivity extends AppCompatActivity {

    private EditText email_field;
    private EditText password_field;
    private EditText confirm_pass;
    private Button reg_btn;
    private Button reg_login;
    private ProgressBar reg_progress;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        mAuth=FirebaseAuth.getInstance();

        email_field=(EditText) findViewById(R.id.reg_email);
        password_field=(EditText) findViewById(R.id.reg_password);
        confirm_pass=(EditText) findViewById(R.id.confirm_password);
        reg_btn=(Button) findViewById(R.id.button);
        reg_login=(Button) findViewById(R.id.button2);
        reg_progress=(ProgressBar) findViewById(R.id.progressBar);

        reg_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(log);
                finish();
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=email_field.getText().toString();
                String pass=password_field.getText().toString();
                String cpass=confirm_pass.getText().toString();

                if(!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(cpass))
                {
                    if(pass.equals(cpass))
                    {
                        reg_progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Intent setupint=new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setupint);
                                    finish();


                                }

                                else
                                {
                                    String errormessage=task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,errormessage,Toast.LENGTH_LONG);
                                }
                                reg_progress.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this,"Confirm password and password field does not match",Toast.LENGTH_LONG);
                    }
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser=mAuth.getCurrentUser();
        if(currentuser != null)
        {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent main=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}