package com.parovsky.shop;

import static com.parovsky.shop.utils.Utils.LOCATION_JSON_EXTRA;
import static com.parovsky.shop.utils.Utils.showToast;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parovsky.shop.adapter.PhotoLibraryAdapter;
import com.parovsky.shop.model.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PlaceDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView photoGalleryRecyclerView;

    private PhotoLibraryAdapter photoLibraryAdapter;

    private TextView locationName;

    private TextView locationNameCard;

    private TextView locationSubtitle;

    private TextView textDescription;

    private ConstraintLayout cardView;

    private ImageView backImage;

    private ImageView favoriteImage;

    private LatLng locationCoordinates = new LatLng( -8.719266, 115.168640);

    private String locationTitle;

    private ProgressDialog progressDialog;

    private boolean isFavorite;

    private final AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        client.setBasicAuth(SaveSharedPreference.getUserEmail(this), SaveSharedPreference.getUserPassword(this));

        String locationJSON = getIntent().getStringExtra(LOCATION_JSON_EXTRA);
        Location location = new Gson().fromJson(locationJSON, new TypeToken<Location>() {}.getType());

        progressDialog = new ProgressDialog(this);

        locationName = findViewById(R.id.locationNamePanel);
        locationNameCard = findViewById(R.id.locationNameCard);
        locationSubtitle = findViewById(R.id.allCategoriesLocationSubtitle);
        textDescription = findViewById(R.id.textDescription);
        cardView = findViewById(R.id.cardBackground);
        backImage = findViewById(R.id.locationDescriptionBackImage);
        favoriteImage = findViewById(R.id.locationDescriptionFavouriteImage);

        isFavorite = false;
        setFavoriteState();

        favoriteImage.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            favoriteImage.setImageResource(isFavorite ? R.drawable.ic_heart : R.drawable.ic_unliked);

            changeFavoriteStatus(isFavorite, location.getId());
        });

        backImage.setOnClickListener(v -> {
            setResult(1);
            finish();
        });

        locationName.setText(location.getName());
        locationTitle = location.getName();

        locationNameCard.setText(location.getName());

        locationSubtitle.setText(location.getSubtitle());

        textDescription.setText(location.getDescription());

        String base64Image = location.getPicture();
        String cleanImage = base64Image.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,","");
        byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        cardView.setBackground(drawable);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(PlaceDetailActivity.this);

        setPhotosRecycler(new LinkedList<>());

        inwokeWS(location.getId());
    }

    private void changeFavoriteStatus(boolean isFavorite, Long locationId) {
        progressDialog.show();
        if (isFavorite) {
            client.post("https://traver.cfapps.eu10.hana.ondemand.com/location/favorite/" + locationId, null,  new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    progressDialog.hide();
                    showToast(PlaceDetailActivity.this, "Локацията е добавена в любими");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressDialog.hide();
                    if (statusCode == 401) {
                        showToast(PlaceDetailActivity.this, "Невалидни потребителски данни");
                    }else if (statusCode == 404) {
                        showToast(PlaceDetailActivity.this, "Страницата не е намерена");
                    }else if (statusCode == 500) {
                        showToast(PlaceDetailActivity.this, "Сървърна грешка");
                    }else {
                        showToast(PlaceDetailActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                    }
                }
            });
        } else {
            client.delete("https://traver.cfapps.eu10.hana.ondemand.com/location/favorite/" + locationId, null,  new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    progressDialog.hide();
                    showToast(PlaceDetailActivity.this, "Локацията е премахната от любими");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressDialog.hide();
                    if (statusCode == 401) {
                        showToast(PlaceDetailActivity.this, "Невалидни потребителски данни");
                    }else if (statusCode == 404) {
                        showToast(PlaceDetailActivity.this, "Страницата не е намерена");
                    }else if (statusCode == 500) {
                        showToast(PlaceDetailActivity.this, "Сървърна грешка");
                    }else {
                        showToast(PlaceDetailActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                    }
                }
            });
        }
    }

    private void inwokeWS(Long id) {
        progressDialog.show();

        client.get("https://traver.cfapps.eu10.hana.ondemand.com/location/" + id + "/photos", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                List<String> photos = new Gson().fromJson(new String(responseBody), new TypeToken<List<String>>() {}.getType());
                photoLibraryAdapter.setPhotoList(photos);
                photoLibraryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                photoLibraryAdapter.setPhotoList(new ArrayList<>());
                photoLibraryAdapter.notifyDataSetChanged();
                if (statusCode == 401) {
                    showToast(PlaceDetailActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    showToast(PlaceDetailActivity.this, "Страницата не е намерена");
                }else if (statusCode == 500) {
                    showToast(PlaceDetailActivity.this, "Сървърна грешка");
                }else {
                    showToast(PlaceDetailActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });

        client.get("https://traver.cfapps.eu10.hana.ondemand.com/locations/favorite/" + id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                isFavorite = true;
                setFavoriteState();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401) {
                    showToast(PlaceDetailActivity.this, "Невалидни потребителски данни");
                }else if (statusCode == 404) {
                    isFavorite = false;
                    setFavoriteState();
                }else if (statusCode == 500) {
                    showToast(PlaceDetailActivity.this, "Сървърна грешка");
                }else {
                    showToast(PlaceDetailActivity.this, "Неочаквана грешка! Проверете дали сте вързани към мрежата интернет");
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(locationCoordinates).title(locationTitle));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationCoordinates));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    private void setPhotosRecycler(List<String> photoList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        photoGalleryRecyclerView = findViewById(R.id.photosRecycler);
        photoGalleryRecyclerView.setLayoutManager(layoutManager);

        photoLibraryAdapter = new PhotoLibraryAdapter(this, photoList);
        photoGalleryRecyclerView.setAdapter(photoLibraryAdapter);
    }

    private void setFavoriteState(){
        favoriteImage.setImageResource(isFavorite ? R.drawable.ic_heart : R.drawable.ic_unliked);
    }
}