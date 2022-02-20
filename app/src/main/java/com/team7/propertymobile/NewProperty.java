package com.team7.propertymobile;

public class NewProperty extends Property {
    private int newProjectId;
    private String date;
    private double landPrice;
    private double predictedPrice;

    public NewProperty() {
    }

    public int getNewProjectId() {
        return newProjectId;
    }

    public void setNewProjectId(int newProjectId) {
        this.newProjectId = newProjectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLandPrice() {
        return landPrice;
    }

    public void setLandPrice(double landPrice) {
        this.landPrice = landPrice;
    }

    public double getPredictedPrice() {
        return predictedPrice;
    }

    public void setPredictedPrice(double predictedPrice) {
        this.predictedPrice = predictedPrice;
    }
}
