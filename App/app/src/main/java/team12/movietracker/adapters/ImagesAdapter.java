package team12.movietracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;



import java.util.List;

import client.pojo.Media;
import team12.movietracker.R;

/**
 * Created by Umar Waqas on 26/11/2017.
 */

/**
 * For More Apps, please contact
 * https://www.fiverr.com/umarwaqas20
 * Email:umarwaqas2010@gmail.com
 * https://www.upwork.com/o/profiles/users/_~01ef1ceb301c094ec2/
 * @author umar waqas
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<Media> mParents;
    private Context mContext;

    public ImagesAdapter(Context mContext) {

        this.mContext = mContext;
    }

    public ImagesAdapter(List<Media> mParents, Context mContext) {
        this.mParents = mParents;
        this.mContext = mContext;
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.rv_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        Media currentParent = mParents.get(position);

        ImageView parentImage = holder.parentImage;

    }

    @Override
    public int getItemCount() {
        return 6;//mParents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        ImageView parentImage;

        public ViewHolder(View itemView) {
            super(itemView);

            parentImage = (ImageView) itemView.findViewById(R.id.iv_test_image);

        }
    }
}
