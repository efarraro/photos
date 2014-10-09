package com.ericfarraro.photos.singletons;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Eric on 10/8/2014.
 */
public class PhotoCache extends LruCache<String, Bitmap> {

    private static final PhotoCache mInstance = new PhotoCache();

    protected PhotoCache() {
        // create a cache that uses 1/8th of the available space
        super((int)Runtime.getRuntime().maxMemory() / 1024 / 8);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount() / 1024;
    }

    public static PhotoCache getInstance() {

        return mInstance;
    }
}
