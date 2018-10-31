package com.medialab.civiclink;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GroupAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private ArrayList<String> web;
    private int[] Imageid;

    ArrayList<String> modelValues = new ArrayList<>();

    public GroupAdapter(Context c, ArrayList<String> web, int[] Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.size();
    }

    public void setWeb(ArrayList<String> web){
        this.web = web;
        int[] imageId2 = new int[web.size()];
        for(int j=0;j<imageId2.length;j++) {
            imageId2[j] = R.drawable.groups;
        }
        this.Imageid = imageId2;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = inflater.inflate(R.layout.custom_groups, null);

        } else {
            grid = (View) convertView;
        }

        TextView textView = (TextView) grid.findViewById(R.id.grouptext);
        ImageView imageView = (ImageView) grid.findViewById(R.id.groupimg);
        textView.setText(web.get(position));
        imageView.setImageResource(Imageid[position]);

        return grid;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                web = (ArrayList<String>) results.values; // has

                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults(); // Holds the
                // results of a
                // filtering
                // operation in
                // values
                // List<String> FilteredArrList = new ArrayList<String>();
                List<String> FilteredArrList = new ArrayList<>();

                /*if (web == null) {
                    web = new ArrayList<>(modelValues); // saves

                }*/

                /********
                 *
                 * If constraint(CharSequence that is received) is null returns
                 * the mOriginalValues(Original) values else does the Filtering
                 * and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = web.size();
                    results.values = web;
                } else {
                    Locale locale = Locale.getDefault();
                    constraint = constraint.toString().toLowerCase(locale);
                    for (int i = 0; i < web.size(); i++) {
                        String model = web.get(i);

                        //String data = model.get_NAME();
                        if (model.toLowerCase(locale).contains(constraint.toString())) {

                            FilteredArrList.add(model);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

                }
                return results;
            }
        };
        return filter;
    }
}