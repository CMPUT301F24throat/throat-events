package com.example.pickme.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Image;

import java.util.ArrayList;

/**
 * Custom adapter for images in GridViews using Glide
 * @author etdong
 * @version 1.0
 */
public class GalleryAdapter extends ArrayAdapter<Image> {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<Image> images;

    public GalleryAdapter(Context context, ArrayList<Image> images) {
        super(context, R.layout.image_gallery_item, images);

        this.context = context;
        this.images = images;
        inflater = LayoutInflater.from(context);
    }

    @Nullable
    @Override
    public Image getItem(int position) {
        return images.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.image_gallery_item, parent, false);
        }

        ImageView iv = convertView.findViewById(R.id.gallery_imageview);
        Image i = images.get(position);
        Glide
                .with(context)
                .load(i.getImageUrl())
                .error(R.drawable.ic_disabled)
                .into(iv);

        return convertView;
    }

}
