package com.example.pickme.views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.pickme.R;
import com.example.pickme.models.Image;
import com.example.pickme.repositories.ImageRepository;

/**
 * Admin image catalog class that allows admins to browse, view, and delete images
 * that have been uploaded to the app.
 */
public class AdminGalleryFragment extends Fragment {

    private final ImageRepository ir = ImageRepository.getInstance();
    private GridView gallery;

    /**
     * Called when the fragment is navigated to and creates the view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_gallery, container, false);
    }

    /**
     * Called after the view is successfully created. Sets the onclick listeners and initial
     * request for all images.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gallery = view.findViewById(R.id.imageCatalogGridView);
        ir.getAllImages(view.getContext(), gallery);

        // gallery item click handler
        gallery.setOnItemClickListener((adapterView, view1, i, l) -> {
            createDeleteDialog(adapterView, view1, i);
        });

        // back button
        ImageView backButton = view.findViewById(R.id.imageCatalogBack);
        backButton.setOnClickListener(v -> Navigation
                .findNavController(view)
                .navigate(R.id.action_adminGalleryFragment_to_adminToolsFragment));

        // refresh button
        ImageView refreshButton = view.findViewById(R.id.imageCatalogRefresh);
        refreshButton.setOnClickListener(v ->  {
            gallery.setAdapter(null);
            ir.getAllImages(view.getContext(), gallery);
        });

    }

    /**
     * Creates a custom dialog showing a larger version of the image clicked, information about
     * the image, and a deletion prompt.
     * @param adapterView The adapterView from the gallery onclick listener
     * @param view The view from the gallery onclick listener
     * @param i The index/position of the item clicked from the gallery onclick listener
     */
    public void createDeleteDialog(AdapterView<?> adapterView, View view, int i) {
        // inflating the custom dialog layout and getting the clicked image
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View dialogView = inflater.inflate(R.layout.dialog_gallery_view, (ViewGroup)view, false);
        Image img = (Image) adapterView.getItemAtPosition(i);

        // building the dialog window to be shown
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setView(dialogView);
        final AlertDialog deleteDialog = builder.create();

        setDialogInfo(dialogView, img);

        // setting the image
        ImageView iv = dialogView.findViewById(R.id.dialogGalleryImageView);
        Glide
                .with(view.getContext())
                .load(img.getImageUrl())
                .error(R.drawable.ic_disabled)
                .into(iv);

        // new instance of image so we can delete it -- casted instance cannot call delete()
        Image temp = new Image(img.getUploaderId(), img.getImageAssociation());

        Button dialog_delete = dialogView.findViewById(R.id.dialogGalleryDeleteButton);
        dialog_delete.setOnClickListener(v -> temp.delete(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(
                        this.getContext(),
                        "Image deleted",
                        Toast.LENGTH_SHORT).show();
                ir.getAllImages(view.getContext(), gallery);
            } else {
                Toast.makeText(
                        this.getContext(),
                        "Error deleting image",
                        Toast.LENGTH_SHORT).show();
            }
            deleteDialog.dismiss();
        }));

        Button dialog_cancel = dialogView.findViewById(R.id.dialogGalleryCancelButton);
        dialog_cancel.setOnClickListener(v -> deleteDialog.dismiss());

        deleteDialog.show();
    }

    /**
     * Helper function to add all relevant image information to the dialog
     * @param v The dialog view
     * @param i The image object with the relevant information
     */
    private void setDialogInfo(View v, Image i) {
        TextView tv_uploaderId = v.findViewById(R.id.dialogGalleryInfo_uploaderId);
        TextView tv_imageAssociation = v.findViewById(R.id.dialogGalleryInfo_imageAssociation);
        TextView tv_imageType = v.findViewById(R.id.dialogGalleryInfo_imageType);
        TextView tv_updatedAt = v.findViewById(R.id.dialogGalleryInfo_updatedAt);
        TextView tv_createdAt = v.findViewById(R.id.dialogGalleryInfo_createdAt);

        tv_uploaderId.append(i.getUploaderId());
        tv_imageAssociation.append(i.getImageAssociation());
        tv_imageType.append(i.getImageType().toString());
        tv_updatedAt.append(i.getUpdatedAt().toDate().toString());
        tv_createdAt.append(i.getCreatedAt().toDate().toString());
    }
}