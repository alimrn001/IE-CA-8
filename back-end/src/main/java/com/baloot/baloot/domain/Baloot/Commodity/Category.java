package com.baloot.baloot.domain.Baloot.Commodity;

import java.util.ArrayList;

public class Category {

    private String categoryName;

    private ArrayList<Integer> commodities;


    public Category(String categoryName) {
        this.categoryName = categoryName;
        this.commodities = new ArrayList<>();
    }

    public void setCommodities(ArrayList<Integer> commodities) {
        this.commodities = commodities;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void addCommodityToCategory(int commodityId) {
        commodities.add(commodityId);
    }

    public boolean isInCommodities(int commodityId) {
        return commodities.contains(commodityId);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ArrayList<Integer> getCommodities() {
        return commodities;
    }

}
