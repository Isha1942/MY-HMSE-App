package com.spark.newapp2.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_device")
public class UserDevice {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userName;
    private String siteName;
    private String deviceNumber;
    private String deviceModel;
    private String simNumber;
    private boolean isSelected;

    public UserDevice(String userName, String siteName, String deviceNumber, String deviceModel, String simNumber, boolean isSelected) {
        this.userName = userName;
        this.siteName = siteName;
        this.deviceNumber = deviceNumber;
        this.deviceModel = deviceModel;
        this.simNumber = simNumber;
        this.isSelected = isSelected;
    }


    @Ignore
    public UserDevice(String userName, String siteName, String deviceNumber, String deviceModel, String simNumber) {
        this.userName = userName;
        this.siteName = siteName;
        this.deviceNumber = deviceNumber;
        this.deviceModel = deviceModel;
        this.simNumber = simNumber;
        this.isSelected = false;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getDeviceNumber() { return deviceNumber; }
    public void setDeviceNumber(String deviceNumber) { this.deviceNumber = deviceNumber; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getSimNumber() { return simNumber; }
    public void setSimNumber(String simNumber) { this.simNumber = simNumber; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { this.isSelected = selected; }
}
