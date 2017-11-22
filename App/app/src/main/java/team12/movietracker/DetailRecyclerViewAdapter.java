package team12.movietracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import client.pojo.Media;
import client.server.ServerHandler;

/**
 * Created by estor on 11/22/2017.
 */

class DetailRecyclerViewAdapter extends RecyclerView.Adapter<DetailRecyclerViewAdapter.MovieImageViewHolder> {


    private List<Integer> mSubscriptions;
    private Context mContext;

    public DetailRecyclerViewAdapter(Context context, List<Integer> subscriptionsList) {
        mContext = context;
        mSubscriptions = subscriptionsList;


    }

    @Override
    public MovieImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new MovieImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieImageViewHolder holder, int position) {
        Integer subItem = mSubscriptions.get(position);
        //Picasso.with(mContext).load(subItem.getImage(subItem));
        System.out.println("Adapter "+subItem);
//        ServerHandler.setupServerHandler();
        Media queryMedia = new Media();
        queryMedia.setMediaId(subItem);
        List<Integer> currentMedia = ServerHandler.queryMedia(queryMedia);
        Media retrievedMedia = ServerHandler.retrieveMedia(currentMedia.get(position));
        holder.title.setText(retrievedMedia.getTitle());
        holder.showTime.setText(retrievedMedia.getShowtimes());


    }

    @Override
    public int getItemCount() {
        return ((mSubscriptions != null) && (mSubscriptions.size() !=0) ? mSubscriptions.size() : 0);
    }

    void loadNewData(List<Integer> newSubscriptions)
    {
        mSubscriptions = newSubscriptions;
        notifyDataSetChanged();
    }


    static class MovieImageViewHolder extends RecyclerView.ViewHolder {
        ImageView poster = null;
        TextView title = null;
        TextView showTime = null;


        public MovieImageViewHolder(View itemView){
            super(itemView);
            this.poster = (ImageView) itemView.findViewById(R.id.posterBrowse);
            this.title = (TextView) itemView.findViewById(R.id.titleBrowse);
            this.showTime = (TextView) itemView.findViewById(R.id.showtimeBrowse);
        }
    }
}
