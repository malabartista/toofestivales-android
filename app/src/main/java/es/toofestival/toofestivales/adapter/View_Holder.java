package es.toofestival.toofestivales.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.util.Config;

public class View_Holder extends RecyclerView.ViewHolder {

    public CardView cv;
    TextView title;
    TextView categories;
    TextView date;
    TextView location;
    TextView views;
    NetworkImageView imageView;

    public View_Holder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        title = (TextView) itemView.findViewById(R.id.title);
        imageView = (NetworkImageView) itemView.findViewById(R.id.imageView);
        date = (TextView) itemView.findViewById(R.id.date);
        views = (TextView) itemView.findViewById(R.id.views);
        if(Config.isProductViewAsList) {
            location = (TextView) itemView.findViewById(R.id.location);
            categories = (TextView) itemView.findViewById(R.id.categories);
        }
    }

    public void bindView(int position) {
        // bindView() method to implement actions
    }


};
