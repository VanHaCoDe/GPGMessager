package com.doan.levanha.pgpmessager.Core.users.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

import java.security.NoSuchAlgorithmException;


public interface AddUserContract {
    interface View {
        void onAddUserSuccess(String message) throws NoSuchAlgorithmException;

        void onAddUserFailure(String message);
    }

    interface Presenter {
        void addUser(Context context, FirebaseUser firebaseUser,String publicKey);
    }

    interface Interactor {
        void addUserToDatabase(Context context, FirebaseUser firebaseUser, String publicKey);
    }

    interface OnUserDatabaseListener {
        void onSuccess(String message) throws NoSuchAlgorithmException;

        void onFailure(String message);
    }
}
