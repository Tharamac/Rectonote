//
// Created by TharaMac on 12/26/2020.
//

#include "MidiPlayer.h"

MidiPlayer::MidiPlayer() {
    settings = new_fluid_settings();
    synth = new_fluid_synth(settings);
    fluid_settings_setstr(settings, "audio.driver", "oboe");
    audioDriver = new_fluid_audio_driver(settings, synth);
    player = new_fluid_player(synth);

}

MidiPlayer::~MidiPlayer() {
    delete_fluid_audio_driver(audioDriver);
    delete_fluid_player(player);
    delete_fluid_synth(synth);
    delete_fluid_settings(settings);
}

void MidiPlayer::loadSoundfont(const char *soundfont_path) {
    if (fluid_is_soundfont(soundfont_path))
        soundfont_id = fluid_synth_sfload(synth, soundfont_path, 1);


}

void MidiPlayer::loadPreset(int channel, int bankNumber, int presetNumber) {
    fluid_synth_program_select(synth, channel, soundfont_id, bankNumber, presetNumber);

}

void MidiPlayer::loadMidiFile(const char *midiPath) {
    fluid_player_add(player, midiPath);
}

void MidiPlayer::play() {
    fluid_player_play(player);
    fluid_player_join(player);
}

void MidiPlayer::stop() {
    fluid_player_stop(player);
    fluid_player_seek(player, 0);
}

void
MidiPlayer::playSingleNote(int channel, int midiNoteNumber, int velocity, double durationInMs) {
    fluid_synth_noteon(synth, channel, midiNoteNumber, velocity);
    usleep(durationInMs * 1000);
    fluid_synth_noteoff(synth, channel, midiNoteNumber);
}

void MidiPlayer::playRestNote(double durationInMs) {
    usleep(durationInMs * 1000);
}

void MidiPlayer::stopMessage(int channel) {
    fluid_synth_all_notes_off(synth, channel);
}

void MidiPlayer::playChord(int channel, int rootNoteNumber, int chordType, int velocity,
                           double durationInMs) {
    //0 = major; 1 = minor
    if (chordType == 0) {
        fluid_synth_noteon(synth, channel, rootNoteNumber, velocity);
        fluid_synth_noteon(synth, channel, rootNoteNumber + 4, velocity);
        fluid_synth_noteon(synth, channel, rootNoteNumber + 7, velocity);
        usleep(durationInMs * 1000);
        fluid_synth_noteoff(synth, channel, rootNoteNumber);
        fluid_synth_noteoff(synth, channel, rootNoteNumber + 4);
        fluid_synth_noteoff(synth, channel, rootNoteNumber + 7);
    } else if (chordType == 1) {
        fluid_synth_noteon(synth, channel, rootNoteNumber, velocity);
        fluid_synth_noteon(synth, channel, rootNoteNumber + 3, velocity);
        fluid_synth_noteon(synth, channel, rootNoteNumber + 7, velocity);
        usleep(durationInMs * 1000);
        fluid_synth_noteoff(synth, channel, rootNoteNumber);
        fluid_synth_noteoff(synth, channel, rootNoteNumber + 3);
        fluid_synth_noteoff(synth, channel, rootNoteNumber + 7);
    }

}
