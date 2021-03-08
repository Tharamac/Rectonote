#include <jni.h>
#include <string>
#include <iostream>
#include <vector>
#include <algorithm>
#include <fluidsynth.h>
#include <unistd.h>
#include <android/log.h>
#include <q/support/frequency.hpp>

#define TAG "CPP_TAG"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,    TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,    TAG, __VA_ARGS__)
#define REAL 0
#define IMAG 1
#define C2 65.41
#define C3 130.81278265

#include "RawAudio.h"
#include "dsp.h"
#include "VoiceActivityDetection.h"
#include "PitchDetection.h"
#include "pitch_tracking.h"

std::string console_out = "";
typedef std::pair<
        std::vector<float>,
        std::vector<float>
> time_frequency_pair;

jintArray packResult(JNIEnv *env, std::vector<int> offset);

jfloatArray packResult(JNIEnv *env, std::vector<float> offset);

extern "C"
JNIEXPORT jintArray JNICALL Java_com_app_rectonote_AddTrackToProjectActivity_startConvert(
        JNIEnv *javaEnvironment,
        jobject __unused obj,
        jint Fs,
        jstring audioPath
) {
    const char *path = javaEnvironment->GetStringUTFChars(audioPath, JNI_FALSE);
    RawAudio audio_input(path);
//    jclass noteOffsetWithTime = javaEnvironment->FindClass("com/app/rectonote/musictheory/NoteOffsetWithTime");
//    jmethodID constructor = javaEnvironment->GetMethodID(noteOffsetWithTime,"<init>","([F[I)V");
    VoiceActivityDetection voiceActivityDetection(audio_input.doubleData, 0.04, 4096, Fs);
    voiceActivityDetection.calculateFeatures();
    voiceActivityDetection.startDecision(40, 5, 30, 2097, 0.1);
    auto vad_result = voiceActivityDetection.getResult();
    std::vector<float> in(audio_input.doubleData.begin(), audio_input.doubleData.end());
    for (size_t i = 0; i < in.size(); ++i) {
        auto vad_result_index = i / 160;
        if (vad_result_index == 0)
            in[i] *= (float) vad_result[vad_result_index];
        else if (vad_result_index >= vad_result.size()) {
            in[i] *= (float) vad_result[vad_result.size() - 1];
        } else {
            in[i] *= (float) (vad_result[vad_result_index] || vad_result[vad_result_index - 1]);
        }
    }


//    auto  vad_result = voiceActivityDetection.getResult();
//    for (size_t i = 0; i <vad_result.size(); ++i) {
//        if (!vad_result[i]) {
//            in[i] = 0;
//        }
//    }

    time_frequency_pair pd_out = process(in, 8000, q::frequency(30), q::frequency(1000));
    auto time_set = pd_out.first;
    auto freq_set = pd_out.second;
    std::vector<float> time;
    std::vector<int> note_offset;
    for (size_t i = 0; i < time_set.size() - 1; i += 2) {
        float time_exact = (std::round(time_set[i] * 100 + (float) .5)) / 100;
        time.push_back(time_exact);
        float chosen_freq_value = (std::abs(time_exact - time_set[i]) <
                                   std::abs(time_exact - time_set[i + 1]))
                                  ? freq_set[i]
                                  : freq_set[i + 1];
        note_offset.push_back(12 * log2(chosen_freq_value / C2));
    }
//    PitchDetection pitchDetection(audio_input.doubleData, 0.04, 4096, Fs);
//    pitchDetection.startDetection();
//    pitchDetection.implementVad(voiceActivityDetection.getResult());
    //jfloatArray time_result = packResult(javaEnvironment,time);
    jintArray offset_result = packResult(javaEnvironment, note_offset);
    javaEnvironment->ReleaseStringUTFChars(audioPath, path);
    return offset_result;
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
    if (result == nullptr) {
        return nullptr; /* out of memory error thrown */
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

jfloatArray packResult(JNIEnv *env, std::vector<float> offset) {
    jfloatArray result;
    const size_t size = offset.size();
    result = env->NewFloatArray(size);
    if (result == nullptr) {
        return nullptr; /* out of memory error thrown */
    }
    // fill a temp structure to use to populate the java int array
    jfloat fill[size];
    for (size_t i = 0; i < size; i++) {
        fill[i] = offset[i]; // put whatever logic you want to populate the values here.
    }
    // move from the temp structure to the java structure
    env->SetFloatArrayRegion(result, 0, size, fill);
    return result;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_app_rectonote_viewmodel_MusicTheoryViewModel_startConvert(JNIEnv *javaEnvironment,
                                                                   jobject thiz,
                                                                   jint Fs, jstring audioPath) {
    const char *path = javaEnvironment->GetStringUTFChars(audioPath, JNI_FALSE);
    RawAudio audio_input(path);
    VoiceActivityDetection voiceActivityDetection(audio_input.doubleData, 0.04, 2048, Fs);
    voiceActivityDetection.calculateFeatures();
    voiceActivityDetection.startDecision(40, 5, 130, 2097, 0.1);
    std::string vad_result = voiceActivityDetection.getStringResult();
    PitchDetection pitchDetection(audio_input.doubleData, 0.04, 4096, Fs);
    pitchDetection.startDetection();
    pitchDetection.implementVad(voiceActivityDetection.getResult());
    jintArray result = packResult(javaEnvironment, pitchDetection.getNoteOffsetResult());
    javaEnvironment->ReleaseStringUTFChars(audioPath, path);
    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_com_app_rectonote_AddTrackToProjectActivity_fluidSynthTest(JNIEnv *env, jobject,
                                                                jstring tempSoundFontPath) {

    const char *soundfont_path = env->GetStringUTFChars(tempSoundFontPath, JNI_FALSE);

    fluid_settings_t *settings = new_fluid_settings();
    fluid_synth_t *synth = new_fluid_synth(settings);
    fluid_settings_setstr(settings, "audio.driver", "oboe");
    fluid_audio_driver_t *adriver = new_fluid_audio_driver(settings, synth);

    // Load sample soundfont
    fluid_synth_sfload(synth, soundfont_path, 1);

    // succesfully loaded soundfont...play something
    fluid_synth_noteon(synth, 0, 60, 127); // play middle C
    sleep(1); // sleep for 1 second
    fluid_synth_noteoff(synth, 0, 60); // stop playing middle C

    fluid_synth_noteon(synth, 0, 62, 127);
    sleep(1);
    fluid_synth_noteoff(synth, 0, 62);

    fluid_synth_noteon(synth, 0, 64, 127);
    sleep(1);
    fluid_synth_noteoff(synth, 0, 64);

    // Clean up
    delete_fluid_audio_driver(adriver);
    delete_fluid_synth(synth);
    delete_fluid_settings(settings);
}

extern "C" JNIEXPORT void JNICALL
Java_com_app_rectonote_fragment_PreviewTracksFragment_play(JNIEnv *env, jobject,
                                                           jstring tempSoundFontPath,
                                                           jint bankNumber, jint presetNumber,
                                                           jstring midiPath) {
    const char *soundfont_path = env->GetStringUTFChars(tempSoundFontPath, JNI_FALSE);
    const char *midi_path = env->GetStringUTFChars(midiPath, JNI_FALSE);

    fluid_settings_t *settings = new_fluid_settings();
    fluid_synth_t *synth = new_fluid_synth(settings);
    fluid_settings_setstr(settings, "audio.driver", "oboe");
    fluid_player_t *player = new_fluid_player(synth);


    int sf_id = -1;

    if (fluid_is_soundfont(soundfont_path)) {
        sf_id = fluid_synth_sfload(synth, soundfont_path, 0);

    } else
        LOGE("Invalid argument: soundfont not found");


    LOGD("sf loaded");

    if (fluid_is_midifile(midi_path)) {
        fluid_player_add(player, midi_path);
    } else
        LOGE("Invalid argument: midi file not found");
    fluid_audio_driver_t *audio_driver = new_fluid_audio_driver(settings, synth);
    // fluid_synth_program_change(synth,0,presetNumber);





    fluid_player_play(player);

    fluid_player_join(player);


    delete_fluid_audio_driver(audio_driver);
    delete_fluid_player(player);
    delete_fluid_synth(synth);
    delete_fluid_settings(settings);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_fragment_PreviewTracksFragment_fluidSynthTest(JNIEnv *env, jobject thiz,
                                                                     jstring temp_sound_font_path,
                                                                     jint note1, jint note2,
                                                                     jint note3) {
    const char *soundfont_path = env->GetStringUTFChars(temp_sound_font_path, JNI_FALSE);

    fluid_settings_t *settings = new_fluid_settings();
    fluid_synth_t *synth = new_fluid_synth(settings);
    fluid_settings_setstr(settings, "audio.driver", "oboe");
    fluid_audio_driver_t *adriver = new_fluid_audio_driver(settings, synth);

    // Load sample soundfont
    fluid_synth_sfload(synth, soundfont_path, 1);

    // succesfully loaded soundfont...play something
    fluid_synth_program_select_by_sfont_name(synth, 0, soundfont_path, 0, 24);
    fluid_synth_noteon(synth, 0, note1, 127); // play middle C
    sleep(1); // sleep for 1 second
    fluid_synth_noteoff(synth, 0, note1); // stop playing middle C

    fluid_synth_noteon(synth, 0, note2, 127);
    sleep(1);
    fluid_synth_noteoff(synth, 0, note2);

    fluid_synth_noteon(synth, 0, note3, 127);
    sleep(1);
    fluid_synth_noteoff(synth, 0, note3);

    // Clean up
    delete_fluid_audio_driver(adriver);
    delete_fluid_synth(synth);
    delete_fluid_settings(settings);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_rectonote_fragment_PreviewTracksFragment_playSingleTrack(JNIEnv *env, jobject thiz,
                                                                      jstring temp_sound_font_path,
                                                                      jint bank_number,
                                                                      jint preset_number,
                                                                      jint channel,
                                                                      jint note_number,
                                                                      jint velocity,
                                                                      jlong duration) {
    const char *soundfont_path = env->GetStringUTFChars(temp_sound_font_path, JNI_FALSE);

    fluid_settings_t *settings = new_fluid_settings();
    fluid_synth_t *synth = new_fluid_synth(settings);
    fluid_settings_setstr(settings, "audio.driver", "oboe");
    fluid_audio_driver_t *adriver = new_fluid_audio_driver(settings, synth);

    // Load sample soundfont
    int sf_id = fluid_synth_sfload(synth, soundfont_path, 1);

    // succesfully loaded soundfont...play something
    fluid_synth_program_select(synth, 0, sf_id, 0, 24);
    fluid_synth_noteon(synth, channel, note_number, velocity); // play middle C
    sleep(1);
    fluid_synth_noteoff(synth, channel, note_number); // play middle C
    delete_fluid_audio_driver(adriver);
    delete_fluid_synth(synth);
    delete_fluid_settings(settings);


}
