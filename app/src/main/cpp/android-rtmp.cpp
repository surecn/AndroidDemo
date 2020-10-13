#include <jni.h>
#include <string>
#include <android/log.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#ifndef WIN32
#include <unistd.h>
#endif


#include "librtmp/rtmp_sys.h"
#include "librtmp/log.h"

extern "C" {
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include "librtmp/rtmp_sys.h"
#include "librtmp/log.h"
#include <unistd.h>
#define HTON16(x)  ((x>>8&0xff)|(x<<8&0xff00))
#define HTON24(x)  ((x>>16&0xff)|(x<<16&0xff0000)|(x&0xff00))
#define HTON32(x)  ((x>>24&0xff)|(x>>8&0xff00)|\
    (x<<8&0xff0000)|(x<<24&0xff000000))
#define HTONTIME(x) ((x>>16&0xff)|(x<<16&0xff0000)|(x&0xff00)|(x&0xff000000))
}


RTMP *rtmp=NULL;
RTMPPacket *packet = NULL;

int rtmp_push_connect(char *url) {
    /* set log level */
    //RTMP_LogLevel loglvl=RTMP_LOGDEBUG;
    //RTMP_LogSetLevel(loglvl);

    rtmp=RTMP_Alloc();
    RTMP_Init(rtmp);
    //set connection timeout,default 30s
    //rtmp->Link.timeout=5;
    rtmp->Link.timeout = 10;
    rtmp->Link.lFlags |= RTMP_LF_LIVE;
    if(!RTMP_SetupURL(rtmp, url))
    {
        RTMP_Log(RTMP_LOGERROR,"SetupURL Err\n");
        RTMP_Free(rtmp);
        return -1;
    }

    //if unable,the AMF command would be 'play' instead of 'publish'
    RTMP_EnableWrite(rtmp);

    if (!RTMP_Connect(rtmp,NULL)){
        RTMP_Log(RTMP_LOGERROR,"Connect Err\n");
        RTMP_Free(rtmp);
        return -1;
    }

    if (!RTMP_ConnectStream(rtmp,0)){
        RTMP_Log(RTMP_LOGERROR,"ConnectStream Err\n");
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        return -1;
    }

    packet =(RTMPPacket*)malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet,1024*64);
    RTMPPacket_Reset(packet);

    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04;
    packet->m_nInfoField2 = rtmp->m_stream_id;

    return 0;
}

int rtmp_send(jbyte *data, int length) {

    RTMP_LogPrintf("Start to send data ...\n");
// TODO
    if (length < 15) {
        return -1;
    }

    //packet attributes
    uint32_t type = 0;
    uint32_t datalength = 0;
    uint32_t timestamp = 0;
    uint32_t streamid = 0;

    memcpy(&type, data, 1);
    data++;

    memcpy(&datalength, data, 3);
    datalength = HTON24(datalength);
    data += 3;

    memcpy(&timestamp, data, 4);
    timestamp = HTONTIME(timestamp);
    data += 4;

    memcpy(&streamid, data, 3);
    streamid = HTON24(streamid);
    data += 3;

    type = 0x09;
    if (type != 0x08 && type != 0x09) {
        return -2;
    }
    if (datalength != (length - 11 - 4)) {
        return -3;
    }

    __android_log_print(ANDROID_LOG_WARN, "eric",
                        "解析包数据：%u,%u,%u,%u,%d",
                        type, datalength, timestamp, streamid, length);

    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nTimeStamp = timestamp;
    packet->m_packetType = type;
    packet->m_nBodySize  = datalength;
    memcpy(packet->m_body, data, length - 11 - 4);

    if (!RTMP_IsConnected(rtmp)){
        RTMP_Log(RTMP_LOGERROR,"rtmp is not connect\n");

        if (packet!=NULL){
            RTMPPacket_Free(packet);
            free(packet);
            packet=NULL;
        }
        return -1;
    }
    if (!RTMP_SendPacket(rtmp,packet,0)){
        RTMP_Log(RTMP_LOGERROR,"Send Error\n");

        return -1;
    }
//    if (packet!=NULL){
//        RTMPPacket_Free(packet);
//        free(packet);
//        packet=NULL;
//    }
//    env->ReleaseByteArrayElements(buf_, buf, 0);
    return 0;
}

int rtmp_close() {
    RTMP_LogPrintf("\nSend Data Over\n");

    if (rtmp!=NULL){
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        rtmp=NULL;
    }
    return 0;
}


char* ConvertJByteaArrayToChars(JNIEnv *env, jbyteArray bytearray) {
    char *chars = NULL;
    jbyte *bytes;
    bytes = env->GetByteArrayElements(bytearray, 0);
    int chars_len = env->GetArrayLength(bytearray);
    chars = new char[chars_len + 1];
    memset(chars, 0, chars_len + 1);
    memcpy(chars, bytes, chars_len);
    chars[chars_len] = 0;

    env->ReleaseByteArrayElements(bytearray, bytes, 0);

    return chars;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_surecn_demo_media_RtmpPublisher_connect(JNIEnv *env, jobject thiz, jstring url) {
    const char *ch = env->GetStringUTFChars(url, 0);
    char *pCh = static_cast<char *>(malloc(512));
    strcpy(pCh, ch);
    //pCh[nLen] = '\n';
    return rtmp_push_connect(pCh);
}

extern "C" JNIEXPORT void JNICALL
Java_com_surecn_demo_media_RtmpPublisher_send(JNIEnv *env, jobject thiz, jbyteArray data) {
    int32_t length = env->GetArrayLength(data);

    jbyte *buf_ = env->GetByteArrayElements(data, NULL);
    jint ret = rtmp_send(buf_, length);
    env->ReleaseByteArrayElements(data, buf_, 0);
//    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_surecn_demo_media_RtmpPublisher_close(JNIEnv *env, jobject thiz) {
    return rtmp_close();
}