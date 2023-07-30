package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar maintool;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton addpost;
    private String curr_user_id;
    private BottomNavigationView bottomnav;
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        maintool= (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(maintool);
        getSupportActionBar().setTitle("Photo Blog");
        if(mAuth.getCurrentUser()!=null) {
            addpost = findViewById(R.id.floatingActionButton);
            bottomnav = findViewById(R.id.bottomnav);
            //fragments
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();
            replaceFragment(homeFragment);
            bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_home:
                            replaceFragment(homeFragment);
                            return true;

                        case R.id.bottom_notify:
                            replaceFragment(notificationFragment);
                            return true;

                        case R.id.bottom_acc:
                            replaceFragment(accountFragment);
                            return true;

                        default:
                            return false;

                    }


                }
            });
            addpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pos = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(pos);
                    finish();

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent loginintent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginintent);
            finish();
            // User is signed in
        } else {
            curr_user_id=mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(curr_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(!task.getResult().exists())
                        {
                            Intent set=new Intent(MainActivity.this,SetupActivity.class);
                            startActivity(set);
                            finish();

                        }
                    }
                    else
                    {
                        String errormessage=task.getException().getMessage();
                        Toast.makeText(MainActivity.this,errormessage,Toast.LENGTH_LONG);
                    }

                }
            });
            // No user is signed in
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout_btn_main:
                mAuth.signOut();
                logOut();
                return true;
            case R.id.account_btn_main:
                Intent m=new Intent(MainActivity.this, SetupActivity.class);
                startActivity(m);
                finish();
                return true;
            default:
                return false;

        }


    }

    private void logOut() {

        Intent log= new Intent(MainActivity.this,LoginActivity.class);
        startActivity(log);
        finish();
    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }
}