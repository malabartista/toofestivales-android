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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.util.ArrayList;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.model.Country;
import es.toofestival.toofestivales.util.SvgDecoder;
import es.toofestival.toofestivales.util.SvgDrawableTranscoder;
import es.toofestival.toofestivales.util.SvgSoftwareLayerSetter;


public class CountriesAdapter extends ArrayAdapter<Country> {
    LayoutInflater flater;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    public CountriesAdapter(Activity context, int resourceId, int textviewId, ArrayList<Country> list){

        super(context,resourceId,textviewId, list);
        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Country country = getItem(position);

        View rowview = flater.inflate(R.layout.listitems_layout,null,true);

        TextView txtTitle = (TextView) rowview.findViewById(R.id.title);
        txtTitle.setText(country.getName());

        ImageView imageView = (ImageView) rowview.findViewById(R.id.icon);
        /*
        Glide.with(imageView.getContext())
                .load(country.getFlag())
                .centerCrop()
                .into(imageView);
        */

        requestBuilder = Glide.with(imageView.getContext())
                .using(Glide.buildStreamModelLoader(Uri.class, imageView.getContext()), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.drawable.ic_action_movie)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());
        Uri uri = Uri.parse(country.getFlag().replace("\"", ""));
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(imageView);

        return rowview;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = flater.inflate(R.layout.listitems_layout,parent, false);
        }
        Country country = getItem(position);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        txtTitle.setText(country.getName());
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        requestBuilder = Glide.with(imageView.getContext())
                .using(Glide.buildStreamModelLoader(Uri.class, imageView.getContext()), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.drawable.ic_action_movie)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());
        Uri uri = Uri.parse(country.getFlag().replace("\"", ""));
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(imageView);
        return convertView;
    }


    /*
    private static class ViewHolder {
        private TextView itemView;
    }

    public CountriesAdapter(Context context, int textViewResourceId, ArrayList<Country> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.category, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.country_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Country item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.itemView.setText(String.format("%s", item.getName()));
        }

        return convertView;
    }
    */
}