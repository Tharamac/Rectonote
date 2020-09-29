//
// Created by TharaMac on 9/29/2020.
//

#ifndef RECTONOTE_DSP_H
#define RECTONOTE_DSP_H

#include <math.h>
#include <vector>
#include <algorithm>
#include <iostream>

void hannWindow(const std::vector<double> &data, double *output, int point_start, int window_size);

#endif //RECTONOTE_DSP_H
