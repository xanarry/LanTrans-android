package com.xanarry.lantrans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xanarry.lantrans.utils.Utils;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.sendBtn).setOnClickListener(new MyOnClickListener());
        findViewById(R.id.recvBtn).setOnClickListener(new MyOnClickListener());
    }

    private final class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (R.id.sendBtn == v.getId()) { //send file
                Intent sendFileIntent = new Intent(getBaseContext(), SendActivity.class);
                startActivity(sendFileIntent);
            } else if (R.id.recvBtn == v.getId()) {//recive file
                Intent receiveFileActivity = new Intent(getBaseContext(), ReceiveActivity.class);
                startActivity(receiveFileActivity);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String msg = "作   者: xanarry\n" +
                    "邮   箱: xanarry@163.com\n" +
                    "完成日期: 2016/5/25\n\n" +

                    "简   介: LanTrans是一个用来解决手机与电脑之间文件互传的问题," +
                    "只要任意两台设备在同一个局域网中, 就能轻松的实现文件的分享, " +
                    "目前移动端只支持安卓, 桌面端支持Windows, Linux, OSX, " +
                    "如果您需要桌面端, 或者源码请到github下载\n\n" +

                    "声   明: app中的文件选择和目录选择非原创, 使用了\n" +
                    "[作者:AlexCheung(张文亮)] github项目:FileSelector" +
                    "中的源码, 在此对原作者表示感谢!\n\n" +

                    "github地址: https://github.com/xanarry\n";
            Utils.showDialog(this, "关于 Lantrans", msg);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
