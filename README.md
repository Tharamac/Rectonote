# Rectonote
RectoNote is an Android application that can jot song ideas by using voice recording converted into melody and chord progression tracks in MIDI. The tracks are stored in projects and can be played. The users of this application are the artists, producers, and composers who do not have experienced in music theory or take much time to translate ideas into songs. The application is developed using Android Studio in Kotlin and C++. The systems inside include voice to tracks conversion, database system, and tracks playback. Voice to tracks conversion contains voice activity detection, pitch detection and chord prediction, and melody/chord arrangement algorithm. The fast fourier transform library, FFTW, is involved in this conversion system. Projects and track data are stored in a database system by using Room Persistent Library. Track’s playback system uses FluidSynth synthesizer API with the transformation of tracks into MIDI message. The result is that the application can store melody and chord progression in song projects by converting voice recording, and users can publish their songs by playing tracks in this application. 
![image](https://user-images.githubusercontent.com/16939538/155734947-a30ddf83-1e69-40cf-9bdf-9f0e73b9efb5.png)

## Why RectoNote?
Idea of a song has tricky way to jot it.
* Voice recording
* Jot in music sheet
* Write MIDI pattern into music creating software
Translate ideas into song tracks requires experience to make it fast. Sometimes good ideas are forgotten because transcribe process take too much time. RectoNote will solve this kind of problem
![image](https://user-images.githubusercontent.com/16939538/155735234-bfeb0473-7aab-4a67-9937-cc83871dc8cd.png)
