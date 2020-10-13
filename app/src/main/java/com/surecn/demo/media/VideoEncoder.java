package com.surecn.demo.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;
import com.surecn.demo.media.flv.FlvPacker;
import com.surecn.demo.media.flv.Packer;
import com.surecn.moat.utils.log;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static android.media.MediaCodec.CONFIGURE_FLAG_ENCODE;
import static android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
import static android.media.MediaFormat.KEY_BIT_RATE;
import static android.media.MediaFormat.KEY_MAX_INPUT_SIZE;


public class VideoEncoder {

    private int mWidth;

    private int mHeight;

    private MediaCodec mVideoEncodec;

    private long presentationTimeUs;

    private int colorFormat;

    private int aSampleRate;
    private int aChanelCount;

    private static final String VCODEC_MIME = MediaFormat.MIMETYPE_VIDEO_AVC;

    private final int FRAME_RATE = 15;

    public static final int VGOP = 30;

    private MediaCodec.BufferInfo vBufferInfo = new MediaCodec.BufferInfo();

    private FlvPacker mFlvPacker;

    private RtmpPublisher rtmpPublisher;

    SrsFlvMuxer srsFlvMuxer;

    private ExecutorService executors = Executors.newSingleThreadExecutor();

    public VideoEncoder(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        rtmpPublisher = new RtmpPublisher();
        initMediaCodec();
        mFlvPacker = new FlvPacker();
        mFlvPacker.initVideoParams(width, height, FRAME_RATE);
        try {
            rtmpPublisher.rtmpConnect("rtmp://192.168.6.41/live/test");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFlvPacker.setPacketListener(new Packer.OnPacketListener() {
            @Override
            public void onPacket(final byte[] data, final int packetType) {
//                if (frame.is_video()) {
//                    publisher.publishVideoData(frame.dts, frame.tag.frame.array(), (frame.avc_aac_type == SrsCodecVideoAVCType.SequenceHeader));
//                } else if (frame.is_audio()) {
//                    publisher.publishAudioData(frame.dts, frame.tag.frame.array());
//                }
                executors.execute(new Runnable() {
                    @Override
                    public void run() {
                        rtmpPublisher.rtmpSend(data);
                    }
                });

            }
        });
        mFlvPacker.start();

//        srsFlvMuxer = new SrsFlvMuxer();
//        try {
//            srsFlvMuxer.start("rtmp://192.168.6.41/live/test");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private MediaCodec initAudioEncoder() throws IOException {
        MediaCodec aencoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        MediaFormat format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC,
                aSampleRate, aChanelCount);
        format.setInteger(KEY_MAX_INPUT_SIZE, 0);
        format.setInteger(KEY_BIT_RATE, 1000 * 30);
        aencoder.configure(format, null, null, CONFIGURE_FLAG_ENCODE);
        return aencoder;
    }

    private void initMediaCodec() {
        //int bitrate = 2 * mWidth * mHeight * FRAME_RATE / 20;
        int bitrate = 500 * 1000;  // 500kbps
        try {
//            MediaCodecInfo mediaCodecInfo = selectCodec(VCODEC_MIME);
//            if (mediaCodecInfo == null) {
//                log.e("mMediaCodec null");
//                throw new RuntimeException("mediaCodecInfo is Empty");
//            }
            int colorFormat = selectColorFormat(VCODEC_MIME);
            //log.w("MediaCodecInfo " + mediaCodecInfo.getName());
            //mVideoEncodec = MediaCodec.createByCodecName(mediaCodecInfo.getName());
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
//            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
//            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VGOP / FRAME_RATE);
            mVideoEncodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mVideoEncodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaCodecInfo selectCodec(String mimeType) {
        MediaCodecInfo info = null;
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            //是否是编码器
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                Log.e("aaa", "equal:" + type);
                if (mimeType.equalsIgnoreCase(type)) {
                    Log.e("aaa", codecInfo.getName());
                    log.e("codecInfo " + codecInfo.getName());
                    info = codecInfo;
                }
            }
        }
        return info;
    }

    private boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            // these are the formats we know how to handle for this test
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                return false;
        }
    }

    private int selectColorFormat(String mimeType) throws IOException {

        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            //是否是编码器
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                Log.e("aaa", "equal:" + type);
                if (mimeType.equalsIgnoreCase(type)) {
                    Log.e("aaa", codecInfo.getName());
                    log.e("codecInfo " + codecInfo.getName());

                    MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
                    for (int j = 0; j < capabilities.colorFormats.length; j++) {
                        int colorFormat = capabilities.colorFormats[j];
                        if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
                            mVideoEncodec = MediaCodec.createByCodecName(codecInfo.getName());
                            return colorFormat;
                        }
                    }
                }
            }
        }

        return 0;   // not reached
    }



    public void putVideoData(byte[] data) {
        final int LENGTH = mHeight * mWidth;
        for (int i = LENGTH; i < (LENGTH + LENGTH / 4); i++) {
            byte temp = data[i];
            data[i] = data[i + LENGTH / 4];
            data[i + LENGTH / 4] = temp;
//            char x = 128;
//            buf[i] = (byte) x;
        }
//        ByteBuffer[] inputBuffers = mVideoEncodec.getInputBuffers();
//        ByteBuffer[] outputBuffers = mVideoEncodec.getOutputBuffers();
//
//        int inputBufferId = mVideoEncodec.dequeueInputBuffer(-1);
//        if (inputBufferId >= 0) {
//            // fill inputBuffers[inputBufferId] with valid data
//            ByteBuffer bb = inputBuffers[inputBufferId];
//            bb.clear();
//            bb.put(data, 0, data.length);
//            long pts = new Date().getTime() * 1000 - presentationTimeUs;
//            mVideoEncodec.queueInputBuffer(inputBufferId, 0, data.length, pts, 0);
//        }
//
//        for (; ; ) {
//            int outputBufferId = mVideoEncodec.dequeueOutputBuffer(vBufferInfo, 0);
//            if (outputBufferId >= 0) {
//                // outputBuffers[outputBufferId] is ready to be processed or rendered.
//                ByteBuffer bb = outputBuffers[outputBufferId];
//                //onEncodedAvcFrame(bb, vBufferInfo);
//                if (bb.hasArray()) {
//                    mFlvPacker.onVideoData(bb, vBufferInfo);
//                }
//                mVideoEncodec.releaseOutputBuffer(outputBufferId, false);
//            }
//            if (outputBufferId < 0) {
//                break;
//            }
//        }


        ByteBuffer[] inputBuffers = mVideoEncodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mVideoEncodec.getOutputBuffers();
        try {
            //查找可用的的input buffer用来填充有效数据
            int bufferIndex = mVideoEncodec.dequeueInputBuffer(-1);
            if (bufferIndex >= 0) {
                //数据放入到inputBuffer中
                ByteBuffer inputBuffer = inputBuffers[bufferIndex];
                inputBuffer.clear();
                inputBuffer.put(data, 0, data.length);
                //把数据传给编码器并进行编码
                mVideoEncodec.queueInputBuffer(bufferIndex, 0,
                        inputBuffers[bufferIndex].position(),
                        System.nanoTime() / 1000, 0);
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                //输出buffer出队，返回成功的buffer索引。
                int outputBufferIndex = mVideoEncodec.dequeueOutputBuffer(bufferInfo, 0);
                while (outputBufferIndex >= 0) {
                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    //进行flv封装
                    //srsFlvMuxer.writeSampleData(SrsFlvMuxer.VIDEO_TRACK, outputBuffer, bufferInfo);
                    mFlvPacker.onVideoData(outputBuffer, bufferInfo);
//                    if (outputBuffer.hasArray()) {
//                        mFlvPacker.onVideoData(outputBuffer, vBufferInfo);
//                    }
                    mVideoEncodec.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = mVideoEncodec.dequeueOutputBuffer(bufferInfo, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
