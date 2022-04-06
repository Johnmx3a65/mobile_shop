package com.parovsky.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        Button greetingStartBtn = findViewById(R.id.greetingStartBtn);
        greetingStartBtn.setOnClickListener(this::greetingStartOnClick);

    }

    private void greetingStartOnClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}