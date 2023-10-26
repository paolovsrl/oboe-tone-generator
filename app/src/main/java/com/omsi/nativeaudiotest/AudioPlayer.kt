package com.omsi.nativeaudiotest

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

//Improvement only for Jetpack compose. Now the c++ is in a separate module that has been included.
object AudioPlayer : DefaultLifecycleObserver {


    // Used to load the 'nativeaudiotest' library on application startup.
    init {
        System.loadLibrary("simpletone")

    }


    private val coroutineScope = CoroutineScope(Dispatchers.Default) + Job()

    private var _playerState = MutableStateFlow<PlayerState>(PlayerState.NoResultYet)
    val playerState = _playerState.asStateFlow()


    var toneGenerator = com.omsi.tonegenerator.CustomToneGenerator



    fun setPlaybackEnabled(isEnabled:Boolean) {
        // Start (and stop) Oboe from a coroutine in case it blocks for too long.
        // If the AudioServer has died it may take several seconds to recover.
        // That can cause an ANR if we are starting audio from the main UI thread.
        coroutineScope.launch {
            val result = if (isEnabled) {
                toneGenerator.startAudioStreamNative()
            } else {
                toneGenerator.stopAudioStreamNative()
            }

            val newUiState = if (result == 0) {
                if (isEnabled) {
                    PlayerState.Started
                } else {
                    PlayerState.Stopped
                }
            }else {
                PlayerState.Unknown(result)
            }
            _playerState.update { newUiState }

        }


    }


    override fun onStop(owner: LifecycleOwner){
        setPlaybackEnabled(false)
        super.onStop(owner)
    }

}

sealed interface PlayerState {
    object NoResultYet : PlayerState
    object Started : PlayerState
    object Stopped : PlayerState
    data class Unknown(val resultCode: Int) : PlayerState
}