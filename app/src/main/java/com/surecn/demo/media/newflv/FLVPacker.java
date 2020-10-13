package com.surecn.demo.media.newflv;

import com.surecn.demo.media.flv.FlvPackerHelper;
import com.surecn.demo.media.flv.Packer;

import java.nio.ByteBuffer;

import static com.surecn.demo.media.flv.FlvPackerHelper.AUDIO_HEADER_SIZE;
import static com.surecn.demo.media.flv.FlvPackerHelper.AUDIO_SPECIFIC_CONFIG_SIZE;
import static com.surecn.demo.media.flv.FlvPackerHelper.FLV_HEAD_SIZE;
import static com.surecn.demo.media.flv.FlvPackerHelper.FLV_TAG_HEADER_SIZE;
import static com.surecn.demo.media.flv.FlvPackerHelper.PRE_SIZE;
import static com.surecn.demo.media.flv.FlvPackerHelper.VIDEO_HEADER_SIZE;
import static com.surecn.demo.media.flv.FlvPackerHelper.VIDEO_SPECIFIC_CONFIG_EXTEND_SIZE;
import static com.surecn.demo.media.flv.FlvPackerHelper.writeFlvTagHeader;

public class FLVPacker implements H264Analyser.OnDataAnalyser {

    public static final int HEADER = 0;
    public static final int METADATA = 1;
    public static final int FIRST_VIDEO = 2;
    public static final int FIRST_AUDIO = 3;
    public static final int AUDIO = 4;
    public static final int KEY_FRAME = 5;
    public static final int INTER_FRAME = 6;

    public static final int FLV_HEAD_SIZE = 9;

    private long mStartTime;

    private int mVideoWidth, mVideoHeight, mVideoFps;

    private int mAudioSampleRate, mAudioSampleSize;

    private boolean mIsStereo;

    private boolean isHeaderWrite;
    private boolean isKeyFrameWrite;

    private Packer.OnPacketListener packetListener;

    public void setPacketListener(Packer.OnPacketListener listener) {
        packetListener = listener;
    }


    public void writePpsAndSps(byte[] pps, byte [] sps) {

    }

    @Override
    public void onSpsPps(byte[] sps, byte[] pps) {
        if(packetListener == null) {
            return;
        }
        //写入Flv header信息
        writeFlvHeader();
        //写入Meta 相关信息
        writeMetaData();
        //写入第一个视频信息
        writeFirstVideoTag(sps, pps);
        //写入第一个音频信息
        writeFirstAudioTag();
        mStartTime = System.currentTimeMillis();
        isHeaderWrite = true;
    }

    @Override
    public void onNalu(byte[] nalu, boolean isKeyFrame) {
        int compositionTime = (int) (System.currentTimeMillis() - mStartTime);
        int packetType = INTER_FRAME;
        if (isKeyFrame) {
            isKeyFrameWrite = true;
            packetType = KEY_FRAME;
        }
        if (!isKeyFrameWrite) {
            return;
        }
        int videoPacketSize = VIDEO_HEADER_SIZE + nalu.length;
        int dataSize = videoPacketSize + FLV_TAG_HEADER_SIZE;
        int size = dataSize + PRE_SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        writeFlvTagHeader(buffer, FlvTag.Video, videoPacketSize, compositionTime);
        writeH264Packet(buffer, nalu, isKeyFrame);
        buffer.putInt(dataSize);

    }

    private void writeFlvHeader() {
        int size = FLV_HEAD_SIZE + PRE_SIZE;
        ByteBuffer headerBuffer = ByteBuffer.allocate(size);
        FlvPackerHelper.writeFlvHeader(headerBuffer, true, true);
        headerBuffer.putInt(0);
        packetListener.onPacket(headerBuffer.array(), HEADER);
    }

    private void writeMetaData() {
        byte[] metaData = FlvPackerHelper.writeFlvMetaData(mVideoWidth, mVideoHeight,
                mVideoFps, mAudioSampleRate, mAudioSampleSize, mIsStereo);
        int dataSize = metaData.length + FLV_TAG_HEADER_SIZE;
        int size = dataSize + PRE_SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        FlvPackerHelper.writeFlvTagHeader(buffer, FlvPackerHelper.FlvTag.Script, metaData.length, 0);
        buffer.put(metaData);
        buffer.putInt(dataSize);
        packetListener.onPacket(buffer.array(), METADATA);
    }

    private void writeFirstVideoTag(byte[] sps, byte[] pps) {
        int firstPacketSize = VIDEO_HEADER_SIZE + VIDEO_SPECIFIC_CONFIG_EXTEND_SIZE + sps.length + pps.length;
        int dataSize = firstPacketSize + FLV_TAG_HEADER_SIZE;
        int size = dataSize + PRE_SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        FlvPackerHelper.writeFlvTagHeader(buffer, FlvPackerHelper.FlvTag.Video, firstPacketSize, 0);
        FlvPackerHelper.writeFirstVideoTag(buffer, sps, pps);
        buffer.putInt(dataSize);
        packetListener.onPacket(buffer.array(), FIRST_VIDEO);
    }

    private void writeFirstAudioTag() {
        int firstAudioPacketSize = AUDIO_SPECIFIC_CONFIG_SIZE + AUDIO_HEADER_SIZE;
        int dataSize = FLV_TAG_HEADER_SIZE + firstAudioPacketSize;
        int size = dataSize + PRE_SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        FlvPackerHelper.writeFlvTagHeader(buffer, FlvPackerHelper.FlvTag.Audio, firstAudioPacketSize, 0);
        FlvPackerHelper.writeFirstAudioTag(buffer, mAudioSampleRate, mIsStereo, mAudioSampleSize);
        buffer.putInt(dataSize);
        packetListener.onPacket(buffer.array(), FIRST_AUDIO);
    }

    /**
     * 生成flv 头信息
     * @param buffer 需要写入数据的byte buffer
     * @param hasVideo 是否有视频
     * @param hasAudio 是否有音频
     * @return byte数组
     */
    public static void writeFlvHeader(ByteBuffer buffer, boolean hasVideo, boolean hasAudio) {
        /**
         *  Flv Header在当前版本中总是由9个字节组成。
         *  第1-3字节为文件标识（Signature），总为“FLV”（0x46 0x4C 0x56），如图中紫色区域。
         *  第4字节为版本，目前为1（0x01）。
         *  第5个字节的前5位保留，必须为0。
         *  第5个字节的第6位表示是否存在音频Tag。
         *  第5个字节的第7位保留，必须为0。
         *  第5个字节的第8位表示是否存在视频Tag。
         *  第6-9个字节为UI32类型的值，表示从File Header开始到File Body开始的字节数，版本1中总为9。
         */
        byte[] signature = new byte[] {'F', 'L', 'V'};  /* always "FLV" */
        byte version = (byte) 0x01;     /* should be 1 */
        byte videoFlag = hasVideo ? (byte) 0x01 : 0x00;
        byte audioFlag = hasAudio ? (byte) 0x04 : 0x00;
        byte flags = (byte) (videoFlag | audioFlag);  /* 4, audio; 1, video; 5 audio+video.*/
        byte[] offset = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09};  /* always 9 */

        buffer.put(signature);
        buffer.put(version);
        buffer.put(flags);
        buffer.put(offset);
    }

    /**
     * 写flv tag头信息
     * @param buffer 需要写入数据的byte buffer
     * @param type 类型：音频（0x8），视频（0x9），脚本（0x12）
     * @param dataSize 数据大小
     * @param timestamp 时间戳
     * @return byte数组
     */
    public static void writeFlvTagHeader(ByteBuffer buffer, int type, int dataSize, int timestamp) {
        /**
         * 第1个byte为记录着tag的类型，音频（0x8），视频（0x9），脚本（0x12）；
         * 第2-4bytes是数据区的长度，UI24类型的值，也就是tag data的长度；注：这个长度等于最后的Tag Size-11
         * 第5-7个bytes是时间戳，UI24类型的值，单位是毫秒，类型为0x12脚本类型数据，则时间戳为0，时间戳控制着文件播放的速度，可以根据音视频的帧率类设置；
         * 第8个byte是扩展时间戳，当24位数值不够时，该字节作为最高位将时间戳扩展为32位值；
         * 第9-11个bytes是streamID，UI24类型的值，但是总为0；
         * tag header 长度为1+3+3+1+3=11。
         */
        int sizeAndType = (dataSize & 0x00FFFFFF) | ((type & 0x1F) << 24);
        buffer.putInt(sizeAndType);
        int time = ((timestamp << 8) & 0xFFFFFF00) | ((timestamp >> 24) & 0x000000FF);
        buffer.putInt(time);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
    }

    /**
     * 写视频tag
     * @param data 视频数据
     * @param isKeyFrame 是否为关键帧
     * @return byte数组
     */
    public static void writeH264Packet(ByteBuffer buffer, byte[] data, boolean isKeyFrame) {
        //生成Flv Video Header
        int flvVideoFrameType = FlvPackerHelper.FlvVideoFrameType.InterFrame;
        if(isKeyFrame) {
            flvVideoFrameType = FlvPackerHelper.FlvVideoFrameType.KeyFrame;
        }
        writeVideoHeader(buffer, flvVideoFrameType, FlvPackerHelper.FlvVideoCodecID.AVC, FlvPackerHelper.FlvVideoAVCPacketType.NALU);

        //写入视频信息
        buffer.put(data);
    }

    /**
     * 封装flv 视频头信息
     * 4 bit Frame Type  ------ 帧类型
     * 4 bit CodecID ------ 视频类型
     * 8 bit AVCPacketType ------ 是NALU 还是 sequence header
     * 24 bit CompositionTime ------ 如果为NALU 则为时间间隔，否则为0
     * @param flvVideoFrameType 参见 class FlvVideoFrameType
     * @param codecID 参见 class FlvVideo
     * @param AVCPacketType 参见 class FlvVideoAVCPacketType
     * @return
     */
    public static void writeVideoHeader(ByteBuffer buffer, int flvVideoFrameType, int codecID, int AVCPacketType) {
        byte first = (byte) (((flvVideoFrameType & 0x0F) << 4)| (codecID & 0x0F));
        buffer.put(first);

        buffer.put((byte) AVCPacketType);
        buffer.put((byte) 0x00);
        buffer.put((byte) 0x00);
        buffer.put((byte) 0x00);
    }

    /**
     * E.4.1 FLV Tag, page 75
     */
    public class FlvTag
    {
        // set to the zero to reserved, for array map.
        public final static int Reserved = 0;
        // 8 = audio
        public final static int Audio = 8;
        // 9 = video
        public final static int Video = 9;
        // 18 = script data
        public final static int Script = 18;
    }

    /**
     * E.4.3.1 VIDEODATA
     * Frame Type UB [4]
     * Type of video frame. The following values are defined:
     *  1 = key frame (for AVC, a seekable frame)
     *  2 = inter frame (for AVC, a non-seekable frame)
     *  3 = disposable inter frame (H.263 only)
     *  4 = generated key frame (reserved for server use only)
     *  5 = video info/command frame
     */
    public class FlvVideoFrameType
    {
        // set to the zero to reserved, for array map.
        public final static int Reserved = 0;
        public final static int Reserved1 = 6;

        public final static int KeyFrame                     = 1;
        public final static int InterFrame                 = 2;
        public final static int DisposableInterFrame         = 3;
        public final static int GeneratedKeyFrame            = 4;
        public final static int VideoInfoFrame                = 5;
    }
}

