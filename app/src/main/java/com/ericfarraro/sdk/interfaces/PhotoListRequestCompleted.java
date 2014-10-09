package com.ericfarraro.sdk.interfaces;

import com.ericfarraro.sdk.models.Photo;

import java.util.ArrayList;

/**
 * Created by Eric on 10/7/2014.
 */
public interface PhotoListRequestCompleted {
    public void onPhotoListRequestCompleted(ArrayList<Photo> photos);
}
