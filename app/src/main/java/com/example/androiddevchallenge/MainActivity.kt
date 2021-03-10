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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.FabPosition
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

// Start building your app here!
@Composable
fun MyApp() {
    var isCountingDown by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var timer by remember { mutableStateOf(9) }

    scope.launch {
        while (true) {
            if (isCountingDown) timer--
            if (timer == 0) isCountingDown = false
            delay(1000)
        }
    }

    fun onPlayOrResume() {
        if (timer > 0) isCountingDown = true
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
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                val firstDigit = timer / 10
                val secondDigit = timer % 10
                EditableDigit(
                    num = firstDigit,
                    editable = !isCountingDown,
                    onClickAdd = { if (timer / 10 < 9) timer += 10 },
                    onClickMinus = { if (timer / 10 > 0) timer -= 10 }
                )
                Spacer(modifier = Modifier.width(8.dp))
                EditableDigit(
                    num = secondDigit,
                    editable = !isCountingDown,
                    onClickAdd = { if (timer < 99) timer++ },
                    onClickMinus = { if (timer > 0) timer-- }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EditableDigit(num: Int, editable: Boolean, onClickAdd: () -> Unit, onClickMinus: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(visible = editable) {
            Button(onClick = onClickAdd) {
                Text(text = "+", fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        DigitGrid(num = num)
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(visible = editable) {
            Button(onClick = onClickMinus) {
                Text(text = "-", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun NumberPixel(on: Boolean) {
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(24.dp)
            .background(if (on) MaterialTheme.colors.secondary else MaterialTheme.colors.background)
    )
}

val digitMapping = arrayOf(
    arrayOf(
        arrayOf(1, 1, 1, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
    ),
    arrayOf(
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
    ),
    arrayOf(
        arrayOf(1, 1, 1, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
        arrayOf(1, 0, 0, 0),
        arrayOf(1, 0, 0, 0),
        arrayOf(1, 1, 1, 1),
    ),
    arrayOf(
        arrayOf(1, 1, 1, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
    ),
    arrayOf(
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
    ),
    arrayOf(
        arrayOf(1, 1, 1, 1),
        arrayOf(1, 0, 0, 0),
        arrayOf(1, 0, 0, 0),
        arrayOf(1, 1, 1, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
    ),
    arrayOf(
        arrayOf(1, 1, 1, 1),
        arrayOf(1, 0, 0, 0),
        arrayOf(1, 0, 0, 0),
        arrayOf(1, 1, 1, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
    ),
    arrayOf(
        arrayOf(1, 1, 1, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
    ),
    arrayOf(
        arrayOf(1, 1, 1, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
    ),
    arrayOf(
        arrayOf(1, 1, 1, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 0, 0, 1),
        arrayOf(1, 1, 1, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
        arrayOf(0, 0, 0, 1),
    ),
)

@Composable
fun DigitGrid(num: Int) {
    Column {
        digitMapping[num]
            .map {
                Row {
                    it.map {
                        NumberPixel(on = it != 0)
                    }
                }
            }
    }
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
