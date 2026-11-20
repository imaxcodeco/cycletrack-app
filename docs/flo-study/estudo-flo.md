# Estudo do App Flo — Design e Funcionalidades

> Estudo feito navegando no app Flo real (org.iggymedia.periodtracker) instalado no emulador LDPlayer, via adb + screenshots. PT-BR.

## 1. Bottom Navigation (5 itens)
1. **Hoje** — ícone calendário, aba inicial/selecionada (rosa)
2. **Conteúdo** — ícone grid/blocos
3. **Papo Privado** — ícone de máscara (chat anônimo/comunidade)
4. **Mensagens** — ícone de balão de chat, com badge vermelho de notificação não lida
5. **Flo a Dois** — ícone de duas pessoas (modo parceiro)

## 2. Tela "Hoje" (Home)
Fundo: gradiente rosa claro (#FFD9E4 → mais claro embaixo) com formas orgânicas/blob decorativas sutis atrás do conteúdo.

**Header (fixo no topo, sem AppBar tradicional):**
- Esquerda: avatar circular rosa com ícone de mascote (raposa estilizada) — abre perfil/configurações
- Centro: data atual por extenso, ex: "20 de novembro"
- Direita: ícone de calendário (outline) — atalho para visão de calendário completa

**Faixa de semana (date strip):**
- 7 dias (D S T Q Q S S), dia atual com círculo outline + label "HOJE" acima do número
- Dias de menstruação confirmados: círculo preenchido rosa sólido com número branco
- Dias previstos (futuro): círculo com borda pontilhada/tracejada rosa, número rosa
- Dias normais: sem círculo, número cinza escuro

**Bloco central de destaque (hero):**
- Texto pequeno centralizado: "A melhor oportunidade de engravidar é em"
- Número grande em destaque (bold, ~40sp): "6 dias"
- Linha secundária: "Menstruação: 3º dia" + ícone "i" de informação (abre explicação)
- (Esse bloco muda de conteúdo conforme a fase do ciclo e o objetivo escolhido no onboarding — aqui o objetivo era "engravidar")

**Row de ações rápidas (4 botões circulares com label embaixo):**
1. "Editar menstruação" — ícone lápis, círculo ROSA preenchido (destaque, é a ação principal)
2. "Sintomas" — ícone "+", círculo branco com borda
3. "Sexo" — ícone coração outline, círculo branco
4. "Teste" — ícone de teste de gravidez/ovulação, círculo branco

**Seção "Meu conteúdo diário · Hoje":**
- Título da seção com separador "·" entre o nome e "Hoje"
- Row horizontal scrollável de cards (carrossel), cada card ~160dp largura, cantos arredondados ~16dp, BORDA ROSA ao redor de cada card (chama atenção, estilo "destaque/novo"):
  - "Calculadora de fertilidade" (fundo lilás/roxo claro, ilustração de gráfico+coração)
  - "20 de novembro: sintomas esperados" (fundo roxo claro, ilustração de carinhas)
  - "Dia do ciclo" + número grande "3" (fundo roxo claro)
  - "Como turbinar a masturbação" (fundo bege, ilustração de pessoa)
  - "Dataровável do parto" + "25 de agosto" (fundo azul claro)
  - (carrossel continua, cortado na captura)

**Seção branca "Prepare-se para a gravidez":**
- Fundo branco (contraste com o rosa de cima), cantos arredondados no topo (como um "sheet")
- 3 itens em grid de ícone outline + label, lado a lado: "Sinais de gravidez", "Sexo para engravidar", "Nada de bebê ainda?"

**Seção "Com base no seu ciclo atual":**
- Cards de artigo em row horizontal: imagem grande no topo (16:9ish), título embaixo (2 linhas), linha de metadado cinza "X min" (tempo de leitura). Ex: "Como saber se a concepção ocorreu?" · 1 min; "Secreção branca: por que aparece um fluido vaginal com aspecto leitoso?" · 3 min; "Quantas vezes [...] engravidar" · 2 min.

**Card de Empty State / incentivo a dados (fundo branco, cantos arredondados, dentro de um card "flutuante"):**
- Ilustração central (mapa dobrado + lápis + 2 selos de check verde/rosa)
- Título bold centralizado: "Ainda não há dados para análise e comparação"
- Lista com ✓ (check rosa) + texto, números em **bold**:
  - "Registre **pelo menos 2** menstruações para obter informações personalizadas sobre seu último ciclo e a duração da sua menstruação"
  - "Registre **pelo menos 3** menstruações para obter informações sobre a regularidade do seu ciclo"
- Botão CTA full-width rosa vibrante, cantos bem arredondados (pill): "Registrar ciclos anteriores"

**Seção "Meus ciclos":**
- Título de seção fora do card
- Card branco com:
  - Row "Histórico" (esquerda) / "Ver tudo" + chevron ">" (direita, cinza)
  - Divisor
  - Row "Ciclo atual: 3 dias" (bold) / "Começou em 18 nov." (cinza, embaixo) + chevron ">" à direita; abaixo, 3 bolinhas rosa pequenas (••• — provavelmente indicador de progresso/dias com dados)
  - Divisor
  - Link centralizado rosa com ícone "+": "Registrar ciclos anteriores"

**Card promocional "Sono" (dark mode card, fundo roxo bem escuro #2E1A47ish, full width, cantos arredondados):**
- 2 chips/tabs no topo: "🌙 Sono" (ativo/branco) e "📊 Relatórios" (inativo)
- Ilustração central: mão segurando celular mostrando gráfico de barras + ícone lua com check verde, ao fundo um arco/círculo decorativo rosa com estrelas
- Badge cinza pill: "Grátis"
- Texto: "Veja como os padrões do sono e do ciclo podem estar conectados para aproveitar o melhor da vida."
- Botão branco full-width: "Ativar o monitoramento do sono"

(Esse card marca o fim do scroll da Home.)

## 3. Tela "Conteúdo"
**Header:** avatar (com badge notificação vermelha) à esquerda, título "Conteúdo" (grande, bold), à direita 2 ícones: bookmark/salvos e sino de notificações (com badge numérico "1"). Fundo branco (diferente da Home que é rosa).

**Row de atalhos (reaproveitada da Home):** "Sinais de gravidez", "Sexo para engravidar", "Nada de bebê ainda?" — ícones outline + label, sem fundo colorido aqui (fundo branco da tela).

**Seção "Em alta"** — carrossel horizontal de cards de artigo (imagem ilustrada ocupando ~80% do card, título na base com fundo da própria cor do card):
- "9 dicas inovadoras de masturbação" (fundo rosa claro)
- "Como lavar sua vagina" (fundo verde água)
- "Guia d..." (cortado, fundo roxo)

**Seção "Quando transar para engravidar?"** — mesmo padrão de carrossel:
- "Sinais físicos de fertilidade" (ilustração anatômica)
- "Quanto tempo dura a ovulação?" (ilustração de óvulo/trompa)
- "Sexo p..." (cortado) — este card tem um ícone de ▶ play no canto, indicando que é VÍDEO, não artigo

**Seção "Os vídeos mais pesquisados"** — cards com badge pill cinza translúcido no canto superior esquerdo indicando tipo: "▶ Video Course" ou "▶ Video". Ex: "Sexo para engravidar" (Video Course), "Como manter o esperma no colo do útero" (Video).

**Seção "Será que engravidei?"** — aqui os cards usam FOTOS REAIS (não ilustração), estilo lifestyle/editorial: "TPM x primeiros sinais de gravidez", "9 primeiros sinais da gravidez", "Sinais..." (cortado)

**Seção "Respostas para dúvidas comuns"** — volta a ilustração: "Nada de orgasmos? Veja motivos", "O que é uma gravidez química?"

**Seção "Novidades para você"** — mistura de ilustração e foto: casal, mulher com teste de gravidez, etc.

**Padrão geral da tela Conteúdo:** feed infinito de seções temáticas (tituladas com perguntas ou frases diretas, tom conversacional/empático), cada seção = 1 carrossel horizontal de 2.5 cards visíveis por vez (cards ~170dp largura, aspect ratio retrato ~4:5, cantos arredondados ~16dp, título na base do card em 1-2 linhas). Conteúdo é personalizado pelo objetivo do onboarding (aqui: focado 100% em "tentando engravidar" — sexo, fertilidade, sinais de gravidez).

## 3b. Tela de Detalhe de Artigo
- Header simples: seta voltar + breadcrumb "Conteúdo" (nome da tela anterior)
- Título do artigo grande, bold, 2 linhas: "Não consegue ter um orgasmo? Essa pode ser a causa"
- Credibilidade médica: avatar circular pequeno (foto real) + "Revisado por" (cinza, pequeno) + nome em bold com selo de verificado azul (✓) ao lado do nome: "**Jordan Rullo, PhD**, Mais de 10 anos de experiência em psicologia clínica, Utah, EUA"
- Imagem ilustrativa grande full-width (ilustração flat colorida, não foto)
- Corpo do artigo: parágrafos de texto normal, subtítulos em bold maiores (ex: "Anorgasmia"), cita especialistas com aspas ("Casey Tanner, terapeuta sexual, diz que...")
- Tom: educativo, empático, direto, linguagem acessível (não acadêmica)

## 4. Tela "Papo Privado" (comunidade anônima)
Header: avatar à esquerda, campo de busca "Buscar" (fundo cinza claro, ícone lupa), bookmark e sino à direita.
Corpo: feed de posts da comunidade (vazio/não carregou no teste — provavelmente requer conexão de rede real ou é lazy-loaded).
FAB: botão pill flutuante centralizado acima da bottom nav, rosa vibrante: "✏ Nova postagem" — permite postar anonimamente pra comunidade.
Ícone da aba: máscara/óculos estilizados (remete a anonimato).

## 5. Tela "Mensagens" (Assistente de Saúde conversacional)
**Lista de mensagens:** header "Mensagens" + ícone de info (ⓘ). Item de lista: avatar circular (ícone de balão de fala com gota), título bold "Ajuda na menstruação", preview cinza "Chegou aquela época do mês 🩸 Tenho alguns tópicos sobre menstruação que podem ser úteis para você.", à direita timestamp "Ontem" + bolinha vermelha (não lida). Swipe no item revela ação (parece arquivar/deletar, não explorado a fundo).

**Dentro da conversa (abre em tela cheia tipo chat):**
- Header: X (fechar) + avatar do app (ícone Flo rosa) + nome "Flo"
- Mensagens do "bot" em balões cinza claro alinhados à esquerda, sequenciais (3 balões empilhados, cada frase curta vira um balão separado):
  - "🤷 Olá, aqui é sua Assistente de Saúde!"
  - "Um novo ciclo começou."
  - "Como você está se sentindo hoje?"
- Abaixo, um painel de resposta rápida (sheet) com RADIO BUTTONS (seleção única):
  - "Estou bem"
  - "Não sei, tem muita coisa acontecendo"
  - "Estou com raiva"
  - "Estou triste"
  - "Prefiro não responder"
- Botão "Enviar" full-width, pill, fica ROSA CLARO/desabilitado até selecionar uma opção (depois vira rosa vibrante)
- Esse é um check-in conversacional gamificado — o app "conversa" com a usuária e personaliza conteúdo/dicas com base na resposta de humor.

## 6. Tela "Flo a Dois" (Modo Parceiro — landing page, provavelmente WebView)
**Hero (fundo verde-água/teal #4FBDB5ish):**
- Título grande 2 linhas: "Flo a Dois"
- Subtítulo: "Compartilhe a jornada até engravidar"
- Ilustração: mulher segurando teste de gravidez, parceiro atrás
- Checklist (✓ preto): "Mostre dados do ciclo ao seu parceiro" (como intro) depois:
  - "Vocês dois vão receber orientações sobre o melhor momento para transar."
  - "Ele vai contar com nossa ajuda para se preparar e manter o sexo divertido."
  - "Ele vai receber informações sobre fertilidade e como pode ajudar."

**Seção "O que seu parceiro pode ver" (fundo cinza claro):**
- Título grande centralizado
- Mockup de celular (frame iOS-style) mostrando a visão do parceiro: "Recomendados • Hoje" com cards "Dia do ciclo" "10", "Alguns possíveis sintomas" (emojis), "Chance de engravidar" "Grande", e um card teal "Menstruação em 1 dia" / "Hoje pode ser o dia da ovulação"

**Rodapé fixo:** botão full-width "Vincular conta" (rosa vibrante)

Obs: conteúdo parece ser servido via WebView (teve um "Carregando a página..." antes de renderizar), e o objetivo é vender/converter pro recurso de compartilhamento com parceiro, personalizado para o objetivo "engravidar" do perfil atual.

## 7. Tela "Configurações" (acessada pelo avatar no header)
Header: X (fechar) + título "Configurações"

**Banner vermelho (sem conta logada):** ícone "!" branco + "Registre-se para salvar seus dados, ou faça o login para acessar sua conta." + botão branco "Continuar"

**Card "Dê o Flo Premium de presente":** row de avatares coloridos tipo mascote (roxo, laranja, rosa, azul, roxo — cada um um "bichinho" diferente), título bold "Dê o Flo Premium de presente", subtítulo "Convide amigos e a família para se juntarem ao seu plano", botão rosa "Experimente o plano Flo Família"

**Card de perfil (dark/escuro, full width, cantos arredondados, textura sutil de folha ao fundo):**
- Avatar circular rosa com mascote + badge lápis de editar
- Título branco bold: "Nenhuma conta"
- Subtítulo cinza: "Editar informações"
- Badge "Flo Premium" (pill rosa) no canto inferior direito do card

**Seletor de objetivo (GRID 2x2 — permite TROCAR o objetivo escolhido no onboarding a qualquer momento!):**
- 4 cards quadrados com ícone circular + label: "Monitorar ciclo", "Engravidar", "Acompanhar gestação", "Acompanhar perimenopausa"
- Card selecionado tem BORDA ROSA + ✓ check vermelho/rosa no canto superior direito (aqui "Engravidar" estava selecionado, batendo com o onboarding)
- Nota: isso é uma 4ª opção de objetivo que não vi no onboarding original — "Acompanhar perimenopausa" — vale considerar incluir no CycleTrack também

**Seção "Sua experiência com o Flo":**
- "🎁 Indique alguém" (ícone presente) — card próprio separado
- Grupo de lista (um card só, itens com divisor fino entre eles), todos com ícone outline à esquerda + label + chevron ">" à direita:
  - ⚙ "Configurações do app"
  - 🚫👁 "Ocultar conteúdo"
  - 📈 "Gráficos e relatórios"
  - 🔄 "Ciclo e ovulação"
  - 🔔 "Lembretes"
  - 🛡 "Configurações de privacidade"
  - ❓ "Ajuda"

**Card de confiança (fundo branco, selo de escudo verde-água):**
- Ícone de escudo com check
- Título bold: "Seus dados estão protegidos"
- Texto: "Sua privacidade é nossa maior prioridade. Nunca venderemos seus dados, e você pode deletá-los quando quiser."
- Botão "Saiba mais" (pill, fundo cinza claro, outline sutil)

**Rodapé (texto cinza centralizado, fora de qualquer card):**
- "Flo - Monitor de menstruação e ovulação"
- "Versão 10.10.0"
- "© 2026 Flo Health, Inc."

## 7b. Sub-tela "Configurações do app"
Lista simples: "Saúde Connect" (integração tipo Health Connect), "Alterar idioma", toggle "Sistema métrico" (ligado = rosa).

## 7c. Sub-tela "Ciclo e ovulação"
- "Duração do ciclo" → campo "Selecionar" com dropdown (chevron)
- "Duração da menstruação" → campo "Selecionar" com dropdown
- Texto explicativo cinza: "O app faz previsões com base nas configurações da duração do ciclo e da menstruação. No entanto, se você registrar sua menstruação regularmente no app, as previsões serão baseadas nos dados registrados."
- Toggle "Probabilidade de engravidar" (verde quando ligado) + explicação: "Se você desativar este parâmetro, apenas o dia da ovulação será exibido."
- "Fase lútea" → "Selecionar" dropdown + explicação: "A fase lútea é o período entre a sua ovulação e o início da sua menstruação. Se você souber a duração da sua fase lútea, registre-a para que as previsões fiquem mais precisas."
- Toggle "Exibir sequência do ciclo" (desligado por padrão) + explicação: "A sequência do ciclo (1º, 2º, 3º dia etc.) será exibida logo acima das datas no calendário."
- **PRÉVIA VISUAL ao vivo** embaixo: fileira de círculos numerados simulando o calendário — dias de período em rosa preenchido (3,4,5), dia com contorno rosa (6, provável "hoje"/fértil), dias normais sem círculo (7,9), dia de ovulação com contorno VERDE-ÁGUA (8). Ótima forma de a usuária entender visualmente o efeito das configurações antes de salvar.

## 7d. Sub-tela "Lembretes"
Organizada em seções com header pequeno uppercase colorido (teal/verde-água):
- **"Papo Privado"**: toggle "Notificações do Papo Privado" — "Sim" (verde)
- **"Conteúdo"**: "Conselhos pessoais" — "Sim"
- Banner de aviso (fundo amarelo claro, ícone ⚠ laranja): "Atualize suas configurações para receber lembretes programados do Flo." + chevron (clicável, leva a preencher dados faltantes)
- **"Ciclo"** (itens esmaecidos/cinza pois dependem de configuração pendente): "Menstruação em alguns dias" 10:00, "Ovulação" 20:00, "Fim da menstruação" Não, "Início da menstruação" 10:00
- **"Medicamentos e contraceptivos"**: "Contraceptivos" Não
- Link "ADICIONAR LEMBRETE" (uppercase, estilo ação/link)
- **"Estilo de vida"**: "Registrar peso" Não (e mais itens, cortado)

Padrão: cada lembrete mostra o nome + valor atual (Sim/Não/horário) embaixo, tudo clicável pra configurar horário/ativar.

## 8. Tela "Calendário" (acessada pelo ícone de calendário no header da Home)
**Header:** X (fechar) à esquerda, segmented control central "Mês" / "Ano" (pill, fundo branco no selecionado "Mês", cinza claro no resto), ícone de engrenagem (configurações) à direita.

**Grade de dias da semana:** D S T Q Q S S (abreviação de 1 letra).

**Scroll vertical CONTÍNUO entre meses** (não paginado com setas como o nosso!) — nome do mês em minúsculas, bold, centralizado como separador de seção: "novembro", "dezembro", depois quando muda o ano mostra "01/2027" (mês/ano quando cruza virada de ano).

**Codificação visual dos dias (muito rica):**
- Dia com menstruação CONFIRMADA (já passou/registrada): círculo preenchido ROSA sólido, número branco
- HOJE: mesmo círculo rosa + anel cinza claro ao redor + label pequeno "HOJE" acima do número
- Dia de menstruação PREVISTA (futuro, com base no ciclo): círculo com borda PONTILHADA rosa (vazado), número rosa
- Dias de janela fértil PREVISTA: número em cor TEAL/VERDE-ÁGUA, SEM círculo
- Dia de ovulação PREVISTA: círculo com borda PONTILHADA teal/verde-água (vazado), número teal
- Dias comuns (sem evento): número preto, sem decoração

**Botão flutuante inferior (sticky):** pill rosa "Editar menstruação" — permanece fixo ao centro-baixo enquanto rola o calendário, para edição rápida de qualquer período.

**Ao tocar em qualquer dia (passado ou futuro):** abre um BOTTOM SHEET (painel deslizante de baixo) com:
- Header do sheet: "24/11 · 7° dia do ciclo" (data + dia do ciclo correspondente) + botão X circular cinza para fechar
- Seção "Meu conteúdo diário": mesmo padrão de carrossel de cards da Home, mas CONTEXTUALIZADO para aquele dia específico (ex: "Secreção" → "Não fértil" nesse dia, "Dia do ciclo" → "7", "Data provável do parto" → "25 de agosto", "Calculadora de fertilidade", "sintomas esperados" daquele dia)
- O dia tocado fica destacado no calendário por trás com um círculo cinza sólido enquanto o sheet está aberto

## 9. Tela "Registro" (log de sintomas — acessada pelo botão "Sintomas" na Home)
**Header:** X (fechar) + navegação de dia: seta "<" / título "Hoje" com subtítulo "3º dia do ciclo" / seta ">" (navega dia a dia, para trás e para frente) + campo "Buscar" (busca entre todos os itens registráveis)

**Card "Como você está se sentindo hoje?"** (atalhos rápidos/sugeridos, personalizados pelo momento do ciclo): 4 ícones circulares coloridos + label: "Cólicas", "Fadiga", "Inchaço", "Calma"

**Seção "Categorias"** + link "Editar" à direita (em rosa — permite customizar quais categorias aparecem)

Cada categoria é um CARD branco com: título (bold), subtítulo cinza explicativo, e CHIPS/PILLS horizontais com wrap (quebram linha), cada chip = ícone pequeno circular colorido + label, fundo do chip com a cor "container" pastel do tema da categoria:

- **"Fluxo menstrual"** (chips rosa) — "Estime seu fluxo médio diário": Leve, Médio, Intenso, Coágulos
- **"Testes de ovulação"** (chips teal/verde-água) — "Registre testes para saber seus dias férteis": Não fiz testes, Teste: positivo, Teste: negativo, Ovulação: meu método
- **"Sexo e libido"** (chips vermelho/rosa) — Não fiz sexo, Sexo com proteção, Sexo sem proteção, Sexo oral, Sexo anal, Masturbação, Toque sensual, Brinquedos sexuais, Orgasmo, Libido alta, Libido neutra, Libido baixa

- **"Humor"** (chips laranja, ícone de carinha expressiva): Calma, Feliz, Energética, Alegre, Mudanças de humor, Irritada, Triste, Ansiosa, Desanimada, Culpada, Pensamentos obsessivos, Pouca energia, Apática, Confusa, Muito autocrítica
- **"Sintomas"** (chips roxo): Está tudo bem, Cólicas, Seios sensíveis, Dor de cabeça, Acne, Dor nas costas, Fadiga, Desejos, Insônia, Dor abdominal, Coceira vaginal (continua — ver próxima captura)

- **"Secreção vaginal"** (chips roxo claro): Sem secreção, Pastosa, Aquosa, Viscosa, Clara de ovo, Sangramento de escape, Corrimento incomum, Branca grumosa, Cinza
- **"Digestão e fezes"** (chips rosa): Náusea, Inchaço, Prisão de ventre, Diarreia
- **"Testes de gravidez"** (chips laranja): Não fiz testes, Positivo, Negativo, Linha fraca
- **"Outros"** (chips pêssego claro): Viagem, Estresse, Meditação, Registros em diário, Exercícios de Kegel, Exercícios de respiração, Doença ou ferimento, Álcool
- **"Atividade física"** (chips verde): Não me exercitei, Ioga, Academia, Aeróbica e dança, Natação, Esportes de equipe, Corrida, Ciclismo, Caminhada
- **"Comprimidos diversos"** / "Registre remédios tomados diariamente": botão pill azul claro "Adicionar comprimido" com ícone "+" circular azul; abaixo link "Criar lembretes" com chevron

**Widgets especiais de métrica (cards com estrutura diferente dos chips, no final da tela):**
- **"Água"** 💧 — botões "−" / "+" circulares à direita do título, contador grande "0 / 2,25 L" (progresso vs meta diária), link "Lembretes e configurações" embaixo
- **"Peso"** ⚖ — ícones lixeira + lápis (editar) à direita, placeholder cinza "Registre seu peso", link "Confira o gráfico"
- **"Temperatura basal"** 🌡 — mesmo padrão (lixeira+lápis), "Registre sua temperatura", "Confira o gráfico"
- **"Notas"** 📝 — mesmo padrão, "Anote outros sintomas ou como você está se sentindo."

**Padrão geral da tela de Registro:** é uma única tela ENORME e scrollável com todas as categorias possíveis sempre visíveis (não precisa navegar entre sub-telas), organizadas em cards por categoria com chips de seleção rápida (tap para marcar/desmarcar, provavelmente multi-select dentro de cada categoria). Chips já marcados provavelmente ficam com fundo mais saturado (não testado neste estudo). Isso contrasta com nosso LoggingScreen atual que é mais compacto/scoped.

## 10. Nota — Assinatura/Premium
A conta de teste usada no LDPlayer já possui Flo Premium ativo (assinatura real vinculada a uma conta Google), por isso nenhum paywall apareceu durante a navegação — "Gerenciar assinatura" abre a tela NATIVA do Google Play (fora do app Flo). Não documentamos o paywall em si neste estudo. Recomendação: se formos implementar monetização, usar Google Play Billing com uma tela de paywall própria (in-app) antes de abrir o fluxo nativo do Play, como a maioria dos apps faz.

## 0. Splash
Fundo rosa vibrante sólido (~#FF5C7A), logo: ícone de pena/folha estilizada em branco + wordmark "Flo" em fonte script/cursiva branca, centralizados verticalmente um pouco acima do meio da tela.

---

## 11. Gap Analysis — Flo vs CycleTrack atual

| Área | Flo | CycleTrack hoje | Gap |
|---|---|---|---|
| **Navegação principal** | 5 abas: Hoje, Conteúdo, Papo Privado, Mensagens, Flo a Dois | 5 abas: Home, Calendário, Registrar, Insights, Perfil | Estrutura equivalente, mas conteúdo de cada aba é muito mais raso no CycleTrack |
| **Home** | Hero dinâmico (previsão central), 4 ações rápidas, carrossel de conteúdo diário, empty-state de dados, card de ciclo atual, promo de features (sono) | Card de ciclo + 2 tiles + 1 card estático de previsão + card de "dias registrados" | Falta: ações rápidas (editar período/sintomas/sexo/teste), carrossel de conteúdo personalizado, empty states, navegação por data strip semanal |
| **Calendário** | Scroll contínuo multi-mês, 5 estados visuais de dia (confirmado/previsto/fértil/ovulação/normal), bottom sheet de detalhe ao tocar num dia | Mês único com setas ⬅➡, 4 estados visuais, sem detalhe ao tocar no dia | Falta: scroll contínuo, bottom sheet de detalhe por dia, toggle Mês/Ano |
| **Registro de sintomas** | 1 tela gigante com 10 categorias (Fluxo, Ovulação, Sexo, Humor, Sintomas, Secreção, Digestão, Testes gravidez, Outros, Atividade física) + widgets de Água/Peso/Temperatura/Notas, navegação dia-a-dia com setas | Tela com fluxo, humor, sintomas básicos (menor escopo) | Falta MUITAS categorias e chips; falta navegação entre dias; falta widgets de água/peso/temperatura basal |
| **Insights** | "Gráficos e relatórios" dedicado nas configs + cards de histórico de ciclo na Home | Tela de Insights simples com médias | Falta gráficos visuais de tendência, histórico de ciclos detalhado |
| **Conteúdo educativo** | Feed completo tipo blog/vídeo, personalizado por objetivo, com créditos médicos | Não existe | Gap total — feature inteira ausente |
| **Chat/Assistente** | "Mensagens" com assistente de saúde conversacional (check-in de humor) | Não existe | Gap total |
| **Comunidade** | "Papo Privado" anônimo | Não existe | Gap total (baixa prioridade, requer backend/moderação) |
| **Modo Parceiro** | "Flo a Dois" com landing + vinculação de conta | Não existe (mencionado no CLAUDE.md como feature flag futura) | Gap total |
| **Configurações** | Objetivo editável pós-onboarding, Ciclo e ovulação (com prévia visual), Lembretes detalhados por categoria, Health Connect, idioma, privacidade | ProfileScreen com toggles simples (lembretes, Health Connect, ciclo) | Falta: edição de objetivo pós-onboarding, prévia visual do calendário, lembretes granulares por categoria |
| **Onboarding** | Ramificado, ilustrado, 20+ telas por trilha | Ramificado, ilustrado, ~15 telas por trilha (JÁ IMPLEMENTADO recentemente) | Próximo do nível do Flo, gap pequeno |

## 12. Plano de Implementação Proposto

### Fase 1 — Home mais rica (baixa/média complexidade)
- Adicionar row de 4 ações rápidas no topo do `HomeScreen.kt` (Editar menstruação, Sintomas, Sexo, Teste) — reaproveitar padrão de botão circular já usado no onboarding
- Adicionar data strip semanal (7 dias com indicadores de período/previsão) acima do hero — reaproveitar lógica de `buildCycleDays()` já existente em `CalendarScreen.kt`
- Empty state "Ainda não há dados" quando `cycleCount < 2` — card com ilustração + checklist + CTA
- **Arquivos**: `HomeScreen.kt`, novo `HomeComponents.kt`
- **Complexidade**: baixa-média

### Fase 2 — Registro de sintomas expandido (média complexidade)
- Expandir `LoggingScreen.kt` para seguir o padrão "1 card por categoria com chips horizontais com wrap"
- Adicionar categorias faltantes: Testes de ovulação, Secreção vaginal, Digestão e fezes, Testes de gravidez, Outros, Atividade física
- Adicionar navegação dia-a-dia (setas ⬅➡ + label "Hoje"/"3º dia do ciclo") no topo
- Adicionar widgets "Água" (contador +/-, meta diária), reaproveitar "Peso"/"Temperatura basal"/"Notas" que já existem parcialmente
- Expandir o domain model `Symptom`/categoria para suportar os novos tipos
- **Arquivos**: `LoggingScreen.kt`, `domain/model/Symptom.kt`, novo `LoggingCategoryCard.kt`
- **Complexidade**: média

### Fase 3 — Calendário avançado (média complexidade)
- Migrar `CalendarScreen.kt` de "mês único + setas" para scroll vertical contínuo multi-mês (`LazyColumn` com meses como seções)
- Adicionar bottom sheet de detalhe ao tocar num dia (conteúdo contextual: dia do ciclo, fase, fertilidade daquele dia)
- Toggle "Mês" / "Ano" no topo (visão anual é complexidade extra, pode ficar pra depois)
- **Arquivos**: `CalendarScreen.kt`, novo `CalendarDayDetailSheet.kt`
- **Complexidade**: média

### Fase 4 — Configurações avançadas (baixa-média complexidade)
- Adicionar seletor de objetivo (grid 2x2, reaproveita `MultiChoiceGridScreen` do onboarding) dentro do `ProfileScreen.kt`, permitindo trocar depois do onboarding
- Expandir sub-tela "Ciclo e ovulação" com prévia visual ao vivo (reaproveita padrão de círculos do `PeriodCalendarScreen`)
- Expandir "Lembretes" com categorias e horários (precisa de `WorkManager` para agendar notificações locais)
- **Arquivos**: `ProfileScreen.kt`, novos `CycleSettingsScreen.kt`, `RemindersScreen.kt`
- **Complexidade**: baixa (UI) a média (agendamento real de notificações)

### Fase 5 — Conteúdo educativo (alta complexidade)
- Nova aba "Conteúdo" com feed de cards por seção temática
- Requer fonte de conteúdo: ou (a) conteúdo estático embutido no app (JSON/Room, mais rápido de implementar, sem custo de backend) ou (b) CMS remoto (mais trabalho, permite atualizar sem nova versão do app)
- Tela de detalhe de artigo (texto + imagem + crédito do revisor)
- **Arquivos**: nova pasta `presentation/ui/content/`, `domain/model/Article.kt`, `data/repository/ContentRepositoryImpl.kt` (já existe como stub!)
- **Complexidade**: alta (especialmente se envolver CMS/backend); baixa-média se for conteúdo estático embutido

### Fase 6 — Assistente conversacional (alta complexidade)
- Nova aba ou feature "Mensagens" com check-ins periódicos (humor, lembrete de novo ciclo)
- Pode começar simples: regras fixas (if novo ciclo → mostrar mensagem X) sem IA real, estilo "notificação rica" dentro do app
- **Arquivos**: novo `presentation/ui/messages/`, `domain/usecase/GenerateCheckInUseCase.kt`
- **Complexidade**: média (versão com regras fixas) a alta (versão com IA/personalização real)

### Fase 7 — Modo Parceiro "Flo a Dois" (alta complexidade)
- Já mencionado como feature flag (`feature.partnerMode.enabled`) no CLAUDE.md, mas não implementado
- Precisa de: tela de convite/vinculação, sincronização de dados entre 2 contas (requer backend — Firebase Firestore já está nas dependências do projeto), visão read-only do parceiro
- **Complexidade**: alta (depende de backend real)

### Fase 8 — Comunidade "Papo Privado" (baixa prioridade)
- Mais custosa em termos de moderação/segurança de conteúdo do que o valor imediato pro produto
- Recomendação: deixar por último, ou nem implementar numa v1

### Observação geral
Em cada fase, seguir os padrões já estabelecidos nesta sessão: cores `#E75480` (rosa), `#F5F5F5` (cinza card), `#FFD6E8` (destaque), `RoundedCornerShape(28.dp)` em botões e `12-16.dp` em cards, e sempre testar de ponta a ponta via adb antes de considerar uma fase concluída (lição aprendida com os bugs de Hilt/Realm desta sessão).

