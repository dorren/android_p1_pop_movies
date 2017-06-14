package com.example.dorren.popmovies.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dorren.popmovies.MoviePoster;


/**
 * Created by dorrenchen on 6/14/17.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movies.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 4;

    private SQLiteDatabase mDb;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + Movie.TABLE_NAME + " (" +
                Movie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Movie.COLUMN_MOVIE_ID   + " TEXT NOT NULL, " +
                Movie.COLUMN_IMAGE_PATH + " TEXT NOT NULL, " +
                Movie.COLUMN_CREATED_AT + " CREATED_AT DEFAULT CURRENT_TIMESTAMP, " +
                Movie.COLUMN_UPDATED_AT + " UPDATED_AT DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Movie.TABLE_NAME);
        onCreate(db);
    }

    public SQLiteDatabase getDb() {
        if(mDb == null) {
            mDb = getWritableDatabase();
        }

        return mDb;
    }

    public MoviePoster[] findAll(){
        Cursor cursor = getDb().query(
            Movie.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            Movie.COLUMN_CREATED_AT + " DESC"
        );

        MoviePoster[] posters = toMoviePosters(cursor);
        return posters;
    }

    private MoviePoster[] toMoviePosters(Cursor cursor){
        MoviePoster[] moviePosters = new MoviePoster[cursor.getCount()];

        for(int i=0; i< cursor.getCount(); i++){
            cursor.moveToPosition(i);
            moviePosters[i] = new MoviePoster();
            moviePosters[i].movieId = cursor.getString(cursor.getColumnIndex(Movie.COLUMN_MOVIE_ID));
            moviePosters[i].imagePath = cursor.getString(cursor.getColumnIndex(Movie.COLUMN_IMAGE_PATH));
        }

        return moviePosters;
    }

    public boolean isSaved(String movieId, MoviePoster[] posters){

        for(MoviePoster poster : posters) {
            if(poster.movieId.equals(movieId)){
                return true;
            }
        }

        return false;
    }


    public long addMovie(String movieId, String imagePath) {
        ContentValues cv = new ContentValues();
        cv.put(Movie.COLUMN_MOVIE_ID, movieId);
        cv.put(Movie.COLUMN_IMAGE_PATH, imagePath);

        return getDb().insert(Movie.TABLE_NAME, null, cv);
    }

    public boolean deleteMovie(String movieId) {
        String[] args = new String[]{movieId};

        return getDb().delete(Movie.TABLE_NAME, Movie.COLUMN_MOVIE_ID + "=?", args) > 0;
    }

}
