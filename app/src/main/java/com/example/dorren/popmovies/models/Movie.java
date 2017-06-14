package com.example.dorren.popmovies.models;

import android.provider.BaseColumns;

/**
 * Created by dorrenchen on 6/14/17.
 */

public class Movie implements BaseColumns {
    public static final String TABLE_NAME = "movies";
    public static final String COLUMN_MOVIE_ID   = "movie_id";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
}
