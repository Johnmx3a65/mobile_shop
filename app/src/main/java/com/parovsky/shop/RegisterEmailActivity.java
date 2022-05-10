package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.EMAIL_EXTRA;
import static com.parovsky.shop.utils.Utils.FIRST_NAME_EXTRA;
import static com.parovsky.shop.utils.Utils.LAST_NAME_EXTRA;
import static com.parovsky.shop.utils.Utils.isNotNull;
import static com.parovsky.shop.utils.Utils.showToast;
import static com.parovsky.shop.utils.Utils.validateEmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterEmailActivity extends AppCompatActivity {

    private ImageView backImage;
    private Button saveButton;
    private TextInputEditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        emailInput = findViewById(R.id.regEmailInput);

        backImage = findViewById(R.id.regEmailBackImage);
        backImage.setOnClickListener(this::backImageOnClick);

        saveButton = findViewById(R.id.regEmailSaveBtn);
        saveButton.setOnClickListener(this::saveButtonOnClick);
    }

    private void saveButtonOnClick(View view) {
        String email = emailInput.getText().toString();
        if(isNotNull(email)) {
            if (validateEmail(email)) {
                sendEmailToNextView(email);
            }else {
                showToast(this, "Моля вкарайте валиден имейл");
            }
        }else {
            showToast(this, "Моля вкарайте имейл");
        }
    }

    private void sendEmailToNextView(String email) {
        Intent intent = new Intent(this, RegisterPasswordActivity.class);
        intent.putExtra(EMAIL_EXTRA, email);
        intent.putExtra(FIRST_NAME_EXTRA, getIntent().getStringExtra(FIRST_NAME_EXTRA));
        intent.putExtra(LAST_NAME_EXTRA, getIntent().getStringExtra(LAST_NAME_EXTRA));
        startActivity(intent);
    }

    private void backImageOnClick(View view) {
        finish();
    }
}