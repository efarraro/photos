package com.ericfarraro.sdk.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;

import com.ericfarraro.sdk.interfaces.ImageDownloadCompleted;
import com.ericfarraro.sdk.util.Utility;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eric on 10/7/2014.
 */
public class ImageDownloader extends HandlerThread {

    public static final String NAME = "ImageDownloader";
    public static final int MESSAGE_DOWNLOAD_IMAGE = 0;

    Handler mHandler;
    final Map<ImageView, String> mRequestMap =
            Collections.synchronizedMap(new HashMap<ImageView, String>());
    protected ImageDownloadCompleted mListener;

    public ImageDownloader() {
        super(NAME);
    }

    /**
     * Requests the the image URL be added to the download queue
     * @param token A token uniquely identifying the image
     * @param url The URL of the image
     */
    public void queueImageUrl(ImageView token, String url) {
        mRequestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD_IMAGE, token).sendToTarget();
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD_IMAGE) {
                    handleRequest((ImageView)msg.obj);
                }
            }
        };
    }

    protected void handleRequest(final ImageView imageView) {

        final String url = mRequestMap.get(imageView);

        try {
            byte[] imageBytes = Utility.getBytesForUrl(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // if the request for this ImageView (token) has changed, ignore it
                    if(mRequestMap.get(imageView) != url)
                        return;

                    mRequestMap.remove(imageView);

                    // notify listeners
                    if(mListener != null) {
                        mListener.onImageDownloadCompleted(imageView, bitmap);
                    }
                }
            });
        } catch(IOException e) {
            // some error occurred getting the bytes
            if(mListener != null) {
                mListener.onImageDownloadCompleted(imageView, null);
            }
        }
    }

    public ImageDownloadCompleted getListener() {
        return mListener;
    }

    public void setListener(ImageDownloadCompleted listener) {
        mListener = listener;
    }
}
