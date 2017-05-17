package com.example.dorren.popmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.dorren.popmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private static final String KLASS = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.movie_posters);

        //LinearLayoutManager layoutManager
        //        = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);


        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        new FetchMoviesTask(this).execute(NetworkUtils.SORT_POPULAR);
    }

    @Override
    public void onClick(MoviePoster poster) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToDetail = new Intent(context, destinationClass);

        String url = poster.detailPath;
        intentToDetail.putExtra(Intent.EXTRA_TEXT, poster.movieId);
        startActivity(intentToDetail);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MoviePoster[]> {
        private MoviePoster[] mPosters;
        private Context mContext;

        public FetchMoviesTask(Context context){
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
            setApiKey();
        }

        @Override
        protected MoviePoster[] doInBackground(String... params) {

            URL url = NetworkUtils.buildMovieURL(params[0]);
            Log.i(KLASS, url.toString());

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(url);
                JSONObject json = new JSONObject(response);
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
                    poster.detailPath= detailPath.toString();

                    mPosters[i] = poster;
                }

                return mPosters;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MoviePoster[] posters) {
            mMovieAdapter.setPosterData(posters);
        }
    }
}
