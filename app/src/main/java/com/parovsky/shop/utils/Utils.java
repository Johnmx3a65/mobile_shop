package com.parovsky.shop.utils;

import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static Pattern pattern;
    private static Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Проверяване на Email чрез Регулярен израз
     * @param email
     * @return връща true за валиден Email и false за невалиден
    Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * проверява за Null String object
     *
     * @param txt
     * @return true връща когато не е нула и false за null String
    object
     */
    public static boolean isNotNull(String txt){
        return txt != null && txt.trim().length() > 0;
    }

    public static void showToast(FragmentActivity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}
