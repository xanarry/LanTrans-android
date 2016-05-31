package com.xanarry.lantrans.network;

import android.util.Log;

import com.xanarry.lantrans.minterfaces.SearchStateListener;
import com.xanarry.lantrans.utils.Configuration;
import com.xanarry.lantrans.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by xanarry on 2016/5/22.
 */
public class UdpClient {
    private SearchStateListener updateState;
    private int times;
    private int timeout;
    private int port;
    private String TAG;

    public UdpClient(SearchStateListener updateState, int timeout, int times, int port) {
        this.updateState = updateState;
        this.timeout = timeout;
        this.times = times;
        this.port = port;
        TAG = UdpClient.class.getName();
    }

    public UdpClient(SearchStateListener updateState, int port) {
        this.updateState = updateState;
        this.timeout = Configuration.SEARCH_TIMOUT;
        this.times = Configuration.SEARCH_TIMES;
        this.port = port;
        TAG = UdpClient.class.getName();
    }

    public UdpClient(SearchStateListener updateState) {
        this.updateState = updateState;
        this.timeout = Configuration.SEARCH_TIMOUT;
        this.times = Configuration.SEARCH_TIMES;
        this.port = Configuration.UDP_PORT;
        TAG = UdpClient.class.getName();
    }

    public HostAddress search() {
        DatagramPacket sendPacket = null;
        DatagramPacket recvPacket = null;
        DatagramSocket clientSocket = null;
        InetAddress address = null;
        DatagramPacket packet;
        String msg = "Lantrans Android UDPCLIENT" + Configuration.DELIMITER;

        byte[] recvBuf = new byte[Configuration.STRING_BUF_LEN];
        byte[] sendBuf = new byte[Configuration.STRING_BUF_LEN];
        address = Utils.getBroadcastAddr();//设置广播地址

        try {
            clientSocket = new DatagramSocket();//创建一个udpClient
            clientSocket.setBroadcast(true);//广播信息
            clientSocket.setSoTimeout(this.timeout * 1000);//如果2秒后没后得到服务器的回应, 抛出超时异常, 以便重新广播
            Log.e(TAG, "本机ip:" + Utils.getLocalHostLanIP() + " 广播地址:" + address);
            sendBuf = msg.getBytes("utf-8");
        } catch (SocketException e3) {
            e3.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
        recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

        int tryTimes = 1;
        while (tryTimes <= times) {//多次尝试
            try {
                clientSocket.send(sendPacket);//向服务器发送数据包
                clientSocket.receive(recvPacket);//如果没有收到数据包, 那么尝试多次
                if (recvPacket != null && new String(recvPacket.getData()).length() > 0) {
                    break;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                updateState.updateState(tryTimes, times);
                Log.e(TAG, "超时: " + tryTimes + "次" + "共:" + times + "次");
                tryTimes++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (tryTimes <= times) {
            String strPort = Utils.getMessage(recvPacket.getData());
            if (strPort != null && strPort.length() > 0) {
                int serverPort = Integer.parseInt(strPort);
                Log.e(TAG, new HostAddress(recvPacket.getAddress(), serverPort).toString());
                return new HostAddress(recvPacket.getAddress(), serverPort);
            }
        }
        return null;
    }
}
