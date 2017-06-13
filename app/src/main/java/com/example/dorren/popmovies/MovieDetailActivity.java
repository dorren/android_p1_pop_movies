package com.example.dorren.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dorren.popmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by dorrenchen on 5/12/17.
 */

public class MovieDetailActivity extends AppCompatActivity
    implements TrailerAdapter.TrailerOnClickHandler{

    private static final String KLASS = MainActivity.class.getSimpleName();
    private RecyclerView mTrailersView;
    private TrailerAdapter mTrailersAdapter;
    private RecyclerView mReviewsView;
    private ReviewAdapter mReviewsAdapter;
    private ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        mSpinner = (ProgressBar) findViewById(R.id.detail_loading_indicator);

        // setup trailers
        mTrailersView = (RecyclerView) findViewById(R.id.movie_trailers);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailersView.setLayoutManager(layoutManager);
        mTrailersView.setHasFixedSize(true);
        mTrailersAdapter = new TrailerAdapter(this);
        mTrailersView.setAdapter(mTrailersAdapter);

        mReviewsView = (RecyclerView) findViewById(R.id.movie_reviews);

        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsView.setLayoutManager(layoutManager2);
        mReviewsView.setHasFixedSize(true);
        mReviewsAdapter = new ReviewAdapter();
        mReviewsView.setAdapter(mReviewsAdapter);

        Intent activityIntent = getIntent();

        if (activityIntent != null) {
            if (activityIntent.hasExtra(Intent.EXTRA_TEXT)) {
                String movieId = activityIntent.getStringExtra(Intent.EXTRA_TEXT);

                new FetchDetailTask(this).execute(movieId);
                new FetchTrailersTask(this).execute(movieId);
                new FetchReviewsTask(this).execute(movieId);
            }
        }
    }

    @Override
    public void onClick(MovieTrailer trailer) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(trailer.videoURL().toString());
        Log.d("trailer click ", trailer.videoURL().toString());
        intent.setData(uri);
        startActivity(intent);
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
        protected void onPreExecute() {
            mSpinner.setVisibility(View.VISIBLE);
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
            mSpinner.setVisibility(View.INVISIBLE);
            mContext.renderPoster(moviePoster);
        }
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, MovieTrailer[]> {
        private MovieDetailActivity mContext;
        private MovieTrailer[] mTrailers;
        private String mErrorMsg;

        public FetchTrailersTask(MovieDetailActivity context){
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieTrailer[] doInBackground(String... params) {
            String movieId = params[0];
            URL trailersPath = NetworkUtils.buildTrailersURL(movieId);

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(trailersPath);
                JSONObject json = new JSONObject(response);
                JSONArray jsonTrailers = json.getJSONArray("results");
                mTrailers = new MovieTrailer[jsonTrailers.length()];

                for(int i=0; i<jsonTrailers.length(); i++){
                    JSONObject trailer = jsonTrailers.getJSONObject(i);
                    mTrailers[i] = new MovieTrailer();
                    mTrailers[i].key  = trailer.getString("key");
                    mTrailers[i].name = trailer.getString("name");
                    mTrailers[i].site = trailer.getString("site");
                    mTrailers[i].type = trailer.getString("type");
                    mTrailers[i].size = trailer.getInt("size");

                    Log.d("detail ", mTrailers[i].toString());
                }
                return mTrailers;

            } catch (Exception e) {
                e.printStackTrace();
                mErrorMsg = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieTrailer[] movieTrailers) {
            if(mErrorMsg != null && !mErrorMsg.isEmpty()) {

            } else {
                mTrailersAdapter.setData(movieTrailers);
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, MovieReview[]> {
        private MovieDetailActivity mContext;
        private MovieReview[] mReviews;
        private String mErrorMsg;

        public FetchReviewsTask(MovieDetailActivity context){
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieReview[] doInBackground(String... params) {
            String movieId = params[0];
            URL reviewsPath = NetworkUtils.buildReviewsURL(movieId);

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(reviewsPath);
                JSONObject json = new JSONObject(response);
                JSONArray jsonArray = json.getJSONArray("results");
                mReviews = new MovieReview[jsonArray.length()];

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    mReviews[i] = new MovieReview();
                    mReviews[i].author  = item.getString("author");
                    mReviews[i].content = item.getString("content");

                    Log.d("detail ", mReviews[i].toString());
                }
                return mReviews;

            } catch (Exception e) {
                e.printStackTrace();
                mErrorMsg = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieReview[] movieReviews) {
            if(mErrorMsg != null && !mErrorMsg.isEmpty()) {

            } else {
                mReviewsAdapter.setData(movieReviews);
            }
        }
    }
}
