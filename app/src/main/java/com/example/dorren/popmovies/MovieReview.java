package com.example.dorren.popmovies;

/**
 * Created by dorrenchen on 6/13/17.
 */

public class MovieReview {
    public String author, content;

    @Override
    public String toString() {
        String result = author + "\n " + content + "\n ";

        return result;
    }
}
