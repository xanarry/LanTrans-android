package com.xanarry.lantrans.utils;

public class FileDesc {
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
