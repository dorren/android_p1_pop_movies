package com.example.dorren.popmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.dorren.popmovies.MoviePoster;
import com.example.dorren.popmovies.R;
import com.google.gson.Gson;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by dorrenchen on 6/13/17.
 */

public class PreferenceUtil {
    private static final String KLASS = PreferenceUtil.class.getSimpleName();
    private static final Set emptySet = new TreeSet<String>();

    public static SharedPreferences getFavPrefs(Context context){
        String favFile = context.getString(R.string.fav_file);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences prefs = (SharedPreferences) context.getSharedPreferences(favFile, Context.MODE_PRIVATE);

        return prefs;
    }
    public static void addFavorite(Context context, MoviePoster moviePoster){
        String favKey = context.getString(R.string.fav_key);
        SharedPreferences prefs = getFavPrefs(context);
        Set<String> favs = prefs.getStringSet(favKey, emptySet);

        // save
        String data = moviePoster.toJson();
        favs.add(data);

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet(favKey, favs);
        editor.commit();
    }

    public  static void removeFavorite(Context context, MoviePoster moviePoster){
        String favKey = context.getString(R.string.fav_key);
        SharedPreferences prefs = getFavPrefs(context);
        Set<String> favs = prefs.getStringSet(favKey, emptySet);

        // save
        String data = moviePoster.toJson();
        favs.remove(data);

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet(favKey, favs);
        editor.commit();
    }

    public  static MoviePoster[] getFavorites(Context context){
        Set<String> favs = getFavoriteSet(context);

        Log.d(KLASS, "fetch favs " + Integer.toString(favs.size()));

        MoviePoster[] mPosters = new MoviePoster[favs.size()];
        int i = 0;
        for (Object s : favs) {
            Gson gson = new Gson();
            String str = (String)s;
            String[] data = gson.fromJson(str, String[].class);
            mPosters[i] = new MoviePoster();
            mPosters[i].movieId = data[0];
            mPosters[i].imagePath = data[1];

            i++;
        }
        return mPosters;
    }

    public static Set getFavoriteSet(Context context){
        SharedPreferences prefs = getFavPrefs(context);

        String favKey = context.getString(R.string.fav_key);
        Set<String> set = prefs.getStringSet(favKey, emptySet);

        return set;
    }

    public static boolean isFavorite(Context context, MoviePoster moviePoster){
        Set<String> favs = getFavoriteSet(context);

        String data = moviePoster.toJson();
        return favs.contains(data);
    }
}
