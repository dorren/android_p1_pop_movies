package com.example.dorren.popmovies;


/**
 * Created by dorrenchen on 5/5/17.
 */

public class MoviePoster{
    public String movieId;
    public String originalTitle, imagePath, detailPath, releaseDate, rating;
    public String overview;


    @Override
    public String toString() {
        String result = movieId       + "\n " +
                        originalTitle + "\n " +
                        imagePath     + "\n " +
                        detailPath    + "\n " +
                        overview      + "\n " +
                        releaseDate   + "\n " +
                        rating;

        return result;
    }
}
