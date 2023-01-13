package com.eits.cameraappdesign.model;

import java.util.Date;

public class fileData_modelData {
    String filename,component,location,facility;
    Date dateTime;
    int average;
    double min,max;

    public fileData_modelData(String filename, String component, String location, String facility, Date dateTime, double min, double max, int average) {
        this.filename = filename;
        this.component = component;
        this.location = location;
        this.facility = facility;
        this.dateTime = dateTime;
        this.min = min;
        this.max = max;
        this.average = average;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public double getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }
}
