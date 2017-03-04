package com.example.vidish.round1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AfterLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        setTitle("Hello "+(getIntent().getStringExtra("name").split(" "))[0]);
        //TODO cardview daalna hai
    }
}
