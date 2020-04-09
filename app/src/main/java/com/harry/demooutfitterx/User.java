package com.harry.demooutfitterx;

import java.io.Serializable;

public class User implements Serializable {
    private String Name;
    private String Email;
    private String Image;
    private String id;

    public User(){}

    public User(String name, String email, String image, String id) {
        this.Name = name;
        this.Email = email;
        this.Image = image;
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getImage() {
        return Image;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

}
