package com.example.mindwell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class Home extends AppCompatActivity {

    CardView quiz,scoreCardView;
    TextView con_quiz,previousButton;
    FirebaseAuth mAuth;
    DatabaseReference db;
    String progress;
    int stage;
    ProgressBar homeProgressBar;
    TextView percentageValue,advice,homeName,homeHealth,viewmore;
    boolean userFirstTime = true;
    ImageView profile;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final int CAMERA_REQUEST = 2;  // Unique request code for camera

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        quiz=findViewById(R.id.home_cardquiz);
        viewmore = findViewById(R.id.home_view);
        scoreCardView = findViewById(R.id.home_card2);
        con_quiz=findViewById(R.id.home_continue);
        previousButton = findViewById(R.id.home_prevrecd);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        homeProgressBar = findViewById(R.id.home_progress);
        percentageValue = findViewById(R.id.home_scoreper);
        advice=findViewById(R.id.home_advice);
        homeName = findViewById(R.id.home_name);
        homeHealth = findViewById(R.id.home_stage);
        profile=findViewById(R.id.home_logo);

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Home.this,Survey.class);
                startActivity(i);
                finish();
            }
        });

        con_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Home.this,Survey.class);
                startActivity(i);
                finish();
            }
        });

        viewmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent init = new Intent(getApplicationContext(), com.example.mindwell.advice.class);
                init.putExtra("stage",""+stage);
                startActivity(init);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Home.this,PScoreDisplay.class);
                startActivity(i);
                finish();
            }
        });

        if(userFirstTime)
        {
            scoreCardView.setVisibility(View.GONE);
            con_quiz.setVisibility(View.VISIBLE);
        }
        else
        {
            scoreCardView.setVisibility(View.VISIBLE);
            con_quiz.setVisibility(View.GONE);
        }
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        CheckCurrentUser();
        FetchData();
    }


    private void takePicture() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 100);
        // Create an intent to open the camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure there's a camera activity to handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create a temporary file to save the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(
                        this,
                        "com.example.mindwell.fileprovider",  // Change to your app's package name
                        photoFile
                );
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  // Prefix
                ".jpg",         // Suffix
                storageDir      // Directory
        );
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            profile.setImageURI(imageUri);  // Set the captured image to the ImageView
            uploadPicture();  // Upload the captured image
        }
    }

    private void uploadPicture()
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey= UUID.randomUUID().toString();
        StorageReference ref=storageReference.child("images/"+randomKey);

        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String,Object> data = new HashMap<>();
                        data.put("imageUrl",uri.toString());
                        db.child(mAuth.getCurrentUser().getUid()).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(),"Image uploaded",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    pd.dismiss();
                                }
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progressPercent=(100.00 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                pd.setMessage("Progress "+(int) progressPercent+"%");
            }
  });
    }

    private void FetchData() {
        db.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getKey().toString().equals("imageUrl")) {
                        Glide.with(getApplicationContext()).load(snapshot1.getValue().toString()).into(profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void CheckCurrentUser()
    {
        db.child("User").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()) {

                    if(snapshot1.getKey().equals("Username"))
                    {
                        homeName.setText(snapshot1.getValue().toString());
                    }

                    if(snapshot1.getKey().equals("Score"))
                    {
                        userFirstTime = false;
                        progress = snapshot1.getValue().toString();
                        homeProgressBar.setProgress(Integer.parseInt(progress));
                        Drawable progressDrawable = homeProgressBar.getProgressDrawable();

                        percentageValue.setText(progress);
                        if(userFirstTime)
                        {
                            scoreCardView.setVisibility(View.GONE);
                            con_quiz.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            scoreCardView.setVisibility(View.VISIBLE);
                            con_quiz.setVisibility(View.GONE);
                        }

                        int score=Integer.parseInt(percentageValue.getText().toString());

                        if(score < 75 && score > 55)
                        {
                            stage=1;
                            homeHealth.setText("Mental Health Stage: Normal");
                            if (progressDrawable instanceof LayerDrawable) {
                                // Cast the progress drawable to a LayerDrawable
                                LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;

                                // Find the item you want to change by its index (0 for the background, 1 for the progress)
                                int progressItemIndex = 0;
                                Drawable progressItem = layerDrawable.getDrawable(progressItemIndex);

                                // Modify the color of the shape inside the progress item
                                if (progressItem instanceof GradientDrawable) {
                                    // Cast the Drawable to a GradientDrawable (assuming it's a shape)
                                    GradientDrawable shape = (GradientDrawable) progressItem;

                                    // Set the new color
                                    shape.setStroke(3,Color.parseColor("#2fde2d")); // Change to your desired color
                                }
                                else
                                {
                                    Toast.makeText(Home.this, "not at all", Toast.LENGTH_SHORT).show();
                                }

                                int progressItemIndex_1 = 1;
                                Drawable progressItem_1 = layerDrawable.getDrawable(progressItemIndex_1);
                                ScaleDrawable scaleDrawable = (ScaleDrawable) progressItem_1;

                                // Get the inner drawable
                                Drawable innerDrawable = scaleDrawable.getDrawable();

                                // If the inner drawable is a GradientDrawable, change its color
                                if (innerDrawable instanceof GradientDrawable) {
                                    GradientDrawable shape = (GradientDrawable) innerDrawable;
                                    shape.setColor(Color.parseColor("#2fde2d")); // Change to your desired color
                                }
                                else
                                {
                                    Toast.makeText(Home.this, "", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Home.this, "not", Toast.LENGTH_SHORT).show();
                            }

                            advice.setText("1.Regular Exercise: Aim for at least 30 minutes of moderate exercise most days of the week. \n 2.Balanced Diet:Eating a balanced diet with plenty of fruits, vegetables, whole grains, lean proteins.");
                        }
                        else if(score<55 && score>35)
                        {
                            stage=2;
                            homeHealth.setText("Mental Health Stage: Intermediate");
                            if (progressDrawable instanceof LayerDrawable) {
                                // Cast the progress drawable to a LayerDrawable
                                LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;

                                // Find the item you want to change by its index (0 for the background, 1 for the progress)
                                int progressItemIndex = 0;
                                Drawable progressItem = layerDrawable.getDrawable(progressItemIndex);

                                // Modify the color of the shape inside the progress item
                                if (progressItem instanceof GradientDrawable) {
                                    // Cast the Drawable to a GradientDrawable (assuming it's a shape)
                                    GradientDrawable shape = (GradientDrawable) progressItem;

                                    // Set the new color
                                    shape.setStroke(3,Color.parseColor("#2d88de")); // Change to your desired color
                                }
                                else
                                {
                                    Toast.makeText(Home.this, "not at all", Toast.LENGTH_SHORT).show();
                                }

                                int progressItemIndex_1 = 1;
                                Drawable progressItem_1 = layerDrawable.getDrawable(progressItemIndex_1);
                                ScaleDrawable scaleDrawable = (ScaleDrawable) progressItem_1;

                                // Get the inner drawable
                                Drawable innerDrawable = scaleDrawable.getDrawable();

                                // If the inner drawable is a GradientDrawable, change its color
                                if (innerDrawable instanceof GradientDrawable) {
                                    GradientDrawable shape = (GradientDrawable) innerDrawable;
                                    shape.setColor(Color.parseColor("#2d88de")); // Change to your desired color
                                }
                                else
                                {
                                    Toast.makeText(Home.this, "", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Home.this, "not", Toast.LENGTH_SHORT).show();
                            }
                            advice.setText("1.Therapy or Counseling: Consider regular sessions with a therapist or counselor. \n 2. Self-care: Doing yoga or meditation , adequate sleep , balanced diet ");
                        }
                        else
                        {
                            stage=3;
                            homeHealth.setText("Mental Health Stage: Critical");
                            if (progressDrawable instanceof LayerDrawable) {
                                // Cast the progress drawable to a LayerDrawable
                                LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;

                                // Find the item you want to change by its index (0 for the background, 1 for the progress)
                                int progressItemIndex = 0;
                                Drawable progressItem = layerDrawable.getDrawable(progressItemIndex);

                                // Modify the color of the shape inside the progress item
                                if (progressItem instanceof GradientDrawable) {
                                    // Cast the Drawable to a GradientDrawable (assuming it's a shape)
                                    GradientDrawable shape = (GradientDrawable) progressItem;

                                    // Set the new color
                                    shape.setStroke(3,Color.parseColor("#de2d2d")); // Change to your desired color
                                }
                                else
                                {
                                    Toast.makeText(Home.this, "not at all", Toast.LENGTH_SHORT).show();
                                }

                                int progressItemIndex_1 = 1;
                                Drawable progressItem_1 = layerDrawable.getDrawable(progressItemIndex_1);
                                ScaleDrawable scaleDrawable = (ScaleDrawable) progressItem_1;

                                // Get the inner drawable
                                Drawable innerDrawable = scaleDrawable.getDrawable();

                                // If the inner drawable is a GradientDrawable, change its color
                                if (innerDrawable instanceof GradientDrawable) {
                                    GradientDrawable shape = (GradientDrawable) innerDrawable;
                                    shape.setColor(Color.parseColor("#de2d2d")); // Change to your desired color
                                }
                                else
                                {
                                    Toast.makeText(Home.this, "", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Home.this, "not", Toast.LENGTH_SHORT).show();
                            }
                            advice.setText("1.Immediate professional help \n 2. Hospitalization if Necessary \n 3.Avoid Alcohol and Substance Use \n 4. Maintain 24/7 Support Network ");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}