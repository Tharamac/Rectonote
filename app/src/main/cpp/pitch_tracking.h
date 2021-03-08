//
// Created by TharaMac on 2/26/2021.
//

#ifndef RECTONOTE_PITCH_TRACKING_H
#define RECTONOTE_PITCH_TRACKING_H

#include <infra/catch.hpp>

#include <q/support/literals.hpp>
#include <q/pitch/dual_pitch_detector.hpp>
#include <q/pitch/pd_preprocessor.hpp>
#include <q_io/audio_file.hpp>

#include <fstream>
#include <vector>
#include <iostream>
#include <chrono>

#include "RawAudio.h"

namespace q = cycfi::q;
using namespace q::literals;

typedef std::pair<
        std::vector<float>,
        std::vector<float>
> time_frequency_pair;

int get_num(std::string const &s, int pos, float &num);

void break_debug();

time_frequency_pair process(
        std::vector<float> in,
        std::uint32_t sps,
        q::frequency lowest_freq,
        q::frequency highest_freq
);

#endif //RECTONOTE_PITCH_TRACKING_H
