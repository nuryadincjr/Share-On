package com.abuunity.shareon.Model;

public class Users {
    private String bio;
    private String email;
    private String id;
    private String imageUrl;
    private String name;
    private String username;

    public Users() {
    }

    public Users(String bio, String email, String id, String imageUrl, String name, String username) {
        this.bio = bio;
        this.email = email;
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
