package com.example.mindwell;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.imageView);
        TextView appName = findViewById(R.id.appName);
        TextView desc = findViewById(R.id.desc);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        logo.startAnimation(slideUp);
        appName.startAnimation(fadeIn);
        desc.startAnimation(fadeIn);

        // Delay for 3 seconds and start the next activity
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(splashscreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);  // 3 seconds delay
    }
}
