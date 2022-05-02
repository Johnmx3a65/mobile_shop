package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.showToast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parovsky.shop.adapter.CategoryAdapter;
import com.parovsky.shop.adapter.LocationAdapter;
import com.parovsky.shop.model.Category;
import com.parovsky.shop.model.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomePageActivity extends AppCompatActivity {

    private RecyclerView catalogRecycler;

    private RecyclerView locationRecycler;

    private CategoryAdapter categoryAdapter;

    private LocationAdapter locationAdapter;

    private ProgressDialog progressDialog;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        gson = new Gson();

        progressDialog = new ProgressDialog(this);
        List<Category> categories = new LinkedList<>();
        List<Location> locations = new LinkedList<>();
        invokeWS(categories, locations);
    }

    private void invokeWS(List<Category> categories, List<Location> locations) {
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();

        client.get("https://traver.cfapps.eu10.hana.ondemand.com/categories", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                List<Category> fetchedCategories = gson.fromJson(new String(responseBody), new TypeToken<List<Category>>() {}.getType());
                categories.addAll(fetchedCategories);
                setCategoryRecycler(categories);
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

        client.get("https://traver.cfapps.eu10.hana.ondemand.com/locations", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                List<Location> fetchedLocations = gson.fromJson(new String(responseBody), new TypeToken<List<Location>>() {}.getType());
                locations.addAll(fetchedLocations);
                setLocationRecycler(locations);
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