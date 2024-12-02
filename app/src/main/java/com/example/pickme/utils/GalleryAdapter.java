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
 * Custom adapter for images in GridViews using Glide.
 * This adapter is used to display images in a grid view by loading them from URLs using Glide.
 *
 * @version 1.0
 */
public class GalleryAdapter extends ArrayAdapter<Image> {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<Image> images;

    /**
     * Constructor for GalleryAdapter.
     *
     * @param context the context in which the adapter is running
     * @param images the list of images to be displayed
     */
    public GalleryAdapter(Context context, ArrayList<Image> images) {
        super(context, R.layout.image_gallery_item, images);

        this.context = context;
        this.images = images;
        inflater = LayoutInflater.from(context);
    }

    /**
     * Get the image item at the specified position.
     *
     * @param position the position of the item within the adapter's data set
     * @return the image at the specified position
     */
    @Nullable
    @Override
    public Image getItem(int position) {
        return images.get(position);
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent the parent that this view will eventually be attached to
     * @return a View corresponding to the data at the specified position
     */
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