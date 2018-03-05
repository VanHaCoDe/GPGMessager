package com.doan.levanha.pgpmessager.UI.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doan.levanha.pgpmessager.Core.chat_private.ChatPrivateContract;
import com.doan.levanha.pgpmessager.Core.chat_private.ChatPrivatePresenter;
import com.doan.levanha.pgpmessager.Core.private_message.pgp.HandlePGP;
import com.doan.levanha.pgpmessager.Core.private_message.puplic_key_cryptosystem.RSA;
import com.doan.levanha.pgpmessager.R;
import com.doan.levanha.pgpmessager.UI.Adapter.ChatRecyclerAdapter;
import com.doan.levanha.pgpmessager.models.Chat;
import com.doan.levanha.pgpmessager.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatPrivateActivity extends AppCompatActivity implements ChatPrivateContract.View, TextView.OnEditorActionListener {
    private RecyclerView mRecyclerViewPrivateChat;
    private EditText mETxtMessage;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatPrivateRecyclerAdapter;

    private ChatPrivatePresenter mChatPrivatePresenter;


    private String passWord;

    public static void startActivity(Context context,
                                     String receiver,
                                     String receiverUid,
                                     String firebaseToken,
                                     String publicKeyReceiver,
                                     String password) {
        Intent intent = new Intent(context, ChatPrivateActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.putExtra(Constants.ARG_PUBLIC_KEY, publicKeyReceiver);
        intent.putExtra(Constants.ARG_PASSWORD, password);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_private);

        binView();
        init();

        if (!passWord.equals("")) {
            try {
                sendSecurePassword();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void binView() {
        mRecyclerViewPrivateChat = (RecyclerView) findViewById(R.id.recycler_view_chat_private);
        mETxtMessage = (EditText) findViewById(R.id.edit_text_message_private);
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("loading");
        mProgressDialog.setMessage("please wait");
        mProgressDialog.setIndeterminate(true);

        mETxtMessage.setOnEditorActionListener(this);

        mChatPrivatePresenter = new ChatPrivatePresenter(this);
        mChatPrivatePresenter.getPrivateMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID));
        passWord = getIntent().getExtras().getString(Constants.ARG_PASSWORD);
        Log.e("Chatprivate", "passwordset: " + passWord);
    }

    private void sendSecurePassword() throws Exception {
        HandlePGP handlePGP = new HandlePGP(this);
        String md5SumPassword = handlePGP.getMD5SumpassWord(passWord);
        String signMd5SumpassWord = handlePGP.signMd5Sum(md5SumPassword);

        String passWord = getIntent().getExtras().getString(Constants.ARG_PASSWORD);
        String publicKeyReceiver = getIntent().getExtras().getString(Constants.ARG_PUBLIC_KEY);
        String securePassWord = handlePGP.securePasswordTosend(passWord, publicKeyReceiver);

        String securePassWordandSignMd5sumPassWord = "pass:" + securePassWord + "md5:" + signMd5SumpassWord;

        String receiver = getIntent().getExtras().getString(Constants.ARG_RECEIVER);
        String receiverUid = getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN);


        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                securePassWordandSignMd5sumPassWord,
                System.currentTimeMillis(),
                md5SumPassword);
        mChatPrivatePresenter.sendPrivateMessage(this, chat, receiverFirebaseToken);
    }

    private void getPassWordfromSecurePasswordReceive(Chat chat) throws Exception {

        HandlePGP handlePGP = new HandlePGP(this);
        Log.e("ChatPrivate", "message have password " + chat.message);
        String message = chat.message;
        String securePass = message.substring(message.indexOf("pass:") + 5, message.indexOf("md5:"));
        Log.e("ChatPrivate", "securePass " + securePass);
        String signMd5 = message.substring(message.indexOf("md5:") + 4);
        Log.e("ChatPrivate", "signMd5" + signMd5);
        String pass = handlePGP.getPassWordfromSecurePasswordreceive(securePass);

        Log.e("ChatPrivate", " password get " + pass);

        RSA rsa = new RSA(this);
        Boolean verify = handlePGP.verifyMd5Sum(chat.getMd5Sum(), signMd5,
                rsa.stringToPubkickey(getIntent().getExtras().getString(Constants.ARG_PUBLIC_KEY)));
        if (verify) {
            passWord = pass;
        } else {
            Toast.makeText(this, "Chat rom no secure. Please exit", Toast.LENGTH_SHORT);
            mETxtMessage.setEnabled(false);
        }
    }

    private void sendSecureMessage() throws Exception {
        String message = mETxtMessage.getText().toString();
        HandlePGP handlePGP = new HandlePGP(this);

        String secureMessage = handlePGP.encryptMessage(message, passWord);
        String receiver = getIntent().getExtras().getString(Constants.ARG_RECEIVER);
        String receiverUid = getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                secureMessage,
                System.currentTimeMillis(),
                null);
        mChatPrivatePresenter.sendPrivateMessage(this, chat, receiverFirebaseToken);
    }

    private String decryptSecureMessage(final String secureMessage) {

        return new HandlePGP(this).decryptMessage(secureMessage, passWord);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (!mETxtMessage.getText().toString().equals(""))
                try {
                    sendSecureMessage();
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
                }
            return true;
        }
        return false;
    }

    @Override
    public void onSendPrivateMessageSuccess() {
        mETxtMessage.setText("");
        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendPrivateMessageFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetPrivateMessagesSuccess(Chat chat) throws Exception {
        if (mChatPrivateRecyclerAdapter == null) {
            mChatPrivateRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
            mRecyclerViewPrivateChat.setAdapter(mChatPrivateRecyclerAdapter);

            if (TextUtils.equals(passWord, "")) {
                getPassWordfromSecurePasswordReceive(chat);
            }
        }
        Log.e("Chatprivate", "message: " + chat.getMessage());

        chat.message = decryptSecureMessage(chat.message);
        mChatPrivateRecyclerAdapter.add(chat);

        mRecyclerViewPrivateChat.smoothScrollToPosition(mChatPrivateRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetPrivateMessagesFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        final String room_type_1 = "private" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_"
                + getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().child(room_type_1).removeValue();
    }
}
