package com.harry.demooutfitterx.User;

import java.io.Serializable;

public class InfoUser implements Serializable {
    private String activeName, adress, gender;
    private long age, weight, height;
    private boolean defaultDelivery;

    public InfoUser() {
    }

    public InfoUser(String activeName, String adress, String gender, long age, long weight, long height, boolean defaultDelivery) {
        this.activeName = activeName;
        this.adress = adress;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.defaultDelivery = defaultDelivery;
    }

    public String getActiveName() {
        return activeName;
    }

    public void setActiveName(String activeName) {
        this.activeName = activeName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public boolean isDefaultDelivery() {
        return defaultDelivery;
    }

    public void setDefaultDelivery(boolean defaultDelivery) {
        this.defaultDelivery = defaultDelivery;
    }
}
