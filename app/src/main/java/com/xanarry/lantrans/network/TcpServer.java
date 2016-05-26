package com.xanarry.lantrans.network;

import android.util.Log;

import com.xanarry.lantrans.utils.FileDesc;
import com.xanarry.lantrans.utils.Configuration;
import com.xanarry.lantrans.minterfaces.ProgressListener;
import com.xanarry.lantrans.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by xanarry on 2016/5/22.
 */
public class TcpServer {
    private ProgressListener progressListener;
    private int port;
    private String TAG;
    private ServerSocket serverSocket;
    private Socket channel;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;

    public TcpServer(int port, ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.port = port;
        TAG = TcpServer.class.getName();
    }

    public TcpServer(ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.port = Configuration.TCP_PORT; //偷懒就使用直接使用65500端口
        TAG = TcpServer.class.getName();
    }

    public ArrayList<FileDesc> waitSenderConnect() {
        ArrayList<FileDesc> files = new ArrayList<>();
        String fileInfo = "";
        byte[] inputBuf = new byte[Configuration.STRING_BUF_LEN];

        try {
            serverSocket = new ServerSocket(this.port);//创建tcp服务器, 接收文件
            channel = serverSocket.accept();//建立链接
            channel.setKeepAlive(Boolean.TRUE);

            //获取socket的输入输出流
            bufferedInputStream = new BufferedInputStream(channel.getInputStream());
            bufferedOutputStream = new BufferedOutputStream(channel.getOutputStream());

            bufferedInputStream.read(inputBuf);//读取要接收文件的描述信息<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            fileInfo = Utils.getMessage(inputBuf);

            //分离字符串形式的文件描述信息, 保持到arraylist
            for (String file : fileInfo.split(Configuration.FILES_SPT)) {
                String[] fd = file.split(Configuration.FILE_LEN_SPT);
                files.add(new FileDesc(fd[0], Long.parseLong(fd[1])));
            }

            bufferedOutputStream.write((Utils.getMessage(inputBuf) + Configuration.DELIMITER).getBytes("utf-8"));//将客户端发送的信息原封回复, 表示可以开始传输文件>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            bufferedOutputStream.flush();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return files;
    }

    public int recieveFile(ArrayList<FileDesc> files, String savePath) {
        byte[] recvBuf = new byte[Configuration.STRING_BUF_LEN];
        int filePosition = 0;
        String msg = "";

        for (; filePosition < files.size(); filePosition++) {
            try {
                long hasRecieve = 0;
                long lastimeRecv = 0;
                long startTime = 0;
                long endTime = 0;
                double speed = 0L;
                int actualLen;
                FileDesc fileDesc = files.get(filePosition);

                FileOutputStream fileOutputStream = null;
                Log.e(TAG, "open file:" + savePath + "/" + fileDesc.getName());
                File newFile = new File(savePath, fileDesc.getName());
                newFile.createNewFile();
                newFile.setWritable(true);
                fileOutputStream = new FileOutputStream(newFile);

                Log.e(TAG, "start receive:" + fileDesc.getName() + " size:" + fileDesc.getLength());

                bufferedInputStream.read(recvBuf);//接收即将要发送的文件<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                msg = Utils.getMessage(recvBuf);

                Log.e("get get:", msg);
                msg += Configuration.DELIMITER;

                bufferedOutputStream.write(msg.getBytes("utf-8"));//发送准备接收的确认>>>>>>>>>>>>>>>>>>>>>>>>>>>
                bufferedOutputStream.flush();

                String[] senderTellFileDesc = msg.split(Configuration.FILE_LEN_SPT);
                Log.e(TAG, "receive file:" + senderTellFileDesc[0] + " size:" + senderTellFileDesc[1]);

                startTime = System.nanoTime();
                if (fileDesc.getLength() == 0) { //接收空文件处理
                    progressListener.updateProgress(filePosition, 100, 100, 888);
                    continue;
                }

                //从网络中读取文件字节流
                byte[] fileBuf = new byte[Configuration.FILE_IO_BUF_LEN];
                while ((actualLen = bufferedInputStream.read(fileBuf, 0, Configuration.FILE_IO_BUF_LEN)) > 0) {
                    //将网络中的字节流写入本地文件
                    fileOutputStream.write(fileBuf, 0, actualLen);
                    endTime = System.nanoTime();
                    hasRecieve += actualLen;

                    long diffTime = endTime - startTime;
                    if (diffTime >= 500000000) {//计算传输速度0.5秒一次更新
                        long diffSize = hasRecieve - lastimeRecv;
                        speed = ((double) diffSize / (double) diffTime) * (1000000000.0 / 1024.0);
                        lastimeRecv = hasRecieve;
                        startTime = endTime;
                    }

                    progressListener.updateProgress(filePosition, hasRecieve, fileDesc.getLength(), new Double(speed).intValue());
                    // recieve all part of file
                    if (hasRecieve == fileDesc.getLength()) {
                        bufferedOutputStream.flush();
                        break;
                    }
                }

                Log.e("=================", hasRecieve + "-----" + fileDesc.getLength());
                if (hasRecieve == fileDesc.getLength()) {
                    String sizeAck = hasRecieve + Configuration.DELIMITER;
                    bufferedOutputStream.write(sizeAck.getBytes("utf-8"));
                    bufferedOutputStream.flush();
                }

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                Log.e(TAG, "EXCEPTION RAISED:" + e1.getMessage());
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                // Log.e(TAG, "发送文件的过程中发送异常, 目测网络挂了或者对方关掉的软件");
            }
        }
        return filePosition;
    }

    public void close() throws IOException {
        bufferedInputStream.close();
        bufferedOutputStream.close();
        serverSocket.close();
        channel.close();
    }
}
