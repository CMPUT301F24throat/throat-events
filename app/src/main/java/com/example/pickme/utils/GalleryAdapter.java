package com.example.pickme.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.pickme.R;

import java.util.ArrayList;

/**
 * Custom adapter for images in GridViews using Glide
 * @author etdong
 * @version 1.0
 */
public class GalleryAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<String> imageUrls;

    public GalleryAdapter(Context context, ArrayList<String> imageUrls) {
        super(context, R.layout.image_gallery_item, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.image_gallery_item, parent, false);
        }

        ImageView i = convertView.findViewById(R.id.gallery_imageview);

        Glide
                .with(context)
                .load(imageUrls.get(position))
                .into(i);

        ImageView delete = convertView.findViewById(R.id.gallery_deleteButton);
        delete.setOnClickListener(v -> {
            Log.d("DELETE", "getView: " + v);
        });

        return convertView;
    }

}
