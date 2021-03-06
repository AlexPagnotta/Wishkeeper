package com.example.alex.wishkeeper.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.alex.wishkeeper.R;
import com.example.alex.wishkeeper.adapters.ProductsAdapter;
import com.example.alex.wishkeeper.adapters.RealmProductsAdapter;
import com.example.alex.wishkeeper.model.ParseURL;
import com.example.alex.wishkeeper.model.Product;
import com.example.alex.wishkeeper.realm.RealmController;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Url;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private ProductsAdapter adapter;
    private Realm realm;
    private FloatingActionButton fab;
    private LayoutInflater inflater;
    private AlertDialog dialog;
    private Validator validator;
    private SearchView searchView;

    @NotEmpty
     EditText editProductTitle;
    @NotEmpty
     EditText editProductPrice ;
    @NotEmpty
    EditText editProductStore ;
    @Url
    @NotEmpty
     EditText editProductImage;
    @Url
    @NotEmpty
     EditText editProductUrl;
    Spinner spCategory;

    TextInputLayout inputLayoutProductUrl;
    Button buttonProductAnalyzeUrl;
    RelativeLayout loadingCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start Realm
        this.realm = RealmController.with(this).getRealm();

        setupViews();

        manageViews();

        setRealmAdapter(RealmController.with(this).getProducts());

        manageIntents();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Search View
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Need to stay here, doesn'twork in other methods
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setRealmAdapter(RealmController.with(MainActivity.this).searchProducts(query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setRealmAdapter(RealmController.with(MainActivity.this).searchProducts(newText));
                return true;
            }
        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (item.getItemId() == R.id.action_filter) {
            Intent intent= new Intent(MainActivity.this,FilterActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViews(){

        //Setup Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Setup Fab
        fab= (FloatingActionButton) findViewById(R.id.fab);

        //Setup Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ProductsAdapter(this);
        recyclerView.setAdapter(adapter);

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

    private void manageViews(){

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

        Log.d("action","" + action);
        Log.d("actiontype","" + type);

        if (Intent.ACTION_SEND.equals(action) && type != null) {

            //Handle intents from other apps
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            String sharedUrl= String.valueOf(extractLink(sharedText).get(0));
            Log.d("intent",sharedText);
            Log.d("intent",sharedUrl);
            addProduct(sharedUrl);
        }

        else if(action == null && type==null){
            //Handle intents from this app

            //Get Intent from filter Activity, and sort products by the data passed
            String filterValue = getIntent().getStringExtra("filterValue");
            String filterType = getIntent().getStringExtra("filterType");

            if(filterType==null||filterType==null){

            }
            else{
                switch (filterType) {
                    case "category":
                        setRealmAdapter(realm.where(Product.class).contains("category",filterValue).findAll());
                        break;
                    case "store":
                        setRealmAdapter(realm.where(Product.class).contains("store",filterValue).findAll());
                        break;
                    case "sort":

                        switch (filterValue) {
                            case "Name (Ascendent)":
                                setRealmAdapter(realm.where(Product.class).findAllSorted("title",Sort.ASCENDING));
                                break;
                            case "Name (Descendent)":
                                setRealmAdapter(realm.where(Product.class).findAllSorted("title",Sort.DESCENDING));
                                break;
                            case "Price (Ascendent)":
                                setRealmAdapter(realm.where(Product.class).findAllSorted("price",Sort.ASCENDING));
                                break;
                            case "Price (Descendent)":
                                setRealmAdapter(realm.where(Product.class).findAllSorted("price",Sort.DESCENDING));
                                break;
                        }

                        break;
                }
            }


        }
        else{

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

        //Setup Validator
        validator = new Validator(this);
        validator.setValidationListener(this);

        //Inflate and setup dialog and dialog views
        inflater = MainActivity.this.getLayoutInflater();
        final View content = inflater.inflate(R.layout.edit_product, null);
        editProductTitle = (EditText) content.findViewById(R.id.edit_product_title);
        editProductPrice = (EditText) content.findViewById(R.id.edit_product_price);
        editProductStore = (EditText) content.findViewById(R.id.edit_product_store);
        editProductImage = (EditText) content.findViewById(R.id.edit_product_image);
        editProductUrl = (EditText) content.findViewById(R.id.edit_product_url);
        inputLayoutProductUrl = (TextInputLayout) content.findViewById(R.id.input_layout_product_url);
        buttonProductAnalyzeUrl = (Button) content.findViewById(R.id.button_product_analyze_url);
        final Button buttonConfirm = (Button) content.findViewById(R.id.button_confirm);
        final Button buttonCancel = (Button) content.findViewById(R.id.button_cancel);
        loadingCircle = (RelativeLayout) content.findViewById(R.id.loading_circle);
        spCategory = (Spinner) content.findViewById(R.id.sp_category);



        //Hide Loading Circle and show button
        loadingCircle.setVisibility(View.GONE);
        buttonProductAnalyzeUrl.setVisibility(View.VISIBLE);

        if (sharedUrl != null && !sharedUrl.isEmpty()) {
            editProductUrl.setText(sharedUrl);
        }

        //Create Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setView(content)
                .setTitle("Add product");

        dialog = builder.create();
        dialog.show();

        //Parse data from the url
        buttonProductAnalyzeUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Hide Loading Circle and show button
                loadingCircle.setVisibility(View.VISIBLE);
                buttonProductAnalyzeUrl.setVisibility(View.GONE);

                //Validate url, check if is empty and if it's a url in http://example.* format
                if(editProductUrl.getText().toString().matches("")){
                    inputLayoutProductUrl.setError("This field is required");
                }
                else{
                    String URL_REGEX = "https?:\\/\\/(www\\.)[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
                    Pattern p = Pattern.compile(URL_REGEX);
                    Matcher m = p.matcher(editProductUrl.getText());//replace with string to compare
                    if(m.find()) {

                        String url=editProductUrl.getText().toString();
                        ( new ParseURL(content) ).execute(new String[]{url});

                    }
                    else{
                        inputLayoutProductUrl.setError("You need to enter a url");
                    }
                }


            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validate data and run onValidationSucceeded method, if data is valid
                validator.validate();

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }


    @Override
    public void onValidationSucceeded() {

        //Set data from textviews to Product Objects
        Product product = new Product();
        product.setId(RealmController.getInstance().getProducts().size() + (int) System.currentTimeMillis());
        product.setTitle(editProductTitle.getText().toString());
        product.setPrice(Float.parseFloat(editProductPrice.getText().toString()));
        product.setStore(editProductStore.getText().toString());
        product.setCategory(String.valueOf(spCategory.getSelectedItem()));
        product.setImageUrl(editProductImage.getText().toString());
        product.setBuyUrl(editProductUrl.getText().toString());

        //Put data in realm
        realm.beginTransaction();
        realm.copyToRealm(product);
        realm.commitTransaction();

        adapter.notifyDataSetChanged();

        //Scroll the recycler view to bottom
        recyclerView.scrollToPosition(RealmController.getInstance().getProducts().size() - 1);

        dialog.dismiss();


    }
            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    View view = error.getView();
                    String message = error.getCollatedErrorMessage(this);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();


            // Display error messages ;)
            if (view instanceof EditText) {

                //((EditText) view).setError(message);

                //Get TextInputLayout, that is parent of edittext, and set error to him, so it has a better ui
                TextInputLayout textInputLayout = (TextInputLayout) view.getParent();
                textInputLayout.setError(message);

                //TODO: BUG: if the url is not validated or empty, the spinner doesn't go away
                //Hide Loading Circle and show button
                loadingCircle.setVisibility(View.GONE);
                buttonProductAnalyzeUrl.setVisibility(View.VISIBLE);

            } else {
                //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

}
