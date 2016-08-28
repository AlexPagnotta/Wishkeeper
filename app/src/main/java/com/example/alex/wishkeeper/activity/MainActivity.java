package com.example.alex.wishkeeper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.alex.wishkeeper.R;
import com.example.alex.wishkeeper.adapters.ProductsAdapter;
import com.example.alex.wishkeeper.adapters.RealmProductsAdapter;
import com.example.alex.wishkeeper.model.ParseURL;
import com.example.alex.wishkeeper.model.Product;
import com.example.alex.wishkeeper.realm.RealmController;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProductsAdapter adapter;
    private Realm realm;
    private FloatingActionButton fab;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();

        manageClicks();

        manageIntents();

        setRealmAdapter(RealmController.with(this).getProducts());


    }

    private void setupViews(){

        //Setup Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup Fab
        fab= (FloatingActionButton) findViewById(R.id.fab);

        //Setup Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ProductsAdapter(this);
        recyclerView.setAdapter(adapter);

        //Setup Realm
        this.realm = RealmController.with(this).getRealm();

    }


    public void setRealmAdapter(RealmResults<Product> Products) {

        //Refresh the realm instance
        RealmChangeListener realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                RealmController.with(MainActivity.this).refresh();
            }
        };

        RealmProductsAdapter realmAdapter = new RealmProductsAdapter(this.getApplicationContext(), Products);

        //Set the data and refresh recycler view
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void manageClicks(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharedUrl=null;
                addProduct(sharedUrl);

            }

        });
    }

    private void manageIntents(){


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        if (Intent.ACTION_SEND.equals(action) && type != null) {

            //Handle intents from other apps
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            String sharedUrl= String.valueOf(extractLink(sharedText).get(0));
            Log.d("intent",sharedText);
            Log.d("intent",sharedUrl);
            addProduct(sharedUrl);
        }
        else {
            // Handle other intents
        }

    }

    private ArrayList extractLink(String sharedText) {

        //Extract links from a string

        ArrayList links = new ArrayList();
        String regex = "\\(?\\b(https://|http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(sharedText);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }


    public void addProduct(String sharedUrl){

        //Inflate and setup dialog and dialog views
        inflater = MainActivity.this.getLayoutInflater();
        final View content = inflater.inflate(R.layout.edit_product, null);
        final EditText editProductTitle = (EditText) content.findViewById(R.id.edit_product_title);
        final EditText editProductPrice = (EditText) content.findViewById(R.id.edit_product_price);
        final EditText editProductImage = (EditText) content.findViewById(R.id.edit_product_image);
        final EditText editProductUrl = (EditText) content.findViewById(R.id.edit_product_url);
        final Button buttonProductAnalyzeUrl = (Button) content.findViewById(R.id.button_product_analyze_url);

        if (sharedUrl != null && !sharedUrl.isEmpty()) {
            editProductUrl.setText(sharedUrl);
        }

        //Create Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(content)
                .setTitle("Add product")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Set data from textviews to Product Objects
                        Product product = new Product();
                        product.setId(RealmController.getInstance().getProducts().size() + (int) System.currentTimeMillis());
                        product.setTitle(editProductTitle.getText().toString());
                        product.setPrice(Float.parseFloat(editProductPrice.getText().toString()));
                        product.setImageUrl(editProductImage.getText().toString());
                        product.setBuyUrl(editProductUrl.getText().toString());

                        if (editProductTitle.getText() == null || editProductTitle.getText().toString().equals("") || editProductTitle.getText().toString().equals(" ")) {
                            Toast.makeText(MainActivity.this, "Entry not saved, missing title", Toast.LENGTH_SHORT).show();
                        } else {

                            //Put data in realm
                            realm.beginTransaction();
                            realm.copyToRealm(product);
                            realm.commitTransaction();

                            adapter.notifyDataSetChanged();

                            //Scroll the recycler view to bottom
                            recyclerView.scrollToPosition(RealmController.getInstance().getProducts().size() - 1);
                        }
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


}
