package com.parovsky.shop.adapter;

import static com.parovsky.shop.utils.Utils.LOCATION_JSON_EXTRA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.parovsky.shop.HomePageActivity;
import com.parovsky.shop.PlaceDetailActivity;
import com.parovsky.shop.R;
import com.parovsky.shop.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private final Context context;

    private List<Location> allLocations;

    private List<Location> locations;

    public LocationAdapter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Location> getAllLocations() {
        return allLocations;
    }

    public void setAllLocations(List<Location> allLocations) {
        this.allLocations = allLocations;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View locationItems;
        if (context instanceof HomePageActivity) {
            locationItems = LayoutInflater.from(context).inflate(R.layout.fav_category_design, parent, false);
        }else {
            locationItems = LayoutInflater.from(context).inflate(R.layout.all_category_design, parent, false);
        }
        return new LocationViewHolder(locationItems);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.locationName.setText(location.getName());
        holder.locationSubtitle.setText(location.getSubtitle());

        String base64Image = location.getPicture();
        String cleanImage = base64Image.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,","");
        byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);

        holder.cardBackground.setBackground(drawable);

        if (!(context instanceof HomePageActivity)) {
            holder.locationDescription.setText(location.getDescription());
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PlaceDetailActivity.class);
            String jsonLocation = new Gson().toJson(location);
            intent.putExtra(LOCATION_JSON_EXTRA, jsonLocation);
            ((Activity) context).startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static final class LocationViewHolder extends RecyclerView.ViewHolder {

        private final TextView locationName;

        private final TextView locationSubtitle;

        private TextView locationDescription;

        private ConstraintLayout cardBackground;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            if (itemView.getContext() instanceof HomePageActivity) {
                locationName = itemView.findViewById(R.id.locationNameCard);
                locationSubtitle = itemView.findViewById(R.id.locationSubtitle);
                cardBackground = itemView.findViewById(R.id.cardBackground);
            }else {
                locationName = itemView.findViewById(R.id.allCategoryLocationName);
                locationSubtitle = itemView.findViewById(R.id.allCategoriesLocationSubtitle);
                locationDescription = itemView.findViewById(R.id.allCategoriesLocationDescription);
                cardBackground = itemView.findViewById(R.id.allCategoriesLocationImage);
            }
        }
    }

}
