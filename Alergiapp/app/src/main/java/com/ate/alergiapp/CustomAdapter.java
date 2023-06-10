package com.ate.alergiapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<CustomObject> {

    public CustomAdapter(Context context, List<CustomObject> objects) {
        super(context, -1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_view_item, null);
            viewHolder = new ViewHolder();
            viewHolder.setView(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mName.setText(getItem(position).getName());
        viewHolder.mAddress.setText(getItem(position).getAddress());
        viewHolder.mPagWeb.setText(getItem(position).getPagWeb());
        viewHolder.mRating.setText(getItem(position).getRating());

        return view;
    }

    private class ViewHolder {

        public TextView mName;
        public TextView mAddress;
        public TextView mPagWeb;
        public TextView mRating;

        public void setView(View view) {
            mName = (TextView) view.findViewById(R.id.textView);
            mAddress = (TextView) view.findViewById(R.id.textView2);
            mPagWeb = (TextView) view.findViewById(R.id.textView3);
            mRating = (TextView) view.findViewById(R.id.textView4);
            view.setTag(this);
        }

    }
}