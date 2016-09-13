package com.example.alex.wishkeeper.navDrawer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.model.ParentListItem;
import com.example.alex.wishkeeper.R;
import com.example.alex.wishkeeper.activity.MainActivity;
import com.example.alex.wishkeeper.model.Product;
import com.example.alex.wishkeeper.realm.RealmController;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmQuery;



public class NavDrawerExpandableAdapter extends ExpandableRecyclerAdapter<NavDrawerParentViewHolder, NavDrawerChildViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private int mParentPosition;
    private DrawerLayout drawerLayout;

    public NavDrawerExpandableAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflater = LayoutInflater.from(context);
        this.context=context;

    }


    @Override
    public NavDrawerParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.nav_drawer_item, parentViewGroup, false);
        return new NavDrawerParentViewHolder(view);
    }

    @Override
    public NavDrawerChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.nav_drawer_child_item, childViewGroup, false);
        return new NavDrawerChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(NavDrawerParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        NavDrawerParentObject navDrawerParentObject = (NavDrawerParentObject) parentListItem;
        parentViewHolder.bind(navDrawerParentObject);
    }

    @Override
    public void onBindChildViewHolder(NavDrawerChildViewHolder childViewHolder, int parentPosition, int childPosition, final Object childListItem) {
        final NavDrawerChildObject navDrawerChildObject = (NavDrawerChildObject) childListItem;
        childViewHolder.bind(navDrawerChildObject);
        mParentPosition=parentPosition;

        childViewHolder.layoutChildItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("parent",String.valueOf(mParentPosition));
                //Start Realm
                Realm realm = RealmController.with((MainActivity)context).getRealm();

                if(mParentPosition==0){ //is a category

                    //Set query
                    RealmQuery<Product> products = realm.where(Product.class).contains("category",((NavDrawerChildObject) childListItem).getName());

                    //Call method from main activity
                    ((MainActivity)context).setRealmAdapter(products.findAll());
                }

                else{ //is a store
                    //Set query
                    RealmQuery<Product> products = realm.where(Product.class).contains("store",((NavDrawerChildObject) childListItem).getName());

                    //Call method from main activity
                    ((MainActivity)context).setRealmAdapter(products.findAll());
                }

                //Close Drawer
                drawerLayout = (DrawerLayout) ((MainActivity) context).findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public void onParentListItemExpanded(int position) {
        super.onParentListItemExpanded(position);
        if(position==0){
            collapseParent(1);
        }
        else{
            collapseParent(0);
        }
    }
}
