package team12.movietracker;


    import android.content.Context;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import java.io.BufferedInputStream;
    import java.io.BufferedReader;
    import java.io.FileInputStream;
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.util.Base64;
    import java.util.List;

    import client.pojo.Media;
    import client.server.ServerHandler;

/**
 * Created by estor on 11/22/2017.
 */

class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.MovieImageViewHolder> {


    private List<Integer> mSubscriptions;
    private Context mContext;

    public SearchRecyclerViewAdapter(Context context, List<Integer> subscriptionsList) {
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
//        Integer subItem = mSubscriptions.get(position);
//        //Picasso.with(mContext).load(subItem.getImage(subItem));
//        Media queryMedia = new Media();
//        queryMedia.setMediaId(subItem);
//        List<Integer> currentMedia = ServerHandler.queryMedia(queryMedia);
        Media retrievedMedia = ServerHandler.retrieveMedia(mSubscriptions.get(position),mContext);
        holder.title.setText(retrievedMedia.getTitle());
        holder.showTime.setText(retrievedMedia.getShowtimes());
        try{
            String filePath = retrievedMedia.getImage().getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            holder.poster.setImageBitmap(bitmap);
        }
        catch(Exception e)
        {
            holder.poster.setImageResource(R.drawable.ic_dashboard_black_24dp);
        }


    }

    @Override
    public int getItemCount() {
        return ((mSubscriptions != null) && (mSubscriptions.size() !=0) ? mSubscriptions.size() : 0);
    }

    public int getSub(int position)
    {
        return ((mSubscriptions != null) && (mSubscriptions.size() != 0) ? (mSubscriptions.get(position)):null );
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

