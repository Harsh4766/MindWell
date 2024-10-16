package com.example.mindwell;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class advice extends AppCompatActivity {

    CardView cardView_1,cardView_2,cardView_3,cardView_4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        cardView_1 = findViewById(R.id.advice_card1);
        cardView_2 = findViewById(R.id.advice_card2);
        cardView_3 = findViewById(R.id.advice_card3);
        cardView_4=findViewById(R.id.home_nearby);

        String stage = getIntent().getStringExtra("stage");
        int currentStage = Integer.parseInt(stage);
        if(currentStage == 1)
        {
            cardView_1.setVisibility(View.VISIBLE);
            cardView_2.setVisibility(View.GONE);
            cardView_3.setVisibility(View.GONE);
        }
        else if(currentStage == 2)
        {
            cardView_1.setVisibility(View.GONE);
            cardView_2.setVisibility(View.VISIBLE);
            cardView_3.setVisibility(View.GONE);
        }
        else
        {
            cardView_1.setVisibility(View.GONE);
            cardView_2.setVisibility(View.GONE);
            cardView_3.setVisibility(View.VISIBLE);
        }

        cardView_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/near+by+hospitals"));
                intent.setPackage("com.google.android.apps.maps");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}