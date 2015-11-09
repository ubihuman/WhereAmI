package org.ubicomplab.campus.whereami;

public class WiFiLocation {

    private String buildingName;
    private String roomName;
    private String apSSID;
    private String apFrequency;
    private String apBSSID;

    public WiFiLocation() {
    }

    public WiFiLocation(String buildingName, String roomName, String apSSID, String apFrequency, String apBSSID) {
        this.buildingName = buildingName;
        this.roomName = roomName;
        this.apSSID = apSSID;
        this.apFrequency = apFrequency;
        this.apBSSID = apBSSID;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getApSSID() {
        return apSSID;
    }

    public void setApSSID(String apSSID) {
        this.apSSID = apSSID;
    }

    public String getApFrequency() {
        return apFrequency;
    }

    public void setApFrequency(String apFrequency) {
        this.apFrequency = apFrequency;
    }

    public String getApBSSID() {
        return apBSSID;
    }

    public void setApBSSID(String apBSSID) {
        this.apBSSID = apBSSID;
    }

    @Override
    public String toString() {
        return "Location based on Wi-Fi" +
                "\n◦ Building Name='" + buildingName + '\'' +
                "\n◦ Room Name='" + roomName + '\'' +
                "\n◦ AP SSID='" + apSSID + '\'' +
                "\n◦ AP Frequency='" + apFrequency + '\'' +
                "\n◦ AP BSSID='" + apBSSID ;
    }
}
