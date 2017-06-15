package com.example.dorren.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

/**
 * Created by dorrenchen on 4/29/17.
 *
 * RecyclerView adapter used in MainActivity to show all movie posters. data could come from
 * moviedb api or favorites content provider.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private static final String KLASS = MovieAdapter.class.getSimpleName();
    private MoviePoster[] mPosters;
    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(MoviePoster poster);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

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
        String url = mPosters[position].imagePath;
        Picasso.with(context).load(url).into(posterView);

    }

    @Override
    public int getItemCount() {
        if(mPosters == null) {
            return 0;
        }else {
            return mPosters.length;
        }
    }

    public void setPosterData(MoviePoster[] posters) {
        mPosters = posters;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView mMoviePosterView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMoviePosterView = (ImageView) view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int n = getAdapterPosition();
            MoviePoster poster = mPosters[n];
            mClickHandler.onClick(poster);

        }
    }
}
