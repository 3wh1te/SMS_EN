package com.example.xx.sms_en;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.security.Key;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.Cipher;


public class SmsReciever extends ActionBarActivity {
    private TextView sender;
    private TextView content;
    private IntentFilter receiveFilter;
    private MessageReceiver messageReceiver;

    public static final int UPDATE_UI = 0;

    public static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if(message.what == UPDATE_UI){
                String num  = message.getData().getString("address");
                String message1  = message.getData().getString("message");
               // content.setText(message1);
                //sender.setText(num);
            }
            return false;
        }
    });

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_receivesms);

        sender = (TextView) findViewById(R.id.sender);
        content = (TextView) findViewById(R.id.smsoutContent);

        //更新文本框
        Intent intent = getIntent();
        sender.setText(intent.getStringExtra("address"));
        content.setText(intent.getStringExtra("message"));


        //返回发送界面
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SmsReciever.this, MainActivity.class));
            }
        });


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

