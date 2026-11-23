package com.seuapp.cycletrack.presentation.ui.onboarding

import androidx.annotation.DrawableRes
import com.seuapp.cycletrack.R

data class ChoiceOption(
    val key: String,
    val text: String,
    val explanation: String? = null
)

data class GridOption(
    val key: String,
    val label: String,
    @DrawableRes val icon: Int
)

sealed class OnboardingStep {
    data class Illustration(
        @DrawableRes val image: Int,
        val title: String,
        val subtitle: String,
        val buttonText: String = "Avançar",
        val bullets: List<String> = emptyList()
    ) : OnboardingStep()

    data class SingleChoice(
        val title: String,
        val subtitle: String,
        val options: List<ChoiceOption>,
        val resultKey: String
    ) : OnboardingStep()

    data class MultiChoiceGrid(
        val title: String,
        val subtitle: String,
        val options: List<GridOption>,
        val resultKey: String
    ) : OnboardingStep()

    data class WheelPicker(
        val title: String,
        val subtitle: String,
        val options: List<String>,
        val resultKey: String
    ) : OnboardingStep()

    data class PeriodCalendar(
        val title: String,
        val subtitle: String
    ) : OnboardingStep()

    data class HeightWeight(
        val title: String,
        val subtitle: String
    ) : OnboardingStep()

    object NotificationPriming : OnboardingStep()

    object PercentLoading : OnboardingStep()
}

const val GOAL_PERIOD = "monitorar_menstruacao"
const val GOAL_PREGNANT = "engravidar"
const val GOAL_EXPECTING = "acompanhar_gravidez"
const val GOAL_BODY = "entender_corpo"

fun introSteps(): List<OnboardingStep> = listOf(
    OnboardingStep.Illustration(
        image = R.drawable.onboarding_welcome,
        title = "Bem-vinda ao CycleTrack",
        subtitle = "Seu companheiro confiável para saúde reprodutiva"
    ),
    OnboardingStep.SingleChoice(
        title = "Privacidade em primeiro lugar",
        subtitle = "Seus dados ficam no seu aparelho. Nós apenas ajudamos.",
        options = listOf(ChoiceOption("agree", "Concordo e continuar")),
        resultKey = "privacy"
    ),
    OnboardingStep.WheelPicker(
        title = "Em que ano você nasceu?",
        subtitle = "Isso nos ajuda a personalizar suas previsões",
        options = (2010 downTo 1950).map { it.toString() },
        resultKey = "birthYear"
    ),
    OnboardingStep.MultiChoiceGrid(
        title = "O que você quer?",
        subtitle = "Escolha a opção que mais combina com você",
        options = listOf(
            GridOption(GOAL_PERIOD, "Monitorar minha menstruação", R.drawable.goal_period),
            GridOption(GOAL_PREGNANT, "Engravidar", R.drawable.goal_body),
            GridOption(GOAL_EXPECTING, "Acompanhar minha gravidez", R.drawable.goal_pregnancy),
            GridOption(GOAL_BODY, "Entender meu corpo", R.drawable.goal_body)
        ),
        resultKey = "goal"
    ),
    OnboardingStep.WheelPicker(
        title = "Qual é o tamanho do seu ciclo?",
        subtitle = "O número de dias entre menstruações",
        options = (21..40).map { it.toString() },
        resultKey = "cycleLength"
    ),
    OnboardingStep.WheelPicker(
        title = "Quantos dias dura sua menstruação?",
        subtitle = "Número médio de dias de sangramento",
        options = (3..7).map { it.toString() },
        resultKey = "periodLength"
    ),
    OnboardingStep.PeriodCalendar(
        title = "Quando começou sua última menstruação?",
        subtitle = "Toque nos dias para marcar o período"
    )
)

fun trackSteps(goal: String): List<OnboardingStep> = when (goal) {
    GOAL_PREGNANT -> listOf(
        OnboardingStep.SingleChoice(
            title = "Há quanto tempo você tenta engravidar?",
            subtitle = "Isso nos ajuda a te orientar melhor",
            options = listOf(
                ChoiceOption("acabei", "Acabei de começar a tentar"),
                ChoiceOption(
                    "3m",
                    "0 a 3 meses",
                    "É um ótimo momento para aprender sobre seu ciclo e aumentar suas chances."
                ),
                ChoiceOption(
                    "6m",
                    "3 a 6 meses",
                    "É compreensível estar ansiosa. Vamos te ajudar a entender quando você está mais fértil."
                ),
                ChoiceOption("1a", "6 meses a 1 ano")
            ),
            resultKey = "tentandoHa"
        ),
        OnboardingStep.SingleChoice(
            title = "Você tenta engravidar nos dias mais férteis?",
            subtitle = "Fazer sexo no período fértil aumenta as chances",
            options = listOf(
                ChoiceOption("sim", "Sim"),
                ChoiceOption("nao", "Não"),
                ChoiceOption(
                    "naosei",
                    "Não sei quando estou mais fértil",
                    "Tudo bem! Vamos te ajudar a descobrir isso acompanhando seus sinais de fertilidade."
                )
            ),
            resultKey = "diasFerteis"
        ),
        OnboardingStep.SingleChoice(
            title = "Você toma vitaminas pré-natais?",
            subtitle = "Ácido fólico é importante na fase de concepção",
            options = listOf(
                ChoiceOption("sim", "Sim, ácido fólico ou multivitamínico"),
                ChoiceOption("nao", "Não", "Podemos te enviar lembretes para não esquecer nenhuma dose.")
            ),
            resultKey = "vitaminas"
        ),
        OnboardingStep.Illustration(
            image = R.drawable.onboarding_pregnancy,
            title = "Você começa bem essa jornada",
            subtitle = "Vamos te ajudar a identificar os padrões de ovulação e o melhor momento para tentar."
        )
    )
    GOAL_EXPECTING -> listOf(
        OnboardingStep.WheelPicker(
            title = "Em que semana de gravidez você está?",
            subtitle = "Isso nos ajuda a acompanhar o desenvolvimento do bebê",
            options = (1..42).map { it.toString() },
            resultKey = "semanaGravidez"
        ),
        OnboardingStep.Illustration(
            image = R.drawable.onboarding_pregnancy,
            title = "O apoio continua durante a gravidez",
            subtitle = "Receba informações diárias e dicas de saúde conforme seu bebê cresce."
        )
    )
    else -> listOf(
        OnboardingStep.SingleChoice(
            title = "Você usou contraceptivos nos últimos 6 meses?",
            subtitle = "Alguns contraceptivos podem afetar seu ciclo",
            options = listOf(
                ChoiceOption("nao", "Não"),
                ChoiceOption("usando", "Ainda estou usando"),
                ChoiceOption("pilula", "Sim, usava pílula"),
                ChoiceOption("diu", "Sim, usava DIU")
            ),
            resultKey = "contraceptivos"
        ),
        OnboardingStep.SingleChoice(
            title = "Você tem alguma condição de saúde reprodutiva?",
            subtitle = "Podemos te enviar conteúdos relevantes sobre isso",
            options = listOf(
                ChoiceOption("nenhuma", "Não tenho nenhuma"),
                ChoiceOption("endometriose", "Endometriose"),
                ChoiceOption("sop", "Síndrome dos ovários policísticos (SOP)"),
                ChoiceOption("naosei", "Não tenho certeza")
            ),
            resultKey = "condicoes"
        ),
        OnboardingStep.Illustration(
            image = R.drawable.onboarding_trust,
            title = "Conheça os sinais do seu corpo",
            subtitle = "Nossa equipe te ajuda a entender seu ciclo com conteúdo confiável."
        )
    )
}

fun closingSteps(): List<OnboardingStep> = listOf(
    OnboardingStep.HeightWeight(
        title = "Altura e peso",
        subtitle = "Usamos para personalizar suas recomendações de saúde"
    ),
    OnboardingStep.NotificationPriming,
    OnboardingStep.PercentLoading,
    OnboardingStep.Illustration(
        image = R.drawable.onboarding_trust,
        title = "Tudo pronto para começar",
        subtitle = "Com o CycleTrack você tem o controle da sua saúde reprodutiva.",
        bullets = listOf(
            "Torne-se especialista na sua própria fertilidade",
            "Identifique os padrões do seu ciclo",
            "Receba dicas diárias personalizadas"
        )
    ),
    OnboardingStep.Illustration(
        image = R.drawable.onboarding_welcome,
        title = "Pronto para começar!",
        subtitle = "Você receberá dicas personalizadas diárias",
        buttonText = "Começar a usar"
    )
)
