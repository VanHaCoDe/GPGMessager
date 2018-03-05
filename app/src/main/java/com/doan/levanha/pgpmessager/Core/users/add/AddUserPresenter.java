package com.doan.levanha.pgpmessager.Core.users.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

import java.security.NoSuchAlgorithmException;


public class AddUserPresenter implements AddUserContract.Presenter, AddUserContract.OnUserDatabaseListener {
    private AddUserContract.View mView;
    private AddUserInteractor mAddUserInteractor;

    public AddUserPresenter(AddUserContract.View view) {
        this.mView = view;
        mAddUserInteractor = new AddUserInteractor(this);
    }

    @Override
    public void addUser(Context context, FirebaseUser firebaseUser, String publicKey) {
        mAddUserInteractor.addUserToDatabase(context, firebaseUser,publicKey);
    }

    @Override
    public void onSuccess(String message) throws NoSuchAlgorithmException {
        mView.onAddUserSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        mView.onAddUserFailure(message);
    }
}
