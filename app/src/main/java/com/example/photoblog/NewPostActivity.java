package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import io.grpc.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private static final int MAX_LENGTH=100;
    private Toolbar newpostt;
    private ImageView posima;
    private EditText des;
    private Button addp;
    private Uri re = null;
    private Uri r=null;
    private Boolean isChanged;
    private StorageReference storref;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String curr_user_id;
    private String shiyam;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private Map<String, Object> usermap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        newpostt = findViewById(R.id.newposttool);
        setSupportActionBar(newpostt);
        getSupportActionBar().setTitle("Add New post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        posima = findViewById(R.id.newpostimage);
        des = findViewById(R.id.postdesc);
        addp = findViewById(R.id.button4);
        mAuth = FirebaseAuth.getInstance();
        curr_user_id = mAuth.getCurrentUser().getUid();
        storref = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        posima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posima.setImageDrawable(null);
                Crop.pickImage(NewPostActivity.this);


            }
        });
        addp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = des.getText().toString();
                if (!TextUtils.isEmpty(desc) && re != null) {
                    String randname = UUID.randomUUID().toString();
                    StorageReference filepath = storref.child("Post_images").child(randname + ".jpg");
                    filepath.putFile(re).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri download_uri;
                                    if (task != null) {
                                        download_uri = uri;
                                    } else {
                                        download_uri = re;
                                    }

//                                    File newImageFile=new File(download_uri.getPath());
//                                    String filePath = SiliCompressor.with(NewPostActivity.this).compress(download_uri.toString(), newImageFile);
//                                    StorageReference thumbFile=storref.child("Post_images/thumbs").child(randname+".jpg");
                                   //String imagePath = re.getPath(); // replace with your image path
//                                    File file = new File(filePath);
//                                    Uri ui = Uri.fromFile(file);
//                                    thumbFile.putFile(ui);

                                    String download_url = re.toString();
                                    //uploadPostImage(re.getPath(),randname);

                                    usermap.put("description", desc.toString());
                                    System.out.println(shiyam);
                                    usermap.put("thumb",uploadPostImage(re.getPath(),randname));
                                    usermap.put("image", download_uri);
                                    usermap.put("user_id", curr_user_id);
                                    usermap.put("timestamp", FieldValue.serverTimestamp());
                                    firebaseFirestore.collection("Posts").document(randname).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(NewPostActivity.this, "Post added successfully", Toast.LENGTH_LONG).show();
                                                Intent n = new Intent(NewPostActivity.this, MainActivity.class);
                                                startActivity(n);
                                                finish();

                                            } else {
                                                String e = task.getException().getMessage();
                                                Toast.makeText(NewPostActivity.this, e, Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            re = result.getData();
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));

        Crop.of(source, destination).asSquare().start(NewPostActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            re = Crop.getOutput(result);
            posima.setImageURI(Crop.getOutput(result));
            isChanged = true;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static String random() {

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
    private Bitmap compressBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratio = (float) width / (float) height;

        if (width > maxWidth || height > maxHeight) {
            if (ratio > 1) {
                width = maxWidth;
                height = (int) (width / ratio);
            } else {
                height = maxHeight;
                width = (int) (height * ratio);
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
    private String uploadPostImage(String imagePath,String randname) {
        // Create a reference to the Firebase Storage location where the thumbnail will be stored
        StorageReference thumbnailRef = storref.child("Post_images/thumbs").child(randname + ".jpg");

        // Create a bitmap from the image file path
        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);

        // Compress the bitmap to a smaller size
        Bitmap compressedBitmap = compressBitmap(originalBitmap, 500, 500);

        // Create an output stream to write the compressed bitmap to the Firebase Storage location
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        // Upload the compressed bitmap to Firebase Storage
        UploadTask uploadTask = thumbnailRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the thumbnail from Firebase Storage
            thumbnailRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Save the download URL to Firebase Firestore
                Map<String, Object> post = new HashMap<>();
                shiyam=uri.toString();
                //usermap.put("thumb", uri.toString());
                post.put("thumb_url",shiyam);
                System.out.println(shiyam+"in the func");

                FirebaseFirestore.getInstance().collection("posts").add(post);
            });
        });
        return shiyam;

    }
}







