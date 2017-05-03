package com.example.dorren.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

/**
 * Created by dorrenchen on 4/29/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private String[] mPosterURLs;

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        ImageView posterView = holder.mMoviePosterView;
        Context context = posterView.getContext();
        String url = mPosterURLs[position];
        Picasso.with(context).load(url).into(posterView);

    }

    @Override
    public int getItemCount() {
        if(mPosterURLs == null) {
            return 0;
        }else {
            return mPosterURLs.length;
        }
    }

    public void setPosterData(String[] urls) {
        mPosterURLs = urls;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder{
        public final ImageView mMoviePosterView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMoviePosterView = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }
}
