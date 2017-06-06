# popmovies_public

This app is a project for the Developing Android Apps course of Udacity's 
Android Developer Nanodegree program.  The app was written from scratch for the exception
of third party libraries.  It retrieves movies from the [TMDB](https://www.themoviedb.org/?language=en), 
a community-built movie database and displays them to the user.
The movie data is queried using TMDB's JSON API over HTTP protocol, 
then downloaded and stored in the local SQLite database.


Key Features:
1) Implementation of SyncAdapter to facilitate updates off UI thread
2) Custom ContentProvider for data management operations and interfacing with local SQLite database
3) Aggressive data pre-fetching
4) Custom image download and local storage mechanism, in order alleviate dependence on network connection availability
5) Flexible Fragment-based UI design
6) Custom swipe dismissible detail pane for tablet layout
7) Expandable reviews list with expansion and scroll state retention
8) UI optimization for multiple screens (phones/tablets)
9) Animated UI transitions
10) Ability to add movies to your local favorites collection


## Try it yourself

In order to use this project, you'll need to register for and obtain a TMDB API Key.  It will not work without it.

You can do so at:  https://www.themoviedb.org/?language=en

Once you have obtained an API key, create a gradle.properties file under /app module, add the following line,
and place your key inside the quotes:
API_KEY="your_key_hex_value_goes_here"

## UI Screenshots

##### Phone Demo
<div>
<img align="left" src="screenshots/phone_demo.gif" width="94%">
</div>


#### Tablet Demo
<div>
<img src="screenshots/tablet_config_change_demo.gif" width="94%">
</div>


