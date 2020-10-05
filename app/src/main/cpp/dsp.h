//
// Created by TharaMac on 9/29/2020.
//

#ifndef RECTONOTE_DSP_H
#define RECTONOTE_DSP_H

#include <math.h>
#include <vector>
#include <algorithm>
#include <iostream>
#include <jni.h>

void hannWindow(const std::vector<double> &data, double *output, int point_start, int window_size);

int noteShift(double freq, double base_freq);

double fftBinToFreq(unsigned int binIdx, size_t fftSize, unsigned int Fs);


#endif //RECTONOTE_DSP_H
