package com.example.xx.sms_en;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.*;

/**
 * Created by Mr.Chen on 2017/5/14.
 */
public class Sms
{
    private String sms,sms_en;
    private KeyGenerator keygen;
    private static final int MAX_ENCRYPT_BLOCK = 117;
    PrivateKey privateKey;
    PublicKey publicKey;
    //RSA加密算法
    public Sms(String sms)
    {
        this.sms = sms;
    }
    public int GetY()
    {
        return 0;
    }
    //生成Key
    public void generateKey()
    {
        privateKey = RSA.getPrivateKey("","");
        publicKey =  RSA.getPublicKey("","");
    }
    public String Encrypt()
    {
        try {
          sms_en = RSA.encryptByPublicKey(sms,RSA.getPublicKey("",""));
            return sms_en;
        }
        catch (Exception e)
        {

        }
        return sms_en;
    }
    public String Decrypt()
    {
        try {
            sms = RSA.decryptByPrivateKey(sms_en,RSA.getPrivateKey("",""));
            return sms;
        }
        catch (Exception e)
        {

        }
        return sms;
    }


}
