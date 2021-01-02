//
// Created by TharaMac on 12/26/2020.
//

#ifndef RECTONOTE_MIDIPLAYER_H
#define RECTONOTE_MIDIPLAYER_H

#include <fluidsynth.h>
#include <unistd.h>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,    TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,    TAG, __VA_ARGS__)

class MidiPlayer {
private:
    fluid_settings_t *settings;
    fluid_synth_t *synth;
    fluid_audio_driver_t *audioDriver;
    int soundfont_id;
    fluid_player_t *player;

public:
    MidiPlayer();

    ~MidiPlayer();

    void loadSoundfont(const char *soundfont_path);

    void loadPreset(int channel, int bankNumber, int presetNumber);

    void play();

    void stop();

    void loadMidiFile(const char *midiPath);

    void playSingleNote(int channel, int midiNoteNumber, int velocity, double durationInMs);

    static void playRestNote(double durationInMs);

    void
    playChord(int channel, int rootNoteNumber, int chordType, int velocity, double durationInMs);

    void stopMessage(int channel);
};


#endif //RECTONOTE_MIDIPLAYER_H
