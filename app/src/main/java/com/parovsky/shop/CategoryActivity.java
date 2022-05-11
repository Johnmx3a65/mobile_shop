package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.CATEGORY_ID_EXTRA;
import static com.parovsky.shop.utils.Utils.showToast;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parovsky.shop.adapter.CategoryAdapter;
import com.parovsky.shop.adapter.LocationAdapter;
import com.parovsky.shop.model.Category;
import com.parovsky.shop.model.Location;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CategoryActivity extends AppCompatActivity {

    private ImageView backImage;

    private RecyclerView catalogRecycler;

    private CategoryAdapter categoryAdapter;

    private RecyclerView locationRecycler;

    private LocationAdapter locationAdapter;

    private AsyncHttpClient client;

    private ProgressDialog progressDialog;

    private Gson gson;

    public LocationAdapter getLocationAdapter() {
        return locationAdapter;
    }

    public void setLocationAdapter(LocationAdapter locationAdapter) {
        this.locationAdapter = locationAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        progressDialog = new ProgressDialog(this);

        gson = new Gson();

        backImage = findViewById(R.id.categoriesBackImage);

        backImage.setOnClickListener(v -> {
            setResult(1);
            finish();
        });

        setCategoryRecycler(new LinkedList<>());
        setLocationRecycler(new LinkedList<>());

        client = new AsyncHttpClient();
        client.setBasicAuth(SaveSharedPreference.getUserEmail(this), SaveSharedPreference.getUserPassword(this));

        Long categoryId = getIntent().getLongExtra(CATEGORY_ID_EXTRA, 0);

        invokeWS(categoryId);

    }

    private void invokeWS(Long categoryId) {
        progressDialog.show();

        client.get("https://traver.cfapps.eu10.hana.ondemand.com/categories", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Category> fetchedCategories = gson.fromJson(new String(responseBody), new TypeToken<List<Category>>() {}.getType());
                categoryAdapter.setCategories(fetchedCategories);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 401) {
                    showToast(CategoryActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(CategoryActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(CategoryActivity.this, "Сървърна грешка");
                }else {
                    showToast(CategoryActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });

        client.get("https://traver.cfapps.eu10.hana.ondemand.com/category/" + categoryId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                Category fetchedCategory = gson.fromJson(new String(responseBody), new TypeToken<Category>() {}.getType());
                locationAdapter.setLocations(fetchedCategory.getLocations());
                locationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 401) {
                    showToast(CategoryActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(CategoryActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(CategoryActivity.this, "Сървърна грешка");
                }else {
                    showToast(CategoryActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });
    }

    private void setCategoryRecycler(List<Category> categories) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        catalogRecycler = findViewById(R.id.categoryListRecycler);
        catalogRecycler.setLayoutManager(layoutManager);

        categoryAdapter = new CategoryAdapter(this, categories);
        catalogRecycler.setAdapter(categoryAdapter);
    }

    private void setLocationRecycler(List<Location> locations) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        locationRecycler = findViewById(R.id.categoryListLocationsRecycler);
        locationRecycler.setLayoutManager(layoutManager);

        locationAdapter = new LocationAdapter(this, locations);
        locationRecycler.setAdapter(locationAdapter);
    }
}