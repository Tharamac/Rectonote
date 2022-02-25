# Rectonote
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**RectoNote** is an Android application that can jot song ideas by using voice recording converted into melody and chord progression tracks in MIDI. The tracks are stored in projects and can be played. 
 The users of this application are the artists, producers, and composers who do not have experienced in music theory or take much time to translate ideas into songs. The application is developed using Android Studio in Kotlin and C++. 
 The systems inside include voice to tracks conversion, database system, and tracks playback. Voice to tracks conversion contains voice activity detection, pitch detection and chord prediction, and melody/chord arrangement algorithm. The fast fourier transform library, FFTW, is involved in this conversion system. Projects and track data are stored in a database system by using Room Persistent Library. Track’s playback system uses FluidSynth synthesizer API with the transformation of tracks into MIDI message. The result is that the application can store melody and chord progression in song projects by converting voice recording, and users can publish their songs by playing tracks in this application. 

## Why RectoNote?
Idea of a song has tricky way to jot it.
* Voice recording
* Jot in music sheet
* Write MIDI pattern into music creating software

Translate ideas into song tracks requires experience to make it fast. Sometimes good ideas are forgotten because transcribe process take too much time. RectoNote will solve this kind of problem

## Inside RectoNote 
![Picture3](https://user-images.githubusercontent.com/16939538/155741232-54db2610-5109-4b78-8011-aaf8fa8916f6.png)

## Code Highlight
C++ develop under std/C++17
* **Voice Activity Detection:** [VoiceActivityDetection.h](https://github.com/Tharamac/Rectonote/blob/master/app/src/main/cpp/VoiceActivityDetection.h) |  [VoiceActivityDetection.cpp](https://github.com/Tharamac/Rectonote/blob/master/app/src/main/cpp/VoiceActivityDetection.cpp)
* **MIDIplayer using Fluidsynth API:** [MidiPlayer.h](https://github.com/Tharamac/Rectonote/blob/master/app/src/main/cpp/MidiPlayer.h) | [MidiPlayer.cpp](https://github.com/Tharamac/Rectonote/blob/master/app/src/main/cpp/MidiPlayer.cpp) 
* **Audio Processing JNI Interface:** [native-lib.cpp](https://github.com/Tharamac/Rectonote/blob/master/app/src/main/cpp/native-lib.cpp)
* **MIDIplayer JNI Interface:**  [midiplayer_interface.cpp](https://github.com/Tharamac/Rectonote/blob/master/app/src/main/cpp/midiplayer_interface.cpp)
