package com.example.dorren.popmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.example.dorren.popmovies.models.FavoritesProvider;
import com.example.dorren.popmovies.models.Movie;
import com.example.dorren.popmovies.models.MoviesDbHelper;
import com.example.dorren.popmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<MoviePoster[]> {
    private static final String KLASS = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorText;
    private ProgressBar mSpinner;
    private String mSort;
    private final String SORT_KEY = "sort";
    private MoviesDbHelper mDbHelper;
    private static final int CURSOR_LOADER_ID = 0;

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

        mDbHelper = new MoviesDbHelper(this);
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    public String getSort(){
        if(mSort == null) {
            mSort = NetworkUtils.SORT_POPULAR;
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mSort = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        return mSort;
    }

    @Override
    protected void onResume() {
        Log.d(KLASS, "onResume");
        showSpinner();
        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_KEY, mSort);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mSort = savedInstanceState.getString(SORT_KEY, NetworkUtils.SORT_POPULAR);
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

        Intent intent = new Intent(this, this.getClass());

        if(itemId == R.id.action_popular){
            intent.putExtra(Intent.EXTRA_TEXT, NetworkUtils.SORT_POPULAR);
        }

        if(itemId == R.id.action_top_rated){
            intent.putExtra(Intent.EXTRA_TEXT, NetworkUtils.SORT_TOP_RATED);
        }

        if(itemId == R.id.action_favorites){
            intent.putExtra(Intent.EXTRA_TEXT, NetworkUtils.SORT_FAVORITE);
        }
        startActivity(intent);

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

    @Override
    public Loader<MoviePoster[]> onCreateLoader(int id, Bundle args) {
        Log.d(KLASS, "onCreateLoader");
        NetworkUtils.KEY = getString(R.string.themoviedb_api_key);

        return new MoviesLoader(this, getSort());
    }

    @Override
    public void onLoadFinished(Loader<MoviePoster[]> loader, MoviePoster[] data) {
        Log.d(KLASS, "load finished ");
        hideSpinner();

        MoviesLoader mloader = (MoviesLoader) loader;
        String errMsg = mloader.getErrorMsg();
        if(errMsg == null) {
            mMovieAdapter.setPosterData(data);
        }else{
            renderError(errMsg);
        }
    }

    @Override
    public void onLoaderReset(Loader<MoviePoster[]> loader) {
        Log.d(KLASS, "onLoaderReset");
        loader.forceLoad();
    }


    public void showSpinner(){
        mSpinner.setVisibility(View.VISIBLE);
    }

    public void hideSpinner(){
        mSpinner.setVisibility(View.INVISIBLE);
    }
}
