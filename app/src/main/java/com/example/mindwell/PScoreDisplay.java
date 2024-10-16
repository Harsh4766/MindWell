package com.example.mindwell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PScoreDisplay extends AppCompatActivity {

    RecyclerView recyclerView;
    PScoreAdapter myAdapter;
    FirebaseAuth mAuth;
    DatabaseReference ref,db;
    ImageView pScoreBack;
    TextView pScoreName;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pscore_display);

        recyclerView = findViewById(R.id.recycler_view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        pScoreBack = findViewById(R.id.pscore_back);
        pScoreName = findViewById(R.id.pscore_name);

        ref = FirebaseDatabase.getInstance().getReference().child("Data");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        ref.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    FirebaseRecyclerOptions<PScoreModel> options =
                            new FirebaseRecyclerOptions.Builder<PScoreModel>()
                                    .setQuery(snapshot.getRef(),PScoreModel.class)
                                    .build();
                    myAdapter = new PScoreAdapter(options,PScoreDisplay.this);
                    myAdapter.startListening();
                    recyclerView.setAdapter(myAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pScoreBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent init = new Intent(getApplicationContext(), Home.class);
                startActivity(init);
                finish();
            }
        });

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
                        pScoreName.setText(snapshot1.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }
}