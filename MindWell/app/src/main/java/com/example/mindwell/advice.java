package com.example.mindwell;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class advice extends AppCompatActivity {

    CardView cardView_1,cardView_2,cardView_3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        cardView_1 = findViewById(R.id.advice_card1);
        cardView_2 = findViewById(R.id.advice_card2);
        cardView_3 = findViewById(R.id.advice_card3);

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
    }
}