package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.EMAIL_EXTRA;
import static com.parovsky.shop.utils.Utils.isNotNull;
import static com.parovsky.shop.utils.Utils.showToast;
import static com.parovsky.shop.utils.Utils.validate;

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
import com.parovsky.shop.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageView backImage;
    private TextInputEditText emailInput;
    private Button submitBtn;
    private ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        prgDialog = new ProgressDialog(this);

        backImage = findViewById(R.id.forgotPasswordBackImage);
        backImage.setOnClickListener(this::backImageOnClick);

        emailInput = findViewById(R.id.forgotPasswordEmailInput);

        submitBtn = findViewById(R.id.forgotPasswordSubmitBtn);
        submitBtn.setOnClickListener(this::submitBtnOnClick);
    }

    private void backImageOnClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void submitBtnOnClick(View view) {
        String email = emailInput.getText().toString();
        if(isNotNull(email)) {
            if (validate(email)) {
                try {
                    invokeWS(email);
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else {
                showToast(this, "Моля вкарайте валиден имейл");
            }
        }else {
            showToast(this, "Моля вкарайте имейл");
        }
    }

    private void invokeWS(String email) throws JSONException, UnsupportedEncodingException {
        prgDialog.show();

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("mail", email);
        StringEntity entity = new StringEntity(jsonParams.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ForgotPasswordActivity.this, "https://traver.cfapps.eu10.hana.ondemand.com/send-verification-code", entity, "application/json",  new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                Intent intent = new Intent(ForgotPasswordActivity.this, VerificationActivity.class);
                intent.putExtra(EMAIL_EXTRA, email);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.hide();
                if (statusCode == 401) {
                    showToast(ForgotPasswordActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(ForgotPasswordActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(ForgotPasswordActivity.this, "Сървърна грешка");
                }else {
                    showToast(ForgotPasswordActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });

    }
}