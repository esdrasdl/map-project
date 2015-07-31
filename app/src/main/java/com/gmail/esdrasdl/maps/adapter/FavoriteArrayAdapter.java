package com.gmail.esdrasdl.maps.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gmail.esdrasdl.maps.R;
import com.gmail.esdrasdl.maps.data.FavoriteEntity;

import java.util.List;

/**
 * Created by esdras on 29/07/15.
 */
public class FavoriteArrayAdapter extends ArrayAdapter<FavoriteEntity> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public FavoriteArrayAdapter(Activity context, List<FavoriteEntity> entities) {
        super(context, 0, entities);
        mContext = context;
        mLayoutInflater = ((Activity) mContext).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        FavoriteViewHolder viewHolder;
        if (v == null) {
            v = mLayoutInflater.inflate(R.layout.favorite_list_item, parent, false);
            viewHolder = new FavoriteViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (FavoriteViewHolder) v.getTag();
        }
        viewHolder.apply(getItem(position), position);
        return v;
    }

    @TargetApi(11)
    public void setList(List<FavoriteEntity> favoriteList) {
        clear();
        if (favoriteList != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                addAll(favoriteList);
            } else {
                for (int i = 0; i < favoriteList.size(); i++) {
                    add(favoriteList.get(i));
                }
            }
        }
    }


    private class FavoriteViewHolder {
        TextView name;
        TextView latitude;
        TextView longitude;
        TextView closeButton;

        public FavoriteViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.fav_name);
            latitude = (TextView) view.findViewById(R.id.fav_lat);
            longitude = (TextView) view.findViewById(R.id.fav_lon);
            closeButton = (TextView) view.findViewById(R.id.fav_delete);
        }

        public void apply(FavoriteEntity entity, final int position) {
            name.setText(entity.getName());
            latitude.setText(String.format("%.6f",entity.getLatitude()));
            longitude.setText(String.format("%.6f", entity.getLongitude()));
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(getItem(position));
                }
            });

        }
    }
}
