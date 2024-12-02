package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.pickme.R;
import com.example.pickme.models.Facility;
import com.example.pickme.repositories.FacilityRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * FacilityEditDialog is a DialogFragment that allows users to edit the details of a facility.
 * It displays a dialog with input fields for the facility name and location, and buttons to save or cancel the changes.
 */
public class FacilityEditDialog extends DialogFragment {

    private Facility facility;
    private EditText editFacilityName, editLocation;

    /**
     * Constructor for FacilityEditDialog.
     *
     * @param facility The facility to be edited.
     */
    public FacilityEditDialog(Facility facility) {
        this.facility = facility;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facility_edit_dialog, container, false);

        editFacilityName = view.findViewById(R.id.facilityEdit_facilityName);
        editLocation = view.findViewById(R.id.facilityEdit_facilityLocation);
        Button btnCancel = view.findViewById(R.id.facilityEdit_btnCancel);
        Button btnSave = view.findViewById(R.id.facilityEdit_btnSave);

        editFacilityName.setText(facility.getFacilityName());
        editLocation.setText(facility.getLocation());

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String facilityName = editFacilityName.getText().toString();
            String facilityLocation = editLocation.getText().toString();

            FacilityRepository.getInstance().updateFacility(facility.getFacilityId(), facilityName, facilityLocation, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Facility successfully updated", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to update facility", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        return view;
    }

    /**
     * Static method to show the FacilityEditDialog.
     *
     * @param fragmentManager The FragmentManager to use for showing the dialog.
     * @param facility The facility to be edited.
     */
    public static void showDialog(FragmentManager fragmentManager, Facility facility) {
        FacilityEditDialog dialog = new FacilityEditDialog(facility);
        dialog.show(fragmentManager, "FacilityEditDialog");
    }
}

/*
  Coding Sources
  <p>
  StackOverflow:
  - https://stackoverflow.com/questions/7977392/android-dialogfragment-vs-dialog
  - https://stackoverflow.com/questions/13765127/dialogfragment-advantages-over-alertdialog?noredirect=1&lq=1
 */