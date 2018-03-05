package com.doan.levanha.pgpmessager.Core.login;

import android.app.Activity;

import java.security.NoSuchAlgorithmException;



public interface LoginContract {
    interface View {
        void onLoginSuccess(String message) throws NoSuchAlgorithmException;

        void onLoginFailure(String message);
    }

    interface Presenter {
        void login(Activity activity, String email, String password);
    }

    interface Interactor {
        void performFirebaseLogin(Activity activity, String email, String password);
    }

    interface OnLoginListener {
        void onSuccess(String message) throws NoSuchAlgorithmException;

        void onFailure(String message);
    }
}
