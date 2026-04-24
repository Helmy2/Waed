package io.github.helmy2.waed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.helmy2.waed.di.appModule
import io.github.helmy2.waed.navigation.WaedNavHost
import io.github.helmy2.waed.ui.theme.WaedTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        enableEdgeToEdge()
        setContent {
            WaedTheme {
                WaedNavHost(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}