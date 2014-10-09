package com.ericfarraro.photos.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ericfarraro.photos.R;
import com.ericfarraro.sdk.core.ImageDownloader;
import com.ericfarraro.sdk.data.FlickrPhotoSource;
import com.ericfarraro.sdk.data.PhotoSource;
import com.ericfarraro.sdk.interfaces.ImageDownloadCompleted;
import com.ericfarraro.sdk.interfaces.PhotoListRequestCompleted;
import com.ericfarraro.sdk.models.Photo;

import java.util.ArrayList;

/**
 * Created by Eric on 10/8/2014.
 */
public class PhotoDetailFragment extends Fragment
        implements ImageDownloadCompleted, PhotoListRequestCompleted {

    protected ImageView mImageView;
    protected TextView mTitleTextView;
    protected String mTitle;

    protected ImageDownloader mImageDownloadHandler;
    protected PhotoSource mCurrentPhotoSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = getArguments().getString("title");

        mImageDownloadHandler = new ImageDownloader();
        mImageDownloadHandler.setListener(this);
        mImageDownloadHandler.getLooper();
        mImageDownloadHandler.start();

        mCurrentPhotoSource = new FlickrPhotoSource();
        mCurrentPhotoSource.setPhotoListRequestCompletedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageDownloadHandler.setListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageDownloadHandler.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_detail, container, false);

        mImageView = (ImageView)rootView.findViewById(R.id.photo_detail_image);
        mTitleTextView = (TextView)rootView.findViewById(R.id.photo_detail_text);
        if(mTitle != null)
            mTitleTextView.setText(mTitle);

        mCurrentPhotoSource.getLargePhoto(getArguments().getString("id"));

        return rootView;
    }

    @Override
    public void onPhotoListRequestCompleted(ArrayList<Photo> photos) {
        // for the detail view, there should be only one photo

        // (we could also display some sort of error here)
        if(photos == null)
            return;

        mImageDownloadHandler.queueImageUrl(mImageView, photos.get(0).getUrl());
    }

    @Override
    public void onImageDownloadCompleted(final ImageView imageView, final Bitmap bitmap) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isVisible())
                    imageView.setImageBitmap(bitmap);
            }
        });
    }
}
