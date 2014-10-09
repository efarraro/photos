package com.ericfarraro.sdk;

import android.os.AsyncTask;

import com.ericfarraro.sdk.interfaces.PhotoListRequestCompleted;
import com.ericfarraro.sdk.models.Photo;

import java.util.ArrayList;

/**
 * Created by Eric on 10/7/2014.
 */
public class FetchPhotoListTask extends AsyncTask<Void, Void, ArrayList<Photo>> {


    //TODO make this class abstract???
    protected PhotoListRequestCompleted mListener;

    @Override
    protected ArrayList<Photo> doInBackground(Void... params) {

        final ArrayList<Photo> photos = new ArrayList<Photo>();
        for(int i = 0; i < 100; i++) {
            photos.add(new Photo());
        }

        return photos;

    }

    @Override
    protected void onPostExecute(ArrayList<Photo> photos) {
        super.onPostExecute(photos);

        if(mListener != null)
            mListener.onPhotoListRequestCompleted(photos);
    }

    public PhotoListRequestCompleted getListener() {
        return mListener;
    }

    public void setListener(PhotoListRequestCompleted listener) {
        mListener = listener;
    }
}
