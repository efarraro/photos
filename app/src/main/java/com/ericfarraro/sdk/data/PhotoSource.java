package com.ericfarraro.sdk.data;

import com.ericfarraro.sdk.FetchPhotoListTask;
import com.ericfarraro.sdk.interfaces.PhotoListRequestCompleted;
import com.ericfarraro.sdk.models.Photo;

import java.util.ArrayList;

/**
 * Created by Eric on 10/7/2014.
 */
public abstract class PhotoSource {

    protected PhotoListRequestCompleted mPhotoListRequestCompletedListener;
    protected int mPhotosPerPage = 50;

    /**
     * A method that returns some 'default' view for the gallery.
     * Caller should implement {@link com.ericfarraro.sdk.interfaces.PhotoListRequestCompleted} to listen for a response
     * @param page The page number to return
     * @return A list of Photos
     */
    public abstract void fetchDefaultPhotos(int page);

    /**
     * Search photos (if applicable)
     * @param query A search query string
     */
    public abstract void searchPhotos(String query, int page);

    /**
     * Gets a {@link com.ericfarraro.sdk.models.Photo} from the {@link com.ericfarraro.sdk.data.PhotoSource}, given some identifier
     * Callers should implement {@link com.ericfarraro.sdk.interfaces.PhotoListRequestCompleted} to listen for a response
     * @param identifier A unique identifier that can be used to retrieve the photo
     */
    public abstract void getLargePhoto(String identifier);

    /**
     * @return A human readable name identifying the PhotoSource (eg: 'Flickr')
     */
    public abstract String getPhotoSourceName();

    public boolean isSearchSupported() { return false; }

    public PhotoListRequestCompleted getPhotoListRequestCompletedListener() {
        return mPhotoListRequestCompletedListener;
    }
    public void setPhotoListRequestCompletedListener(PhotoListRequestCompleted photoListRequestCompletedListener) {
        mPhotoListRequestCompletedListener = photoListRequestCompletedListener;
    }
    public int getPhotosPerPage() {
        return mPhotosPerPage;
    }
    public void setPhotosPerPage(int photosPerPage) {
        mPhotosPerPage = photosPerPage;
    }
}
