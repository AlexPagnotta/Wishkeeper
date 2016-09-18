package com.example.alex.wishkeeper.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.alex.wishkeeper.R;
import com.example.alex.wishkeeper.activity.DetailActivity;
import com.example.alex.wishkeeper.activity.MainActivity;
import com.example.alex.wishkeeper.model.ParseURL;
import com.example.alex.wishkeeper.model.Product;
import com.example.alex.wishkeeper.realm.RealmController;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Url;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ProductsAdapter extends RealmRecyclerViewAdapter<Product> {

    final Context context;


    public ProductsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        final Product product = getItem(position);
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        holder.textProductsTitle.setText(product.getTitle());
        //.format locale %.21 ecc. is used to display number with two decimal position ex. 20.00
        holder.textProductsPrice.setText(String.format(java.util.Locale.US,"%.2f", product.getPrice())+" €");

        if (product.getImageUrl() != null) {
            Picasso.with(context)
                    .load(product.getImageUrl())
                    .into(holder.imageProductsBackground);
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("productId", product.getId());
                context.startActivity(intent);
            }
        });

    }

    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }



    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView textProductsTitle;
        public TextView textProductsPrice;
        public ImageView imageProductsBackground;

        public CardViewHolder(View itemView) {

            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_products);
            textProductsTitle = (TextView) itemView.findViewById(R.id.text_products_title);
            textProductsPrice = (TextView) itemView.findViewById(R.id.text_products_price);
            imageProductsBackground = (ImageView) itemView.findViewById(R.id.image_products_background);

        }
    }
}