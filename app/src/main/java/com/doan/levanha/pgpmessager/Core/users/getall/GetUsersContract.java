package com.doan.levanha.pgpmessager.Core.users.getall;

import com.doan.levanha.pgpmessager.models.User;

import java.util.List;


public interface GetUsersContract {
    interface View {
        void onGetAllUsersSuccess(List<User> users);

        void onGetAllUsersFailure(String message);


    }

    interface Presenter {
        void getAllUsers();


    }

    interface Interactor {
        void getAllUsersFromFirebase();


    }

    interface OnGetAllUsersListener {
        void onGetAllUsersSuccess(List<User> users);

        void onGetAllUsersFailure(String message);
    }


}
