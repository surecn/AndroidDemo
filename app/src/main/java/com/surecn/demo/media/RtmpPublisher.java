package com.surecn.demo.media;

import java.io.IOException;

public class RtmpPublisher {

    private Object mutex = new Object();

    static {
        System.loadLibrary("android-rtmp");
    }

    public void rtmpConnect(String url) throws IOException,IllegalStateException {
        synchronized (mutex) {
            connect(url);
        }
    }

    public void rtmpSend(byte[] data) throws IllegalStateException {
        if (data == null || data.length == 0) {
            throw new IllegalStateException("Invalid Video Data");
        }
        synchronized (mutex) {
            send(data);
        }
    }

    public void rtmpClose() {
        synchronized (mutex) {
            close();
        }
    }

    private native int connect(String url);

    private native void send(byte[] data);

    private native int close();
}
