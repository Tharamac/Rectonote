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
#include "PitchDetection.h"

std::string console_out = "";

jintArray packResult(JNIEnv *env, std::vector<int> offset);

extern "C"
JNIEXPORT jstring JNICALL Java_com_app_rectonote_AddTrackToProjectActivity_startConvert(
        JNIEnv *javaEnvironment,
        jobject __unused obj,
        jint Fs,
        jstring audioPath,
        jstring convertMode) {
    const char *path = javaEnvironment->GetStringUTFChars(audioPath, JNI_FALSE);
    const char *convert_mode = javaEnvironment->GetStringUTFChars(convertMode, JNI_FALSE);
    RawAudio audio_input(path);
    VoiceActivityDetection voiceActivityDetection(audio_input.doubleData, 0.04, 2048, Fs);
    voiceActivityDetection.calculateFeatures();
    voiceActivityDetection.startDecision(40, 5, 130, 2097, 0.1);
    std::string vad_result = voiceActivityDetection.getStringResult();
    PitchDetection pitchDetection(audio_input.doubleData, 0.04, 4096, Fs);
    pitchDetection.startDetection();
    pitchDetection.implementVad(voiceActivityDetection.getResult());
    packResult(javaEnvironment, pitchDetection.getNoteOffsetResult());
    console_out = audio_input.toString() + " Mode:" + convert_mode + "\n" + vad_result;
    javaEnvironment->ReleaseStringUTFChars(audioPath, path);
    return javaEnvironment->NewStringUTF(console_out.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL Java_com_app_rectonote_AddTrackToProjectActivity_debug(JNIEnv *env,
                                                                                 jobject thiz) {
    std::string console_out = "Hello";
    return env->NewStringUTF(console_out.c_str());
}

jintArray packResult(JNIEnv *env, std::vector<int> offset) {
    jintArray result;
    const size_t size = offset.size();
    result = env->NewIntArray(size);
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }
    // fill a temp structure to use to populate the java int array
    jint fill[size];
    for (size_t i = 0; i < size; i++) {
        fill[i] = offset[i]; // put whatever logic you want to populate the values here.
    }
    // move from the temp structure to the java structure
    env->SetIntArrayRegion(result, 0, size, fill);
    return result;
}