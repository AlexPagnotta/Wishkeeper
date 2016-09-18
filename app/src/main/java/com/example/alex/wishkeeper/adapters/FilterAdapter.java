package com.example.alex.wishkeeper.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alex.wishkeeper.R;
import com.example.alex.wishkeeper.activity.DetailActivity;
import com.example.alex.wishkeeper.activity.MainActivity;
import com.example.alex.wishkeeper.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.CardViewHolder> {

     private Context context;
    private ArrayList<String> itemList;
    private String type;


    public FilterAdapter(Context context,ArrayList<String> itemList,String type) {
        this.context = context;
        this.itemList = itemList;
        this.type=type;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);

        return new CardViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        holder.textFilter.setText(itemList.get(position));
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                Bundle extras = new Bundle();
                extras.putString("filterValue", holder.textFilter.getText().toString());
                extras.putString("filterType", type);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout itemLayout;
        public TextView textFilter;


        public CardViewHolder(View itemView) {

            super(itemView);

            itemLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
            textFilter = (TextView) itemView.findViewById(R.id.text_filter);


        }
    }
}