package com.example.dorren.popmovies.models;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.example.dorren.popmovies.MoviePoster;

/**
 * Created by dorrenchen on 6/14/17.
 */

public class Movie implements BaseColumns {
    public static final String TABLE_NAME = "movies";
    public static final String COLUMN_MOVIE_ID   = "movie_id";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static Cursor findAll(Context context){
        return context.getContentResolver().query(
                FavoritesProvider.CONTENT_URI, null, null, null, COLUMN_CREATED_AT + " DESC");
    }

    public static MoviePoster[] toMoviePosters(Cursor cursor){
        MoviePoster[] moviePosters = new MoviePoster[cursor.getCount()];

        for(int i=0; i< cursor.getCount(); i++){
            cursor.moveToPosition(i);
            moviePosters[i] = new MoviePoster();
            moviePosters[i].movieId = cursor.getString(cursor.getColumnIndex(Movie.COLUMN_MOVIE_ID));
            moviePosters[i].imagePath = cursor.getString(cursor.getColumnIndex(Movie.COLUMN_IMAGE_PATH));
        }

        return moviePosters;
    }

    public static MoviePoster[] allFavorites(Context context){
        Cursor cursor = findAll(context);
        return toMoviePosters(cursor);
    }
}
