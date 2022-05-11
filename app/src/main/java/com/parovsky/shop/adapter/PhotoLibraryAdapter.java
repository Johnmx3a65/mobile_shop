package com.parovsky.shop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.parovsky.shop.R;

import java.util.List;

public class PhotoLibraryAdapter extends RecyclerView.Adapter<PhotoLibraryAdapter.PhotoLibraryViewHolder> {

    private final Context context;

    private List<String> photoList;

    public PhotoLibraryAdapter(Context context, List<String> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    public List<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoLibraryAdapter.PhotoLibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View photoItems = LayoutInflater.from(context).inflate(R.layout.photo_gallery_card, parent, false);
        return new PhotoLibraryViewHolder(photoItems);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoLibraryAdapter.PhotoLibraryViewHolder holder, int position) {
        if (photoList.get(position) != null) {
            String photo = photoList.get(position);
            String cleanImage = photo.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,","");
            byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            holder.cardBackground.setBackground(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static final class PhotoLibraryViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout cardBackground;

        public PhotoLibraryViewHolder(@NonNull View itemView) {
            super(itemView);

            cardBackground = itemView.findViewById(R.id.photoGalleryCardBackground);
        }
    }
}
