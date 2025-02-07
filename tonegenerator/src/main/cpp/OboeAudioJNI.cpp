#include <jni.h>
#include <string>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include <android/log.h>
#include "SimpleToneMaker.h"


static const char *TAG = "OboeAudioJNI";



// JNI functions are "C" calling convention
#ifdef __cplusplus
extern "C" {
#endif

static SimpleToneMaker sPlayer;


JNIEXPORT jint JNICALL Java_com_omsi_tonegenerator_CustomToneGenerator_startAudioStreamNative(
        JNIEnv *, jobject){
   // __android_log_print(ANDROID_LOG_INFO, TAG, "%s", __func__);
    oboe::Result result = sPlayer.open();
    if(result == oboe::Result::OK){
        result = sPlayer.start();
    }
    return (jint) result;
}

JNIEXPORT void JNICALL Java_com_omsi_tonegenerator_CustomToneGenerator_setAudioStreamNativeFrequency(
        JNIEnv *, jobject, jfloat frequency){
    // __android_log_print(ANDROID_LOG_INFO, TAG, "%s", __func__);
    sPlayer.setToneFrequency(frequency);
}

JNIEXPORT jint JNICALL
Java_com_omsi_tonegenerator_CustomToneGenerator_stopAudioStreamNative(JNIEnv *env, jobject thiz) {
    //__android_log_print(ANDROID_LOG_INFO, TAG, "%s", __func__);
    // We need to close() even if the stop() fails because we need to delete the resources.
    oboe::Result result1 = sPlayer.stop();
    oboe::Result result2 = sPlayer.close();
    //Return first failure code.
    return (jint) ((result1 !=oboe::Result::OK? result1 : result2));
}





#ifdef __cplusplus
}
#endif