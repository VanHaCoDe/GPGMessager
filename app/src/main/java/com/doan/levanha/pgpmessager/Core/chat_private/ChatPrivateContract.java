package com.doan.levanha.pgpmessager.Core.chat_private;

import android.content.Context;

import com.doan.levanha.pgpmessager.models.Chat;


public interface ChatPrivateContract {
    interface View {
        void onSendPrivateMessageSuccess();

        void onSendPrivateMessageFailure(String message);

        void onGetPrivateMessagesSuccess(Chat chat) throws Exception;

        void onGetPrivateMessagesFailure(String message);
    }

    interface Presenter {
        void sendPrivateMessage(Context context, Chat chat, String receiverFirebaseToken);

        void getPrivateMessage(String senderUid, String receiverUid);
    }

    interface Interactor {
        void sendPrivateMessageToFirebaseUser(Context context, Chat chat, String receiverFirebaseToken);

        void getPrivateMessageFromFirebaseUser(String senderUid, String receiverUid);
    }

    interface OnSendMessageListener {
        void onSendPrivateMessageSuccess();

        void onSendPrivateMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetPrivateMessagesSuccess(Chat chat) throws Exception;

        void onGetPrivateMessagesFailure(String message);
    }
}
