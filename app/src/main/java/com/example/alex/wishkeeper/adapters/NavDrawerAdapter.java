package com.example.alex.wishkeeper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.wishkeeper.R;

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder>{

    private String titles[];

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;


    public NavDrawerAdapter(String titles[]){
        this.titles = titles;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER){

            View headerRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_header, parent , false);
            ViewHolder headerHolder = new ViewHolder(headerRootView , TYPE_HEADER);
            return headerHolder;

        } else if (viewType == TYPE_ITEM){

            View itemRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_item , parent , false);
            ViewHolder itemHolder = new ViewHolder(itemRootView , TYPE_ITEM);
            return itemHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.holderID == TYPE_HEADER){

            holder.navDrawerHeader.setImageResource(R.mipmap.ic_launcher);

        }else {
            holder.textParentItem.setText(titles[position-1]);
        }

    }

    @Override
    public int getItemCount() {
        return titles.length +1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_HEADER)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textParentItem;
        ImageView navDrawerHeader;

        int holderID;

        public ViewHolder(View itemView , int viewType) {
            super(itemView);

            if (viewType == TYPE_HEADER){

                navDrawerHeader = (ImageView) itemView.findViewById(R.id.nav_drawer_header);
                holderID = TYPE_HEADER;

            } else if (viewType == TYPE_ITEM){

                textParentItem = (TextView) itemView.findViewById(R.id.text_parent_item);
                holderID = TYPE_ITEM;

            }

        }
    }
}