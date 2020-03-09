# Popular Movies Stage 2

## General Overview

This repo contains all the work done for the **Popular Movies Stage 2** project of the Udacity's Android Developer Nanodegree.

## Application description

This application is built on top of [Popular Movies Stage 1](https://github.com/acasadoquijada/popularmovies-stage1). Because of this, it has all the Stage 1 functionalities plus:

* User's are now able to play movie trailers in the Youtube app
 * Reviews are shown in the DetailActivity of each movie
 * Added option to mark/unmark movies as favorite.
 * Added sort option to sort by favorites
 * The favorites movies are stored in a SQLite DataBase
	
## Application class structure

    |-- activities package
    |   |-- DetailActivity.java
    |   |-- MainActivity.java
    |   |-- SettingActivity.java
    |-- database package
    |   |-- ListConverter.java
    |   |-- MovieConverter.java
    |   |-- MovieDAO.java
    |   |-- MovieDataBase.java
    |-- fragments package
    |   |-- SettingFragments.java
    |-- movie package
    |   |-- Movie.java
    |   |-- MovieAdapter.java
    |-- utilities package
    |   |-- AppExecutor.java 
    |   |-- JsonMovieUtils.java
    |   |-- NetworkUtils.java
    |-- viewmodel package
    |   |-- MainViewModel.java 
    |   |-- MovieAPIViewModel.java

**Activities package**

* DetailActivity: Activity where the details of a movie are shown
* MainActivity: Main Activity of the app. All the network request are done here
* SettingActivity: Activiy in charge of handling the settings. In this case, the sort option

**Database package**

Contains all the necesary classes to use Room to store the favorite movies of the user in a SQL database

**Movie package**

* Movie: Represents a movie with all its info
* MovieAdapter: RecyclerView in charge of present the movies to the user

**Utilities package**

* JsonMovieUtils: Parses the movie info obtained from JSON as results of network requets
* NetWorkUtils: Request movie info to themoviedb.org
* AppExecutor: Provides infrastructure to perform tasks in different thread

**ViewModel package**

* MainViewModel: Contains LiveData. This LiveData is the favorite movies of the user. 

* MovieAPIViewModel: Requets the movies to themoviedb.org. 

   
## Add your themoviedb.org API key

You need to register in themoviedb.org to obtain a key for their API in order to use this app.

The key goes in the NetworkUtils.java class of the utilties package line 26. [shortcut](https://github.com/acasadoquijada/popularmovies-stage2/blob/master/app/src/main/java/com/example/popularmoviesstage2/utilities/NetworkUtils.java#L26)

## Logos

The res > drawable > **ic_video_camera.xml** icon has been **designed by Those Icons** from ** flaticom.com ** 

More information here:
Icons made by <a href="https://www.flaticon.com/authors/those-icons" title="Those Icons">Those Icons</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
