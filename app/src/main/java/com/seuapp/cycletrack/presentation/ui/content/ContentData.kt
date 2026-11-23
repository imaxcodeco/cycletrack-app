package com.seuapp.cycletrack.presentation.ui.content

import com.seuapp.cycletrack.domain.model.Article

data class ContentSection(val title: String, val articles: List<Article>)

val contentSections: List<ContentSection> = listOf(
    ContentSection(
        title = "Entendendo seu ciclo",
        articles = listOf(
            Article(
                id = "ciclo_fases",
                section = "Entendendo seu ciclo",
                title = "As 4 fases do ciclo menstrual explicadas",
                summary = "Menstrual, folicular, ovulatória e lútea: o que acontece no seu corpo em cada uma.",
                body = listOf(
                    "O ciclo menstrual é dividido em quatro fases principais, cada uma marcada por mudanças hormonais que afetam o corpo e o humor.",
                    "Fase menstrual: o revestimento do útero é eliminado, geralmente entre 3 e 7 dias. É comum sentir cólicas e fadiga nesse período.",
                    "Fase folicular: começa no primeiro dia da menstruação e termina na ovulação. O estrogênio sobe, preparando os óvulos nos ovários.",
                    "Ovulação: um óvulo maduro é liberado, geralmente por volta do 14º dia em um ciclo de 28 dias. É a janela de maior fertilidade.",
                    "Fase lútea: entre a ovulação e a próxima menstruação. A progesterona aumenta e, se não houver gravidez, cai novamente, iniciando um novo ciclo."
                ),
                reviewerName = "Dra. Camila Nogueira",
                reviewerCredential = "Ginecologista, 12 anos de experiência clínica"
            ),
            Article(
                id = "ciclo_irregular",
                section = "Entendendo seu ciclo",
                title = "Ciclo irregular: quando se preocupar?",
                summary = "Variações são normais, mas alguns sinais merecem atenção médica.",
                body = listOf(
                    "É normal que o ciclo varie alguns dias de um mês para outro. Fatores como estresse, viagens, mudança de peso e sono afetam o equilíbrio hormonal.",
                    "Procure um profissional se os ciclos tiverem menos de 21 ou mais de 35 dias com frequência, se faltarem períodos por 3 meses ou mais, ou se o sangramento for muito intenso.",
                    "Registrar seus ciclos regularmente ajuda a identificar padrões e torna a consulta médica mais produtiva."
                ),
                reviewerName = "Dra. Camila Nogueira",
                reviewerCredential = "Ginecologista, 12 anos de experiência clínica"
            ),
            Article(
                id = "ciclo_hormonios",
                section = "Entendendo seu ciclo",
                title = "Hormônios: os mensageiros do seu ciclo",
                summary = "Estrogênio, progesterona, LH e FSH — o papel de cada um.",
                body = listOf(
                    "Quatro hormônios principais comandam o ciclo menstrual: estrogênio, progesterona, hormônio luteinizante (LH) e hormônio folículo-estimulante (FSH).",
                    "O FSH estimula o crescimento dos folículos ovarianos. O estrogênio prepara o endométrio e desencadeia o pico de LH, que causa a ovulação.",
                    "Após a ovulação, a progesterona domina, mantendo o endométrio espesso caso ocorra gravidez. Se não houver fecundação, os níveis caem e a menstruação começa."
                ),
                reviewerName = "Dr. Felipe Andrade",
                reviewerCredential = "Endocrinologista, especialista em saúde hormonal"
            )
        )
    ),
    ContentSection(
        title = "Sintomas e bem-estar",
        articles = listOf(
            Article(
                id = "tpm_alivio",
                section = "Sintomas e bem-estar",
                title = "TPM: estratégias para aliviar os sintomas",
                summary = "Alimentação, exercício e sono podem reduzir o desconforto pré-menstrual.",
                body = listOf(
                    "A tensão pré-menstrual (TPM) afeta até 75% das pessoas que menstruam, com sintomas como irritabilidade, inchaço, sensibilidade nos seios e alterações de humor.",
                    "Reduzir sal, cafeína e açúcar nos dias que antecedem a menstruação pode ajudar a diminuir o inchaço e as oscilações de energia.",
                    "Exercícios leves, como caminhada e ioga, liberam endorfina e ajudam a regular o humor. Priorizar o sono também é fundamental.",
                    "Se os sintomas forem incapacitantes, vale conversar com um profissional sobre transtorno disfórico pré-menstrual (TDPM)."
                ),
                reviewerName = "Dra. Camila Nogueira",
                reviewerCredential = "Ginecologista, 12 anos de experiência clínica"
            ),
            Article(
                id = "colica_quando_preocupar",
                section = "Sintomas e bem-estar",
                title = "Cólica menstrual: quando é normal e quando investigar",
                summary = "Dor leve é comum, mas dor intensa pode indicar endometriose.",
                body = listOf(
                    "Cólicas leves a moderadas nos primeiros dias da menstruação são causadas pelas contrações do útero e são consideradas normais.",
                    "Compressas quentes, exercícios leves e anti-inflamatórios de venda livre costumam aliviar o desconforto.",
                    "Dor intensa que interfere nas atividades diárias, piora com o tempo ou vem acompanhada de sangramento muito forte pode ser sinal de endometriose ou outras condições — procure avaliação médica."
                ),
                reviewerName = "Dra. Camila Nogueira",
                reviewerCredential = "Ginecologista, 12 anos de experiência clínica"
            ),
            Article(
                id = "sono_ciclo",
                section = "Sintomas e bem-estar",
                title = "Por que o sono muda ao longo do ciclo?",
                summary = "A progesterona afeta diretamente a qualidade do sono.",
                body = listOf(
                    "Na fase lútea, o aumento da progesterona pode causar sonolência durante o dia e sono mais leve à noite para algumas pessoas.",
                    "Já na fase menstrual, cólicas e quedas de ferro (por sangramento) podem atrapalhar o descanso.",
                    "Manter uma rotina de sono consistente e evitar telas antes de dormir ajuda a minimizar esses efeitos ao longo do ciclo."
                ),
                reviewerName = "Dr. Felipe Andrade",
                reviewerCredential = "Endocrinologista, especialista em saúde hormonal"
            )
        )
    ),
    ContentSection(
        title = "Fertilidade e ovulação",
        articles = listOf(
            Article(
                id = "janela_fertil",
                section = "Fertilidade e ovulação",
                title = "Janela fértil: como identificar seus dias mais férteis",
                summary = "O óvulo vive até 24h, mas o espermatozoide pode sobreviver dias.",
                body = listOf(
                    "A janela fértil é o período em que a chance de engravidar é maior — geralmente os 5 dias antes da ovulação mais o próprio dia da ovulação.",
                    "Isso acontece porque o espermatozoide pode sobreviver no corpo por até 5 dias, enquanto o óvulo liberado vive apenas cerca de 24 horas.",
                    "Sinais como muco cervical transparente e elástico, leve aumento da temperatura basal e dor leve em um dos lados do abdômen podem indicar a ovulação."
                ),
                reviewerName = "Dra. Camila Nogueira",
                reviewerCredential = "Ginecologista, 12 anos de experiência clínica"
            ),
            Article(
                id = "testes_ovulacao",
                section = "Fertilidade e ovulação",
                title = "Como funcionam os testes de ovulação?",
                summary = "Eles detectam o pico do hormônio LH na urina.",
                body = listOf(
                    "Os testes de ovulação detectam o aumento do hormônio luteinizante (LH), que dispara cerca de 24 a 36 horas antes da ovulação.",
                    "O ideal é começar a testar alguns dias antes da data estimada de ovulação, sempre no mesmo horário do dia, evitando urina muito diluída.",
                    "Um resultado positivo indica que a ovulação deve ocorrer nas próximas 24 a 36 horas — o melhor momento para tentar engravidar."
                ),
                reviewerName = "Dr. Felipe Andrade",
                reviewerCredential = "Endocrinologista, especialista em saúde hormonal"
            ),
            Article(
                id = "temperatura_basal",
                section = "Fertilidade e ovulação",
                title = "Temperatura basal: um termômetro para o seu ciclo",
                summary = "Pequenas variações revelam quando você ovulou.",
                body = listOf(
                    "A temperatura corporal basal (TCB) é a temperatura do corpo em repouso, medida ao acordar, antes de qualquer atividade.",
                    "Após a ovulação, a progesterona eleva a TCB em cerca de 0,3 a 0,5°C, e essa elevação se mantém até a próxima menstruação.",
                    "Registrar a TCB diariamente por alguns meses ajuda a identificar o padrão do seu ciclo, mas funciona melhor combinado com outros sinais de fertilidade."
                ),
                reviewerName = "Dra. Camila Nogueira",
                reviewerCredential = "Ginecologista, 12 anos de experiência clínica"
            )
        )
    ),
    ContentSection(
        title = "Perguntas frequentes",
        articles = listOf(
            Article(
                id = "faq_atraso",
                section = "Perguntas frequentes",
                title = "Menstruação atrasou: e agora?",
                summary = "Estresse, viagens e exercício intenso podem atrasar o ciclo.",
                body = listOf(
                    "Um atraso ocasional geralmente não é motivo de preocupação. Estresse, mudanças de rotina, viagens e exercício físico intenso podem adiar a ovulação e, consequentemente, a menstruação.",
                    "Se há possibilidade de gravidez, um teste de farmácia feito após o atraso costuma ser confiável.",
                    "Atrasos frequentes ou prolongados merecem avaliação médica para investigar causas hormonais."
                ),
                reviewerName = "Dra. Camila Nogueira",
                reviewerCredential = "Ginecologista, 12 anos de experiência clínica"
            ),
            Article(
                id = "faq_fluxo_cores",
                section = "Perguntas frequentes",
                title = "O que as cores do fluxo menstrual significam?",
                summary = "Do vermelho vivo ao marrom escuro — o que é normal.",
                body = listOf(
                    "Vermelho vivo costuma indicar fluxo mais rápido, comum no início da menstruação. Vermelho escuro ou marrom geralmente é sangue mais antigo, comum no fim do período.",
                    "Rosa claro pode aparecer quando o fluxo se mistura com o muco cervical, especialmente em fluxos mais leves.",
                    "Cores muito fora do comum, com odor forte ou acompanhadas de dor intensa, devem ser avaliadas por um profissional de saúde."
                ),
                reviewerName = "Dra. Camila Nogueira",
                reviewerCredential = "Ginecologista, 12 anos de experiência clínica"
            ),
            Article(
                id = "faq_anticoncepcional",
                section = "Perguntas frequentes",
                title = "Como o anticoncepcional muda seu ciclo?",
                summary = "Métodos hormonais alteram ou suprimem a ovulação.",
                body = listOf(
                    "A maioria dos anticoncepcionais hormonais impede a ovulação, tornando o 'ciclo' resultante um sangramento de privação hormonal, não uma menstruação verdadeira.",
                    "Por isso, os sintomas de TPM costumam ser mais leves e previsíveis em quem usa métodos hormonais combinados.",
                    "Ao parar de usar, pode levar algumas semanas ou meses até o ciclo natural se reestabelecer — isso varia de pessoa para pessoa."
                ),
                reviewerName = "Dr. Felipe Andrade",
                reviewerCredential = "Endocrinologista, especialista em saúde hormonal"
            )
        )
    )
)

fun findArticle(id: String): Article? =
    contentSections.flatMap { it.articles }.find { it.id == id }
