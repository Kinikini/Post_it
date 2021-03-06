package com.example.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginb;
    private Button newhb;
    private ProgressBar loginprogress;

    private FirebaseAuth mAuth;

    @Override
    public void onResume()
    {
        super.onResume();

        loginprogress = findViewById(R.id.login_progress);

        loginprogress.setVisibility(View.INVISIBLE);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        loginb = findViewById(R.id.loginb);
        newhb = findViewById(R.id.newhb);
        loginprogress = findViewById(R.id.login_progress);

        loginprogress.setVisibility(View.INVISIBLE);


        newhb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginprogress.setVisibility(View.VISIBLE);
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);


            }
        });


        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String loginEmail = email.getText().toString();
                String loginPass = password.getText().toString();

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){


                    //loginprogress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loginprogress.setVisibility(View.VISIBLE);

                            if (task.isSuccessful()){

                                sendToMain();

                            } else {

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error : "+ errorMessage, Toast.LENGTH_LONG).show();
                                loginprogress.setVisibility(View.INVISIBLE);


                            }

                            //



                        }
                    });

                    loginprogress.setVisibility(View.INVISIBLE);


                }

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendToMain();

        } else {

        }

    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}