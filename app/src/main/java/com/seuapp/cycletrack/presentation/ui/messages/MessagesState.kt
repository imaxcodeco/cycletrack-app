package com.seuapp.cycletrack.presentation.ui.messages

import androidx.compose.runtime.mutableStateListOf

/**
 * Estado compartilhado simples (em memoria) marcando quais threads de
 * check-in ja foram respondidas nesta sessao do app.
 */
val answeredThreads = mutableStateListOf<String>()
