package com.parovsky.shop.adapter;

import android.content.Context;
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

import com.parovsky.shop.R;
import com.parovsky.shop.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private Context context;

    private List<Location> locations;

    public LocationAdapter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View locationItems = LayoutInflater.from(context).inflate(R.layout.fav_category_design, parent, false);
        return new LocationViewHolder(locationItems);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.locationName.setText(locations.get(position).getName());
        holder.locationSubtitle.setText(locations.get(position).getSubtitle());

        String base64Image = locations.get(position).getPicture();
        String cleanImage = base64Image.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,","");
        byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        holder.cardBackground.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static final class LocationViewHolder extends RecyclerView.ViewHolder {

        private TextView locationName;

        private TextView locationSubtitle;

        private ConstraintLayout cardBackground;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            locationName = itemView.findViewById(R.id.locationName);
            locationSubtitle = itemView.findViewById(R.id.locationSubtitle);
            cardBackground = itemView.findViewById(R.id.cardBackground);
        }
    }

}
