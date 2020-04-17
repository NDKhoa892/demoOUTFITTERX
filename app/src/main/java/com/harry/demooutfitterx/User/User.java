package com.harry.demooutfitterx.User;

import java.io.Serializable;

public class User implements Serializable {
    private String Name;
    private String Email;
    private String Image;
    private String id;
    private InfoUser infoUser;
    private PostAndFollow postAndFollow;

    public User(){}

    public User(String name, String email, String image, String id, InfoUser infoUser, PostAndFollow postAndFollow) {
        Name = name;
        Email = email;
        Image = image;
        this.id = id;
        this.infoUser = infoUser;
        this.postAndFollow = postAndFollow;
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

    public InfoUser getInfoUser() { return infoUser; }

    public PostAndFollow getPostAndFollow() { return postAndFollow; }

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

    public void setInfoUser(InfoUser infoUser) { this.infoUser = infoUser; }

    public void setPostAndFollow(PostAndFollow postAndFollow) { this.postAndFollow = postAndFollow; }
}
