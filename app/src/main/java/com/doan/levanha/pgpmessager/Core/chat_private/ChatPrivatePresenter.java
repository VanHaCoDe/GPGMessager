package com.doan.levanha.pgpmessager.Core.chat_private;

import android.content.Context;

import com.doan.levanha.pgpmessager.models.Chat;



public class ChatPrivatePresenter implements ChatPrivateContract.Presenter, ChatPrivateContract.OnSendMessageListener,
        ChatPrivateContract.OnGetMessagesListener {
    private ChatPrivateContract.View mView;
    private ChatPrivateInteractor mChatPrivateInteractor;

    public ChatPrivatePresenter(ChatPrivateContract.View view) {
        this.mView = view;
        mChatPrivateInteractor = new ChatPrivateInteractor(this, this);
    }

    @Override
    public void sendPrivateMessage(Context context, Chat chat, String receiverFirebaseToken) {
        mChatPrivateInteractor.sendPrivateMessageToFirebaseUser(context, chat, receiverFirebaseToken);
    }

    @Override
    public void getPrivateMessage(String senderUid, String receiverUid) {
        mChatPrivateInteractor.getPrivateMessageFromFirebaseUser(senderUid, receiverUid);
    }

    @Override
    public void onSendPrivateMessageSuccess() {
        mView.onSendPrivateMessageSuccess();
    }

    @Override
    public void onSendPrivateMessageFailure(String message) {
        mView.onSendPrivateMessageFailure(message);
    }

    @Override
    public void onGetPrivateMessagesSuccess(Chat chat) throws Exception {
        mView.onGetPrivateMessagesSuccess(chat);
    }

    @Override
    public void onGetPrivateMessagesFailure(String message) {
        mView.onGetPrivateMessagesFailure(message);
    }
}
