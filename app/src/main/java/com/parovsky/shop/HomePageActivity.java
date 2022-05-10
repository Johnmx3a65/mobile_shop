package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.CURRENT_USER_EXTRA;
import static com.parovsky.shop.utils.Utils.showToast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parovsky.shop.adapter.CategoryAdapter;
import com.parovsky.shop.adapter.LocationAdapter;
import com.parovsky.shop.model.Category;
import com.parovsky.shop.model.Location;
import com.parovsky.shop.model.User;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomePageActivity extends AppCompatActivity {

    private RecyclerView catalogRecycler;

    private RecyclerView locationRecycler;

    private CategoryAdapter categoryAdapter;

    private LocationAdapter locationAdapter;

    private ProgressDialog progressDialog;

    private Gson gson;

    private TextView greetingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        gson = new Gson();

        progressDialog = new ProgressDialog(this);

        Intent currentIntent = getIntent();
        String userJson = currentIntent.getStringExtra(CURRENT_USER_EXTRA);
        User currentUser = gson.fromJson(userJson, new TypeToken<User>() {}.getType());

        greetingText = findViewById(R.id.homePageGreetingTextView);
        greetingText.setText("Здравейте, " + currentUser.getName() + "!");

        setLocationRecycler(currentUser.getFavoriteLocations());

        invokeWS();
    }

    private void invokeWS() {
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();

        client.get("https://traver.cfapps.eu10.hana.ondemand.com/categories", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                List<Category> fetchedCategories = gson.fromJson(new String(responseBody), new TypeToken<List<Category>>() {}.getType());
                setCategoryRecycler(fetchedCategories);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 401) {
                    showToast(HomePageActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(HomePageActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(HomePageActivity.this, "Сървърна грешка");
                }else {
                    showToast(HomePageActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });

    }

    private void setCategoryRecycler(List<Category> categories) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        catalogRecycler = findViewById(R.id.categoryRecycler);
        catalogRecycler.setLayoutManager(layoutManager);

        categoryAdapter = new CategoryAdapter(this, categories);
        catalogRecycler.setAdapter(categoryAdapter);
    }

    private void setLocationRecycler(List<Location> locations) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        locationRecycler = findViewById(R.id.locationRecycler);
        locationRecycler.setLayoutManager(layoutManager);

        locationAdapter = new LocationAdapter(this, locations);
        locationRecycler.setAdapter(locationAdapter);
    }
}