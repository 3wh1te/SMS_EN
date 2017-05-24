package com.example.xx.sms_en;

import android.util.Base64;
import android.widget.Toast;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by xx on 2017/5/19.
 */
public class Coder
{

    public static RSAPublicKey getRSAPublidKeyBybase64(String base64s) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(base64s,Base64.DEFAULT));
        RSAPublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return publicKey;
    }

    public static RSAPrivateKey getRSAPrivateKeyBybase64(String base64s) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(base64s, Base64.DEFAULT));
        RSAPrivateKey privateKey = null;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return privateKey;
    }
    public static Key getKeyBybase64(String base64s)
    {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.decode(base64s, Base64.DEFAULT),"AES");
        SecretKey k = null;
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES");
            k = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return k;
    }
}
