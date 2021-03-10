/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.Instant

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

data class Timer(val hours: Int, val minutes: Int, val seconds: Int)

typealias Milliseconds = Long

fun Timer.toMilliseconds(): Milliseconds = (hours * 60 * 60 + minutes * 60 + seconds) * 1000L

fun Milliseconds.toTimer(): Timer {
    val hours: Int = ((this / 3600) / 1000).toInt()
    val minutes: Int = ((this / 1000 - hours * 3600) / 60).toInt()
    val seconds: Int = (this / 1000 - hours * 3600 - minutes * 60).toInt()
    println("===========================\n$this\n${hours}h ${minutes}m ${seconds}s")
    return Timer(hours, minutes, seconds)
}

fun pad(x: Int): String = if (x < 10) "0$x" else "$x"

fun Timer.format(): String {
    return "${pad(hours)}:${pad(minutes)}:${pad(seconds)}"
}

typealias EditableTimer = List<Char>

fun EditableTimer.toTimer(): Timer {
    val significantValues = if (size >= 6)
        takeLast(6)
    else
        (0..6 - size).map { '0' } + this

    val hours = significantValues.subList(0, 2).joinToString("").toInt()
    val minutes = significantValues.subList(2, 4).joinToString("").toInt()
    val seconds = significantValues.subList(4, 6).joinToString("").toInt()
    return Timer(hours, minutes, seconds)
}

enum class CountdownState { COUNTING, PAUSED }

data class Countdown(val timer: Timer, val state: CountdownState, val expiration: Instant? = null)

// Start building your app here!
@Composable
fun MyApp() {
    val start = Instant.now()
    var isEditing by remember { mutableStateOf(true) }
    var isCountingDown by remember { mutableStateOf(false) }
    var editableTime by remember { mutableStateOf(listOf<Char>()) }
    var countdownState: CountdownState by remember { mutableStateOf(CountdownState.PAUSED) }
    val length = editableTime.toTimer()
    var remainingMilliseconds by remember { mutableStateOf(length.toMilliseconds()) }
    val scope = rememberCoroutineScope()
    val expiration = start.plusMillis(length.toMilliseconds())
    scope.launch {
        if (countdownState == CountdownState.COUNTING) {
            while (expiration.isAfter(Instant.now())) {
                remainingMilliseconds = (expiration.toEpochMilli() - Instant.now().toEpochMilli())
                delay(1000)
            }
            remainingMilliseconds = 0
        }
    }
    fun updateEditableTime(block: (MutableList<Char>) -> Unit) {
        val numbers = editableTime.toMutableList()
        block(numbers)
        editableTime = numbers
    }

    fun onClickNumber(num: Int) {
        updateEditableTime {
            it.add("$num".first())
        }
    }

    fun onClickDelete() {
        updateEditableTime {
            if (it.isNotEmpty()) it.removeLast()
        }
    }

    fun onPlayOrResume() {
        isCountingDown = true
        isEditing = false
    }

    fun onPause() {
        isCountingDown = false
    }

    fun onClickFab() {
        if (isCountingDown) {
            onPause()
        } else {
            onPlayOrResume()
        }
    }

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = ::onClickFab) {
                    Text(text = if (isCountingDown) "||" else ">")
                }
            }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isEditing) {
                    TimerText(text = length.format(), Modifier.clickable { isEditing = true })
                    NumberPad(onClickNumber = ::onClickNumber, onClickDelete = ::onClickDelete)
                } else {
                    TimerText(text = remainingMilliseconds.toTimer().format())
                }
            }
        }
    }
}

@Composable
fun TimerText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1,
        fontSize = 32.sp,
        modifier = modifier
    )
}

val keyPadding = 16.dp

@Composable
fun NumberPad(onClickNumber: (Int) -> Unit, onClickDelete: () -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            NumberKey(number = 1, onClick = onClickNumber)
            NumberKey(number = 2, onClick = onClickNumber)
            NumberKey(number = 3, onClick = onClickNumber)
        }
        Row {
            NumberKey(number = 4, onClick = onClickNumber)
            NumberKey(number = 5, onClick = onClickNumber)
            NumberKey(number = 6, onClick = onClickNumber)
        }
        Row {
            NumberKey(number = 7, onClick = onClickNumber)
            NumberKey(number = 8, onClick = onClickNumber)
            NumberKey(number = 9, onClick = onClickNumber)
        }
        Row {
            Text(text = " ", Modifier.padding(keyPadding))
            NumberKey(number = 0, onClick = onClickNumber)
            Text(text = "<",
                Modifier
                    .clickable { onClickDelete() }
                    .padding(keyPadding))
        }
    }
}

@Composable
fun NumberKey(number: Int, onClick: (Int) -> Unit) {
    Text(text = "$number",
        Modifier
            .clickable { onClick(number) }
            .padding(keyPadding))
}


@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
