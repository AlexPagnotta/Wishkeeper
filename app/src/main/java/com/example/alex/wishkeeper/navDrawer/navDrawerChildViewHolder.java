package com.example.alex.wishkeeper.navDrawer;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.example.alex.wishkeeper.R;

public class NavDrawerChildViewHolder extends ChildViewHolder {

    public TextView textChildDrawerTitle;
    public RelativeLayout layoutChildItem;


    public NavDrawerChildViewHolder(View itemView) {
        super(itemView);

        textChildDrawerTitle = (TextView) itemView.findViewById(R.id.text_child_item);
        layoutChildItem = (RelativeLayout) itemView.findViewById(R.id.layout_child_item);
    }

    public void bind(NavDrawerChildObject childObject) {
        textChildDrawerTitle.setText(childObject.getName());
    }
}
