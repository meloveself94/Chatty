package com.example.android.chatty;

/**
 * Created by Soul on 10/3/2017.
 */

public class SingleUser  {
    public String display_name;
    public String image;
    public String user_status;
    public String thumb_image;

    public SingleUser() {

    }

    public SingleUser(String display_name, String image, String user_status, String thumb_image) {
        this.display_name = display_name;
        this.image = image;
        this.user_status = user_status;
        this.thumb_image = thumb_image;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
