#include <jni.h>
#include <string>
#include <iostream>
#include <vector>
#include <algorithm>
#include <fluidsynth.h>
#include <unistd.h>
#include <android/log.h>

#define TAG "CPP_TAG"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,    TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,    TAG, __VA_ARGS__)
//
// Created by TharaMac on 12/26/2020.
//
#include "MidiPlayer.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativeNew(JNIEnv *env, jobject thiz) {
    auto *player = new MidiPlayer();
    return reinterpret_cast<jlong>(player);
}

static MidiPlayer *getObject(JNIEnv *env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    if (!cls)
        env->FatalError("GetObjectClass failed");
    jfieldID nativeObjectPointerId = env->GetFieldID(cls, "nativeObjectPointer", "J");
    if (!nativeObjectPointerId)
        env->FatalError("GetFieldId failed");
    jlong nativeObjectPointer = env->GetLongField(thiz, nativeObjectPointerId);
    return reinterpret_cast<MidiPlayer *>(nativeObjectPointer);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativeLoadSoundfont(JNIEnv *env, jobject thiz,
                                                                          jstring soundfont_path) {
    MidiPlayer *player = getObject(env, thiz);
    const char *soundfont_pathname = env->GetStringUTFChars(soundfont_path, NULL);
    player->loadSoundfont(soundfont_pathname);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativeLoadPreset(JNIEnv *env, jobject thiz,
                                                                       jint channel,
                                                                       jint bank_number,
                                                                       jint prog_number) {
    MidiPlayer *player = getObject(env, thiz);
    player->loadPreset(channel, bank_number, prog_number);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativeLoadMidiFile(JNIEnv *env, jobject thiz,
                                                                         jstring midi_path) {
    MidiPlayer *player = getObject(env, thiz);
    const char *midifile_path = env->GetStringUTFChars(midi_path, JNI_FALSE);
    player->loadMidiFile(midifile_path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativePlayMidi(JNIEnv *env, jobject thiz) {
    MidiPlayer *player = getObject(env, thiz);
    player->play();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativeStopMidi(JNIEnv *env, jobject thiz) {
    MidiPlayer *player = getObject(env, thiz);
    player->stop();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativeStopMessage(JNIEnv *env, jobject thiz,
                                                                        jint channel) {
    MidiPlayer *player = getObject(env, thiz);
    player->stopMessage(0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativePlaySingleNote(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jint channel,
                                                                           jint midi_note_number,
                                                                           jint velocity,
                                                                           jdouble duration_in_ms) {
    MidiPlayer *player = getObject(env, thiz);
    player->playSingleNote(channel, midi_note_number, velocity, duration_in_ms);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativePlayRestNote(JNIEnv *env, jobject thiz,
                                                                         jdouble duration_in_ms) {
    MidiPlayer::playRestNote(duration_in_ms);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativeRemovePlayer(JNIEnv *env,
                                                                         jobject thiz) {
    MidiPlayer *player = getObject(env, thiz);
    delete player;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_midiplayback_MIDIPlayerChannel_nativePlayChord(JNIEnv *env, jobject thiz,
                                                                      jint channel,
                                                                      jint rootNoteNumber,
                                                                      jint chordType, jint velocity,
                                                                      jdouble durationInMs) {
    MidiPlayer *player = getObject(env, thiz);
    player->playChord(channel, rootNoteNumber, chordType, velocity, durationInMs);
}