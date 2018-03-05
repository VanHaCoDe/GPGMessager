package com.doan.levanha.pgpmessager.UI.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doan.levanha.pgpmessager.Core.chat.ChatContract;
import com.doan.levanha.pgpmessager.Core.chat.ChatPresenter;
import com.doan.levanha.pgpmessager.R;
import com.doan.levanha.pgpmessager.UI.Adapter.ChatRecyclerAdapter;
import com.doan.levanha.pgpmessager.models.Chat;
import com.doan.levanha.pgpmessager.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements ChatContract.View, TextView.OnEditorActionListener {
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    private AlertDialog dialogSetPassword;




    public static void startActivity(Context context,
                                     String receiver,
                                     String receiverUid,
                                     String firebaseToken,
                                     String publicKeyReceiver) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.putExtra(Constants.ARG_PUBLIC_KEY, publicKeyReceiver);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        binView();
        init();
    }

    private void binView() {
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) findViewById(R.id.edit_text_message);
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("loading");
        mProgressDialog.setMessage("please wait");
        mProgressDialog.setIndeterminate(true);

        mETxtMessage.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.setChatActivityOpen(false);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (!mETxtMessage.getText().toString().equals(""))
                sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        String message = mETxtMessage.getText().toString();
        String receiver = getIntent().getExtras().getString(Constants.ARG_RECEIVER);
        String receiverUid = getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                System.currentTimeMillis(), null);
        mChatPresenter.sendMessage(this.getApplicationContext(),
                chat,
                receiverFirebaseToken);
    }

    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        Log.e("Chat", chat.message.toString());
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    //
    //
    //
    //
    //
    //
    //
    //


    //Create menu item privatechat
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int view = item.getItemId();
        switch (view) {
            case R.id.action_private_chat:
                checkPrivateromExist();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPrivateromExist() {


        final String room_type_1 = "private" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_"
                + getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);
        final String room_type_2 = "private" + getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID) + "_"
                + FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    goToPrivatechatActivity("");


                } else if (dataSnapshot.hasChild(room_type_2)) {
                    goToPrivatechatActivity("");

                } else createDialogSetpassword();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void createDialogSetpassword() {

        LayoutInflater inflater = getLayoutInflater();
        View setPassWordview = inflater.inflate(R.layout.dialog_set_password, null);
        final EditText editTextDialogpassword = (EditText) setPassWordview.findViewById(R.id.edit_text_dialog_password);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setView(setPassWordview);
        alert.setCancelable(false);


        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passWord = editTextDialogpassword.getText().toString();

                if (!passWord.equals("")) {
                    Log.e("Chatactivity", passWord);
                    goToPrivatechatActivity(passWord);

                }
            }
        });
        dialogSetPassword = alert.create();
        dialogSetPassword.show();


    }


    private void goToPrivatechatActivity(String passWord) {
        ChatPrivateActivity.startActivity(this,
                getIntent().getExtras().getString(Constants.ARG_RECEIVER),
                getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN),
                getIntent().getExtras().getString(Constants.ARG_PUBLIC_KEY),
                passWord);
    }


}
