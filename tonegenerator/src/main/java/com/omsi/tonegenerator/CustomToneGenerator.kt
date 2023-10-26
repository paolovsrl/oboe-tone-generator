package com.omsi.tonegenerator

public object CustomToneGenerator {

    init {
        System.loadLibrary("simpletone")

    }


    external fun startAudioStreamNative(): Int
    external fun stopAudioStreamNative(): Int

}