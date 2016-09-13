package com.example.alex.wishkeeper.navDrawer;



import com.bignerdranch.expandablerecyclerview.model.ParentListItem;

import java.util.List;

public class NavDrawerParentObject implements ParentListItem {

    private String mChildTitle;
    private List<NavDrawerChildObject> mChildObjects;

    public NavDrawerParentObject(String childTitle, List<NavDrawerChildObject> childObjects) {
        mChildTitle = childTitle;
        mChildObjects = childObjects;
    }

    public String getName() {
        return mChildTitle;
    }

    @Override
    public List<?> getChildItemList() {
        return mChildObjects;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}