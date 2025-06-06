package com.omsi.toneplayer

import android.content.res.AssetManager
import android.util.Log
import java.io.IOException

class TonePlayer {

    companion object {
        // Used to load the 'toneplayer' library on application startup.
        init {
            System.loadLibrary("toneplayer")
        }

        // Sample attributes
        val NUM_PLAY_CHANNELS: Int = 2  // The number of channels in the player Stream.
        // Stereo Playback, set to 1 for Mono playback

        val TAG: String = "TonePlayer"
    }


    fun setupAudioStream() {
        setupAudioStreamNative(NUM_PLAY_CHANNELS)
    }

    fun startAudioStream() {
        startAudioStreamNative()
    }

    fun teardownAudioStream() {
        teardownAudioStreamNative()
    }

    fun loadWavAsset(assetMgr: AssetManager, assetName: String, pan: Float):Int {
        var position = -1
        try {
            val assetFD = assetMgr.openFd(assetName)
            val dataStream = assetFD.createInputStream()
            val dataLen = assetFD.getLength().toInt()
            val dataBytes = ByteArray(dataLen)
            dataStream.read(dataBytes, 0, dataLen)
           position = loadWavAssetNative(dataBytes, pan)
            assetFD.close()
        } catch (ex: IOException) {
            Log.i(TAG, "IOException$ex")
        }
        return position
    }

    fun unloadWavAssets() {
        unloadWavAssetsNative()
    }

    private external fun setupAudioStreamNative(numChannels: Int)
    private external fun startAudioStreamNative()
    private external fun teardownAudioStreamNative()

    private external fun loadWavAssetNative(wavBytes: ByteArray,  pan: Float):Int
    private external fun unloadWavAssetsNative()

    external fun trigger(index: Int)
    external fun stopTrigger(index: Int)

    external fun setPan(index: Int, pan: Float)
    external fun getPan(index: Int): Float

    external fun setGain(index: Int, gain: Float)
    external fun getGain(index: Int): Float

    external fun setLoopMode(index: Int, isLoopMode: Boolean)

    external fun getOutputReset() : Boolean
    external fun clearOutputReset()

    external fun restartStream()
}