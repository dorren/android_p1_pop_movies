package com.example.dorren.popmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.dorren.popmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by dorrenchen on 4/20/17.
 */

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185";
    public static final String POPULAR_PATH   = "movie/popular";
    public static final String TOP_RATED_PATH = "movie/top_rated";

    public static final String API_KEY_NAME= "api_key";
    public static String KEY;

    public static final String SORT_POPULAR   = "popular";
    public static final String SORT_TOP_RATED = "top_rated";

    public static final String YOUTUBE_URL = "https://www.youtube.com";


    /**
     * build API url
     *
     * @param sort string, "popular", or "top_rated"
     * @return full api output URL
     *
     * sample output URL:
     * https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=abc123
     * https://api.themoviedb.org/3/discover/movie?certification_country=US&sort_by=vote_average.desc&api_key=abc123
     */
     public static URL buildMovieURL(String sort){
        Uri builtUri = null;
        if( sort == SORT_POPULAR) {
            builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendEncodedPath(POPULAR_PATH)
                    .appendQueryParameter(API_KEY_NAME, KEY)
                    .build();
        }else if (sort == SORT_TOP_RATED){
            builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendEncodedPath(TOP_RATED_PATH)
                    .appendQueryParameter(API_KEY_NAME, KEY)
                    .build();
        }else {
            Log.e(TAG, " sorting " + sort + " not matched ");
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * build movie image url.
     *
     * Sample output url:
     * https://image.tmdb.org/t/p/w500/9Hj2bqi955SvTa5zj7uZs6sic29.jpg
     *
     * @param imagePath
     * @return String imageURL
     *
     */
    public static URL buildImageURL(String imagePath){
        // remove leading slash
        imagePath = imagePath.replaceAll("^/", "");
        Uri imageUri = Uri.parse(IMAGE_BASE_URL).buildUpon().appendEncodedPath(imagePath).build();

        try {
            URL url = new URL(imageUri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * sample output url
     *     https://api.themoviedb.org/3/movie/278?api_key=abc123
     *
     * @param movieId
     * @return movie detail URL
     */
    public static URL buildDetailURL(String movieId) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath("movie")
                .appendPath(movieId)
                .appendQueryParameter(API_KEY_NAME, KEY)
                .build();

        try {
            URL url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get trailers metadata for given movie.
     *
     * sample output url
     *     https://api.themoviedb.org/3/movie/278/videos?api_key=abc123
     *
     * @param movieId
     * @return movie trailers URL
     */
    public static URL buildTrailersURL(String movieId) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter(API_KEY_NAME, KEY)
                .build();

        try {
            URL url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * return youtube trailer url
     *
     * sample output url
     *     https://api.themoviedb.org/3/movie/278/videos?api_key=abc123
     *
     * @param key, youtube video key parameter
     * @return movie trailers URL
     */
    public static URL buildYoutubeURL(String key) {
        Uri uri = Uri.parse(YOUTUBE_URL).buildUpon()
                .appendPath("watch")
                .appendQueryParameter("v", key)
                .build();

        try {
            URL url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get reviews  for given movie.
     *
     * sample output url
     *     https://api.themoviedb.org/3/movie/278/reviews?api_key=abc123
     *
     * @param movieId
     * @return movie reviews URL
     */
    public static URL buildReviewsURL(String movieId) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter(API_KEY_NAME, KEY)
                .build();

        try {
            URL url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
