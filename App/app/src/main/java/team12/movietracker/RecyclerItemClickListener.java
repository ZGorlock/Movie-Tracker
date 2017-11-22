package team12.movietracker;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by estor on 11/22/2017.
 */

class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);


    }

    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener listener)
    {
        mListener = listener;
        mGestureDetector = null;
    }

    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
    {
        return super.onInterceptTouchEvent(rv, e);
    }
}
