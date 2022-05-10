package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.EMAIL_EXTRA;
import static com.parovsky.shop.utils.Utils.PARENT_EXTRA;
import static com.parovsky.shop.utils.Utils.USER_EXTRA;
import static com.parovsky.shop.utils.Utils.VERIFY_CODE_EXTRA;
import static com.parovsky.shop.utils.Utils.showToast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chaos.view.PinView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class VerificationActivity extends AppCompatActivity {

    private Button saveBtn;

    private PinView pinView;

    private ProgressDialog prgDialog;

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        prgDialog = new ProgressDialog(this);

        pinView = findViewById(R.id.verificationCodePinView);

        backBtn = findViewById(R.id.verificationBackBtn);
        backBtn.setOnClickListener(this::backBtnOnClick);

        saveBtn = findViewById(R.id.verificationSaveBtn);
        saveBtn.setOnClickListener(this::saveBtnOnClick);
    }

    private void saveBtnOnClick(View view) {
        Intent intent = getIntent();
        String code = pinView.getText().toString();
        if (code.length() < 4) {
            showToast(VerificationActivity.this, "Попълнете полето");
        } else {
            String email = intent.getStringExtra(EMAIL_EXTRA);
            try {
                invokeWS(email);
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void invokeWS(String email) throws JSONException, UnsupportedEncodingException {
        prgDialog.show();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mail", email);
        jsonObject.put("verifyCode", pinView.getText().toString());
        StringEntity entity = new StringEntity(jsonObject.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(),"https://traver.cfapps.eu10.hana.ondemand.com/check-verification-code", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                String parent = getIntent().getStringExtra(PARENT_EXTRA);
                if (parent != null && parent.equals("RegisterPasswordActivity")) {
                    startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                }else {
                    Bundle extras = new Bundle();
                    extras.putString(EMAIL_EXTRA, email);
                    extras.putString(VERIFY_CODE_EXTRA, pinView.getText().toString());
                    Intent intent = new Intent(VerificationActivity.this, NewPasswordActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.hide();
                if (statusCode == 401) {
                    showToast(VerificationActivity.this, "Невалидни потребителски данни");
                } else if (statusCode == 409){
                    showToast(VerificationActivity.this, "Невалиден код");
                } else if (statusCode == 404) {
                    showToast(VerificationActivity.this, "Страницата не е намерена");
                } else if (statusCode == 500) {
                    showToast(VerificationActivity.this, "Сървърна грешка");
                } else {
                    showToast(VerificationActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });
    }

    private void backBtnOnClick(View view) {
        finish();
    }
}