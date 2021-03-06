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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAdminActivity extends AppCompatActivity {

    private EditText emailView, passwordView;

    private Button createBtnView;

    private ProgressBar regprogress;

    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_admin);

        emailView = findViewById(R.id.input_email);
        passwordView = findViewById(R.id.input_password);
        createBtnView = findViewById(R.id.create_btn);

        firebaseFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        regprogress = findViewById(R.id.login_progress);

        createBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) ) {



                        regprogress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    //firebaseFirestore.collection("Admin/"+mAuth.getCurrentUser().getUid()+"/").add(new HashMap<>());

                                    //Toast.makeText(CreateAdminActivity.this,mAuth.getCurrentUser().getUid(),Toast.LENGTH_LONG).show();

                                    String newId = mAuth.getCurrentUser().getUid().toString();

                                    Map<String, Object> map =new HashMap<>();
                                    map.put("admin",new Boolean(true));

                                    firebaseFirestore.collection("Admin/"+newId+"/Admin").add(map);
                                    firebaseFirestore.collection("Admin").document(newId).set(map);



                                    Intent setupIntent = new Intent(CreateAdminActivity.this, AdminPanel.class);
                                    startActivity(setupIntent);
                                    finish();


                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(CreateAdminActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                                }

                            }
                        });

                    }
            }
        });
    }
}