package com.ericfarraro.photos.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ericfarraro.photos.R;
import com.ericfarraro.photos.singletons.PhotoCache;
import com.ericfarraro.sdk.core.ImageDownloader;
import com.ericfarraro.sdk.interfaces.ImageDownloadCompleted;
import com.ericfarraro.sdk.models.Photo;

import java.util.List;

/**
 * Created by Eric on 10/7/2014.
 */
public class PhotoGalleryItemListAdapter extends ArrayAdapter<Photo> {

    protected ImageDownloader mImageDownloadHandler;

    public PhotoGalleryItemListAdapter(
            Context context, List<Photo> objects, ImageDownloader downloader) {
        super(context, 0, objects);

        mImageDownloadHandler = downloader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.grid_item_photo, parent, false);
        }

        ImageView image = (ImageView)convertView.findViewById(R.id.grid_item_photo_root);
        Photo photo = getItem(position);

        // set a default placeholder
        image.setImageResource(R.drawable.image_placeholder);

        // store a reference to the image's URL so that we can retrieve it later
        image.setTag(photo.getUrl());

        if(PhotoCache.getInstance().get(photo.getUrl()) != null)
            image.setImageBitmap(PhotoCache.getInstance().get(photo.getUrl()));
        else {
            mImageDownloadHandler.queueImageUrl(image, getItem(position).getUrl());
        }

        return convertView;
    }
}
