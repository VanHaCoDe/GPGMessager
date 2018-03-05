package com.doan.levanha.pgpmessager.Core.users.add;

import android.content.Context;
import android.support.annotation.NonNull;

import com.doan.levanha.pgpmessager.models.User;
import com.doan.levanha.pgpmessager.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.NoSuchAlgorithmException;


public class AddUserInteractor implements AddUserContract.Interactor {
    private AddUserContract.OnUserDatabaseListener mOnUserDatabaseListener;

    public AddUserInteractor(AddUserContract.OnUserDatabaseListener onUserDatabaseListener) {
        this.mOnUserDatabaseListener = onUserDatabaseListener;
    }

    @Override
    public void addUserToDatabase(final Context context, FirebaseUser firebaseUser, String publicKey) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                FirebaseInstanceId.getInstance().getToken(),
                publicKey);
        database.child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            try {
                                mOnUserDatabaseListener.onSuccess("Add user success");
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mOnUserDatabaseListener.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }
}
