package io.github.youhwanjung.trailog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.github.youhwanjung.trailog.ui.navigation.TrailogApp
import io.github.youhwanjung.trailog.ui.theme.TrailogTheme

/**
 * @AndroidEntryPoint 가 있어야 이 Activity 와 그 안의 Composable 이
 * Hilt 그래프에 접근할 수 있다. hiltViewModel() 은 이 주입 지점을 타고 올라간다.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        /**
         * setContent 안에는 화면 정의만 이루어지고,
         * 이벤트들이 왔을 때 어떻게 사용할지를 정한다.
         * */
        setContent {
            TrailogTheme {
                TrailogApp()
            }
        }
    }
}
