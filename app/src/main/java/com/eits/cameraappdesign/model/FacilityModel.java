package com.eits.cameraappdesign.model;

public class FacilityModel {
    int facID;
    String facName;
    String facLocation;

    public FacilityModel(int facID, String facName, String facLocation) {
        this.facID = facID;
        this.facName = facName;
        this.facLocation = facLocation;
    }

    public int getFacID() {
        return facID;
    }

    public void setFacID(int facID) {
        this.facID = facID;
    }

    public String getFacName() {
        return facName;
    }

    public void setFacName(String facName) {
        this.facName = facName;
    }

    public String getFacLocation() {
        return facLocation;
    }

    public void setFacLocation(String facLocation) {
        this.facLocation = facLocation;
    }
}
