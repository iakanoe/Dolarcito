package io.iakanoe.github.dolarcito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import io.iakanoe.github.dolarcito.ui.DolarcitoNavigation
import io.iakanoe.github.dolarcito.ui.common.theme.DolarcitoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DolarcitoTheme {
                DolarcitoNavigation()
            }
        }
    }
}

