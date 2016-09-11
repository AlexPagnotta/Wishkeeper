package com.example.alex.wishkeeper.model;

import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.alex.wishkeeper.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParseURL extends AsyncTask<String, Void, ArrayList> {

    View content;
    String titleSelector;
    String imgSelector;
    String priceSelector;
    ArrayList product;
    Document url;
    String title;
    String price;
    String store;
    String img;

    public ParseURL(View content){
        this.content=content;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {


        product = new ArrayList();

        try {
            url = Jsoup.connect(strings[0]).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:48.0) Gecko/20100101 Firefox/48.0").get();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(strings[0].contains("amazon")){
            cleanAmazonData();
        }

        else if(strings[0].contains("ebay")){
            cleanEbayData();
        }

        else if(strings[0].contains("zalando")){
            cleanZalandoData();
        }

        return product;
    }

    private void cleanAmazonData(){

        imgSelector = "#landingImage";
        priceSelector = "#priceblock_ourprice";

        Element imgElement = url.select(imgSelector).first();
        Element priceElement = url.select(priceSelector).first();

        if(imgElement == null||priceElement == null){
            Log.d("test","VUOTO");
        }
        else {
            title = url.title();

            store= "Amazon";

            //Clean Image
            ArrayList links = new ArrayList();
            String regex = "\\(?\\b(https://|http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(imgElement.attr("data-a-dynamic-image"));
            while (m.find()) {
                String urlStr = m.group();
                if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                    urlStr = urlStr.substring(1, urlStr.length() - 1);
                }
                links.add(urlStr);

            }

            img = links.get(0).toString();

            //Clean Price
            String split[] = priceElement.text().split(" ");
            price = split[1].replace(",", ".");

            product.add(title);
            product.add(price);
            product.add(store);
            product.add(img);
        }


    }

    private void cleanEbayData(){

        imgSelector = "#icImg";
        priceSelector = ".notranslate";

        Element imgElement = url.select(imgSelector).first();
        Element priceElement = url.select(priceSelector).first();

        if(imgElement == null||priceElement == null){
            Log.d("test","VUOTO");
        }
        else {
            title = url.title();

            img = imgElement.attr("src"); //TODO: Find an image with a better resolution

            store="Ebay";

            price = priceElement.attr("content");

            product.add(title);
            product.add(price);
            product.add(store);
            product.add(img);
        }

    }

    private void cleanZalandoData(){

        imgSelector = "[name=twitter:image]";
        priceSelector = "#articlePrice";

        Element imgElement = url.select(imgSelector).first();
        Element priceElement = url.select(priceSelector).first();

        if(imgElement == null||priceElement == null){
            Log.d("test","VUOTO");
        }
        else {

            title = url.title();

            img = imgElement.attr("content");

            store="Zalando";

            //Clean Price
            String split[] = priceElement.text().split(" ");
            price = split[1].replace(",", ".");

            product.add(title);
            product.add(price);
            product.add(store);
            product.add(img);
        }

    }


    @Override
    protected void onPostExecute(ArrayList product) {

        super.onPostExecute(product);

        EditText editProductTitle = (EditText) content.findViewById(R.id.edit_product_title);
        EditText editProductPrice = (EditText) content.findViewById(R.id.edit_product_price);
        EditText editProductStore = (EditText) content.findViewById(R.id.edit_product_store);
        EditText editProductImage = (EditText) content.findViewById(R.id.edit_product_image);
        TextInputLayout inputLayoutProductUrl = (TextInputLayout) content.findViewById(R.id.input_layout_product_url);
        RelativeLayout loadingCircle = (RelativeLayout) content.findViewById(R.id.loading_circle);
        Button buttonProductAnalyzeUrl = (Button) content.findViewById(R.id.button_product_analyze_url);

        //Hide Loading Circle and show button
        loadingCircle.setVisibility(View.GONE);
        buttonProductAnalyzeUrl.setVisibility(View.VISIBLE);


        if(!product.isEmpty()){
            editProductTitle.setText(product.get(0).toString());
            editProductPrice.setText(product.get(1).toString());
            editProductStore.setText(product.get(2).toString());
            editProductImage.setText(product.get(3).toString());

        }
        else{
            inputLayoutProductUrl.setError("The url is wrong, or the site is not yet compatible");
        }


    }

}