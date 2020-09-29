//
// Created by TharaMac on 9/28/2020.
//


#ifndef RECTONOTE_RAWAUDIO_H
#define RECTONOTE_RAWAUDIO_H

#include <fstream>
#include <vector>
#include <iostream>

class RawAudio {
private:
    char *byteData;
    int sampleSize;
public:
    std::vector<short> shortData;
    std::vector<double> doubleData;

    RawAudio(const char *path);

    ~RawAudio();

    int getSampleSize();
};


#endif //RECTONOTE_RAWAUDIO_H
