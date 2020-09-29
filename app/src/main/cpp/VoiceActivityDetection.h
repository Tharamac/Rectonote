//
// Created by TharaMac on 9/29/2020.
//

#ifndef RECTONOTE_VOICEACTIVITYDETECTION_H
#define RECTONOTE_VOICEACTIVITYDETECTION_H

#include <fstream>
#include <vector>
#include <iostream>
#include <fftw3.h>

class VoiceActivityDetection {
private:
    double frameSize;
    int samplePerFrame;
    int fftSize;
    int fftRealSize;

    std::vector<double> audioSample;
    fftw_complex *spectrum;
    fftw_plan plan;
    std::vector<double> realSpectrum;

    std::vector<double> frameEnergy;
    std::vector<double> framePeakFreq;
    std::vector<double> frameSpectralFlatness;
    std::vector<double> frameZCR;

    std::vector<bool> vadResult;
public:
    VoiceActivityDetection(const std::vector<double> &audio_sample, const double frameSize,
                           const int FFT_size);

    void calculateFeatures();

    void startDecision();

    void implementVAD();


};


#endif //RECTONOTE_VOICEACTIVITYDETECTION_H
