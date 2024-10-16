package com.example.mindwell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView login,textView,faculty;
    ProgressBar progressBar;
    boolean flag=false;
    private TextInputEditText emailTextView, passwordTextView, userNameTextView;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);
        progressBar=findViewById(R.id.Progressbar);
        textView=findViewById(R.id.signsub);
        emailTextView = findViewById(R.id.sign_in_email);
        passwordTextView = findViewById(R.id.sign_in_pass);
        textView = findViewById(R.id.signsub);
        mAuth = FirebaseAuth.getInstance();
        userNameTextView = findViewById(R.id.sign_in_full_name);
        db = FirebaseDatabase.getInstance();
        LoginPage();
        SignIn();
        AutoLogin();

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        linearLayout.startAnimation(fadeInAnimation);

        View signInButton = findViewById(R.id.signsub);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        signInButton.startAnimation(slideUpAnimation);
    }

    private void registerNewUser()
    {
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // Validations for input email and password

        if(TextUtils.isEmpty(userNameTextView.getText()))
        {
            Toast.makeText(getApplicationContext(),"Please enter Full Name!!",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            textView.setText("SIGN IN");
            flag=false;
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),"Please enter email!!",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            textView.setText("SIGN IN");
            flag=false;
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            textView.setText("SIGN IN");
            flag=false;
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    HashMap<String,String> data = new HashMap();
                    data.put("Username",userNameTextView.getText().toString());
                    data.put("Email",email);

                    db.getReference().child("User").child(mAuth.getCurrentUser().getUid()).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Register Successful!", Toast.LENGTH_SHORT).show();
                                ProfilePage();
                            } else
                            {
                                progressBar.setVisibility(View.GONE);
                                textView.setText("SIGN IN");
                                flag=false;
                                Toast.makeText(MainActivity.this, "error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    textView.setText("SIGN IN");
                    flag=false;
                    Toast.makeText(MainActivity.this, "error: " + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void LoginPage()
    {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void SignIn()
    {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==false) {
                    textView.setText("Please wait");
                    progressBar.setVisibility(View.VISIBLE);
                    flag=true;
                    registerNewUser();
                }
            }
        });
    }

    private void ProfilePage()
    {
        Intent init = new Intent(getApplicationContext(), Home.class);
        startActivity(init);
        finish();
    }

    private void AutoLogin()
    {
        if(mAuth.getCurrentUser() != null)
        {
            FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1: snapshot.getChildren())
                    {
                        if(snapshot1.getKey().toString().equals(mAuth.getCurrentUser().getUid()))
                        {
                            return;
                        }
                    }
                    ProfilePage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    }