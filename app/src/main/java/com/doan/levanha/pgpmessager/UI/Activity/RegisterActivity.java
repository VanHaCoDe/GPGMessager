package com.doan.levanha.pgpmessager.UI.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doan.levanha.pgpmessager.Core.private_message.puplic_key_cryptosystem.RSA;
import com.doan.levanha.pgpmessager.Core.registration.RegisterContract;
import com.doan.levanha.pgpmessager.Core.registration.RegisterPresenter;
import com.doan.levanha.pgpmessager.Core.users.add.AddUserContract;
import com.doan.levanha.pgpmessager.Core.users.add.AddUserPresenter;
import com.doan.levanha.pgpmessager.R;
import com.doan.levanha.pgpmessager.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        RegisterContract.View, AddUserContract.View {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private RegisterPresenter mRegisterPresenter;
    private AddUserPresenter mAddUserPresenter;

    private EditText mETxtEmail, mETxtPassword;
    private Button mBtnRegister;

    private ProgressDialog mProgressDialog;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        bindViews();

        init();
    }

    private void init() {
        mRegisterPresenter = new RegisterPresenter(this);
        mAddUserPresenter = new AddUserPresenter(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("loading");
        mProgressDialog.setMessage("please_wait");
        mProgressDialog.setIndeterminate(true);

        mBtnRegister.setOnClickListener(this);
    }

    private void bindViews() {
        mETxtEmail = (EditText) findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) findViewById(R.id.edit_text_password);
        mBtnRegister = (Button) findViewById(R.id.button_register);
    }

    @Override
    public void onClick(View v) {
        int viewid = v.getId();
        switch (viewid) {
            case R.id.button_register:
                onRegister();
                break;
        }

    }

    private void onRegister() {
        String emailId = mETxtEmail.getText().toString();
        String password = mETxtPassword.getText().toString();
        if (!emailId.equals("") || !password.equals("")) {
            mRegisterPresenter.register(this, emailId, password);
            mProgressDialog.show();
        } else
            Toast.makeText(getApplicationContext(), "email and password can not null", Toast.LENGTH_SHORT);
    }
    private void updateUserPublickey(String uid) throws NoSuchAlgorithmException {
        RSA rsa = new RSA(this);
        rsa.createGenerateKey();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(uid)
                .child(Constants.ARG_PUBLIC_KEY)
                .setValue(rsa.publicKeyTosring(rsa.getPublicKey()));
    }
    @Override
    public void onRegistrationSuccess(FirebaseUser firebaseUser) {
        mProgressDialog.setMessage("adding user to database");
        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
        mAddUserPresenter.addUser(this.getApplicationContext(), firebaseUser, null);


    }

    @Override
    public void onRegistrationFailure(String message) {
        mProgressDialog.dismiss();
        mProgressDialog.setMessage("Please wait");
        Log.e(TAG, "onRegistrationFailure: " + message);
        Toast.makeText(this, "Registration failed!+\n" + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddUserSuccess(String message) throws NoSuchAlgorithmException {
        mProgressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        updateUserPublickey(FirebaseAuth.getInstance().getCurrentUser().getUid());
        UserListActivity.startActivity(this,
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onAddUserFailure(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


