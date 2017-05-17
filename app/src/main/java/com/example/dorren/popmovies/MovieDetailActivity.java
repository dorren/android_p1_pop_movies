package com.example.dorren.popmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dorren.popmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by dorrenchen on 5/12/17.
 */

public class MovieDetailActivity extends AppCompatActivity {
    private static final String KLASS = MainActivity.class.getSimpleName();
    private TextView mDetailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        Intent activityIntent = getIntent();
        Context context = getApplicationContext();


        if (activityIntent != null) {
            if (activityIntent.hasExtra(Intent.EXTRA_TEXT)) {
                String movieId = activityIntent.getStringExtra(Intent.EXTRA_TEXT);

                new FetchDetailTask(this).execute(movieId);
            }

        }
    }

    public void renderPoster(MoviePoster poster){
        Log.i(KLASS, "render poster " + poster.toString());

        TextView titleView = (TextView) findViewById(R.id.movie_title);
        titleView.setText(poster.originalTitle);

        ImageView thumbView = (ImageView) findViewById(R.id.movie_thumb);
        String url = poster.imagePath;
        Picasso.with(this).load(url).into(thumbView);

        TextView overviewView = (TextView) findViewById(R.id.movie_overview);
        overviewView.setText(poster.overview);

        TextView ratingView = (TextView) findViewById(R.id.movie_rating);
        ratingView.setText("Rating: " + poster.rating);

        TextView dateView = (TextView) findViewById(R.id.movie_release_date);
        dateView.setText("Release Date: " + poster.releaseDate);
    }

    public class FetchDetailTask extends AsyncTask<String, Void, MoviePoster> {
        private MovieDetailActivity mContext;
        private MoviePoster mPoster;

        public FetchDetailTask(MovieDetailActivity context){
            super();
            mContext = context;
        }

        @Override
        protected MoviePoster doInBackground(String... params) {
            String movieId = params[0];
            URL detailPath = NetworkUtils.buildDetailURL(movieId);

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(detailPath);
                JSONObject movie = new JSONObject(response);
                mPoster = new MoviePoster();

                mPoster.movieId = movie.getString("id");
                mPoster.originalTitle = movie.getString("original_title");

                mPoster.overview = movie.getString("overview");

                String imagePath = movie.getString("poster_path");
                URL fullPath = NetworkUtils.buildImageURL(imagePath);
                mPoster.imagePath = fullPath.toString();

                mPoster.detailPath= detailPath.toString();

                mPoster.releaseDate = movie.getString("release_date");
                mPoster.rating = movie.getString("vote_average");

                return mPoster;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MoviePoster moviePoster) {
            mContext.renderPoster(moviePoster);
        }
    }
}
