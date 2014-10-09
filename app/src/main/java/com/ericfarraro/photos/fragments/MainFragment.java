package com.ericfarraro.photos.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import com.ericfarraro.photos.R;
import com.ericfarraro.photos.activities.PhotoDetailActivity;
import com.ericfarraro.photos.adapters.PhotoGalleryItemListAdapter;
import com.ericfarraro.photos.core.EndlessScrollListener;
import com.ericfarraro.photos.singletons.PhotoCache;
import com.ericfarraro.sdk.FetchPhotoListTask;
import com.ericfarraro.sdk.core.ImageDownloader;
import com.ericfarraro.sdk.data.FlickrPhotoSource;
import com.ericfarraro.sdk.interfaces.ImageDownloadCompleted;
import com.ericfarraro.sdk.interfaces.PhotoListRequestCompleted;
import com.ericfarraro.sdk.data.PhotoSource;
import com.ericfarraro.sdk.models.Photo;

import java.util.ArrayList;

/**
 * Created by Eric on 10/7/2014.
 */
public class MainFragment extends Fragment
        implements PhotoListRequestCompleted, ImageDownloadCompleted {

    protected GridView mGridView;

    // Simple adapter used to format the images in the grid view
    protected PhotoGalleryItemListAdapter mAdapter;

    // Interface for grabbing photos/data from a datasource (eg: local, Flickr, etc... )
    protected PhotoSource mCurrentPhotoSource;

    // Handler used to download images
    protected ImageDownloader mImageDownloadHandler;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up the Handler we'll use to download the images
        mImageDownloadHandler = new ImageDownloader();
        mImageDownloadHandler.setListener(this);
        mImageDownloadHandler.getLooper();
        mImageDownloadHandler.start();

        // setup a default photo source (Flickr) and request a list of images to display by default
        mCurrentPhotoSource = new FlickrPhotoSource();
        mCurrentPhotoSource.setPhotoListRequestCompletedListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onImageDownloadCompleted(final ImageView imageView, final Bitmap bitmap) {

        // when the images have finished downloading, set the bitmap for the ImageView
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isVisible())
                        imageView.setImageBitmap(bitmap);

                    // cache the bitmap data by the ImageView's tag (URL)
                    if(imageView.getTag() != null && bitmap != null)
                        PhotoCache.getInstance().put((String)imageView.getTag(), bitmap);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // store reference to the main picture grid and support infinite scrolling
        mGridView = (GridView) rootView.findViewById(R.id.main_photo_grid_view);
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void loadMore(int page) {
                mCurrentPhotoSource.fetchDefaultPhotos(page);
            }
        });

        // clicking an image brings up a larger version of that image, as well as some details
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Photo photo = (Photo)parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                intent.putExtras(photo.asBundle());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onPhotoListRequestCompleted(ArrayList<Photo> photos) {

        // we've retrieved the list of photos from the service; update the adapter
        if (mAdapter == null) {
            mAdapter = new PhotoGalleryItemListAdapter(getActivity(), photos, mImageDownloadHandler);
            mGridView.setAdapter(mAdapter);
        } else {
            mAdapter.addAll(photos);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.main, menu);

        final SearchView searchView = (SearchView)menu.getItem(0).getActionView();
        searchView.setQueryHint(String.format(getString(R.string.search_source),
                mCurrentPhotoSource.getPhotoSourceName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager)getActivity().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}