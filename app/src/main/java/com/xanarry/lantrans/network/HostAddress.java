package com.xanarry.lantrans.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by xanarry on 2016/5/22.
 */
public class HostAddress {
    private InetAddress address;
    private int port;

    public HostAddress(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public HostAddress(String ip, int port) {
        try {
            this.address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "HostAddress{" +
                "address=" + address +
                ", port=" + port +
                '}';
    }
}
