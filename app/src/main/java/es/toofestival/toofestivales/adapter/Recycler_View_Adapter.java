package es.toofestival.toofestivales.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.toolbox.ImageLoader;

import java.util.Collections;
import java.util.List;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.app.AppController;
import es.toofestival.toofestivales.model.Post;
import es.toofestival.toofestivales.util.Config;

public abstract class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> {

    List<Post> list = Collections.emptyList();
    Context context;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    // Two view types which will be used to determine whether a row should be displaying
    // data or a Progressbar
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ACTIVITY = -1;
    private static final int FOOTER_VIEW = 1;
    // the serverListSize is the total number of items on the server side,
    // which should be returned from the web request results
    protected int serverListSize = 1;

    protected Activity mActivity;
    //private OnItemClickListener mListener;

    public Recycler_View_Adapter(Activity mActivity, List<Post> list, Context context) {
        this.mActivity = mActivity;
        this.list = list;
        this.context = context;
    }

    public void setServerListSize(int serverListSize){
        this.serverListSize = serverListSize;
    }
    /*
    public interface OnItemClickListener {
        void onItemClick(Post post);
    }
    */

    // Define a view holder for Footer view
    public class FooterViewHolder extends View_Holder {
        public FooterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Do whatever you want on clicking the item
                }
            });
        }
    }

    // Now define the viewholder for Normal list item
    public class NormalViewHolder extends View_Holder {
        public NormalViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(Config.isProductViewAsList ? R.layout.row_layout_list : R.layout.row_layout_grid, null);
        View_Holder holder = new View_Holder(v);
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_list, parent, false);
        //View_Holder holder = new View_Holder(v);
        return holder;
        */
        View v;
        if (viewType == VIEW_TYPE_LOADING) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress, parent, false);
            FooterViewHolder vh = new FooterViewHolder(v);
            return vh;
        }

        v = LayoutInflater.from(parent.getContext()).inflate(Config.isProductViewAsList ? R.layout.row_layout_list : R.layout.row_layout_grid, null);
        NormalViewHolder vh = new NormalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder,final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        /*
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        holder.cv.setTag(position);
        holder.imageView.setImageUrl(list.get(position).getThumbnailUrl(),imageLoader);
        holder.title.setText(list.get(position).getTitle());
        holder.date.setText(list.get(position).getDate());
        if (Config.isProductViewAsList) {
            holder.location.setText(list.get(position).getLocation_town() + ", " + list.get(position).getLocation_country());
            String catStr = "";
            if (list.get(position).getCategories() != null) {
                for (String str : list.get(position).getCategories()) {
                    catStr += str + ", ";
                }
            }
            catStr = catStr.length() > 0 ? catStr.substring(0,
                    catStr.length() - 2) : catStr;
            holder.categories.setText(catStr);
        }
        */
        try {
            if (holder instanceof NormalViewHolder) {
                NormalViewHolder vh = (NormalViewHolder) holder;
                vh.bindView(position);

                if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();
                holder.cv.setTag(position);
                holder.imageView.setImageUrl(list.get(position).getThumbnailUrl(),imageLoader);
                holder.title.setText(list.get(position).getTitle());
                holder.date.setText(list.get(position).getDate());
                if (Config.isProductViewAsList) {
                    holder.location.setText(list.get(position).getLocation_town() + ", " + list.get(position).getLocation_country());
                    String catStr = "";
                    if (list.get(position).getCategories() != null) {
                        for (String str : list.get(position).getCategories()) {
                            catStr += str + ", ";
                        }
                    }
                    catStr = catStr.length() > 0 ? catStr.substring(0,
                            catStr.length() - 2) : catStr;
                    holder.categories.setText(catStr);
                    holder.views.setText(list.get(position).getViewCount());
                }
            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder vh = (FooterViewHolder) holder;
                //holder.title.setText("Loading more festivals...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(list.get(position));
            }
        });
        */
        //animate(holder);
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        if (list == null) {
            return 0;
        }

        if (list.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }

        // Add extra view to show the footer view
        return list.size() + 1;

        //return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, Post data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(Post data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    public void swap(List<Post> datas){
        list.clear();
        list.addAll(datas);
        notifyDataSetChanged();
    }


    /**
     * disable click events on indicating rows
     */
    public boolean isEnabled(int position) {

        return getItemViewType(position) == VIEW_TYPE_ACTIVITY;
    }

    /**
     * One type is normal data row, the other type is Progressbar
     */
    public int getViewTypeCount() {
        return 2;
    }


    /**
     * the size of the List plus one, the one is the last row, which displays a Progressbar
     */
    public int getCount() {
        return list.size() + 1;
    }

    /**
     * return the type of the row,
     * the last row indicates the user that the ListView is loading more data
     */
    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return (position >= list.size()) ? VIEW_TYPE_LOADING
                : VIEW_TYPE_ACTIVITY;
    }


    public Post getItem(int position) {
        // TODO Auto-generated method stub
        return (getItemViewType(position) == VIEW_TYPE_ACTIVITY) ? list
                .get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return (getItemViewType(position) == VIEW_TYPE_ACTIVITY) ? position
                : -1;
    }

    /**
     *  returns the correct view
     */
    public  View getView(int position, View convertView, ViewGroup parent){
        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            // display the last row
            return getFooterView(position, convertView, parent);
        }
        View dataRow = convertView;
        dataRow = getDataRow(position, convertView, parent);

        return dataRow;
    };

    /**
     * A subclass should override this method to supply the data row.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public abstract View getDataRow(int position, View convertView, ViewGroup parent);

    /**
     * returns a View to be displayed in the last row.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getFooterView(int position, View convertView,
                              ViewGroup parent) {
        if (position >= serverListSize && serverListSize > 0) {
            // the ListView has reached the last row
            View row = (View) mActivity.findViewById(R.id.progress);
            return row;
        }

        View row = convertView;
        if (row == null) {
            row = mActivity.getLayoutInflater().inflate(
                    R.layout.progress, parent, false);
        }

        return row;
    }

}