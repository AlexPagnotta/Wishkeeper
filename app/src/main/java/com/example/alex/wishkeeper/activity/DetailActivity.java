package com.example.alex.wishkeeper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.wishkeeper.R;
import com.example.alex.wishkeeper.model.ParseURL;
import com.example.alex.wishkeeper.model.Product;
import com.example.alex.wishkeeper.realm.RealmController;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Url;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DetailActivity extends AppCompatActivity implements Validator.ValidationListener {

    TextView textProductsTitle;
    TextView textProductsPrice;
    ImageView imageProductsBackground;
    Button buttonProductEdit;
    Button buttonProductRemove;
    Button buttonProductOpen;
    Product product;
    int productId;
    Realm realm;
    private LayoutInflater inflater;
    private AlertDialog dialog;
    private Validator validator;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setupViews();

        //Get id of realm, and set the product
        productId = getIntent().getIntExtra("productId",0);

         product = realm.where(Product.class)
                .equalTo("id", productId).findFirst();

        setViewsData();

        manageClicks();

    }

    private void setupViews(){

        textProductsTitle = (TextView) findViewById(R.id.text_products_title);
        textProductsPrice = (TextView) findViewById(R.id.text_products_price);
        imageProductsBackground = (ImageView) findViewById(R.id.image_products_background);
        buttonProductEdit = (Button) findViewById(R.id.button_product_edit);
        buttonProductRemove = (Button) findViewById(R.id.button_product_remove);
        buttonProductOpen = (Button) findViewById(R.id.button_product_open);

        //Setup Realm
        this.realm = RealmController.with(this).getRealm();


    }

    private void setViewsData(){

        Product product = realm.where(Product.class).equalTo("id", productId).findFirst();

        textProductsTitle.setText(product.getTitle());
        //.format locale %.21 ecc. is used to display number with two decimal position ex. 20.00
        textProductsPrice.setText(String.format(java.util.Locale.US,"%.2f", product.getPrice())+" €");

        if (product.getImageUrl() != null) {
            Picasso.with(this)
                    .load(product.getImageUrl())
                    .into(imageProductsBackground);
        }

    }

    private void manageClicks(){
        buttonProductEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editProduct();

            }
        });

        //Remove Product
        buttonProductRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(DetailActivity.this)
                        .setMessage("Do you really want to remove this product?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                realm.beginTransaction();

                                product.deleteFromRealm();

                                realm.commitTransaction();

                                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                startActivity(intent);

                            }})
                        .setNegativeButton("No", null).show();

            }
        });

        //Open product's url in broswer
        buttonProductOpen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String url = product.getBuyUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                DetailActivity.this.startActivity(i);
            }
        });
    }

    public void editProduct(){

        //Setup Validator
        validator = new Validator(this);
        validator.setValidationListener(this);

        //Inflate and setup dialog and dialog views
        inflater = DetailActivity.this.getLayoutInflater();
        final View content = inflater.inflate(R.layout.edit_product, null);
        editProductTitle = (EditText) content.findViewById(R.id.edit_product_title);
        editProductPrice = (EditText) content.findViewById(R.id.edit_product_price);
        editProductStore = (EditText) content.findViewById(R.id.edit_product_store);
        editProductImage = (EditText) content.findViewById(R.id.edit_product_image);
        editProductUrl = (EditText) content.findViewById(R.id.edit_product_url);
        inputLayoutProductUrl = (TextInputLayout) content.findViewById(R.id.input_layout_product_url);
        final Button buttonProductAnalyzeUrl = (Button) content.findViewById(R.id.button_product_analyze_url);
        final Button buttonConfirm = (Button) content.findViewById(R.id.button_confirm);
        final Button buttonCancel = (Button) content.findViewById(R.id.button_cancel);
        final RelativeLayout loadingCircle = (RelativeLayout) content.findViewById(R.id.loading_circle);
        spCategory = (Spinner) content.findViewById(R.id.sp_category);

        //Hide Loading Circle and show button
        loadingCircle.setVisibility(View.GONE);
        buttonProductAnalyzeUrl.setVisibility(View.VISIBLE);

        editProductTitle.setText(product.getTitle());
        editProductPrice.setText(String.valueOf(product.getPrice()));
        editProductStore.setText(product.getStore());
        for (int i=0;i<spCategory.getCount();i++){
            if (spCategory.getItemAtPosition(i).equals(product.getCategory())){
                spCategory.setSelection(i);
            }
        }
        editProductImage.setText(product.getImageUrl());
        editProductUrl.setText(product.getBuyUrl());

        //Create Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);

        builder.setView(content)
                .setTitle("Edit product");

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

        //Update data in realm
        realm.beginTransaction();

        product.setTitle(editProductTitle.getText().toString());
        product.setPrice(Float.parseFloat(editProductPrice.getText().toString()));
        product.setStore(editProductStore.getText().toString());
        product.setCategory(String.valueOf(spCategory.getSelectedItem()));
        product.setImageUrl(editProductImage.getText().toString());
        product.setBuyUrl(editProductUrl.getText().toString());

        realm.commitTransaction();

        //Refresh Activity data
        setViewsData();

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
            } else {
                //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
