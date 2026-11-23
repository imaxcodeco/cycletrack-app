package com.seuapp.cycletrack.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.seuapp.cycletrack.presentation.navigation.CycleTrackApp
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal do CycleTrack.
 *
 * Single Activity + Compose Navigation (padrão Compose-only).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CycleTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CycleTrackApp()
                }
            }
        }
    }
}
