//
// Created by TharaMac on 9/29/2020.
//


#include "VoiceActivityDetection.h"


VoiceActivityDetection::VoiceActivityDetection(
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

VoiceActivityDetection::~VoiceActivityDetection() {
    fftw_free(spectrum);
}

void VoiceActivityDetection::calculateFeatures() {
    int currentWindowStart = 0;
    numOfFrame = 0;
    do {
        framePortion = new double[fftSize];
        hannWindow(audioSample, framePortion, currentWindowStart, samplePerFrame);
        frameEnergy.push_back(shortTermEnergy(framePortion));
        frameZCR.push_back(zcr(framePortion));
        plan = fftw_plan_dft_r2c_1d(fftSize, framePortion, spectrum, FFTW_ESTIMATE);
        fftw_execute(plan);
        fftw_destroy_plan(plan);
        fftw_cleanup();
        for (int i = 0; i < fftRealSize; i++) {
            realSpectrum[i] = sqrt(
                    spectrum[REAL][i] * spectrum[REAL][i] + spectrum[IMAG][i] * spectrum[IMAG][i]);
        }
        framePeakFreq.push_back(findPeakFreq(realSpectrum));
        frameSpectralFlatness.push_back(abs(calcSpectralFlatness(realSpectrum)));
        numOfFrame++;
        currentWindowStart += (samplePerFrame / 2);
        delete[] framePortion;
    } while (currentWindowStart + samplePerFrame <= audioSample.size());
}

double VoiceActivityDetection::shortTermEnergy(double *frame) {
    double sum = 0;
    for (size_t i = 0; i < fftSize; ++i) {
        sum += frame[i] * frame[i];
    }
    return sum;
}

double VoiceActivityDetection::zcr(double *frame) {
    double sum = 0.0;
    for (size_t i = 0; i < fftSize - 1; i++) {
        sum += (frame[i] * frame[i + 1] < 0) ? 1 : 0;
    }
    return sum / (fftSize - 1);
}

double VoiceActivityDetection::findPeakFreq(std::vector<double> &real_spectrum) {
    int index = max_element(real_spectrum.begin(), real_spectrum.end()) - real_spectrum.begin();
    return fftBinToFreq(index, fftSize, Fs);
}

double VoiceActivityDetection::calcSpectralFlatness(std::vector<double> &real_spectrum) {
    double arithMean = 0;
    double geoMean = geometricMean(real_spectrum);
    for (size_t i = 0; i < fftRealSize; ++i) {
        arithMean += real_spectrum[i];
    }
    arithMean /= (double) fftRealSize;
    return 10 * log10(geoMean / arithMean);
}

double VoiceActivityDetection::geometricMean(std::vector<double> const &data) {
    long long ex = 0;
    auto do_bucket = [&data, &ex](int first, int last) -> double {
        double ans = 1.0;
        for (; first != last; ++first) {
            int i;
            ans *= std::frexp(data[first], &i);
            ex += i;
        }
        return ans;
    };

    const int bucket_size = -std::log2(std::numeric_limits<double>::min());
    std::size_t buckets = data.size() / bucket_size;

    double invN = 1.0 / data.size();
    double m = 1.0;

    for (std::size_t i = 0; i < buckets; ++i)
        m *= std::pow(do_bucket(i * bucket_size, (i + 1) * bucket_size), invN);

    m *= std::pow(do_bucket(buckets * bucket_size, data.size()), invN);

    return std::pow(std::numeric_limits<double>::radix, ex * invN) * m;
}

void VoiceActivityDetection::startDecision(
        const double energyPThresh,
        const double sfmThresh,
        const double freqLowerBound,
        const double freqUpperBound,
        const double zcrThresh) {
    // Frame #0 - #28 assuming silence.
    vadResult.resize(numOfFrame);
    double energyMin = *min_element(frameEnergy.begin(), frameEnergy.begin() + 28);
    double energyThresh = energyPThresh * energyMin;
    for (int i = 29; i < numOfFrame; ++i) {
        int frameScore = 0;
        if (frameEnergy[i] >= energyThresh)
            frameScore++;
        if (frameZCR[i] <= zcrThresh)
            frameScore++;
        if (frameSpectralFlatness[i] > sfmThresh)
            frameScore++;
        if (framePeakFreq[i] >= freqLowerBound && framePeakFreq[i] <= freqUpperBound)
            frameScore++;
        if (frameScore > 2) {
            vadResult[i] = true;
        } else {
            vadResult[i] = false;
        }
    }
}

std::vector<bool> VoiceActivityDetection::getResult() {
    return vadResult;
}

std::string VoiceActivityDetection::getStringResult() {
    std::string out = "num of frame = " + std::to_string(numOfFrame) + "\nVadResult = ";
    for (size_t i = 0; i < vadResult.size(); ++i) {
        out += std::to_string(vadResult[i]);
    }
    return out;
}