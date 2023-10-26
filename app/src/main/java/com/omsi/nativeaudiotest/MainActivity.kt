package com.omsi.nativeaudiotest

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omsi.nativeaudiotest.ui.theme.SamplesTheme

class MainActivity : ComponentActivity() {

    private lateinit var audioManager:AudioManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //IMPROVE
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            1,
            0)

        var toneGenerator = com.omsi.tonegenerator.CustomToneGenerator
       // ProcessLifecycleOwner.get().lifecycle.addObserver(AudioPlayer)

        setContent {
            SamplesTheme {
                var playerState by remember{ mutableStateOf(false) }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainControls2 (playerState){ play ->
                        run {
                            if (play) {
                                toneGenerator.startAudioStreamNative()
                            } else {
                                toneGenerator.stopAudioStreamNative()
                            }
                            playerState = play
                        }
                    }
                }
            }
        }

    }

}


@Composable
fun MainControls() {
    val playerState by AudioPlayer.playerState.collectAsStateWithLifecycle()
    MainControls(playerState, AudioPlayer::setPlaybackEnabled)
}

@Composable
fun MainControls(playerState: PlayerState, setPlaybackEnabled: (Boolean) -> Unit) {

    Column {

        val isPlaying = playerState is PlayerState.Started

        Text(text = "Minimal Oboe!")

        Row {
            Button(
                onClick = { setPlaybackEnabled(true) },
                enabled = !isPlaying
            ) {
                Text(text = "Start Audio")
            }
            Button(
                onClick = { setPlaybackEnabled(false) },
                enabled = isPlaying
            ) {
                Text(text = "Stop Audio")
            }
        }

        // Create a status message for displaying the current playback state.
        val uiStatusMessage = "Current status: " +
                when (playerState) {
                    PlayerState.NoResultYet -> "No result yet"
                    PlayerState.Started -> "Started"
                    PlayerState.Stopped -> "Stopped"
                    is PlayerState.Unknown -> {
                        "Unknown. Result = " + playerState.resultCode
                    }
                }

        Text(uiStatusMessage)
    }
}





@Composable
fun MainControls2(isPlaying: Boolean, setPlaybackEnabled: (Boolean) -> Unit) {

    Column {


        Text(text = "Minimal Oboe!")

        Row {
            Button(
                onClick = { setPlaybackEnabled(true) },
                enabled = !isPlaying
            ) {
                Text(text = "Start Audio")
            }
            Button(
                onClick = { setPlaybackEnabled(false) },
                enabled = isPlaying
            ) {
                Text(text = "Stop Audio")
            }
        }

    }
}









@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SamplesTheme {
        MainControls(PlayerState.Started) { }
    }
}