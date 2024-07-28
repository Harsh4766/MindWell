package com.example.mindwell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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



        CheckCurrentUser();
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