package com.transportervendor.Bean;
public class Vehicle {
    private String vehicelId;
    private String name;
    private int count;
    private String imgUrl;

    public Vehicle() {
    }

    public Vehicle(String vehicelId, String name, int count, String imgUrl) {
        this.vehicelId = vehicelId;
        this.name = name;
        this.count = count;
        this.imgUrl = imgUrl;
    }



    public String getVehicelId() {
        return vehicelId;
    }

    public void setVehicelId(String vehicelId) {
        this.vehicelId = vehicelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}