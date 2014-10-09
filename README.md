photos
======

Photos is a very simple Flickr client.  Users can search Flickr's content by keyword, view large versions of a selected image and share those images with friends.  It is not intended to be a fully featured Flickr client; rather, it aims to demonstrate core Android concepts and best practices.

Architecture & Design
------
Photos uses a fairly straightforward architecture.  There are a few notable bits that help to ensure smooth performance and ease of use:
* `PhotoSource` is an abstract class for which Photos implements the `FlickrPhotoSource`.  The intent is that users could also define other types of `PhotoSource`, and that the client application would be able to interact with the `PhotoSource` through the same common set of API's.

Limitations
-----
* By default, Photos retrieves a list of 'recent' Flickr photos.  Without some sort of timestamp, it seems like there are often "duplicate" items in the photo results
* Currently, it's only possible to share an image URL via the Share action, not the image content itself
* The application has been tested in portrait and landscape mode, but not on tablet

Next steps
-----
There are so many things that could be added to improve the application.  A few possibilities:

* The client could implement additional `PhotoSource` (besides Flickr), allowing the client application to access local images, Google images, or any other image source, with the same set of API's.
* There could be additional layouts for tablet.  The master-detail pattern is very common, and could work well here.  Instead of starting a new activity and taking the user to a new page after clicking an image, there could be a grid of images on one side, and a larger version of the image in the detail view.  This type of layout would take advantage of the tablet's larger screen real estate.
