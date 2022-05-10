package com.parovsky.shop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parovsky.shop.R;
import com.parovsky.shop.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;

    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.category_design, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.categoryName.setText(categories.get(position).getName());

        String base64Image = categories.get(position).getPicture();
        String cleanImage = base64Image.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,","");
        byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.categoryPicture.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryName;

        private ImageView categoryPicture;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.categoryName);
            categoryPicture = itemView.findViewById(R.id.categoryPicture);
        }
    }

}