package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.CURRENT_USER_EXTRA;
import static com.parovsky.shop.utils.Utils.USER_ID_EXTRA;
import static com.parovsky.shop.utils.Utils.showToast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parovsky.shop.model.User;
import com.parovsky.shop.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog prgDialog;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button signInBtn;
    private Button createAccount;
    private TextView forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prgDialog = new ProgressDialog(this);
        emailInput = findViewById(R.id.loginEmailInput);
        passwordInput = findViewById(R.id.loginPasswordInput);

        forgotPasswordText = findViewById(R.id.loginForgotPasswordText);
        forgotPasswordText.setOnClickListener(this::forgotPasswordOnClick);

        signInBtn = findViewById(R.id.loginSignInBtn);
        signInBtn.setOnClickListener(this::signInOnClick);

        createAccount = findViewById(R.id.loginCreateAccountBtn);
        createAccount.setOnClickListener(this::createAccountOnClick);
    }

    private void createAccountOnClick(View view) {
        startActivity(new Intent(this, RegisterNameActivity.class));
    }

    private void forgotPasswordOnClick(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    private void signInOnClick(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(Utils.isNotNull(email) && Utils.isNotNull(password)) {
            if (Utils.validateEmail(email)) {
                invokeWS(email, password);
            }else {
                showToast(this, "Моля вкарайте валиден имейл");
            }
        }else {
            showToast(this, "Моля попълнете всички полета");
        }
    }

    private void invokeWS(String email, String password) {
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(email, password);
        client.get("https://traver.cfapps.eu10.hana.ondemand.com/current-user", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                SaveSharedPreference.setUserEmail(LoginActivity.this, email);
                SaveSharedPreference.setUserPassword(LoginActivity.this, password);
                Intent homePageIntent = new Intent(LoginActivity.this, HomePageActivity.class);
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    Bundle bundle = new Bundle();
                    bundle.putString(CURRENT_USER_EXTRA, jsonObject.toString());
                    homePageIntent.putExtras(bundle);
                    startActivity(homePageIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.hide();
                if (statusCode == 401) {
                    showToast(LoginActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(LoginActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(LoginActivity.this, "Сървърна грешка");
                }else {
                    showToast(LoginActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });

    }
}