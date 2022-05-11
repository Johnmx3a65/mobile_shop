package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.EMAIL_EXTRA;
import static com.parovsky.shop.utils.Utils.PARENT_EXTRA;
import static com.parovsky.shop.utils.Utils.VERIFY_CODE_EXTRA;
import static com.parovsky.shop.utils.Utils.showToast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

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

    // 4 minutes
    private static final long COUNTDOWN_TIME = 4 * 60 * 1000;
    private TextView countDownText;
    private long timeLeft = COUNTDOWN_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        prgDialog = new ProgressDialog(this);

        countDownText = findViewById(R.id.verificationTimerText);

        pinView = findViewById(R.id.verificationCodePinView);

        backBtn = findViewById(R.id.verificationBackBtn);
        backBtn.setOnClickListener(this::backBtnOnClick);

        saveBtn = findViewById(R.id.verificationSaveBtn);
        saveBtn.setOnClickListener(this::saveBtnOnClick);

        timerStart();
    }

    private void timerStart() {
        CountDownTimer countDownTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long l) {
                timeLeft = l;
                updateTimer();
            }

            @Override
            public void onFinish() {
                try {
                    sendVerificationCode();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(VerificationActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
                timeLeft = COUNTDOWN_TIME;
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        String timeLeftText = String.format("%02d:%02d", minutes, seconds);
        countDownText.setText(timeLeftText);
    }

    private void sendVerificationCode() throws UnsupportedEncodingException, JSONException {
        String email = getIntent().getStringExtra(EMAIL_EXTRA);
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("mail", email);
        StringEntity entity = new StringEntity(jsonParams.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(VerificationActivity.this, "https://traver.cfapps.eu10.hana.ondemand.com/send-verification-code", entity, "application/json",  new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                showToast(VerificationActivity.this, "Изпратено е ново писмо за потвърждение");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401) {
                    showToast(VerificationActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(VerificationActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(VerificationActivity.this, "Сървърна грешка");
                }else {
                    showToast(VerificationActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });
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