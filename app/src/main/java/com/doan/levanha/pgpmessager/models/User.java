package com.doan.levanha.pgpmessager.models;

/**
 * Created by delaroy on 4/13/17.
 */
public class User {
    public String uid;
    public String email;
    public String firebaseToken;
    public String publicKey;

    public User(){

    }

    public User(String uid, String email, String firebaseToken , String publicKey){
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.publicKey=publicKey;
    }
}
