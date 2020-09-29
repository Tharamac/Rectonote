//
// Created by TharaMac on 9/29/2020.
//

#include "PitchDetection.h"
#include "dsp.h"

PitchDetection::PitchDetection(
        std::vector<double> &audio_sample,
        const double frame_size,
        const int FFT_size,
        const int Fs
) : audioSample(audio_sample),
    fftRealSize(FFT_size / 2 + 1),
    fftSize(FFT_size),
    samplePerFrame(frameSize * Fs),
    frameSize(frame_size),
    Fs(Fs) {
    realSpectrum.resize(fftRealSize);
    spectrum = (fftw_complex *) fftw_malloc(sizeof(fftw_complex) * (fftSize));
}

PitchDetection::~PitchDetection() {
    fftw_free(spectrum);
}

void PitchDetection::startDetection() {
    int currentWindowStart = 0;
    numOfFrame = 0;
    do {
        framePortion = new double[fftSize];
        hannWindow(audioSample, framePortion, currentWindowStart, samplePerFrame);
        plan = fftw_plan_dft_r2c_1d(fftSize, framePortion, spectrum, FFTW_ESTIMATE);
        fftw_execute(plan);
        fftw_destroy_plan(plan);
        fftw_cleanup();
        for (int i = 0; i < fftRealSize; i++) {
            realSpectrum[i] = sqrt(
                    spectrum[REAL][i] * spectrum[REAL][i] + spectrum[IMAG][i] * spectrum[IMAG][i]);
        }
        double peakOfEachNotes[48];
        int note = 0;
        double maxPeak = 0;
        for (size_t i = 0; i < 48; i++) {
            peakOfEachNotes[i] = calcPeakFromFilterBank(realSpectrum, i, C3, fftSize);
            if (peakOfEachNotes[i] > maxPeak) {
                maxPeak = peakOfEachNotes[i];
                note = i;
            }
        }
        offsetNotesFromFilterBank.push_back(note);
        numOfFrame++;
        currentWindowStart += (samplePerFrame / 2);
        delete[] framePortion;
    } while (currentWindowStart + samplePerFrame <= audioSample.size());
}

double PitchDetection::calcPeakFromFilterBank(std::vector<double> &real_spectrum, int note_offset,
                                              double ref_note, size_t FFT_size) {
    std::vector<double> temp(real_spectrum.size());

    double start_freq = ref_note * pow(2, (note_offset - 1) / 12.0);
    double mid_freq = ref_note * pow(2, note_offset / 12.0);
    double end_freq = ref_note * pow(2, (note_offset + 1) / 12.0);
    const double log_slope = 12.0;

    for (size_t i = 1; i < real_spectrum.size(); i++) {
        double freq_i = fftBinToFreq(i, fftSize, Fs);
        if (freq_i < start_freq) {
            temp[i] = 0;
        } else if (freq_i <= mid_freq) {
            temp[i] = log_slope * log2(freq_i / start_freq) * real_spectrum[i];
        } else if (freq_i <= end_freq) {
            temp[i] = log_slope * log2(end_freq / freq_i) * real_spectrum[i];
        } else {
            temp[i] = 0;
        }
    }
    double peak = *max_element(temp.begin(), temp.end());
    return peak;
}

void PitchDetection::implementVad(std::vector<bool> vadResult) {
    for (size_t i = 0; i < offsetNotesFromFilterBank.size(); ++i) {
        if (!vadResult[i]) {
            offsetNotesFromFilterBank[i] = -1;
        }
    }
}

std::vector<int> PitchDetection::getNoteOffsetResult() {
    return offsetNotesFromFilterBank;
}
