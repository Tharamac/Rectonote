//
// Created by TharaMac on 9/29/2020.
//

#ifndef RECTONOTE_VOICEACTIVITYDETECTION_H
#define RECTONOTE_VOICEACTIVITYDETECTION_H

#include <vector>
#include <iostream>

#include <cmath>
#include <fftw3.h>
#include <cstdlib>

#define REAL 0
#define IMAG 1


class VoiceActivityDetection {
private:
    const double frameSize;
    const size_t samplePerFrame;
    const size_t fftSize;
    const size_t fftRealSize;
    double *framePortion;
    const size_t Fs;
    int numOfFrame;
    std::vector<double> &audioSample;
    fftw_complex *spectrum;
    fftw_plan plan;
    std::vector<double> realSpectrum;

    std::vector<double> frameEnergy;
    std::vector<double> framePeakFreq;
    std::vector<double> frameSpectralFlatness;
    std::vector<double> frameZCR;

    std::vector<bool> vadResult;

    double shortTermEnergy(double *frame);
    double zcr(double *frame);
    double findPeakFreq(std::vector<double> &real_spectrum);
    double calcSpectralFlatness(std::vector<double> &real_spectrum);
    double geometricMean(std::vector<double> const &data);

public:
    VoiceActivityDetection(std::vector<double> &audio_sample,
                           const double frameSize,
                           const int FFT_size,
                           const int Fs
    );
    ~VoiceActivityDetection();
    void calculateFeatures();
    void startDecision(
            const double energyThresh,
            const double sfmThresh,
            const double freqLowerBound,
            const double freqUpperBound,
            const double zcrThresh
    );
    std::vector<bool> getResult();
};


#endif //RECTONOTE_VOICEACTIVITYDETECTION_H
