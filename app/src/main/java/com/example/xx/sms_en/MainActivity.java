package com.example.xx.sms_en;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import com.google.zxing.client.android.CaptureActivity;

import java.io.File;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends ActionBarActivity {

    EditText number,content;
    Button quit,send,cryptSend,saveKey;
    SmsManager sManager;
    Key k;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取SmsManager即信息管理器
        sManager = SmsManager.getDefault();
        //获取文本框和按钮
        number = (EditText)findViewById(R.id.number);
        content = (EditText)findViewById(R.id.content);
        quit = (Button)findViewById(R.id.quit);
        send = (Button)findViewById(R.id.send);
        cryptSend = (Button)findViewById(R.id.cryptSend);
        saveKey = (Button)findViewById(R.id.save);


        //发送
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                    //创建一个PendingIntent对象
                    PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, new Intent(), 0);
                    //发送短信
                    //短信具有字数限制，分条发送
                    ArrayList<String> list = sManager.divideMessage(content.getText().toString());  //因为一条短信有字数限制，因此要将长短信拆分
                    for(String text:list)
                    {
                        sManager.sendTextMessage(number.getText().toString(), null, text, pi, null);
                    }

                    // sManager.sendTextMessage(number.getText().toString(), null, content.getText().toString(), pi, null);
//                if (content.getText().toString().length() > 70) {
//                    ArrayList<String> msgs = sManager.divideMessage(content.getText().toString());
//                    ArrayList<PendingIntent> sentIntents =  new ArrayList<PendingIntent>();
//                    for(int i = 0;i<msgs.size();i++){
//                        sentIntents.add(pi);
//                    }
//                    sManager.sendMultipartTextMessage(number.getText().toString(), null, msgs, sentIntents, null);
//                } else {
//                    sManager.sendTextMessage(number.getText().toString(), null, content.getText().toString(), pi, null);
//                }
                    //提示短信发送成功
                    Toast.makeText(MainActivity.this, "短信发送成功", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        //加密发送
        cryptSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String num = number.getText().toString();
                String key = DatabaseOp.queryKey(num, "1", MainActivity.this);
                SecretKeySpec keySpec = new SecretKeySpec(Base64.decode(key, Base64.DEFAULT),"AES");
                //content.setText(key);
                try {
                    Cipher cf = Cipher.getInstance("AES");
                 //   k = Coder.getKeyBybase64(key);
//                    if(k==null)
//                        content.setText("K为空");
                    cf.init(Cipher.ENCRYPT_MODE, keySpec);
                    //加密
                    byte secret[] = cf.doFinal(content.getText().toString().getBytes());
                    //进行Base64编码
                    String secretBase64 = "密文：" + Base64.encodeToString(secret, Base64.DEFAULT);
                    //创建一个PendingIntent对象
                    PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, new Intent(), 0);
                    //发送短信
                    //短信具有字数限制，分条发送
                   // ArrayList<String> list = sManager.divideMessage(secretBase64);  //因为一条短信有字数限制，因此要将长短信拆分
                  //  for (String text : list) {
                  //      sManager.sendTextMessage(number.getText().toString(), null, text, pi, null);
                  //  }
                    // sManager.sendTextMessage(number.getText().toString(), null, content.getText().toString(), pi, null);
                    if (secretBase64.length() > 70) {
                        ArrayList<String> msgs = sManager.divideMessage(secretBase64);
                        ArrayList<PendingIntent> sentIntents =  new ArrayList<PendingIntent>();
                        for(int i = 0;i<msgs.size();i++){
                            sentIntents.add(pi);
                        }
                        sManager.sendMultipartTextMessage(number.getText().toString(), null, msgs, sentIntents, null);
                    } else {
                        sManager.sendTextMessage(number.getText().toString(), null, secretBase64, pi, null);
                    }
                    //提示短信发送成功
                    Toast.makeText(MainActivity.this, "短信发送成功", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        //退出
        quit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
                System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
            }
        });

        //发送密钥
        findViewById(R.id.sendKey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //1、生成对称密钥
                    KeyGenerator kg = KeyGenerator.getInstance("AES");
                    //使用用户提供的随机源初始化此密钥生成器，使其具有确定的密钥大小。
                    /*keysize - 密钥大小。这是特定于算法的一种规格，是以位数为单位指定的。
                      random - 此密钥生成器的随机源 */
                    kg.init(128, new SecureRandom());//SecureRandom()此类提供强加密随机数生成器 (RNG)。
                    k = kg.generateKey();
                    //1.1将密钥转化成字符串
                    byte b[] = k.getEncoded();
                    String key = Base64.encodeToString(b, Base64.DEFAULT);
                    //content.setText(key);
                    //获取电话号码
                    String num = number.getText().toString();
                    //0标识公钥，1标识对称密钥,将密钥放入数据库
                    String flag = DatabaseOp.queryKey(num, "1", MainActivity.this);
                    if (flag.equals("Empty"))
                        DatabaseOp.insertKey(num, key, "1", MainActivity.this);
                    else
                        DatabaseOp.updateKey(num, key, "1", MainActivity.this);
                    //content.setText(" "+flag);
                    //将密钥加密
                    //从数据库中取出公钥密钥
                    String publicKey = DatabaseOp.queryKey(num, "0", MainActivity.this);
                    //转换为公钥对象
                    RSAPublicKey key_public = Coder.getRSAPublidKeyBybase64(publicKey);
                    //加密
                    String encryptDate = "密钥：" + RSA.encryptByPublicKey(key, key_public);

                   // number.setText(encryptDate.length());
                    content.setText(encryptDate);

                    //发送
                    PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, new Intent(), 0);
                    //短信具有字数限制，分条发送
                    //ArrayList<String> list = sManager.divideMessage(encryptDate);
                    //因为一条短信有字数限制，因此要将长短信拆分
                    // for (String text : list) {
                    // sManager.sendTextMessage(number.getText().toString(), null, text, pi, null);
                    // }
                    //sManager.sendTextMessage(number.getText().toString(), null, encryptDate, pi, null);
                    if (encryptDate.length() > 70) {
                        ArrayList<String> msgs = sManager.divideMessage(encryptDate);
                        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                        for (int i = 0; i < msgs.size(); i++) {
                            sentIntents.add(pi);
                        }
                        sManager.sendMultipartTextMessage(number.getText().toString(), null, msgs, sentIntents, null);
                    }
                    else {
                        sManager.sendTextMessage(number.getText().toString(), null,encryptDate, pi, null);
                    }
                    //提示短信发送成功
                    Toast.makeText(MainActivity.this, "密钥发送成功", Toast.LENGTH_LONG).show();


                } catch (Exception e) {
                    e.getStackTrace().toString();
                }
                //startActivity(new Intent(MainActivity.this, SmsReciever.class));
            }
        });

        //保存公钥
        saveKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = content.getText().toString();
                String num = number.getText().toString();
                //0标识公钥，1标识对称密钥
                long flag = DatabaseOp.insertKey(num, key, "0", MainActivity.this);
              if(flag==-1)
                  Toast.makeText(MainActivity.this, "密钥保存失败", Toast.LENGTH_LONG).show();
              else
                  Toast.makeText(MainActivity.this, "密钥保存成功", Toast.LENGTH_LONG).show();
            }
        });

        //生成自己的公和私钥，并保存
        findViewById(R.id.generateKey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HashMap<String, Object> map = RSA.getKeys();
                    //生成公钥和私钥
                    RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
                    RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
                    //转化编码
                    byte b1[] = publicKey.getEncoded();
                    String c1 = Base64.encodeToString(b1, Base64.DEFAULT);
                    byte b2[] = privateKey.getEncoded();
                    String c2 = Base64.encodeToString(b2, Base64.DEFAULT);
                    String key = DatabaseOp.queryKey("0", "0", MainActivity.this);
                    if (key.equals("Empty")) {
                        //存入数据库
                        DatabaseOp.insertKey("0", c1, "0", MainActivity.this);
                        long flag = DatabaseOp.insertKey("1", c2, "0", MainActivity.this);
                    } else {
                        DatabaseOp.updateKey("0", c1, "0", MainActivity.this);
                        DatabaseOp.updateKey("1", c2, "0", MainActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "密钥生成成功", Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content.setText("");
                number.setText("");
            }
        });
        //查询公钥
        findViewById(R.id.query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = DatabaseOp.queryKey(number.getText().toString(),"0",MainActivity.this);
                content.setText(key);
            }
        });
        //生成二维码
        findViewById(R.id.QRmake).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                boolean success = false;
                String in = content.getText().toString();
                String filePath = getFileRoot(MainActivity.this) + File.separator
                        + "qr_" + System.currentTimeMillis() + ".jpg";
                if (in.equals("")){
                    Toast.makeText(MainActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }else{
                     success = QRCodeUtil.createQRImage(in,1000,1000,BitmapFactory.decodeResource(getResources(), R.mipmap.sms),filePath);
                }
                if(success)
                {
                    Intent intent =  new Intent(MainActivity.this,QR.class);
//                    Bundle b = new Bundle();
//                    b.putString("dir", filePath);
                    intent.putExtra("dir",filePath);
                    startActivity(intent);
                }
        }});
//扫描二维码
        findViewById(R.id.QRscan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent,1);
            }
        });
        }
    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if (resultCode==RESULT_OK){
                String result=data.getStringExtra("result");
                content.setText(result);
         //   }
       // }
    }
    }


