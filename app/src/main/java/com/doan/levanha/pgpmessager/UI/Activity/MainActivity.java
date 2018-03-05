package com.doan.levanha.pgpmessager.UI.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.doan.levanha.pgpmessager.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    // Firebase instance variables

    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                // check if user is already logged in or not
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    // if logged in redirect the user to user listing activity
                    UserListActivity.startActivity(MainActivity.this);
                } else {
                    // otherwise redirect the user to login activity
                    LoginActivity.startIntent(MainActivity.this);
                }
                finish();
            }
        };
        mHandler.postDelayed(mRunnable, 2000);
    }

    //Check chat ativity open
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        MainActivity.sIsChatActivityOpen = isChatActivityOpen;
    }
}
