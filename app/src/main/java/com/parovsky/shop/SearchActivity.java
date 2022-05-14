package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.SEARCH_EXTRA;
import static com.parovsky.shop.utils.Utils.showToast;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parovsky.shop.adapter.LocationAdapter;
import com.parovsky.shop.model.Location;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private TextView searchTextView;

    private AsyncHttpClient client;

    private ProgressDialog progressDialog;

    private RecyclerView locationRecycler;

    private LocationAdapter locationAdapter;

    private ImageView backImage;

    private SearchView searchView;

    public static final String SEARCH_TEXT_VIEW_VALUE = "Намерихме {0} резултата за: {1}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        progressDialog = new ProgressDialog(this);

        client = new AsyncHttpClient();
        client.setBasicAuth(SaveSharedPreference.getUserEmail(getApplicationContext()), SaveSharedPreference.getUserPassword(getApplicationContext()));

        backImage = findViewById(R.id.searchBackImage);
        backImage.setOnClickListener(v -> finish());

        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String s) {
                List<Location> locations = locationAdapter.getAllLocations();
                List<Location> filteredLocations = locations.stream().filter(location -> location.getName().toLowerCase().contains(s.toLowerCase())).collect(Collectors.toList());
                locationAdapter.setLocations(filteredLocations);
                locationAdapter.notifyDataSetChanged();
                searchTextView.setText(MessageFormat.format(SEARCH_TEXT_VIEW_VALUE, filteredLocations.size(), s));
                return false;
            }
        });

        searchTextView = findViewById(R.id.searchTextView);
        String search = getIntent().getStringExtra(SEARCH_EXTRA);

        invokeWS(search);
    }

    private void invokeWS(String search) {
        progressDialog.show();

        client.get("https://traver.cfapps.eu10.hana.ondemand.com/locations", new AsyncHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                List<Location> locations = new Gson().fromJson(new String(responseBody), new TypeToken<List<Location>>() {}.getType());
                List<Location> filteredLocations = locations.stream().filter(location -> location.getName().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
                searchTextView.setText(MessageFormat.format(SEARCH_TEXT_VIEW_VALUE, filteredLocations.size(), search));
                setLocationRecycler(filteredLocations);
                locationAdapter.setAllLocations(locations);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                if (statusCode == 401) {
                    showToast(SearchActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(SearchActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(SearchActivity.this, "Сървърна грешка");
                }else {
                    showToast(SearchActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });
    }

    private void setLocationRecycler(List<Location> locations) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        locationRecycler = findViewById(R.id.searchLocationsRecycler);
        locationRecycler.setLayoutManager(layoutManager);

        locationAdapter = new LocationAdapter(this, locations);
        locationRecycler.setAdapter(locationAdapter);
    }
}