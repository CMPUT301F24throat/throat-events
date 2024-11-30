package com.example.pickme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pickme.R;
import com.example.pickme.models.Event;
import com.example.pickme.models.User;
import com.example.pickme.utils.WaitingListUtils;

public class EventWaitingListFragment extends Fragment {
    private Event event;
    private User currentUser;
    private WaitingListUtils waitingListUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_waitinglist, container, false);
    }

}
