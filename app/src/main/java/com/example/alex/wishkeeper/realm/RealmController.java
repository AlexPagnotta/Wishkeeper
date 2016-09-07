package com.example.alex.wishkeeper.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import com.example.alex.wishkeeper.model.Product;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.waitForChange();

    }

    //Clear all objects from Product.class
    public void clearAll() {

        realm.beginTransaction();
        realm.delete(Product.class);
        realm.commitTransaction();

    }

    //Find all objects in the Product.class
    public RealmResults<Product> getProducts() {

        return realm.where(Product.class).findAll();
    }


    //Check if Product.class is empty
    public boolean hasProducts() {

        return !realm.where(Product.class).findAll().isEmpty();

    }

    //Search products by name
    public RealmResults<Product> searchProducts(String text) {

        return realm.where(Product.class)
                .contains("title", text, Case.INSENSITIVE)
                .findAll();
    }


    //Query example
    public RealmResults<Product> queryExample() {

        return realm.where(Product.class)
                .contains("title", "Realm")
                .findAll();

    }
}