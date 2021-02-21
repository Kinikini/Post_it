package com.example.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText regemail;
    private EditText regpassword;
    private EditText confirmpassword;
    private Button createaccount;
    private Button alreadyb;
    private ProgressBar regprogress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regemail = findViewById(R.id.reg_email);
        regpassword = findViewById(R.id.reg_password);
        confirmpassword = findViewById(R.id.confirmPassword);
        createaccount = findViewById(R.id.loginb);
        alreadyb = findViewById(R.id.newhb);
        regprogress = findViewById(R.id.login_progress);


        alreadyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = regemail.getText().toString();
                String password = regpassword.getText().toString();
                String passconfirm = confirmpassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passconfirm)) {

                    if (password.equals(passconfirm)) {

                        regprogress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();


                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                                }

                            }
                        });

                    } else {

                        Toast.makeText(RegisterActivity.this, "Confirm Password and Password field doesn't match", Toast.LENGTH_LONG).show();

                    }

                }


            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendToMainPage();
        }
    }

    private void sendToMainPage () {

            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();

        }

    }

