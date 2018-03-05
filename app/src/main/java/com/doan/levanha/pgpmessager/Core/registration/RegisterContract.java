package com.doan.levanha.pgpmessager.Core.registration;

import android.app.Activity;

import com.google.firebase.auth.FirebaseUser;

import java.security.NoSuchAlgorithmException;


public interface RegisterContract {
    interface View {
        void onRegistrationSuccess(FirebaseUser firebaseUser) throws NoSuchAlgorithmException;

        void onRegistrationFailure(String message);
    }

    interface Presenter {
        void register(Activity activity, String email, String password);
    }

    interface Interactor {
        void performFirebaseRegistration(Activity activity, String email, String password);
    }

    interface OnRegistrationListener {
        void onSuccess(FirebaseUser firebaseUser) throws NoSuchAlgorithmException;

        void onFailure(String message);
    }
}
