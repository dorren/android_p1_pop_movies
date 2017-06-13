package com.example.dorren.popmovies;

import android.net.Uri;

import com.example.dorren.popmovies.utilities.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dorrenchen on 6/12/17.
 */

public class MovieTrailer {
    public String key, name, site, type;
    public int size;

    public URL videoURL() {
        return NetworkUtils.buildYoutubeURL(key);
    }

    @Override
    public String toString() {
        String result = videoURL()    + "\n " +
                        name          + "\n " +
                        type          + "\n " +
                        size;

        return result;
    }
}
