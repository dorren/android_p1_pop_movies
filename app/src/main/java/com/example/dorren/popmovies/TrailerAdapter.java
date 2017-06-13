package com.example.dorren.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dorrenchen on 6/12/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private static final String KLASS = MovieAdapter.class.getSimpleName();
    private MovieTrailer[] mTrailers;
    private final TrailerOnClickHandler mClickHandler;

    public interface TrailerOnClickHandler {
        void onClick(MovieTrailer trailer);
    }

    public TrailerAdapter(TrailerOnClickHandler handler){
        mClickHandler = handler;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        int n = position + 1;
        holder.mTitle.setText("Trailer " + n);
    }

    @Override
    public int getItemCount() {
        if (mTrailers == null){
            return 0;
        } else {
            return mTrailers.length;
        }
    }

    public void setData(MovieTrailer[] trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView mPlayBtn;
        public final TextView mTitle;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mPlayBtn = (ImageView) itemView.findViewById(R.id.play_button);
            mPlayBtn.setOnClickListener(this);
            mTitle = (TextView) itemView.findViewById(R.id.trailer_text);
        }

        @Override
        public void onClick(View view) {
            int n = getAdapterPosition();
            MovieTrailer trailer = mTrailers[n];
            mClickHandler.onClick(trailer);
        }
    }
}
