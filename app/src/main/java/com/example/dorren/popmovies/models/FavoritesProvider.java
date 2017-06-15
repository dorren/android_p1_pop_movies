package com.example.dorren.popmovies.models;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by dorrenchen on 6/14/17.
 */

public class FavoritesProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.dorren.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";
    public static final Uri CONTENT_URI =  BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITE  = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, PATH_FAVORITES, CODE_FAVORITES);
        matcher.addURI(AUTHORITY, PATH_FAVORITES + "/#", CODE_FAVORITE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query( Uri uri,
                         String[] projection,  String selection,
                         String[] selectionArgs,  String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES: {
                cursor = mDbHelper.getDb().query(
                        Movie.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        String result;

        switch (match){
            case CODE_FAVORITES:{
                result = ContentResolver.CURSOR_DIR_BASE_TYPE;
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        return result;
    }

    @Override
    public Uri insert(Uri uri,  ContentValues values) {
        Uri returnUri;
        Log.d("provider ", "insert ");

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES: {
                long id = mDbHelper.getDb().insert(Movie.TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(CONTENT_URI, id);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri,
                      String selection, String[] selectionArgs) {
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                count = mDbHelper.getDb().delete(Movie.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return count;
    }

    @Override
    public int update(Uri uri,
                      ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("update() not implemented");
    }
}
