#include <jni.h>
#include <string>
#include <iostream>

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_AddNewIdea_dsp(JNIEnv *env, jobject thiz, jchar mode) {
    std::cout << mode << std::endl;
}