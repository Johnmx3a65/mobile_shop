package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.EMAIL_EXTRA;
import static com.parovsky.shop.utils.Utils.FIRST_NAME_EXTRA;
import static com.parovsky.shop.utils.Utils.LAST_NAME_EXTRA;
import static com.parovsky.shop.utils.Utils.PARENT_EXTRA;
import static com.parovsky.shop.utils.Utils.isNotNull;
import static com.parovsky.shop.utils.Utils.showToast;
import static com.parovsky.shop.utils.Utils.validatePassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterPasswordActivity extends AppCompatActivity {

    private static final String REGISTER_URL = "https://traver.cfapps.eu10.hana.ondemand.com/register";

    private static final String SEND_VERIFICATION_CODE_URL = "https://traver.cfapps.eu10.hana.ondemand.com/send-verification-code";

    private ImageView backImage;

    private TextInputEditText newPasswordInput;

    private TextInputEditText confirmPasswordInput;

    private Button saveBtn;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);

        progressDialog = new ProgressDialog(this);

        backImage = findViewById(R.id.regPasswordBackImage);
        backImage.setOnClickListener(this::backImageOnClick);

        newPasswordInput = findViewById(R.id.regPasswordInput);
        confirmPasswordInput = findViewById(R.id.regConfirmPasswordInput);

        saveBtn = findViewById(R.id.regPasswordSaveBtn);
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
        String firstName = getIntent().getStringExtra(FIRST_NAME_EXTRA);
        String lastName = getIntent().getStringExtra(LAST_NAME_EXTRA);
        String email = getIntent().getStringExtra(EMAIL_EXTRA);
        JSONObject newUser = new JSONObject();
        newUser.put("name", firstName + " " + lastName);
        newUser.put("mail", email);
        newUser.put("password", newPassword);
        newUser.put("role", "USER");
        StringEntity entity = new StringEntity(newUser.toString(), "UTF-8");
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), REGISTER_URL, entity, "application/json",  new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                try {
                    sendActivationCode(email);
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    showToast(RegisterPasswordActivity.this, "Възникна неочаквана грешка");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 401) {
                    showToast(RegisterPasswordActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(RegisterPasswordActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(RegisterPasswordActivity.this, "Сървърна грешка");
                }else {
                    showToast(RegisterPasswordActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });
    }

    private void sendActivationCode(String email) throws JSONException, UnsupportedEncodingException {
        progressDialog.show();

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("mail", email);
        StringEntity entity = new StringEntity(jsonParams.toString());

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(RegisterPasswordActivity.this, SEND_VERIFICATION_CODE_URL, entity, "application/json",  new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                Intent intent = new Intent(RegisterPasswordActivity.this, VerificationActivity.class);
                intent.putExtra(EMAIL_EXTRA, email);
                intent.putExtra(PARENT_EXTRA, "RegisterPasswordActivity");
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 401) {
                    showToast(RegisterPasswordActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(RegisterPasswordActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(RegisterPasswordActivity.this, "Сървърна грешка");
                }else {
                    showToast(RegisterPasswordActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });
    }

    private void backImageOnClick(View view) {
        finish();
    }
}