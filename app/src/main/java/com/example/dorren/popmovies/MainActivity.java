package com.example.dorren.popmovies;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity {
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
        mMovieAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);

        new FetchMoviesTask(this).execute(NetworkUtils.SORT_POPULAR);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
        private String[] mPosterURLs;
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
        protected String[] doInBackground(String... params) {

            URL url = NetworkUtils.buildMovieURL(params[0]);
            Log.i(KLASS, url.toString());

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(url);
                JSONObject json = new JSONObject(response);
                JSONArray movies = json.getJSONArray("results");
                mPosterURLs = new String[movies.length()];

                for (int i = 0; i < movies.length(); i++) {
                    JSONObject movie = movies.getJSONObject(i);
                    String title = movie.getString("title");

                    String imagePath = movie.getString("poster_path");
                    URL fullPath = NetworkUtils.buildImageURL(imagePath);
                    mPosterURLs[i] = fullPath.toString();

                }

                return mPosterURLs;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            Log.i(KLASS, strings.length + " movies poster found");
            mMovieAdapter.setPosterData(strings);
        }
    }
}
