//
// Created by TharaMac on 9/29/2020.
//

#ifndef RECTONOTE_PITCHDETECTION_H
#define RECTONOTE_PITCHDETECTION_H

#include <vector>
#include <iostream>
#include "fftw3.h"

#define REAL 0
#define IMAG 1
#define C3 130.81278265


class PitchDetection {
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

    std::vector<int> offsetNotesFromFilterBank;

    double
    calcPeakFromFilterBank(std::vector<double> &real_spectrum, int note_offset, double ref_note,
                           size_t FFT_size);

public:
    PitchDetection(std::vector<double> &audio_sample,
                   const double frameSize,
                   const int FFT_size,
                   const int Fs
    );

    ~PitchDetection();

    void startDetection();

    void implementVad(std::vector<bool> vadResult);

    std::vector<int> getNoteOffsetResult();
};


#endif //RECTONOTE_PITCHDETECTION_H
