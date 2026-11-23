package com.seuapp.cycletrack.domain.usecase

import com.seuapp.cycletrack.domain.model.CheckInThread
import com.seuapp.cycletrack.domain.model.MoodOption

private val defaultMoodOptions = listOf(
    MoodOption("bem", "Estou bem"),
    MoodOption("sobrecarregada", "Tenho muita coisa acontecendo"),
    MoodOption("raiva", "Estou com raiva"),
    MoodOption("triste", "Estou triste"),
    MoodOption("prefiro_nao_dizer", "Prefiro não responder")
)

/**
 * Gera o check-in conversacional do dia a partir de regras fixas (sem IA
 * real), baseado na fase e no dia do ciclo (Fase 6).
 */
object GenerateCheckInUseCase {

    operator fun invoke(dayOfCycle: Int, cyclePhase: CyclePhase): CheckInThread {
        return when {
            dayOfCycle == 1 -> CheckInThread(
                id = "checkin_novo_ciclo",
                title = "Novo ciclo",
                preview = "Um novo ciclo começou. Como você está se sentindo hoje?",
                timestamp = "Agora",
                botLines = listOf(
                    "Olá, aqui é sua Assistente de Saúde!",
                    "Um novo ciclo começou.",
                    "Como você está se sentindo hoje?"
                ),
                moodOptions = defaultMoodOptions
            )
            cyclePhase == CyclePhase.MENSTRUAL -> CheckInThread(
                id = "checkin_menstruacao",
                title = "Ajuda na menstruação",
                preview = "Mais um dia de menstruação por aqui. Separei algumas dicas que podem ajudar.",
                timestamp = "Hoje",
                botLines = listOf(
                    "Mais um dia de menstruação por aqui.",
                    "Cólicas e cansaço são comuns nesta fase.",
                    "Como você está se sentindo hoje?"
                ),
                moodOptions = defaultMoodOptions
            )
            cyclePhase == CyclePhase.OVULATION -> CheckInThread(
                id = "checkin_ovulacao",
                title = "Janela fértil",
                preview = "Você está na sua janela fértil. Como está se sentindo hoje?",
                timestamp = "Hoje",
                botLines = listOf(
                    "Você está na sua janela fértil.",
                    "É normal sentir mais energia ou sensibilidade nesta fase.",
                    "Como você está se sentindo hoje?"
                ),
                moodOptions = defaultMoodOptions
            )
            else -> CheckInThread(
                id = "checkin_diario",
                title = "Check-in diário",
                preview = "Como você está se sentindo hoje?",
                timestamp = "Hoje",
                botLines = listOf(
                    "Oi! Passando para o seu check-in diário.",
                    "Como você está se sentindo hoje?"
                ),
                moodOptions = defaultMoodOptions
            )
        }
    }
}
