package com.example.dorren.popmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.dorren.popmovies.models.Movie;
import com.example.dorren.popmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by dorrenchen on 6/15/17.
 */

public class MoviesLoader extends AsyncTaskLoader<MoviePoster[]> {
    private static final String KLASS = MoviesLoader.class.getSimpleName();
    private MoviePoster[] mPosters;
    private String mErrorMsg;
    private String mSort;

    public MoviesLoader(Context context, String sort){
        super(context);
        this.mSort = sort;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    @Override
    protected void onStartLoading() {
        Log.d(KLASS, "onStartLoading");
        forceLoad();
    }

    @Override
    public MoviePoster[] loadInBackground() {
        Log.d(KLASS, "loadInBackground sort " + mSort);

        if(mSort.equals(NetworkUtils.SORT_POPULAR) ||
                mSort.equals(NetworkUtils.SORT_TOP_RATED)) {
            mPosters = fromApi();
        }else {
            mPosters = fromFavorites();
        }

        return mPosters;
    }

    private MoviePoster[] fromApi(){
        URL url = NetworkUtils.buildMovieURL(mSort);
        Log.i(KLASS, url.toString());

        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            JSONObject json = new JSONObject(response);

            if(json.has("status_message")) {
                mErrorMsg = json.getString("status_message");
            }else {
                JSONArray movies = json.getJSONArray("results");
                mPosters = new MoviePoster[movies.length()];

                for (int i = 0; i < movies.length(); i++) {
                    MoviePoster poster = new MoviePoster();

                    JSONObject movie = movies.getJSONObject(i);
                    poster.movieId = movie.getString("id");
                    poster.originalTitle = movie.getString("original_title");

                    String imagePath = movie.getString("poster_path");
                    URL fullPath = NetworkUtils.buildImageURL(imagePath);
                    poster.imagePath = fullPath.toString();

                    URL detailPath = NetworkUtils.buildDetailURL(poster.movieId);
                    poster.detailPath = detailPath.toString();

                    mPosters[i] = poster;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrorMsg = e.getMessage();
            return null;
        }
        return mPosters;
    }

    private MoviePoster[] fromFavorites() {
        return Movie.allFavorites(getContext());
    }
}

