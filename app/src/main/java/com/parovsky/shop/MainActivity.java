package com.parovsky.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openLogInBtn = findViewById(R.id.logInButtonGreeting);
        openLogInBtn.setOnClickListener(this::openLoginPage);

        TextView openSignInTxt = findViewById(R.id.signInTextGreeting);
        openSignInTxt.setOnClickListener(this::openSignIn);
    }

    /**
     * Методът се задейства, когато Login бутона е натиснат
     *
     * @param view
     */
    private void openLoginPage(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    /**
     * Методът се задейства, когато Sign in текста е натиснат
     *
     * @param view
     */
    private void openSignIn(View view) {
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
    }
}