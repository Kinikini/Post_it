package com.example.postit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add new post");
    }
}