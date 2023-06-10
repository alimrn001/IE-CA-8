package com.baloot.baloot.domain.Baloot.Provider;

import java.time.LocalDate;
import java.util.ArrayList;

public class Provider {

    private int id;

    private String name;

    private LocalDate registryDate;

    private int commoditiesNum;

    private double avgCommoditiesRate;

    private ArrayList<Integer> commoditiesProvided;

    private String image;


    public Provider(int id, String name, String registryDate, String image) {
        this.id = id;
        this.name = name;
        this.registryDate = LocalDate.parse(registryDate);
        this.commoditiesNum = 0;
        this.avgCommoditiesRate = 0;
        this.commoditiesProvided = new ArrayList<>();
        this.image = image;
    }

    public void initializeGsonNullValues() {
        this.commoditiesNum = 0;
        this.avgCommoditiesRate = 0;
        this.commoditiesProvided = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegistryDate(String registryDate) {
        this.registryDate = LocalDate.parse(registryDate);
    }

    public void setCommoditiesNum(int commoditiesNum) {
        this.commoditiesNum = commoditiesNum;
    }

    public void setAvgCommoditiesRate(double avgCommoditiesRate) {
        this.avgCommoditiesRate = avgCommoditiesRate;
    }

    public void addProvidedCommodity(int commId) {
        commoditiesProvided.add(commId);
    }

    public void setCommoditiesProvided(ArrayList<Integer> commoditiesProvided) {
        this.commoditiesProvided = commoditiesProvided;
    }

    public void updateCommoditiesData(double newCommodityRating) {
        this.commoditiesNum ++;
        this.avgCommoditiesRate = ((this.avgCommoditiesRate*(this.commoditiesNum-1)) + newCommodityRating)/(this.commoditiesNum);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getRegistryDate() {
        return registryDate;
    }

    public int getCommoditiesNum() {
        return commoditiesNum;
    }

    public double getAvgCommoditiesRate() {
        return avgCommoditiesRate;
    }

    public ArrayList<Integer> getCommoditiesProvided() {
        return commoditiesProvided;
    }

    public String getImage() {
        return image;
    }

}
