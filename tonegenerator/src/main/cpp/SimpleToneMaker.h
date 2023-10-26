//
// Created by visierip on 24/10/2023.
//

#ifndef NATIVEAUDIOTEST_SIMPLETONEMAKER_H
#define NATIVEAUDIOTEST_SIMPLETONEMAKER_H

#include <oboe/Oboe.h>


class SimpleToneMaker{
public:

    oboe::Result open();
    oboe::Result start();
    oboe::Result stop();
    oboe::Result close();
    void setToneFrequency(float frequency);

private:

    class MyDataCallback : public oboe::AudioStreamDataCallback{
    public:
        oboe::DataCallbackResult onAudioReady(
                oboe::AudioStream *audioStream,
                void *audioData,
                int32_t numFrames) override;

    };

    class MyErrorCallback : public oboe::AudioStreamErrorCallback{
    private:
        SimpleToneMaker *mParent;

    public:
        MyErrorCallback(SimpleToneMaker *parent) : mParent(parent) {}

        virtual ~MyErrorCallback(){

        }

        void onErrorAfterClose(oboe::AudioStream *oboeStream, oboe::Result error) override;
    };


    std::shared_ptr<oboe::AudioStream> mStream;
    std::shared_ptr<MyDataCallback> mDataCallback;
    std::shared_ptr<MyErrorCallback> mErrorCallback;

   static constexpr int kChannelCount = 2;
     /*static constexpr float kPi = M_PI;
    static constexpr float kTwoPi = kPi * 2;
    static constexpr float kDefaultFrequency = 2000.0;
    float mFrequency{kDefaultFrequency};
*/
};





#endif //NATIVEAUDIOTEST_SIMPLETONEMAKER_H
