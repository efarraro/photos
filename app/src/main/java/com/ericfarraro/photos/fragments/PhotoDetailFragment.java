package com.ericfarraro.photos.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ericfarraro.photos.R;
import com.ericfarraro.photos.core.ImageDownloader;
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
    protected String mCurrentPhotoUrl;
    protected ProgressBar mProgressBar;

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

        setHasOptionsMenu(true);
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

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.photo_detail_progress);

        // request a large version of this photo from the photo source
        mCurrentPhotoSource.getLargePhoto(getArguments().getString("id"));

        return rootView;
    }

    @Override
    public void onPhotoListRequestCompleted(ArrayList<Photo> photos) {
        // for the detail view, there should be only one photo

        // (we could also display some sort of error here)
        if(photos == null)
            return;

        mCurrentPhotoUrl = photos.get(0).getUrl();
        mImageDownloadHandler.queueImageUrl(mImageView, mCurrentPhotoUrl);
    }

    @Override
    public void onImageDownloadCompleted(final ImageView imageView, final Bitmap bitmap) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isVisible()) {

                    // animating in the image give it a less jarring feel
                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(1000);
                    imageView.setImageBitmap(bitmap);
                    imageView.startAnimation(animation);
                }

                // hide the progress bar
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.photo_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, mCurrentPhotoUrl);
            intent.setType("text/plain");
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
