#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include <android/log.h>

// parselib includes
#include <stream/MemInputStream.h>
#include <wav/WavStreamReader.h>

#include <player/OneShotSampleSource.h>
#include <player/SimpleMultiPlayer.h>

static const char *TAG = "TonePlayerJNI";

// JNI functions are "C" calling convention
#ifdef __cplusplus
extern "C" {
#endif

using namespace iolib;
using namespace parselib;

static SimpleMultiPlayer sDTPlayer;


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_setupAudioStreamNative(JNIEnv *env, jobject thiz,
                                                           jint numChannels) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "init()");
    sDTPlayer.setupAudioStream(numChannels);

}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_startAudioStreamNative(JNIEnv *env, jobject thiz) {
    sDTPlayer.startStream();
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "Start stream");
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_teardownAudioStreamNative(JNIEnv *env, jobject thiz) {
    // we know in this case that the sample buffers are all 1-channel, 44.1K
    sDTPlayer.teardownAudioStream();
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_omsi_toneplayer_TonePlayer_loadWavAssetNative(JNIEnv *env, jobject thiz,
                                                       jbyteArray bytearray, jfloat pan) {

    int len = env->GetArrayLength (bytearray);
    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion (bytearray, 0, len, reinterpret_cast<jbyte*>(buf));

    MemInputStream stream(buf, len);

    WavStreamReader reader(&stream);
    reader.parse();

    reader.getNumChannels();

    SampleBuffer* sampleBuffer = new SampleBuffer();
    sampleBuffer->loadSampleData(&reader);

    OneShotSampleSource* source = new OneShotSampleSource(sampleBuffer, pan);
    int index = sDTPlayer.addSampleSource(source, sampleBuffer);

    delete[] buf;
    return index;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_unloadWavAssetsNative(JNIEnv *env, jobject thiz) {
    sDTPlayer.unloadSampleData();
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_trigger(JNIEnv *env, jobject thiz, jint index) {
    sDTPlayer.triggerDown(index);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_stopTrigger(JNIEnv *env, jobject thiz, jint index) {
    sDTPlayer.triggerUp(index);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_setPan(JNIEnv *env, jobject thiz, jint index, jfloat pan) {
    sDTPlayer.setPan(index, pan);
}


extern "C"
JNIEXPORT jfloat JNICALL
Java_com_omsi_toneplayer_TonePlayer_getPan(JNIEnv *env, jobject thiz, jint index) {
    return sDTPlayer.getPan(index);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_setGain(JNIEnv *env, jobject thiz, jint index, jfloat gain) {
    sDTPlayer.setGain(index, gain);
}


extern "C"
JNIEXPORT jfloat JNICALL
Java_com_omsi_toneplayer_TonePlayer_getGain(JNIEnv *env, jobject thiz, jint index) {
    return sDTPlayer.getGain(index);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_setLoopMode(JNIEnv *env, jobject thiz, jint index,
                                                jboolean isLoopMode) {
    sDTPlayer.setLoopMode(index, isLoopMode);
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_omsi_toneplayer_TonePlayer_getOutputReset(JNIEnv *env, jobject thiz) {
    return sDTPlayer.getOutputReset();
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_clearOutputReset(JNIEnv *env, jobject thiz) {
    sDTPlayer.clearOutputReset();
}


extern "C"
JNIEXPORT void JNICALL
Java_com_omsi_toneplayer_TonePlayer_restartStream(JNIEnv *env, jobject thiz) {
    sDTPlayer.resetAll();
    if (sDTPlayer.openStream() && sDTPlayer.startStream()){
        __android_log_print(ANDROID_LOG_INFO, TAG, "openStream successful");
    } else {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "openStream failed");
    }
}


#ifdef __cplusplus
}
#endif
