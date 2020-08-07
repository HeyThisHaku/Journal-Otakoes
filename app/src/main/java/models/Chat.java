package models;

import android.util.Log;
import android.widget.ImageView;

public class Chat implements Comparable{
    private String username,userID,lastMessage,email;
    private ImageView img;

    public Chat(String username, String userID, String lastMessage, ImageView img,String email) {
        this.username = username;
        this.userID = userID;
        this.lastMessage = lastMessage;
        this.img = img;
        this.email=email;
        Log.d("salahke","masuk isni");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
