//
// Created by visierip on 24/10/2023.
//


#include <stdlib.h>
#include <stdint.h>
#include <android/log.h>

static const char *TAG = "SimpleToneMaker";

#include "SimpleToneMaker.h"

constexpr float kPi = M_PI;
constexpr float kTwoPi = kPi * 2;
constexpr float kDefaultFrequency = 800.0;
float mFrequency = kDefaultFrequency;
float amplitude = 1.0f; // Amplitudes from https://epubs.siam.org/doi/pdf/10.1137/S00361445003822
double phase_ = 0.0;
//Sources from SynthSound.h in oboe samples

oboe::Result SimpleToneMaker::open() {
    // Use shared_ptr to prevent use of a deleted callback.
    mDataCallback = std::make_shared<MyDataCallback>();
    mErrorCallback = std::make_shared<MyErrorCallback>(this);

    oboe::AudioStreamBuilder builder;
    oboe::Result result = builder.setSharingMode(oboe::SharingMode::Exclusive)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setFormat(oboe::AudioFormat::Float)
            ->setChannelCount(kChannelCount)
            ->setDataCallback(mDataCallback)
            ->setErrorCallback(mErrorCallback)
            ->openStream(mStream); //open using a shared_ptr
    return result;
}

oboe::Result SimpleToneMaker::start(){
    return mStream->requestStart(); //mStream is a pointer!! ->
}

oboe::Result SimpleToneMaker::stop(){
    return mStream->requestStop();
}

oboe::Result SimpleToneMaker::close(){
    return mStream->close();
}




/**
 * This callback method will be called from a high priority audio thread.
 * It should only do math and not do any blocking operations like
 * reading or writing files, memory allocation, or networking.
 * @param audioStream
 * @param audioData pointer to an array of samples to be filled
 * @param numFrames number of frames needed
 * @return
 */
oboe::DataCallbackResult  SimpleToneMaker::MyDataCallback::onAudioReady(
        oboe::AudioStream *audioStream,
        void *audioData,
        int32_t numFrames ){

    // We requested float when we built the stream.
    float *output = (float *) audioData;

    int sampleRate = audioStream->getSampleRate();
    float mPhaseIncrement = kTwoPi * mFrequency / static_cast<float>(sampleRate);




    //Fill buffer:
    int numSamples = numFrames * kChannelCount;
    for (int i = 0; i<numSamples; i++){
        //TODO: get sample rate and create square wave

        //Increments the phase, handling wrap around.
        phase_ += mPhaseIncrement;
        if(phase_ >= kTwoPi) phase_ -= kTwoPi;


        //*output++ = (float) ((drand48() - 0.5) * 0.6);
        *output++=(float) (sin(phase_)>0?  amplitude : 0);
    }

    return oboe::DataCallbackResult::Continue;
}

void SimpleToneMaker::MyErrorCallback::onErrorAfterClose(
        oboe::AudioStream *oboeStream, oboe::Result error
){
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s() -error = %s", oboe::convertToText(error));
    //Try to open and start a new stream after a disconnect.
    if(mParent->open() == oboe::Result::OK){
        mParent->start();
    }
}

void setToneFrequency(float frequency){
    if(frequency>100 & frequency<22000)
        mFrequency = frequency;
}
