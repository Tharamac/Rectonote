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
    std::string status;
public:
    std::vector<short> shortData;
    std::vector<double> doubleData;
    std::vector<float> floatData;

    RawAudio(const char *path);

    ~RawAudio();

    int getSampleSize();

    std::string toString();

};


#endif //RECTONOTE_RAWAUDIO_H
