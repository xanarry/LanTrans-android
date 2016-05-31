package com.xanarry.lantrans.utils;

import java.io.Serializable;

/**
 * Created by xanarry on 2016/5/24.
 */
public class FileDesc implements Serializable {
    //用户生成文件的描述信息发送给
    private String name;
    private long length;

    public FileDesc(String name, long length) {
        super();
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "FileDesc [name=" + name + ", length=" + length + "]";
    }
}
