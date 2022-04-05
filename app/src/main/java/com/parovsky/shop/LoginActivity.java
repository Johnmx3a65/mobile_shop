package com.parovsky.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parovsky.shop.utils.Utils;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog prgDialog;
    private TextInputEditText loginEmailInput;
    private TextInputEditText loginPasswordInput;
    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prgDialog = new ProgressDialog(this);
        loginEmailInput = findViewById(R.id.loginEmailInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        signInBtn = findViewById(R.id.loginSignInBtn);

        signInBtn.setOnClickListener(this::signInOnClick);
    }

    private void signInOnClick(View view) {
        String email = loginEmailInput.getText().toString();
        String password = loginPasswordInput.getText().toString();

        if(Utils.isNotNull(email) && Utils.isNotNull(password)) {
            if (Utils.validate(email)) {
                invokeWS(email, password);
            }else {
                Toast.makeText(this, "Моля вкарайте валиден имейл", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Моля попълнете всички полета", Toast.LENGTH_LONG).show();
        }
    }

    private void invokeWS(String email, String password) {
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(email, password);
        client.get("https://traver.cfapps.eu10.hana.ondemand.com/user", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                Toast.makeText(LoginActivity.this, "Успешно влизане", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.hide();
                if (statusCode == 401) {
                    Toast.makeText(LoginActivity.this, "Невалидни потребителски данни", Toast.LENGTH_LONG).show();
                }else if (statusCode == 404) {
                    Toast.makeText(LoginActivity.this, "Страницата не е намерена", Toast.LENGTH_LONG).show();
                }else if (statusCode == 500) {
                    Toast.makeText(LoginActivity.this, "Сървърна грешка", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(LoginActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}