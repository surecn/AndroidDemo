package com.surecn.demo.media.newflv;

import android.media.MediaCodec;
import com.surecn.demo.utils.log;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class H264Analyser {

    private final static int TYPE_IDR = 5;

    private final static int TYPE_SPS = 7;

    private final static int TYPE_PPS = 8;

    private final static int TYPE_UNIT_DELIMITER = 9;

    private byte[] mFramePPS;

    private byte[] mFrameSPS;

    private boolean mUploadPpsSps = true;

    private OnDataAnalyser mOnDataAnalyser;

    public static interface OnDataAnalyser {
        void onSpsPps(byte[] sps, byte[] pps);
        void onNalu(byte[] nalu, boolean isKeyFrame);
    }

    public H264Analyser(OnDataAnalyser onDataAnalyser) {
        mOnDataAnalyser = onDataAnalyser;
    }

    /**
     * 搜索0x000001
     * @return
     */
    int findFirstCode3(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        int index = byteBuffer.position();
        while (index < bufferInfo.offset + bufferInfo.size - 3) {
            if (byteBuffer.get(index) == 0x0 && byteBuffer.get(index + 1) == 0x0) {
                if (byteBuffer.get(index + 2) == 0x01) {
                    return index + 3;
                }
            }
        }
        return -1;
    }

//    /**
//     * 搜索0x0000000001
//     * @return
//     */
//    int findFirstCode4(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
//        int index = byteBuffer.position();
//        while (index < bufferInfo.offset + bufferInfo.size - 4) {
//            if (byteBuffer.get(index) == 0x0 && byteBuffer.get(index + 1) == 0x0 && byteBuffer.get(index + 2) == 0x0) {
//                if (byteBuffer.get(index + 3) == 0x01) {
//                    return index + 3;
//                }
//            }
//        }
//        return -1;
//    }

    byte[] getNALU(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        //获取NALU头部标识
        int position = findFirstCode3(byteBuffer, bufferInfo);
        if(position < 3) {
            return null;
        }
        for (int i = 0; i < position; i++) {
            byteBuffer.get();
        }
        int nextPosition = findFirstCode3(byteBuffer, bufferInfo);
        if (nextPosition < 3) {
            nextPosition = bufferInfo.size + 3;
        }
        byte[] naluBytes = new byte[nextPosition - position - 3];
        byteBuffer.get(naluBytes);
        return naluBytes;
    }

    /**
     * 判断是不是类型9
     * @param nalu
     * @return
     */
    boolean isAccessUnitDelimiter(byte[] nalu) {
        if (nalu.length <= 0) {
            return false;
        }
        int nalu_type = nalu[0] | 0x1f;//nalu第一个字节后5位表示类型
        return nalu_type == TYPE_UNIT_DELIMITER;
    }

    /**
     * 判断是不是类型8
     * @return
     */
    boolean isPPS(byte[] nalu) {
        if (nalu.length <= 0) {
            return false;
        }
        int nalu_type = nalu[0] | 0x1f;//nalu第一个字节后5位表示类型
        return nalu_type == TYPE_PPS;
    }

    /**
     * 判断是不是类型7
     * @return
     */
    boolean isSPS(byte[] nalu) {
        if (nalu.length <= 0) {
            return false;
        }
        int nalu_type = nalu[0] | 0x1f;//nalu第一个字节后5位表示类型
        return nalu_type == TYPE_SPS;
    }

    boolean isKeyFrame(byte[] nalu) {
        if (nalu.length <= 0) {
            return false;
        }
        int nalu_tpe = nalu[0] | 0x1f;//nalu第一个字节后5位表示类型
        return nalu_tpe == TYPE_IDR;
    }

    /**
     *
     */
    void parse(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        boolean isKeyFrame = false;
        ArrayList<byte[]> frames = new ArrayList<>();
        while (byteBuffer.position() < bufferInfo.offset + bufferInfo.size) {
            byte[] nalu = getNALU(byteBuffer, bufferInfo);
            if(nalu == null) {
                log.i("nalu not match");
                break;
            }
            //忽略类型9
            if (isAccessUnitDelimiter(nalu)) {
                continue;
            }
            if (isSPS(nalu)) {
                mFrameSPS = nalu;
                continue;
            }
            if (isPPS(nalu)) {
                mFramePPS = nalu;
                continue;
            }
            if (isKeyFrame(nalu)) {
                isKeyFrame = true;
            } else {
                isKeyFrame = false;
            }
            //长度
            int length = nalu.length;
            byte[] lenBytes=new byte[4];
            lenBytes[3] = (byte) (length>>24);
            lenBytes[2] = (byte) (length>>16);
            lenBytes[1] = (byte) (length>>8);
            lenBytes[0] = (byte) length;
            frames.add(lenBytes);
            frames.add(nalu);
        }
        if (mFramePPS != null && mFrameSPS != null && mUploadPpsSps) {
            if(mOnDataAnalyser != null) {
                mOnDataAnalyser.onSpsPps(mFrameSPS, mFramePPS);
            }
            mUploadPpsSps = false;
        }
        if(frames.size() == 0) {
            return;
        }
        int size = 0;
        for (int i = 0; i < frames.size(); i++) {
            byte[] frame = frames.get(i);
            size += frame.length;
        }
        byte[] data = new byte[size];
        int currentSize = 0;
        for (int i = 0; i < frames.size(); i++) {
            byte[] frame = frames.get(i);
            System.arraycopy(frame, 0, data, currentSize, frame.length);
            currentSize += frame.length;
        }
        if(mOnDataAnalyser != null) {
            mOnDataAnalyser.onNalu(data, isKeyFrame);
        }

    }


}
