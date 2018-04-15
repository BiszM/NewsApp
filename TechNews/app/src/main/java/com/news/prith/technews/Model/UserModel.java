package com.news.prith.technews.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by prith on 2/21/2018.
 */

public class UserModel{

    private String username;
    private String email;
    private String gender;
    private String image;
    private String fbId;

    public UserModel(String fbId, String username, String email, String gender, String image){
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.image = image;
        this.fbId = fbId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setProfilePic(String image) {
        this.image = image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", image='" + image+ '\'' +
                '}';
    }
}
