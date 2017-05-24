package com.example.xx.sms_en;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Base64;

import java.security.Key;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageReceiver extends BroadcastReceiver {
    public MessageReceiver() {
    }

    private static int id = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Bundle bundle = intent.getExtras();
        // 提取短信消息
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < messages.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }

        String address = messages[0].getOriginatingAddress().substring(3); // 获取发送方号码
        String fullMessage = "";
        //合并短信
        for (SmsMessage message : messages) {

            fullMessage += message.getMessageBody(); // 获取短信内容

        }

        String s[] = fullMessage.split("：");
        String title = s[0];
        System.out.println(title);
        if(title.equals("密文"))
        {
            byte m[] = Base64.decode(s[1],Base64.DEFAULT);
            String key = DatabaseOp.queryKey(address, "1", context);
            System.out.println(key);
            try{
                Cipher cf = Cipher.getInstance("AES");
                SecretKeySpec keySpec = new SecretKeySpec(Base64.decode(key, Base64.DEFAULT),"AES");
               // Key k = Coder.getKeyBybase64(key);
                cf.init(Cipher.DECRYPT_MODE, keySpec);
                byte b[] = cf.doFinal(m);
                fullMessage = new String(b);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(title.equals("密钥"))
        {
            long l1=-3,l2=-3;

            String flag = DatabaseOp.queryKey(address, "1", context);
            String key;
            String privatekey = DatabaseOp.queryKey("1", "0", context);
            RSAPrivateKey pk = Coder.getRSAPrivateKeyBybase64(privatekey);
            try{
                key = RSA.decryptByPrivateKey(s[1],pk);
                System.out.println(key);
                //插入数据
                if(flag.equals("Empty"))
                    l1 = DatabaseOp.insertKey(address,key, "1",context);
                else//更新密钥
                    l2 = DatabaseOp.updateKey(address,key,"1",context);
            }catch (Exception e)
            {
                System.out.println("l1:"+l1);
                System.out.println("l1:"+l2);
                System.out.println("出错");
                e.printStackTrace();
            }
        }
        Intent intent1 = new Intent(context,SmsReciever.class);
        intent1.putExtra("address",address);
        intent1.putExtra("message", fullMessage);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(context)
                .setContentTitle("来自"+address+":")
                .setContentText(fullMessage)
                .setSmallIcon(R.mipmap.sms)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(id++, notification);
        this.abortBroadcast();//没用只在模拟器里有用


        //Message message = SmsReciever.handler.obtainMessage(SmsReciever.UPDATE_UI);
        //Bundle bundle1 = new Bundle();
       // bundle1.putString("address",address);
        //bundle1.putString("message", fullMessage);
       // message.setData(bundle1);
       // message.sendToTarget();
    }
}
