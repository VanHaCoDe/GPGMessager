package com.doan.levanha.pgpmessager.Core.private_message.puplic_key_cryptosystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Admin on 6/7/2017.
 */

public class RSA {
    private final String PRIVATE_KEY = "privateKey";
    private final String PUBLIC_KEY = "publicKey";

    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    public SharedPreferences sharedPreferences;

    public static PublicKey getPublicKey() {
        return publicKey;
    }


    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    Context context;

    public RSA(Context context) {
        this.context = context;
    }

    public void createGenerateKey() throws NoSuchAlgorithmException {


        SecureRandom sr = new SecureRandom();
        //Thuật toán phát sinh khóa - Rivest Shamir Adleman (RSA)
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048, sr);

        //Phát sinh cặp khóa
        KeyPair kp = kpg.genKeyPair();
        //PublicKey
        publicKey = kp.getPublic();

        //PrivateKey
        privateKey = kp.getPrivate();
        saveRsaKey();
    }

    public void saveRsaKey() {
        sharedPreferences = context.getSharedPreferences("rsaKey", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PUBLIC_KEY, publicKeyTosring(publicKey));
        editor.putString(PRIVATE_KEY, privateKeyTosring(privateKey));
        editor.apply();
    }

    public void getRsaKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        sharedPreferences = context.getSharedPreferences("rsaKey", MODE_PRIVATE);
        privateKey = stringToPrivatekey(sharedPreferences.getString(PRIVATE_KEY, null));
        publicKey = stringToPubkickey(sharedPreferences.getString(PUBLIC_KEY, null));
    }

    public String publicKeyTosring(final PublicKey pubKey) {
        return Base64.encodeToString(pubKey.getEncoded(), Base64.DEFAULT);
    }

    public PublicKey stringToPubkickey(final String strPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyByte = Base64.decode(strPublicKey.getBytes(), Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public String privateKeyTosring(final PrivateKey priKey) {
        return Base64.encodeToString(priKey.getEncoded(), Base64.DEFAULT);
    }

    public PrivateKey stringToPrivatekey(final String strPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyByte = Base64.decode(strPrivateKey.getBytes(), Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePrivate(keySpec);

    }

    // mã hóa RSA bằng khóa công khai người nhận
    public String RSAEncrypt(final String plain, final PublicKey publicKeyReceiver) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {


        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyReceiver);
        byte[] encryptedBytes = cipher.doFinal(plain.getBytes());
        System.out.println("EEncrypted?????" + Base64.encodeToString(encryptedBytes, Base64.DEFAULT));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    //Giải mã RSA khóa bí mật của mình
    public String RSADecrypt(final String encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher1.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
        String decrypted = new String(decryptedBytes);
        return decrypted;
    }

    // kí số RSA bằng khóa bí mật của mình
    public String RSAsign(final String plainText) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes());

        byte[] signature = privateSignature.sign();

        return Base64.encodeToString(signature, Base64.DEFAULT);
    }

    // xác thực RSA bằng kháo công khai người gửi
    public boolean RSAverify(final String plainText, final String signature, final PublicKey publicKeySender) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKeySender);
        publicSignature.update(plainText.getBytes());

        byte[] signatureBytes = Base64.decode(signature, Base64.DEFAULT);

        return publicSignature.verify(signatureBytes);
    }


}
