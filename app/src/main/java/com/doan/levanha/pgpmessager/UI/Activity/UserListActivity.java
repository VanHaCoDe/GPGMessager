package com.doan.levanha.pgpmessager.UI.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.doan.levanha.pgpmessager.Core.logout.LogoutContract;
import com.doan.levanha.pgpmessager.Core.logout.LogoutPresenter;
import com.doan.levanha.pgpmessager.Core.private_message.puplic_key_cryptosystem.RSA;
import com.doan.levanha.pgpmessager.Core.users.getall.GetUsersContract;
import com.doan.levanha.pgpmessager.Core.users.getall.GetUsersPresenter;
import com.doan.levanha.pgpmessager.R;
import com.doan.levanha.pgpmessager.UI.Adapter.UserListingRecyclerAdapter;
import com.doan.levanha.pgpmessager.models.User;
import com.doan.levanha.pgpmessager.utils.ItemClickSupport;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;


public class UserListActivity extends AppCompatActivity implements ItemClickSupport.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, LogoutContract.View, GetUsersContract.View {


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;

    private LogoutPresenter mLogoutPresenter;
    private GetUsersPresenter mGetUsersPresenter;
    private UserListingRecyclerAdapter mUserListingRecyclerAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserListActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int flags) {
        Intent intent = new Intent(context, UserListActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_listing);

        bindViews();
        try {
            init();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private void bindViews() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        this.mRecyclerViewAllUserListing = (RecyclerView) findViewById(R.id.recycler_view_all_user_listing);
    }

    private void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLogoutPresenter = new LogoutPresenter(this);
        mGetUsersPresenter = new GetUsersPresenter(this);

        getUsers();
        getRSAKey();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemClickListener(this);

    }

    private void getRSAKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        RSA rsa = new RSA(this);
        rsa.getRsaKey();
    }

    //get User from database
    private void getUsers() {
        Log.e("UserListActivity", "getuser");
        mGetUsersPresenter.getAllUsers();
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        Log.e("UserListActivity", "list user:  " + users.size());
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mUserListingRecyclerAdapter = new UserListingRecyclerAdapter(users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewAllUserListing.setLayoutManager(linearLayoutManager);
        mRecyclerViewAllUserListing.setAdapter(mUserListingRecyclerAdapter);
        mUserListingRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetAllUsersFailure(String message) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        ChatActivity.startActivity(this,
                mUserListingRecyclerAdapter.getUser(position).email,
                mUserListingRecyclerAdapter.getUser(position).uid,
                mUserListingRecyclerAdapter.getUser(position).firebaseToken,
                mUserListingRecyclerAdapter.getUser(position).publicKey);
        Log.e("Users list", mUserListingRecyclerAdapter.getUser(position).publicKey);
    }


    @Override
    public void onRefresh() {
        getUsers();
    }


    //Create menu item Logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int view = item.getItemId();
        switch (view) {
            case R.id.action_logout:
                Log.e("onclick", "logout");
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

//Logout AlertDialog

    private void logout() {
        Log.e("UserListActivity", "Logout");
        new AlertDialog.Builder(this)
                .setTitle("logout")
                .setMessage("are you sure")
                .setPositiveButton("logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mLogoutPresenter.logout();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    // Return Login Activity
    @Override
    public void onLogoutSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        LoginActivity.startIntent(this,
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onLogoutFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
