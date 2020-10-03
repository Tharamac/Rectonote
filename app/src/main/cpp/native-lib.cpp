#include <jni.h>
#include <string>
#include <iostream>
#include <vector>
#include <algorithm>

#define REAL 0
#define IMAG 1
#define C3 130.81278265

#include "RawAudio.h"
#include "dsp.h"
#include "VoiceActivityDetection.h"


extern "C"
JNIEXPORT void JNICALL Java_com_app_rectonote_AddTrackToProjectActivity_startConvert(
        JNIEnv *javaEnvironment,
        jobject __unused obj,
        jint Fs,
        jstring audioPath,
        jboolean isMelody) {
        const char *path = javaEnvironment->GetStringUTFChars(audioPath, JNI_FALSE);
        RawAudio audio_input(path);
        javaEnvironment->ReleaseStringUTFChars(audioPath, path);
}

extern "C"
JNIEXPORT void JNICALL Java_com_app_rectonote_AddTrackToProjectActivity_debug(JNIEnv *env,
                                                                                  jobject thiz){
        std::cout << "Hello world" << std::endl;
}
