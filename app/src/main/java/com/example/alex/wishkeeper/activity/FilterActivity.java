package com.example.alex.wishkeeper.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.alex.wishkeeper.R;
import com.example.alex.wishkeeper.adapters.FilterAdapter;
import com.example.alex.wishkeeper.adapters.ProductsAdapter;
import com.example.alex.wishkeeper.adapters.RealmProductsAdapter;
import com.example.alex.wishkeeper.model.Product;
import com.example.alex.wishkeeper.realm.RealmController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class FilterActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategory;
    private RecyclerView recyclerViewStore;
    private RecyclerView recyclerViewSort;
    private GridLayoutManager layoutManager;
    private FilterAdapter adapter;
    private Realm realm;
    private ArrayList<String> categoryList=new ArrayList();
    private ArrayList<String> storeList=new ArrayList();
    private ArrayList<String> orderList=new ArrayList();
    private Button resetButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //Setup Realm
        this.realm = RealmController.with(this).getRealm();

        setupViews();
        loadRecyclerViews();
        manageViews();
    }

    private void setupViews(){

        //Setup Recycler View
        recyclerViewCategory = (RecyclerView) findViewById(R.id.recyclerViewCategory);
        recyclerViewStore = (RecyclerView) findViewById(R.id.recyclerViewStore);
        recyclerViewSort = (RecyclerView) findViewById(R.id.recyclerViewSort);

        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStore.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSort.setLayoutManager(new LinearLayoutManager(this));

        //Other Views
        resetButton = (Button) findViewById(R.id.reset_button);


    }

    private void loadRecyclerViews(){

        //Put data from realm in an array
        for (int i=0;i<realm.where(Product.class).findAll().size();i++) {
            categoryList.add(realm.where(Product.class).findAll().get(i).getCategory());
        }

        //Remove Duplicates
        Set<String> set = new HashSet<>();
        set.addAll(categoryList);
        categoryList.clear();
        categoryList.addAll(set);

        //Put data in the recycler view
        adapter = new FilterAdapter(this,categoryList,"category");
        recyclerViewCategory.setAdapter(adapter);
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setNestedScrollingEnabled(false);

        //Second Recycler View
        for (int i=0;i<realm.where(Product.class).findAll().size();i++) {
            storeList.add(realm.where(Product.class).findAll().get(i).getStore());
        }

        set = new HashSet<>();
        set.addAll(storeList);
        storeList.clear();
        storeList.addAll(set);

        adapter = new FilterAdapter(this,storeList,"store");
        recyclerViewStore.setAdapter(adapter);
        recyclerViewStore.setHasFixedSize(true);
        recyclerViewStore.setNestedScrollingEnabled(false);

        //Third Recycler View
        orderList.add("Name (Ascendent)");
        orderList.add("Name (Descendent)");
        orderList.add("Price (Ascendent)");
        orderList.add("Price (Descendent)");
        adapter = new FilterAdapter(this,orderList,"sort");
        recyclerViewSort.setAdapter(adapter);
        recyclerViewSort.setHasFixedSize(true);
        recyclerViewSort.setNestedScrollingEnabled(false);
    }

    private void manageViews(){
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset all filters and sort
                Intent intent = new Intent(FilterActivity.this, MainActivity.class);
                FilterActivity.this.startActivity(intent);
            }

        });

    }

}
