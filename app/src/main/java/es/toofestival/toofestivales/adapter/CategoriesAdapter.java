package es.toofestival.toofestivales.adapter;

import android.app.Activity;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.util.ArrayList;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.model.Category;


public class CategoriesAdapter extends ArrayAdapter<Category> {

    LayoutInflater flater;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    public CategoriesAdapter(Activity context, int resourceId, int textviewId, ArrayList<Category> list){

        super(context,resourceId,textviewId, list);
        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Category category = getItem(position);

        View rowview = flater.inflate(R.layout.listitems_layout,null,true);

        TextView txtTitle = (TextView) rowview.findViewById(R.id.title);
        txtTitle.setText(category.getName());

        ImageView imageView = (ImageView) rowview.findViewById(R.id.icon);
        Glide.with(imageView.getContext())
                .load(category.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_action_movie)
                .into(imageView);


        return rowview;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = flater.inflate(R.layout.listitems_layout,parent, false);
        }
        Category category= getItem(position);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        txtTitle.setText(category.getName());
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        Glide.with(imageView.getContext())
                .load(category.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_action_movie)
                .into(imageView);
        return convertView;
    }
    /*
    private static class ViewHolder {
        private TextView itemView;
    }

    public CategoriesAdapter(Context context, int textViewResourceId, ArrayList<Category> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.category, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.category_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Category item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.itemView.setText(String.format("%s", item.getName()));
        }

        return convertView;
    }
    */
}