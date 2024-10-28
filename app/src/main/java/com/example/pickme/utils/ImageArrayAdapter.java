package com.example.pickme.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.pickme.R;

import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter<String> {
    private final Context context;

    private final ArrayList<String> imageUrls;

    public ImageArrayAdapter(Context context, ArrayList<String> imageUrls) {
        super(context, R.layout.image_gallery_item, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.image_gallery_item, parent, false);
        } else {
            view = convertView;
        }

        ImageView img = view.findViewById(R.id.gallery_imageview);
        Glide.with(context)
                .load(imageUrls.get(position))
                .into(img);

        return view;
    }

}
