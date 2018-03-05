package com.doan.levanha.pgpmessager.Core.private_message.pgp;

import android.content.Context;

import com.doan.levanha.pgpmessager.Core.private_message.hash.MD5;
import com.doan.levanha.pgpmessager.Core.private_message.private_key_cryptosystem.AES;
import com.doan.levanha.pgpmessager.Core.private_message.puplic_key_cryptosystem.RSA;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Admin on 6/8/2017.
 */

public class HandlePGP {
    Context context;

    public HandlePGP(Context context) {
        this.context = context;
    }

    public String getMD5SumpassWord(final String password) throws Exception {
        // tạo md5 sum của pasword để xác đính tính toàn vẹn
        return new MD5().createMD5Sum(password);
    }

    public String signMd5Sum(final String md5Sum) throws Exception {
        // mã hóa md5Dum bằng privateKey người gửi để xác thực
        return new RSA(context).RSAsign(md5Sum);
    }

    public boolean verifyMd5Sum(final String plainText, final String signature, final PublicKey publicKeySender) throws Exception {
        return new RSA(context).RSAverify(plainText, signature, publicKeySender);
    }


    public String securePasswordTosend(final String password, final String publicKeyReceiver) throws Exception {
        // mã hóa pasword bằng khóa công khai người nhận đẻ truyền an toàn
        RSA rsa = new RSA(context);
        return rsa.RSAEncrypt(password, rsa.stringToPubkickey(publicKeyReceiver));

    }

    public String getPassWordfromSecurePasswordreceive(final String securePassWord) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        // Giải mã tin nhắn mã hóa bằng khóa bí mật của mình
        return new RSA(context).RSADecrypt(securePassWord);
    }

    public String encryptMessage(final String message, final String passWord) {
        // mã hóa tin nhắn gửi đi bằng mật khẩu
        return new AES().encrypt(message, passWord);
    }

    public String decryptMessage(final String encryptMessage, final String passWord) {
        // giải mã tin nhắn nhận đc bằng mật khẩu
        return new AES().decrypt(encryptMessage, passWord);
    }
}
