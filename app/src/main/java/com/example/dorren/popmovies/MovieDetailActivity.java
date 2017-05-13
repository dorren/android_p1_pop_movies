package com.example.dorren.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by dorrenchen on 5/12/17.
 */

public class MovieDetailActivity extends AppCompatActivity {
    private TextView mDetailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                String url = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mDetailText = (TextView) findViewById(R.id.movie_detail);
                mDetailText.setText(url);
            }
        }
    }
}
