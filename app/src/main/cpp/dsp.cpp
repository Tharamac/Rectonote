//
// Created by TharaMac on 9/29/2020.
//


#include "dsp.h"

void hannWindow(const std::vector<double> &data, double *output, int point_start, int window_size) {
    //all this function argument are in sampling point
    for (int i = point_start; i < point_start + window_size; i++) {
        output[i - point_start] = data[i] * (0.5 * (1 - cos(2 * M_PI * i / (window_size))));
    }
}

int noteShift(double freq, double base_freq) {
    return round(12 * log2(freq / base_freq));
}

double fftBinToFreq(unsigned int binIdx, size_t fftSize, unsigned int Fs) {
    return (double) 0.5 * binIdx * Fs / fftSize;
}


