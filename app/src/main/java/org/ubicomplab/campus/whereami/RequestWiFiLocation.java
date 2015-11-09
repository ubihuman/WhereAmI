package org.ubicomplab.campus.whereami;

public class RequestWiFiLocation {

    private String apiID;
    private String deviceInformation;
    private String nearestBSSID;

    public RequestWiFiLocation() {
    }

    public RequestWiFiLocation(String apiID, String deviceInformation, String nearestBSSID) {
        this.apiID = apiID;
        this.deviceInformation = deviceInformation;
        this.nearestBSSID = nearestBSSID;
    }

    public String getApiID() {
        return apiID;
    }

    public void setApiID(String apiID) {
        this.apiID = apiID;
    }

    public String getDeviceInformation() {
        return deviceInformation;
    }

    public void setDeviceInformation(String deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public String getNearestBSSID() {
        return nearestBSSID;
    }

    public void setNearestBSSID(String nearestBSSID) {
        this.nearestBSSID = nearestBSSID;
    }

    @Override
    public String toString() {
        return "RequestWiFiLocation{" +
                "apiID='" + apiID + '\'' +
                ", deviceInformation='" + deviceInformation + '\'' +
                ", nearestBSSID='" + nearestBSSID + '\'' +
                '}';
    }
}
