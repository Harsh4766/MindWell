package com.example.mindwell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class Login extends AppCompatActivity {

    TextView signin;
    private TextInputEditText emailTextView, passwordTextView;

    ProgressBar progressBar;
    private TextView login;
    boolean flag=false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signin = findViewById(R.id.signin);
        emailTextView = findViewById(R.id.login_email);
        passwordTextView =findViewById(R.id.login_password);
        progressBar = findViewById(R.id.Progressbar);
        login = findViewById(R.id.logsub);
        mAuth = FirebaseAuth.getInstance();

        Login();
        SignIn();

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        linearLayout.startAnimation(fadeInAnimation);

        View LogInButton = findViewById(R.id.logsub);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        LogInButton.startAnimation(slideUpAnimation);
    }

    private void loginUserAccount()
    {
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),"Please enter email!!",Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    ProfilePage();
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    login.setText("LOG IN");
                    flag=false;
                    Toast.makeText(Login.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("NotConstructor")
    private void Login()
    {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==false) {
                    login.setText("Please wait");
                    progressBar.setVisibility(View.VISIBLE);
                    loginUserAccount();
                    flag=true;
                }
            }
        });
    }

    private void SignIn()
    {
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void ProfilePage()
    {
        Intent init = new Intent(getApplicationContext(), Home.class);
        startActivity(init);
        finish();
    }
    }