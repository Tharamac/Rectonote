/*=============================================================================
   Copyright (c) 2014-2020 Joel de Guzman. All rights reserved.

   Distributed under the MIT License [ https://opensource.org/licenses/MIT ]
=============================================================================*/
#include "pitch_tracking.h"

constexpr bool skip_tests = true;
constexpr auto break_time = 100.0;

time_frequency_pair process(
        std::vector<float> in,
        std::uint32_t sps,
        q::frequency lowest_freq,
        q::frequency highest_freq
) {

    ////////////////////////////////////////////////////////////////////////////
    // Output
    constexpr auto n_channels = 2;
    time_frequency_pair output;


    ////////////////////////////////////////////////////////////////////////////
    // Process
    q::dual_pitch_detector pd{lowest_freq, highest_freq, sps};
    q::pd_preprocessor::config cfg;
    q::pd_preprocessor pp{cfg, lowest_freq, highest_freq, sps};

    std::uint64_t nanoseconds = 0;
    int num_of_frame = 0;
    for (auto i = 0; i != in.size(); ++i) {
        auto pos = i * n_channels;
        auto ch1 = pos;      // input
        auto ch2 = pos + 1;    // frequency

        float time = i / float(sps);

        auto s = in[i];

        // Preprocessor
        s = pp(s);
        // Pitch Detect
        auto start = std::chrono::high_resolution_clock::now();
        bool ready = pd(s);
        auto elapsed = std::chrono::high_resolution_clock::now() - start;
        auto duration = std::chrono::duration_cast<std::chrono::nanoseconds>(elapsed);
        nanoseconds += duration.count();

        // Print the frequency
        if (ready) {
            auto f = pd.get_frequency();
            output.first.push_back(time);
            output.second.push_back(f);
            num_of_frame++;
        }
    }
    return output;

}



//
// Created by TharaMac on 2/26/2021.
//

