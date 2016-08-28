package com.example.alex.wishkeeper.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import com.example.alex.wishkeeper.model.Product;
import io.realm.Realm;
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

    //Query a single item with the given id
    public Product getProductsById(String id) {

        return realm.where(Product.class).equalTo("id", id).findFirst();
    }

    //Check if Product.class is empty
    public boolean hasProducts() {

        return !realm.where(Product.class).findAll().isEmpty();

    }

    //Query example
    public RealmResults<Product> queryExample() {

        return realm.where(Product.class)
                .contains("title", "Realm")
                .findAll();

    }
}