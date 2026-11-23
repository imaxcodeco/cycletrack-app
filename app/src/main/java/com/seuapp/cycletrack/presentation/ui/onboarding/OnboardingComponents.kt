package com.seuapp.cycletrack.presentation.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.delay

private val Rose = Color(0xFFE75480)
private val LightRose = Color(0xFFFFD6E8)
private val CardGray = Color(0xFFF5F5F5)

@Composable
fun FullIllustrationScreen(step: OnboardingStep.Illustration, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = step.image),
            contentDescription = step.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = step.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = step.subtitle,
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        if (step.bullets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                step.bullets.forEach { bullet ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("✓ ", color = Rose, fontWeight = FontWeight.Bold)
                        Text(bullet, fontSize = 14.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Rose),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(step.buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SingleChoiceScreen(step: OnboardingStep.SingleChoice, onSelect: (ChoiceOption) -> Unit) {
    var expandedKey by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(step.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(step.subtitle, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

        step.options.forEach { option ->
            val isExpanded = expandedKey == option.key
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (option.explanation != null && expandedKey != option.key) {
                            expandedKey = option.key
                        } else {
                            onSelect(option)
                        }
                    }
                    .background(
                        if (isExpanded) Rose else CardGray,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        option.text,
                        fontSize = 16.sp,
                        fontWeight = if (isExpanded) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isExpanded) Color.White else Color.Black
                    )
                    AnimatedVisibility(visible = isExpanded && option.explanation != null) {
                        Text(
                            option.explanation.orEmpty(),
                            fontSize = 13.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (isExpanded) {
                LaunchedEffect(option.key) {
                    delay(1400)
                    onSelect(option)
                }
            }
        }
    }
}

@Composable
fun MultiChoiceGridScreen(step: OnboardingStep.MultiChoiceGrid, onSelect: (GridOption) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(step.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(step.subtitle, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(step.options) { option ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onSelect(option) }
                        .background(CardGray, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(LightRose, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = option.icon),
                            contentDescription = option.label,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        option.label,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun WheelPickerScreen(step: OnboardingStep.WheelPicker, onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(step.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(step.subtitle, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

        step.options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option) }
                    .background(CardGray, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(option, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PeriodCalendarScreen(
    step: OnboardingStep.PeriodCalendar,
    onConfirm: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val month = YearMonth.from(today)
    var selectedStart by remember { mutableStateOf(today.minusDays(3)) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(step.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(step.subtitle, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { label ->
                Text(
                    label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val first = month.atDay(1)
        val offset = (first.dayOfWeek.value - DayOfWeek.MONDAY.value + 1) % 7
        val daysInMonth = month.lengthOfMonth()
        val cells = offset + daysInMonth
        val rows = (cells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val dayNumber = row * 7 + col - offset + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNumber in 1..daysInMonth) {
                            val date = month.atDay(dayNumber)
                            val isPeriodDay = !date.isBefore(selectedStart) &&
                                !date.isAfter(selectedStart.plusDays(4)) &&
                                !date.isAfter(today)
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        if (isPeriodDay) Rose else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable(enabled = !date.isAfter(today)) {
                                        selectedStart = date
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$dayNumber",
                                    color = if (isPeriodDay) Color.White else Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { onConfirm(selectedStart) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Rose),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Confirmar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HeightWeightScreen(
    step: OnboardingStep.HeightWeight,
    onConfirm: (Int, Float) -> Unit
) {
    var useMetric by remember { mutableStateOf(true) }
    val heightOptions = remember { (140..200).toList() }
    val weightOptions = remember { (35..150).toList() }
    var selectedHeight by remember { mutableStateOf(165) }
    var selectedWeight by remember { mutableStateOf(60) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(step.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(step.subtitle, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardGray, RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            listOf("Métrico", "Imperial").forEachIndexed { index, label ->
                val selected = (index == 0) == useMetric
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { useMetric = index == 0 }
                        .background(
                            if (selected) Color.White else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Altura (cm)", fontSize = 13.sp, color = Color.Gray)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        heightOptions.forEach { value ->
                            val selected = value == selectedHeight
                            Text(
                                "$value",
                                fontSize = if (selected) 20.sp else 15.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Rose else Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedHeight = value }
                                    .padding(vertical = 6.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Peso (kg)", fontSize = 13.sp, color = Color.Gray)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        weightOptions.forEach { value ->
                            val selected = value == selectedWeight
                            Text(
                                "$value",
                                fontSize = if (selected) 20.sp else 15.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Rose else Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedWeight = value }
                                    .padding(vertical = 6.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { onConfirm(selectedHeight, selectedWeight.toFloat()) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Rose),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Avançar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun NotificationPrimingScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(LightRose, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = com.seuapp.cycletrack.R.drawable.onboarding_notification),
                contentDescription = "Notificações",
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Não perca seus dias importantes",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Toque em \"Permitir\" na próxima tela para receber lembretes de período e ovulação.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Rose),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Avançar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PercentLoadingScreen(onFinished: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val steps = 30
        repeat(steps) {
            progress = (it + 1) / steps.toFloat()
            delay(45)
        }
        delay(300)
        onFinished()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(140.dp),
                color = Rose,
                strokeWidth = 10.dp,
                trackColor = CardGray
            )
            Text(
                "${(progress * 100).toInt()}%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Personalizando sua experiência...",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
