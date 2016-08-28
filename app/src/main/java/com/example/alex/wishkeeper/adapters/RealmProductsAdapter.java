package com.example.alex.wishkeeper.adapters;

import android.content.Context;

import com.example.alex.wishkeeper.model.Product;
import io.realm.RealmResults;

public class RealmProductsAdapter extends RealmModelAdapter<Product> {

    public RealmProductsAdapter(Context context, RealmResults<Product> realmResults) {

        super(context, realmResults);
    }
}