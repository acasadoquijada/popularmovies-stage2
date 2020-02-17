# Popular Movies Stage 2

## General Overview

This repo contains all the work done for the **Popular Movies Stage 2** project of the Udacity's Android Developer Nanodegree.

## Application description

This application is built on top of [Popular Movies Stage 1](https://github.com/acasadoquijada/popularmovies-stage1). Because of this, it has all the Stage 1 functionalities plus:

* User's are now able to play movie trailers in the Youtube app
    You’ll allow users to view and play trailers ( either in the youtube app or a web browser).
 * Reviews are shown in the DetailActivity of each movie
 * Added option to mark/unmark movies as favorite.
 * Added sort option to sort by favorites
 * The favorites movies are stored in a SQLite DataBase
	
## Application class structure

    |-- activities package
    |   |-- DetailActivity.java
    |   |-- MainActivity.java
    |-- movie package
    |   |-- Movie.java
    |   |-- MovieAdapter.java
    |-- utilities package
    |   |-- JsonMovieUtils.java
    |   |-- NetworkUtils.java
    |-- database package
    |   |-- DataBaseHelper.java

**Activities package**

* DetailActivity: Activity where the details of a movie are shown
* MainActivity: Main Activity of the app. All the network request are done here

**Movie package**
-
* Movie: Represents a movie with all its info
* MovieAdapter: RecyclerView in charge of present the movies to the user

**Utilities package**

* JsonMovieUtils: Parses the movie info obtained from JSON as results of network requets
* NetWorkUtils: Request movie info to themoviedb.org

**Database package**

* DataBaseHelper: Store in a SQLite database the movies marked as favorite by the user

   
## Add your themoviedb.org API key

You need to register in themoviedb.org to obtain a key for their API in order to use this app.

The key goes in the NetworkUtils.java class of the utilties package line 26. [shortcut](https://github.com/acasadoquijada/popularmovies-stage2/blob/master/app/src/main/java/com/example/popularmoviesstage2/utilities/NetworkUtils.java#L26)