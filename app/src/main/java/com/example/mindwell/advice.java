package com.example.mindwell;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class advice extends AppCompatActivity {

    CardView cardView_1, cardView_2, cardView_3, cardView_4;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        // Initialize FusedLocationProviderClient for location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Find views by ID
        cardView_1 = findViewById(R.id.advice_card1);
        cardView_2 = findViewById(R.id.advice_card2);
        cardView_3 = findViewById(R.id.advice_card3);
        cardView_4 = findViewById(R.id.home_nearby);

        // Get the stage from the Intent and show the appropriate card view
        String stage = getIntent().getStringExtra("stage");
        int currentStage = Integer.parseInt(stage);

        if (currentStage == 1) {
            cardView_1.setVisibility(View.VISIBLE);
            cardView_2.setVisibility(View.GONE);
            cardView_3.setVisibility(View.GONE);
        } else if (currentStage == 2) {
            cardView_1.setVisibility(View.GONE);
            cardView_2.setVisibility(View.VISIBLE);
            cardView_3.setVisibility(View.GONE);
        } else {
            cardView_1.setVisibility(View.GONE);
            cardView_2.setVisibility(View.GONE);
            cardView_3.setVisibility(View.VISIBLE);
        }


        cardView_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(advice.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(advice.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {

                    getCoordinates();
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/search/near+by+psychiatrist"));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            }
        });
    }

    private void getCoordinates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Toast.makeText(this, "Longtitude:"+longitude+" latitude:"+latitude, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
