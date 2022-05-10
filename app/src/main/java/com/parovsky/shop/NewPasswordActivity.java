package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.CURRENT_USER_EXTRA;
import static com.parovsky.shop.utils.Utils.EMAIL_EXTRA;
import static com.parovsky.shop.utils.Utils.FIRST_NAME_EXTRA;
import static com.parovsky.shop.utils.Utils.LAST_NAME_EXTRA;
import static com.parovsky.shop.utils.Utils.USER_EXTRA;
import static com.parovsky.shop.utils.Utils.VERIFY_CODE_EXTRA;
import static com.parovsky.shop.utils.Utils.isNotNull;
import static com.parovsky.shop.utils.Utils.showToast;
import static com.parovsky.shop.utils.Utils.validatePassword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewPasswordActivity extends AppCompatActivity {

    private static final String RESET_PASSWORD_URL = "https://traver.cfapps.eu10.hana.ondemand.com/reset-password";

    private ImageView backImage;

    private TextInputEditText newPasswordInput;

    private TextInputEditText confirmPasswordInput;

    private Button saveBtn;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        progressDialog = new ProgressDialog(this);

        backImage = findViewById(R.id.newPasswordBackImage);
        backImage.setOnClickListener(this::backImageOnClick);

        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.newConfirmPasswordInput);

        saveBtn = findViewById(R.id.newPasswordSaveBtn);
        saveBtn.setOnClickListener(this::saveBtnOnClick);
    }

    private void saveBtnOnClick(View view) {
        String newPassword = newPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        if (isNotNull(newPassword) && isNotNull(confirmPassword)) {
            if (newPassword.equals(confirmPassword)) {
                if (validatePassword(newPassword)) {
                    try {
                        invokeWS(newPassword);
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        showToast(this, "Възникна неочаквана грешка");
                    }
                } else {
                    showToast(this, "Паролата трябва да започва с голяма буква, да е поне 6 символа и да включва поне 1 цифра");
                }
            } else {
                showToast(this, "Паролите не съвпадат");
            }
        } else {
            showToast(this, "Попълнете всички полета");
        }
    }

    private void invokeWS(String newPassword) throws JSONException, UnsupportedEncodingException {
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", newPassword);
        jsonObject.put("mail", getIntent().getStringExtra(EMAIL_EXTRA));
        jsonObject.put("verifyCode", getIntent().getStringExtra(VERIFY_CODE_EXTRA));
        StringEntity entity = new StringEntity(jsonObject.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.put(getApplicationContext(), RESET_PASSWORD_URL, entity, "application/json",  new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                startActivity(new Intent(NewPasswordActivity.this, LoginActivity.class));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 401) {
                    showToast(NewPasswordActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(NewPasswordActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(NewPasswordActivity.this, "Сървърна грешка");
                }else {
                    showToast(NewPasswordActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });
    }

    private void backImageOnClick(View view) {
        finish();
    }
}