package com.team7.propertymobile;

import java.io.Serializable;

public class Property implements Serializable {
    private int projectId;
    private String propertyName;
    private String region;
    private String street;
    private String xCoordinates;
    private String yCoordinates;

    public Property(int projectId, String propertyName, String region, String street, String xCoordinates, String yCoordinates) {
        this.projectId = projectId;
        this.propertyName = propertyName;
        this.region = region;
        this.street = street;
        this.xCoordinates = xCoordinates;
        this.yCoordinates = yCoordinates;
    }

    public Property(String propertyName, String street) {
        this.propertyName = propertyName;
        this.street = street;
    }

    public Property(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Property() {
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setxCoordinates(String xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public void setyCoordinates(String yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getStreet() {
        return street;
    }

    public String getxCoordinates() {
        return xCoordinates;
    }

    public String getyCoordinates() {
        return yCoordinates;
    }
}
