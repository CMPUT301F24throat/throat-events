package com.example.pickme.utils;

import com.example.pickme.models.Image;
/**
 * Callback interface specifically for accessing the entire collection.
 * @author etdong
 * @version 1.0
 * @Overload <b>onQuerySuccess(List<DocumentSnapshot> docs)</b> to access the list of docs
 * @Overload <b>onQueryEmpty()</b> to handle empty queries
 */
public interface ImageQuery {
    void onSuccess(Image image);
    void onEmpty();
}
