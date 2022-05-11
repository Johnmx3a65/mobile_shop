package com.parovsky.shop;

import static com.parovsky.shop.SaveSharedPreference.getUserEmail;
import static com.parovsky.shop.SaveSharedPreference.getUserPassword;
import static com.parovsky.shop.utils.Utils.CURRENT_USER_EXTRA;
import static com.parovsky.shop.utils.Utils.SEARCH_EXTRA;
import static com.parovsky.shop.utils.Utils.showToast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

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

    private SearchView searchView;

    private ImageView noContentImage;

    @Override
    protected void onResume() {
        super.onResume();
        updateLocations();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        gson = new Gson();

        progressDialog = new ProgressDialog(this);

        Intent currentIntent = getIntent();
        Bundle currentBundle = currentIntent.getExtras();
        String currentUserString = currentBundle.getString(CURRENT_USER_EXTRA);
        User currentUser = gson.fromJson(currentUserString, new TypeToken<User>() {}.getType());

        noContentImage = findViewById(R.id.noContentImage);

        greetingText = findViewById(R.id.homePageGreetingTextView);
        greetingText.setText("Здравейте, " + currentUser.getName() + "!");

        searchView = findViewById(R.id.homeSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent searchIntent = new Intent(HomePageActivity.this, SearchActivity.class);
                searchIntent.putExtra(SEARCH_EXTRA, s);
                startActivity(searchIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        setLocationRecycler(currentUser.getFavoriteLocations());

        if (currentUser.getFavoriteLocations().isEmpty()) {
            noContentImage.setVisibility(View.VISIBLE);
            locationRecycler.setVisibility(View.GONE);
        }

        invokeWS();
    }

    private void updateLocations() {
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(getUserEmail(this), getUserPassword(this));
        client.get("https://traver.cfapps.eu10.hana.ondemand.com/locations/favorite", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                List<Location> locations = new Gson().fromJson(new String(responseBody), new TypeToken<List<Location>>() {}.getType());
                setLocationRecycler(locations);

                if (locations == null || locations.isEmpty()) {
                    noContentImage.setVisibility(View.VISIBLE);
                    locationRecycler.setVisibility(View.GONE);
                }else {
                    noContentImage.setVisibility(View.GONE);
                    locationRecycler.setVisibility(View.VISIBLE);
                }
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