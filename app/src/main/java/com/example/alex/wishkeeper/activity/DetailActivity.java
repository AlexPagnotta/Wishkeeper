package com.example.alex.wishkeeper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class DetailActivity extends AppCompatActivity {

    TextView textProductsTitle;
    TextView textProductsPrice;
    ImageView imageProductsBackground;
    Product product;
    int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setupViews();

        //Get id of realm, not sure if use this or position from adapter
        Intent intent = getIntent();
        productId = intent.getIntExtra("productId",0);
        Log.d("test", String.valueOf(productId));

        setViewsData();

    }

    private void setupViews(){

        textProductsTitle = (TextView) findViewById(R.id.text_products_title);
        textProductsPrice = (TextView) findViewById(R.id.text_products_price);
        imageProductsBackground = (ImageView) findViewById(R.id.image_products_background);


    }

    private void setViewsData(){

        product = RealmController.with(this).getProductsById(productId);

        textProductsTitle.setText(product.getTitle());
        textProductsPrice.setText(String.valueOf(product.getPrice()+" €"));

        if (product.getImageUrl() != null) {
            Picasso.with(this)
                    .load(product.getImageUrl())
                    .into(imageProductsBackground);
        }

    }
}
