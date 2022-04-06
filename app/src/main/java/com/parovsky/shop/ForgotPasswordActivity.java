package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.isNotNull;
import static com.parovsky.shop.utils.Utils.showToast;
import static com.parovsky.shop.utils.Utils.validate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.parovsky.shop.utils.Utils;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageView backImage;
    private TextInputEditText emailInput;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

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

            }else {
                showToast(this, "Моля вкарайте валиден имейл");
            }
        }else {
            showToast(this, "Моля вкарайте имейл");
        }
    }
}