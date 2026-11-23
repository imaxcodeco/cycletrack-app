package com.seuapp.cycletrack.domain.usecase

import com.seuapp.cycletrack.domain.model.CheckInThread
import com.seuapp.cycletrack.domain.model.MoodOption

private val defaultMoodOptions = listOf(
    MoodOption("bem", "Estou bem"),
    MoodOption("sobrecarregada", "Tenho muita coisa acontecendo"),
    MoodOption("raiva", "Estou com raiva"),
    MoodOption("triste", "Estou triste"),
    MoodOption("prefiro_nao_dizer", "Prefiro nao responder")
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
                preview = "Um novo ciclo comecou. Como voce esta se sentindo hoje?",
                timestamp = "Agora",
                botLines = listOf(
                    "Ola, aqui e sua Assistente de Saude!",
                    "Um novo ciclo comecou.",
                    "Como voce esta se sentindo hoje?"
                ),
                moodOptions = defaultMoodOptions
            )
            cyclePhase == CyclePhase.MENSTRUAL -> CheckInThread(
                id = "checkin_menstruacao",
                title = "Ajuda na menstruacao",
                preview = "Mais um dia de menstruacao por aqui. Separei algumas dicas que podem ajudar.",
                timestamp = "Hoje",
                botLines = listOf(
                    "Mais um dia de menstruacao por aqui.",
                    "Colicas e cansaco sao comuns nesta fase.",
                    "Como voce esta se sentindo hoje?"
                ),
                moodOptions = defaultMoodOptions
            )
            cyclePhase == CyclePhase.OVULATION -> CheckInThread(
                id = "checkin_ovulacao",
                title = "Janela fertil",
                preview = "Voce esta na sua janela fertil. Como esta se sentindo hoje?",
                timestamp = "Hoje",
                botLines = listOf(
                    "Voce esta na sua janela fertil.",
                    "E normal sentir mais energia ou sensibilidade nesta fase.",
                    "Como voce esta se sentindo hoje?"
                ),
                moodOptions = defaultMoodOptions
            )
            else -> CheckInThread(
                id = "checkin_diario",
                title = "Check-in diario",
                preview = "Como voce esta se sentindo hoje?",
                timestamp = "Hoje",
                botLines = listOf(
                    "Oi! Passando para o seu check-in diario.",
                    "Como voce esta se sentindo hoje?"
                ),
                moodOptions = defaultMoodOptions
            )
        }
    }
}
