package com.example.dorren.popmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dorren.popmovies.utilities.NetworkUtils;
import com.example.dorren.popmovies.utilities.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private static final String KLASS = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorText;
    private ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpinner = (ProgressBar) findViewById(R.id.main_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.movie_posters);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);


        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        new FetchMoviesTask(this).execute(NetworkUtils.SORT_POPULAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.action_popular){
            new FetchMoviesTask(this).execute(NetworkUtils.SORT_POPULAR);
        }

        if(itemId == R.id.action_top_rated){
            new FetchMoviesTask(this).execute(NetworkUtils.SORT_TOP_RATED);
        }


        if(itemId == R.id.action_favorites){
            new FetchFavsTask(this).execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(MoviePoster poster) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToDetail = new Intent(context, destinationClass);

        intentToDetail.putExtra(Intent.EXTRA_TEXT, poster.movieId);
        startActivity(intentToDetail);
    }

    public void renderError(String msg) {
        mErrorText = (TextView) findViewById(R.id.main_error_message);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(mErrorText.getText() + "\n\n" + msg);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MoviePoster[]> {
        private MoviePoster[] mPosters;
        private MainActivity mContext;
        private String mErrorMsg;

        public FetchMoviesTask(MainActivity context){
            super();
            mContext = context;
        }


        /**
         * get api key from xml
         */
        public void setApiKey(){
            NetworkUtils.KEY = getString(R.string.themoviedb_api_key);
        }

        @Override
        protected void onPreExecute() {
            mSpinner.setVisibility(View.VISIBLE);
            setApiKey();
        }

        @Override
        protected MoviePoster[] doInBackground(String... params) {

            URL url = NetworkUtils.buildMovieURL(params[0]);
            Log.i(KLASS, url.toString());

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(url);
                JSONObject json = new JSONObject(response);

                if(json.has("status_message")) {
                    mErrorMsg = json.getString("status_message");
                }

                if(mErrorMsg == null) {
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

                    return mPosters;
                }else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                mErrorMsg = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MoviePoster[] posters) {
            mSpinner.setVisibility(View.INVISIBLE);

            if(mErrorMsg != null && !mErrorMsg.isEmpty()) {
                mContext.renderError(mErrorMsg);
            } else {
                mMovieAdapter.setPosterData(posters);
            }
        }
    }

    public class FetchFavsTask extends AsyncTask<String, Void, MoviePoster[]> {
        private MoviePoster[] mPosters;
        private MainActivity mContext;
        private String mErrorMsg;

        public FetchFavsTask(MainActivity context) {
            super();
            mContext = context;
        }

        @Override
        protected MoviePoster[] doInBackground(String... params) {
            mPosters = PreferenceUtil.getFavorites(mContext);

            return mPosters;
        }

        @Override
        protected void onPostExecute(MoviePoster[] posters) {
            mSpinner.setVisibility(View.INVISIBLE);

            if(mErrorMsg != null && !mErrorMsg.isEmpty()) {
                mContext.renderError(mErrorMsg);
            } else {
                mMovieAdapter.setPosterData(posters);
            }
        }
    }
}
