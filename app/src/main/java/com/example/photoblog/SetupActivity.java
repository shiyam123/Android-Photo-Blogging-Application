package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private EditText setupname;
    private Button set_btn;
    private StorageReference storref;
    private FirebaseAuth firebaseauth;
    private FirebaseFirestore firebasefirestore;
    private String user_id;
    private Uri re=null;
    private ProgressBar progn;
    private boolean isChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);




        Toolbar setuptoolbar=findViewById(R.id.setuptool);
        setSupportActionBar(setuptoolbar);
        getSupportActionBar().setTitle("Account Setup");

        firebaseauth=FirebaseAuth.getInstance();
        user_id=firebaseauth.getCurrentUser().getUid();
        firebasefirestore=FirebaseFirestore.getInstance();
        storref= FirebaseStorage.getInstance().getReference();

        setupImage = findViewById(R.id.profile_image);
        setupname=findViewById(R.id.textpersonname);
        set_btn=findViewById(R.id.button3);
        progn=findViewById(R.id.progressBar2);

        firebasefirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        String name=task.getResult().getString("name");
                        String image=task.getResult().getString("image");
                        re=Uri.parse(image);
                        RequestOptions placeholderrequest=new RequestOptions();
                        placeholderrequest.placeholder(R.drawable.default_image);
                        setupname.setText(name);

                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderrequest).load(image).into(setupImage);
                        Toast.makeText(SetupActivity.this,"Data Exist",Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(SetupActivity.this,"Data does'nt Exist",Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    String e=task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,e,Toast.LENGTH_LONG).show();
                }
            }
        });

        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = setupname.getText().toString();

                if(isChanged) {
                    System.out.println("sai");
                    System.out.println(re.toString());
                    //String user_name = setupname.getText().toString();
                    progn.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(user_name) && re != null) {
                        user_id = firebaseauth.getCurrentUser().getUid();
                        StorageReference image_path = storref.child("Profile images").child(user_id + ".png");
                        final StorageReference tem=image_path;
                        image_path.putFile(re).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storefire(task, image_path, user_name);
                                } else {
                                    String err = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, err, Toast.LENGTH_LONG).show();
                                }


                                progn.setVisibility(View.INVISIBLE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SetupActivity.this, "Failed to upload the image", Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                }
                else
                {
                    storefire(null,null,user_name);
                }
            }
        });
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if(ContextCompat.checkSelfPermission(SetupActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(SetupActivity.this,"Permission denied",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }
                    else
                    {
                        setupImage.setImageDrawable(null);
                        Crop.pickImage(SetupActivity.this);
                        Toast.makeText(SetupActivity.this,"You already have permission granted",Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    setupImage.setImageDrawable(null);
                    Crop.pickImage(SetupActivity.this);
                    Toast.makeText(SetupActivity.this,"You already have permission granted",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void storefire(Task<UploadTask.TaskSnapshot> task,StorageReference image_path,String user_name) {
        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(task!=null)
                {
                    Uri download_uri=uri;
                }
                else
                {
                    Uri download_uri=re;
                }

                String download_url=uri.toString();
                Map<String,String> usermap=new HashMap<>();
                usermap.put("name",user_name);
                usermap.put("image",download_url);
                firebasefirestore.collection("Users").document(user_id).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this,"Profile updated successfully",Toast.LENGTH_LONG).show();
                            Intent n=new Intent(SetupActivity.this,MainActivity.class);
                            startActivity(n);
                            finish();

                        }
                        else
                        {
                            String e=task.getException().getMessage();
                            Toast.makeText(SetupActivity.this,e,Toast.LENGTH_LONG).show();
                            progn.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                Toast.makeText(SetupActivity.this,"Image uploaded",Toast.LENGTH_LONG).show();
                System.out.println("checking.............................................................");
                System.out.println(download_url);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            re=result.getData();
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));

        Crop.of(source, destination).asSquare().start(SetupActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            re=Crop.getOutput(result);
            setupImage.setImageURI(Crop.getOutput(result));
            isChanged=true;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}