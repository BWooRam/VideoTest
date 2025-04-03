package com.hyundaiht.videotest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.hyundaiht.videotest.exoplayer.ExoplayerTestActivity
import com.hyundaiht.videotest.pip.PipTest1Activity
import com.hyundaiht.videotest.pip.PipTest2Activity
import com.hyundaiht.videotest.pip.PipTest3Activity
import com.hyundaiht.videotest.pip.PipTest4Activity
import com.hyundaiht.videotest.ui.TextWithButton
import com.hyundaiht.videotest.ui.theme.VideoTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            VideoTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        TextWithButton(
                            "ExoPlayer 사용법"
                        ) {
                            startActivity(Intent(context, ExoplayerTestActivity::class.java))
                        }

                        TextWithButton(
                            "PIP 동영상 테스트(혼합 방법)"
                        ) {
                            startActivity(Intent(context, PipTest1Activity::class.java))
                        }

                        TextWithButton(
                            "PIP 동영상 테스트(Only Compose)"
                        ) {
                            startActivity(Intent(context, PipTest2Activity::class.java))
                        }

                        TextWithButton(
                            "PIP 버튼 테스트(Broadcast)"
                        ) {
                            startActivity(Intent(context, PipTest3Activity::class.java))
                        }

                        TextWithButton(
                            "PIP 버튼 테스트(Activity)"
                        ) {
                            startActivity(Intent(context, PipTest4Activity::class.java))
                        }
                    }
                }
            }
        }
    }
}