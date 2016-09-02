package com.example.alex.wishkeeper.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.alex.wishkeeper.R;
import com.example.alex.wishkeeper.model.ParseURL;
import com.example.alex.wishkeeper.model.Product;
import com.example.alex.wishkeeper.realm.RealmController;
import com.squareup.picasso.Picasso;
import io.realm.Realm;
import io.realm.RealmResults;

public class ProductsAdapter extends RealmRecyclerViewAdapter<Product> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

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

        realm = RealmController.getInstance().getRealm();

        final Product product = getItem(position);
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        holder.textProductsTitle.setText(product.getTitle());
        holder.textProductsPrice.setText(String.valueOf(product.getPrice()+" €"));

        if (product.getImageUrl() != null) {
            Picasso.with(context)
                    .load(product.getImageUrl())
                    .into(holder.imageProductsBackground);
        }


        holder.buttonProductRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RealmResults<Product> results = realm.where(Product.class).findAll();

                realm.beginTransaction();

                //Remove single match from realm
                results.deleteFromRealm(position);

                realm.commitTransaction();
                notifyDataSetChanged();

            }
        });

        holder.buttonProductEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View content = inflater.inflate(R.layout.edit_product, null);
                final EditText editProductTitle = (EditText) content.findViewById(R.id.edit_product_title);
                final EditText editProductPrice = (EditText) content.findViewById(R.id.edit_product_price);
                final EditText editProductImage = (EditText) content.findViewById(R.id.edit_product_image);
                final EditText editProductUrl = (EditText) content.findViewById(R.id.edit_product_url);
                final Button buttonProductAnalyzeUrl = (Button) content.findViewById(R.id.button_product_analyze_url);

                editProductTitle.setText(product.getTitle());
                editProductPrice.setText(String.valueOf(product.getPrice()));
                editProductImage.setText(product.getImageUrl());
                editProductUrl.setText(product.getBuyUrl());

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(content)
                        .setTitle("Edit Product")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RealmResults<Product> results = realm.where(Product.class).findAll();

                                realm.beginTransaction();
                                results.get(position).setTitle(editProductTitle.getText().toString());
                                results.get(position).setPrice(Float.parseFloat(editProductPrice.getText().toString()));
                                results.get(position).setImageUrl(editProductImage.getText().toString());
                                results.get(position).setBuyUrl(editProductUrl.getText().toString());

                                realm.commitTransaction();

                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                //Parse data from the url
                buttonProductAnalyzeUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url=editProductUrl.getText().toString();
                        ( new ParseURL(content) ).execute(new String[]{url});

                    }
                });


            }
        });

        //Open product's url in broswer
        holder.buttonProductOpen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RealmResults<Product> results = realm.where(Product.class).findAll();
                String url = results.get(position).getBuyUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
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
        public Button buttonProductEdit;
        public Button buttonProductRemove;
        public Button buttonProductOpen;

        public CardViewHolder(View itemView) {

            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_products);
            textProductsTitle = (TextView) itemView.findViewById(R.id.text_products_title);
            textProductsPrice = (TextView) itemView.findViewById(R.id.text_products_price);
            imageProductsBackground = (ImageView) itemView.findViewById(R.id.image_products_background);
            buttonProductEdit= (Button) itemView.findViewById(R.id.button_product_edit);
            buttonProductRemove= (Button) itemView.findViewById(R.id.button_product_remove);
            buttonProductOpen= (Button) itemView.findViewById(R.id.button_product_open);

            //TODO: Move these buttons in another activity
            buttonProductEdit.setVisibility(View.GONE);
            buttonProductRemove.setVisibility(View.GONE);
            buttonProductOpen.setVisibility(View.GONE);
        }
    }
}