package es.toofestival.toofestivales.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.model.Country;

public class ExpandableListCountriesAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, ArrayList<Country>> expandableListDetail;
    public int ITEMS_CHECKED = 0;

    public ExpandableListCountriesAdapter(Context context, List<String> expandableListTitle,
                                          HashMap<String, ArrayList<Country>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Country expandedListText = (Country) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.country, null);
        }
        CheckedTextView expandedListTextView = (CheckedTextView) convertView
                .findViewById(R.id.country_name);
        expandedListTextView.setText(expandedListText.getName());
        final Pair<Long, Long> tag = new Pair<Long, Long>(
                getGroupId(listPosition),
                getChildId(listPosition, expandedListPosition));
        expandedListTextView.setTag(tag);
        expandedListTextView.setChecked(((Country) getChild(listPosition, expandedListPosition)).getSelected() == "1");
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_countries, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listCountriesTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        TextView childCounterView = (TextView) convertView
                .findViewById(R.id.childCounter);
        if(childCounterView!=null) {
            if (ITEMS_CHECKED > 0) {
                childCounterView.setText(String.valueOf(ITEMS_CHECKED));
                childCounterView.setBackgroundResource(R.drawable.bg_grey);
            } else {
                childCounterView.setText("");
                childCounterView.setBackgroundResource(R.drawable.bg_white);
            }
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}