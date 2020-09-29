#include <jni.h>
#include <string>
#include <iostream>
#include <vector>
#include <algorithm>
#include "fftw3.h"

#define REAL 0
#define IMAG 1
#define C3 130.81278265

#include "includes/RawAudio.h"
#include "includes/dsp.h"
#include "includes/VoiceActivityDetection.h"


extern "C" {
JNIEXPORT void JNICALL Java_com_app_rectonote_AddTrackToProjectActivity_startConvert(
        JNIEnv *javaEnvironment,
        jobject __unused obj,
        jint Fs,
        jstring audioPath,
        jboolean isMelody
) {
    const char *path = javaEnvironment->GetStringUTFChars(audioPath, JNI_FALSE);
    RawAudio audio_input(path);
    javaEnvironment->ReleaseStringUTFChars(audioPath, path);
}
}