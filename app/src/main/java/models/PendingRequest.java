package models;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

public class PendingRequest {
    private String username,userID;
    private ImageView img;
    Button acceptBtn,declineBtn;

    public PendingRequest() {

    }

    public PendingRequest(String username, String userID, ImageView img, Button acceptBtn, Button declineBtn) {
        this.username = username;
        this.userID = userID;
        this.img = img;
        this.acceptBtn = acceptBtn;
        this.declineBtn = declineBtn;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public Button getAcceptBtn() {
        return acceptBtn;
    }

    public void setAcceptBtn(Button acceptBtn) {
        this.acceptBtn = acceptBtn;
    }

    public Button getDeclineBtn() {
        return declineBtn;
    }

    public void setDeclineBtn(Button declineBtn) {
        this.declineBtn = declineBtn;
    }
}
