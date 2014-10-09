photos
======

Photos is a very simple Flickr client.  Users can search Flickr's content by keyword, view large versions of a selected image and share those images with friends.  It is not intended to be a fully featured Flickr client; rather, it aims to demonstrate core Android concepts and best practices.

Building 'Photos'
-----
Photos was built using Android Studio 0.8.2 (beta) and was tested on a Nexus 5

Architecture & Design
------
Photos uses a fairly straightforward architecture.  There are a few notable bits that help to ensure smooth performance and ease of use.

### PhotoSource
`PhotoSource` is an abstract class for which Photos implements the `FlickrPhotoSource`.  The intent is that users could also define other types of `PhotoSource`, and that the client application would be able to interact with the `PhotoSource` through the same common set of API's.

The following methods are currently available:
* `fetchDefaultPhotos` In the case of `FlickrPhotoSource`, this can be used to get a query-less set of recent photos
* `searchPhotos` Requests photos for the specified search query and page number
* `getLargePhoto` Given some identifier, returns the URL of a large photo

Clients using `PhotoSource` should implement the `PhotoListRequestCompleted` interface, which `PhotoSource` uses to return a list of photos.

### Performance considerations
* Image caching is used to optimize the performance of scrolling, particularly scrolling that involves scrolling both up and down.  In this case, we use an LRU cache with the image's URL as the key.  When reloading an image that we've seen before, we first check the image cache.  If the image is present, we can use that image instead of downloading it again.
* Endless scrolling is implemented by listening to the `OnScroll` event of the `GridView`.  After a certain threshold, the `EndlessScrollListener` increments the page, and notifies listeners that they should load additional results.
* `ImageDownloader` is a HandlerThread that uses a looper to process image downloads in the background.  Clients can request an image to be downloaded in the background using `queueImageUrl` and can implement the `ImageDownloadCompleted` interface to listen for the resulting bitmap data.

#Visual design
* Opacity animations are used to soften the somewhat jarring feel of images 'appearing' while scrolling through the list
* Photos includes the SearchView within the ActionBar, maximizing the use of screen real-estate for showing the user images
* Where possible Fragments are used, instead of standalone activities.  Although this isn't quite as important in a small app, this would help to ensure that our fragments could be easily used elsewhere.  This is particularly important when considering alternate layout designs, such as the master-detail view for tablet (note: there is not currently a tablet UI, but this is discussed as a possiblity below)

Limitations
-----
* By default, Photos retrieves a list of 'recent' Flickr photos.  Without some sort of timestamp, it seems like there are often "duplicate" items in the photo results
* Currently, it's only possible to share an image URL via the Share action, not the image content itself
* The application has been tested in portrait and landscape mode, but not on tablet

Next steps
-----
There are so many things that could be added to improve the application.  A few possibilities:

* The client could implement additional `PhotoSource` (besides Flickr), allowing the client application to access local images, Google images, or any other image source, with the same set of API's.  The client would have some sort of 
* There could be additional layouts for tablet.  The master-detail pattern is very common, and could work well here.  Instead of starting a new activity and taking the user to a new page after clicking an image, there could be a grid of images on one side, and a larger version of the image in the detail view.  This type of layout would take advantage of the tablet's larger screen real estate.
* Besides the photo grid, users may wish to have other options by which to sort or visualize the collection of images.  Smaller/larger thumbnails, a masonry (Pinterest) style view, or more advanced sorting options could help a user find the photos they's more interested in.
* For `PhotoSource` that support photographers (as opposed to the local gallery/anonymous image sharing services), each photo could have a link that allowed the user to see other images from the same photographer.  Additionally, comments (where applicable) could be shown on the Photo Detail page
