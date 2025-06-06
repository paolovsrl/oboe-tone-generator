package com.omsi.nativeaudiotest

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.omsi.toneplayer.TonePlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var audioManager:AudioManager;
    private var tonePlayer = TonePlayer()

    var toneIndex = -1

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
                    Column(){
                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f), contentAlignment = Alignment.Center) {
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
                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center){

                            var loopMode by remember{ mutableStateOf(false) }
                            LaunchedEffect("") {
                                coroutineScope.launch {
                                    delay(1000)
                                    tonePlayer.setGain(toneIndex, 2.0f) //0-2
                                }

                            }
                            Column (Modifier.fillMaxSize()) {
                                Button(onClick = {
                                    coroutineScope.launch {
                                        tonePlayer.trigger(toneIndex)
                                        Log.i("TonePlayer", "gain=${tonePlayer.getGain(toneIndex)}")
                                    }
                                }) {
                                    Text("Play")
                                }

                                Button(onClick = {
                                    coroutineScope.launch { tonePlayer.stopTrigger(toneIndex) }
                                }) {
                                    Text("Stop")
                                }

                                Button(onClick = {
                                    loopMode= !loopMode
                                    coroutineScope.launch { tonePlayer.setLoopMode(toneIndex, loopMode) }
                                }) {
                                    Text(if(loopMode) "Loop ON" else "Loop OFF")
                                }
                            }
                        }
                    }

                }
            }
        }

    }


    override fun onStart() {
        super.onStart()
        tonePlayer.setupAudioStream()
        toneIndex = tonePlayer.loadWavAsset(assets, "car_emergency_lights.wav", 0f)//PAN=0 -> dead center
        Log.i("TonePlayer", "toneIndex=$toneIndex")
        tonePlayer.startAudioStream()
    }

    override fun onStop() {
        tonePlayer.teardownAudioStream()
        tonePlayer.unloadWavAssets()
        toneIndex = -1
        super.onStop()
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