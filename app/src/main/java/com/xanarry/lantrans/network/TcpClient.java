package com.xanarry.lantrans.network;

/**
 * Created by xanarry on 2016/5/22.
 */

import android.util.Log;

import com.xanarry.lantrans.utils.Configuration;
import com.xanarry.lantrans.minterfaces.ProgressListener;
import com.xanarry.lantrans.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;


public class TcpClient {
    private InetAddress serverAddress;
    private int serverPort;
    private Socket clientSocket;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private ProgressListener progressListener;
    private String TAG;

    public TcpClient(HostAddress address, ProgressListener progressListener) {
        this.serverAddress = address.getAddress();
        this.serverPort = address.getPort();
        this.clientSocket = null;
        this.bufferedInputStream = null;
        this.bufferedOutputStream = null;
        this.progressListener = progressListener;
        this.TAG = this.getClass().getName();
    }

    public TcpClient(InetAddress address, int port, ProgressListener progressListener) {
        this.serverAddress = address;
        this.serverPort = port;
        this.clientSocket = null;
        this.bufferedInputStream = null;
        this.bufferedOutputStream = null;
        this.progressListener = progressListener;
        this.TAG = this.getClass().getName();
    }

    public void close() {
        try {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            if(bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String connectReceiver(String greetingMsg) {
        //发送文件描述信息
        if (greetingMsg.length() == 0) {
            greetingMsg = "hello";
        }
        greetingMsg += Configuration.DELIMITER;

        //在Tcp连接的建立中, 无需发送机器相关的信息, 只发送文件描述信息;
        String replyMsg = "";

        for (int i = 0; i < Configuration.CONNECT_TIMES; i++) {
            try {
                //创建socket并得到其输入输出流用于传输数据
                clientSocket = new Socket(serverAddress, serverPort);

                bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
                bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());

                //将文件描述发送给服务器, 以便服务器做好准备接收的工作
                Log.e(TAG, "给接收方发送文件描述:" + greetingMsg);
                bufferedOutputStream.write(greetingMsg.getBytes("utf-8"));//>>>>>>>>>>>>>>>>
                bufferedOutputStream.flush();

                byte[] buf = new byte[Configuration.STRING_BUF_LEN];
                bufferedInputStream.read(buf);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                replyMsg = Utils.getMessage(buf);
                Log.e(TAG, "收到回复信息:" + replyMsg);
                break;
            } catch (IOException e) {
                Log.e(TAG, ">>>ERROR<<" + e.getStackTrace().toString());
            }
        }
        return replyMsg;
    }

    public int sendFile(ArrayList<File> files) {
        /////////////////////////////socket的输出流       发送的文件
        Log.e(TAG, "正在发送:" + files.toString());
        int filePosition = 0;
        for (; filePosition < files.size(); filePosition++) {
            long hasSend = 0;
            long lastimeSend = 0;
            long startTime = 0;
            long endTime = 0;
            double speed = 0L;
            int actualRead = 0;

            File file = files.get(filePosition);

            byte[] fileBuf = new byte[Configuration.FILE_IO_BUF_LEN];
            byte[] ackBuf = new byte[Configuration.STRING_BUF_LEN];

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);//打开文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                String curFileInfo = file.getName() + Configuration.FILE_LEN_SPT + file.length() + Configuration.DELIMITER;

                //发送当前文件描述
                Log.e(TAG, "start send:" + curFileInfo);
                bufferedOutputStream.write(curFileInfo.getBytes());//>>>>>>>>>>>>>>>>>>>>>>>>>
                bufferedOutputStream.flush();

                ackBuf = new byte[Configuration.STRING_BUF_LEN];
                //接收确认
                bufferedInputStream.read(ackBuf);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                Log.e(TAG, "reciver ack" + Utils.getMessage(ackBuf));

                startTime = System.nanoTime();
                if (file.length() == 0) {
                    progressListener.updateProgress(filePosition, 100, 100, 888);
                    continue;
                }

                //读取文件并发送文件
                while ((actualRead = fileInputStream.read(fileBuf, 0, Configuration.FILE_IO_BUF_LEN)) > 0) {//读本地文件
                    // send to server
                    bufferedOutputStream.write(fileBuf, 0, actualRead);//写入到网络
                    endTime = System.nanoTime();

                    // accumulate total size
                    hasSend += actualRead;
                    long diffTime = endTime - startTime;
                    //计算传输速度
                    if (diffTime >= 500000000) {//0.5秒更新一次
                        long diffSize = hasSend - lastimeSend;
                        speed = ((double) diffSize / (double) diffTime) * (1000000000.0 / 1024.0);
                        lastimeSend = hasSend;
                        startTime = endTime;
                    }
                    //Log.e(TAG, filePosition + "   " + hasSend + "   " + file.length() + "   " + speed);
                    progressListener.updateProgress(filePosition, hasSend, file.length(), new Double(speed).intValue());

                    if (hasSend == file.length()) {//传输完毕
                        bufferedOutputStream.flush();
                        break;
                    }
                }

                ackBuf = new byte[Configuration.STRING_BUF_LEN];
                //接收接收文件大小确认
                bufferedInputStream.read(ackBuf);
                String ackSize = Utils.getMessage(ackBuf);
                Log.e(TAG, "reciver ack filesize" + ackSize);

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return filePosition;
            }
        }
        return filePosition;
    }
}
