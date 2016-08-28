package com.example.alex.wishkeeper.model;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    String img;

    public ParseURL(View content){
        this.content=content;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {

        product = new ArrayList();

        try {
            url = Jsoup.connect(strings[0]).get();
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

        titleSelector = "#productTitle";
        imgSelector = "#landingImage";
        priceSelector = "#priceblock_ourprice";

        Element titleElement = url.select(titleSelector).first();
        Element imgElement = url.select(imgSelector).first();
        Element priceElement = url.select(priceSelector).first();

        title=titleElement.text();

        //Clean Image
        ArrayList links = new ArrayList();
        String regex = "\\(?\\b(https://|http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(imgElement.attr("data-a-dynamic-image"));
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);

        }

        img=links.get(0).toString();

        //Clean Price
        String split[] = priceElement.text().split(" ");
        price = split[1].replace(",",".");

        product.add(title);
        product.add(price);
        product.add(img);


    }

    private void cleanEbayData(){

    }

    private void cleanZalandoData(){


    }


    @Override
    protected void onPostExecute(ArrayList product) {

        super.onPostExecute(product);

        EditText editProductTitle = (EditText) content.findViewById(R.id.edit_product_title);
        EditText editProductPrice = (EditText) content.findViewById(R.id.edit_product_price);
        EditText editProductImage = (EditText) content.findViewById(R.id.edit_product_image);

        editProductTitle.setText(product.get(0).toString());
        editProductPrice.setText(product.get(1).toString());
        editProductImage.setText(product.get(2).toString());

    }

}