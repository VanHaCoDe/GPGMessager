package com.doan.levanha.pgpmessager.UI.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doan.levanha.pgpmessager.Core.login.LoginContract;
import com.doan.levanha.pgpmessager.Core.login.LoginPresenter;
import com.doan.levanha.pgpmessager.Core.private_message.puplic_key_cryptosystem.RSA;
import com.doan.levanha.pgpmessager.R;
import com.doan.levanha.pgpmessager.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginContract.View {
    private LoginPresenter mLoginPresenter;

    private EditText mETxtEmail, mETxtPassword;
    private Button mBtnLogin, mBtnRegister;

    private ProgressDialog mProgressDialog;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startIntent(Context context, int flags) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        bindViews();
        init();
    }

    private void bindViews() {
        mETxtEmail = (EditText) findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) findViewById(R.id.edit_text_password);
        mBtnLogin = (Button) findViewById(R.id.button_login);
        mBtnRegister = (Button) findViewById(R.id.button_register);
    }

    private void init() {
        mLoginPresenter = new LoginPresenter(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("loading");
        mProgressDialog.setMessage("please wait");
        mProgressDialog.setIndeterminate(true);

        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.button_login:
                onLogin();
                break;
            case R.id.button_register:
                onRegister();
                break;
        }
    }

    private void onLogin() {
        String emailId = mETxtEmail.getText().toString();
        String password = mETxtPassword.getText().toString();
        if (!emailId.equals("") || !password.equals("")) {
            mLoginPresenter.login(this, emailId, password);
            mProgressDialog.show();
        }
        else Toast.makeText(this,"email and password can not null",Toast.LENGTH_SHORT);
    }

    private void onRegister() {
        RegisterActivity.startActivity(this);
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
    public void onLoginSuccess(String message) throws NoSuchAlgorithmException {
        mProgressDialog.dismiss();
        Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
        updateUserPublickey(FirebaseAuth.getInstance().getCurrentUser().getUid());
        UserListActivity.startActivity(this,
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onLoginFailure(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }
}
