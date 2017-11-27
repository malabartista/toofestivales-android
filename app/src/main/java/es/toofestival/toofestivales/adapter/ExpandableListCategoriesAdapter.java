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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.model.Category;

public class ExpandableListCategoriesAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, ArrayList<Category>> expandableListDetail;
    public Set<Pair<Long, Long>> mCheckedItems = new HashSet<Pair<Long, Long>>();
    public int ITEMS_CHECKED = 0;

    public ExpandableListCategoriesAdapter(Context context, List<String> expandableListTitle,
                                           HashMap<String, ArrayList<Category>> expandableListDetail) {
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
        final Category expandedListText = (Category) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category, null);
        }
        CheckedTextView expandedListTextView = (CheckedTextView) convertView
                .findViewById(R.id.category_name);
        expandedListTextView.setText(expandedListText.getName());
        final Pair<Long, Long> tag = new Pair<Long, Long>(
                getGroupId(listPosition),
                getChildId(listPosition, expandedListPosition));
        expandedListTextView.setTag(tag);
        expandedListTextView.setChecked(((Category) getChild(listPosition, expandedListPosition)).getSelected() == "1");
        // set checked if groupId/childId in checked items
        //expandedListTextView.setChecked(mCheckedItems.contains(tag));
        //expandedListTextView.setChecked(list.get(listPosition).isSelected());
        return convertView;
    }

    public Set<Pair<Long, Long>> getCheckedItems() {
        return mCheckedItems;
    }
    public void setCheckedItems(Set<Pair<Long, Long>> mCheckedItems) {
        this.mCheckedItems = mCheckedItems;
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
            convertView = layoutInflater.inflate(R.layout.list_categories, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listCategoriesTitle);
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