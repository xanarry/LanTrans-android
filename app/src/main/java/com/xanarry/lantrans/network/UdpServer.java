package com.xanarry.lantrans.network;

import android.util.Log;

import com.xanarry.lantrans.utils.Configuration;
import com.xanarry.lantrans.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by xanarry on 2016/5/22.
 */
public class UdpServer {
    private int port;
    private String TAG;
    DatagramSocket serverSockent;

    public UdpServer(int port) {
        this.port = port;
        this.TAG = UdpServer.class.getName();
    }

    public DatagramPacket waitClient() {
        //server wait and receive message broadcasted by client
        DatagramPacket packet = null;
        byte[] recvBuf = new byte[Configuration.STRING_BUF_LEN];
        try {
            Log.e(TAG, "open udp server at" + port);
            serverSockent = new DatagramSocket(port);//设置服务器端口, 监听广播信息
            DatagramPacket message = new DatagramPacket(recvBuf, recvBuf.length);

            serverSockent.receive(message);//接收client的广播信息
            String strmsg = Utils.getMessage(message.getData());
            Log.e(TAG, "got sender:" + strmsg);
            message.setData((Configuration.currentTcpPort + Configuration.DELIMITER).getBytes("utf-8"));//将服务器的主机名发送给client
            serverSockent.send(message);//回复信息tcp要使用的Tcp端口给client
            Log.e(TAG, "find client at: " + message.getAddress().getHostAddress() + " port:" + message.getPort() + " name:" + strmsg);

            message.setData(strmsg.getBytes("utf-8"));
            packet = message;
        } catch (SocketException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return packet;//将client发送的数据包返回给调用者, 里面包含client的地址, 端口, 主机名
    }
    public void close() {
        serverSockent.close();
    }
}
