package com.omsi.nativeaudiotest

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.omsi.nativeaudiotest.ui.theme.SamplesTheme
import com.omsi.tonegenerator.CustomToneGenerator
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var audioManager:AudioManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*2/6,
            0)

        var toneGenerator = CustomToneGenerator

        setContent {
            var coroutineScope = rememberCoroutineScope()

            SamplesTheme {
                var playerState by remember{ mutableStateOf(false) }
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        MainControls(
                            isPlaying = playerState,
                            setPlaybackEnabled = { play ->
                                coroutineScope.launch {
                                    if (play) {
                                        toneGenerator.startAudioStreamNative()
                                    } else {
                                        toneGenerator.stopAudioStreamNative()
                                    }
                                    playerState = play
                                }
                            },
                            setToneFrequency = { frequency ->
                                coroutineScope.launch {
                                    toneGenerator.setAudioStreamNativeFrequency(frequency)
                                }
                            })
                    }
                }
            }
        }

    }

}


@Composable
fun MainControls(isPlaying: Boolean, setPlaybackEnabled: (Boolean) -> Unit, setToneFrequency: (Float) -> Unit) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        var frequency by remember{mutableStateOf(800f)}

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

        Slider(
            value = frequency,
            onValueChange = {
                frequency = it.coerceIn(10f, 1000f)

            },
            onValueChangeFinished = {
                setToneFrequency(frequency)
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            valueRange = 10f..1000f

        )

    }
}









@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SamplesTheme {
        MainControls(
            isPlaying = true,
            setPlaybackEnabled = {}
        ) { }
    }
}