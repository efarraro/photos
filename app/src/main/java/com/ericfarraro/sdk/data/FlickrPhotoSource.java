package com.ericfarraro.sdk.data;

import android.net.Uri;
import android.util.Log;

import com.ericfarraro.sdk.FetchPhotoListTask;
import com.ericfarraro.sdk.interfaces.PhotoListRequestCompleted;
import com.ericfarraro.sdk.interfaces.UrlContentRetrieved;
import com.ericfarraro.sdk.models.Photo;
import com.ericfarraro.sdk.util.UrlFetchTask;

import junit.framework.Assert;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Eric on 10/7/2014.
 * Flickr public API documentation https://www.flickr.com/services/api/
 */
public class FlickrPhotoSource extends PhotoSource implements PhotoListRequestCompleted {

    public static final String TAG = "FlickrPhotoSource";

    public static final String ENDPOINT ="https://api.flickr.com/services/rest/";
    private static final String API_KEY = "4c279506ba25a863b18568fe644f8a78";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String METHOD_SIZES = "flickr.photos.getSizes";
    private static final String PARAM_PER_PAGE = "per_page";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_EXTRAS = "extras";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String PARAM_TEXT = "text";
    private static final String XML_PHOTO = "photo";
    private static final String XML_SIZE = "size";

    @Override
    public void fetchDefaultPhotos(int page) {

        //Documentation: https://www.flickr.com/services/api/flickr.photos.getRecent.html

        UrlFetchTask task = new UrlFetchTask();
        task.setListener(new UrlContentRetrieved() {
            @Override
            public void onUrlContentRetrieved(String content) {
                parseResults(content);
            }
        });

        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_PER_PAGE, Integer.toString(mPhotosPerPage))
                .appendQueryParameter(PARAM_PAGE, Integer.toString(page))
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL).build().toString();

        Log.d(TAG, url);

        task.execute(url);
    }

    @Override
    public String getPhotoSourceName() {
        return "Flickr";
    }

    @Override
    public void searchPhotos(String query, int page) {

        // Documentation: https://www.flickr.com/services/api/flickr.photos.search.html

        UrlFetchTask task = new UrlFetchTask();
        task.setListener(new UrlContentRetrieved() {
            @Override
            public void onUrlContentRetrieved(String content) {
                parseResults(content);
            }
        });

        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_TEXT, query)
                .appendQueryParameter(PARAM_PER_PAGE, Integer.toString(mPhotosPerPage))
                .appendQueryParameter(PARAM_PAGE, Integer.toString(page))
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL).build().toString();

        Log.d(TAG, url);

        task.execute(url);
    }

    @Override
    public void getLargePhoto(String identifier) {

        //Documentation: https://www.flickr.com/services/api/flickr.photos.getSizes.html

        UrlFetchTask task = new UrlFetchTask();
        task.setListener(new UrlContentRetrieved() {
            @Override
            public void onUrlContentRetrieved(String content) {
                parseGetSizesResponse(content);
            }
        });

        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SIZES)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("photo_id", identifier).build().toString();

        task.execute(url);
    }

    /**
     * Parses response for Flickr's XML response to retrieve the URL of a large image
     * @param xml XML representing the response from Flickr
     */
    protected void parseGetSizesResponse(String xml) {

        ArrayList<Photo> photos = new ArrayList<Photo>();

        Log.d("debug", xml);

        int largestSize = -1;
        String largestUrl = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            // iterate over the XML response, parsing the result
            int eventType = parser.next();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if(eventType == XmlPullParser.START_TAG && XML_SIZE.equals(parser.getName())) {

                    // the sizes are not always the same, so lets just grab the largest one,
                    // to make things easy

                    int width = Integer.parseInt(parser.getAttributeValue(null, "width"));
                    int height = Integer.parseInt(parser.getAttributeValue(null, "height"));

                    // ran into an issue where some images were simply too large to be used
                    if(width * height > largestSize && width * height < 4096*4096) {
                        largestSize = width * height;
                        largestUrl = parser.getAttributeValue(null, "source");
                    }
                }

                eventType = parser.next();
            }
        } catch(XmlPullParserException e) {
            onPhotoListRequestCompleted(null);
            return;
        } catch(IOException e) {
            onPhotoListRequestCompleted(null);
            return;
        }

        Assert.assertTrue("Expected: at least one image", largestUrl != null);

        // if we found at least one picture (and we should have, at this point), return it
        if(largestUrl != null) {
            Photo photo = new Photo();
            photo.setUrl(largestUrl);
            photos.add(photo);
        }

        // notify listeners that the photo results are ready
        onPhotoListRequestCompleted(photos);
    }

    /**
     * Parses Flickr XML response for photos.  Notifies listeners when complete
     * @param xml XML representing the response from Flickr
     */
    protected void parseResults(String xml) {

        xml = xml.replaceAll("\n", "");

        ArrayList<Photo> photos = new ArrayList<Photo>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            // TODO need to set UTF-8 encoding here?

            // iterate over the XML response, parsing the result
            int eventType = parser.next();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {

                    String id = parser.getAttributeValue(null, "id");
                    String caption = parser.getAttributeValue(null, "title");
                    String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);

                    Photo item = new Photo();
                    item.setId(id);
                    item.setTitle(caption);
                    item.setUrl(smallUrl);

                    // the URL might be null is this image didn't have a URL (eg: no "extra small"
                    // image was found)  For purposes of this exercise, ignore these images
                    if(item.getUrl() != null)
                        photos.add(item);
                }

                eventType = parser.next();
            }

        } catch(XmlPullParserException e) {
            // respond with null; we weren't able to create a parser
            onPhotoListRequestCompleted(null);
        } catch(IOException ioe) {
            // respond with null; some sort of IO exception occurred
            onPhotoListRequestCompleted(null);
        }

        // notify listeners that the photo results are ready
        onPhotoListRequestCompleted(photos);
    }

    @Override
    public boolean isSearchSupported() {
        return true;
    }

    @Override
    public void onPhotoListRequestCompleted(ArrayList<Photo> photos) {
        if(mPhotoListRequestCompletedListener != null)
            mPhotoListRequestCompletedListener.onPhotoListRequestCompleted(photos);
    }
}
