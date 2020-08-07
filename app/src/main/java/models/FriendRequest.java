package models;

import android.widget.Button;
import android.widget.ImageView;

public class FriendRequest {
    private String username,userID,email;
    private ImageView img;
    Button addBtn,cancelBtn;

    public FriendRequest() {
    }

    public FriendRequest(String userID,String username, ImageView img, Button addBtn, Button cancelBtn,String email) {
        this.userID=userID;
        this.username = username;
        this.img = img;
        this.addBtn = addBtn;
        this.cancelBtn = cancelBtn;
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public Button getAddBtn() {
        return addBtn;
    }

    public void setAddBtn(Button addBtn) {
        this.addBtn = addBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public void setCancelBtn(Button cancelBtn) {
        this.cancelBtn = cancelBtn;
    }
}
