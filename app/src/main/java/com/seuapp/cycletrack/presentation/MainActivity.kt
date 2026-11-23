package com.seuapp.cycletrack.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackTheme
import com.seuapp.cycletrack.presentation.navigation.CycleTrackApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CycleTrackTheme {
                CycleTrackApp()
            }
        }
    }
}
