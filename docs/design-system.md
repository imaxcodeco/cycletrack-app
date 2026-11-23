# CycleTrack Design System - Figma → Compose Mapping

**Versão:** 1.0  
**Data:** 17 de novembro de 2026  
**Status:** Em desenvolvimento

---

## 1. Visão Geral

Este documento define o sistema de design do CycleTrack, mapeando tokens do Figma para implementação em Jetpack Compose Material3. O objetivo é consistência visual, acessibilidade (WCAG 2.1 AA) e manutenibilidade.

### 1.1 Princípios de Design
- **Calmo e Confiável**: Cores suaves, tipografia legível, animações sutis
- **Inclusivo**: Suporte a daltonismo, alto contraste, escalas de fonte
- **Funcional**: Hierarquia clara, feedback imediato, estados bem definidos
- **Brasileiro**: Terminologia PT-BR, formatos de data/hora/moeda locais

---

## 2. Cores (Color Tokens)

### 2.1 Paleta Principal

| Token Figma | Compose Token | Light Value | Dark Value | Uso |
|-------------|---------------|-------------|------------|-----|
| `primary/50` | `primaryContainer` | `#FCE4EC` | `#4A1A2E` | Container primário |
| `primary/100` | - | `#F8BBD0` | `#3D1526` | Hover/pressed |
| `primary/200` | - | `#F48FB1` | `#33101E` | - |
| `primary/300` | - | `#F06292` | `#E8A8B8` | - |
| `primary/400` | - | `#EC407A` | `#D48AA0` | - |
| **`primary/500`** | **`primary`** | **`#E91E63`** | **`#F06292`** | **Cor principal da marca** |
| `primary/600` | - | `#D81B60` | `#EC407A` | - |
| `primary/700` | `primaryFixed` | `#C2185B` | `#D81B60` | Texto em container |
| `primary/800` | - | `#AD1457` | `#C2185B` | - |
| `primary/900` | `onPrimaryContainer` | `#880E4F` | `#FCE4EC` | Texto sobre primary |
| `primary/950` | - | `#560027` | `#F8BBD0` | - |

### 2.2 Paleta Secundária (Teal - Saúde/Calma)

| Token Figma | Compose Token | Light Value | Dark Value | Uso |
|-------------|---------------|-------------|------------|-----|
| `secondary/50` | `secondaryContainer` | `#E0F2F1` | `#003D39` | Container secundário |
| `secondary/100` | - | `#B2DFDB` | `#003330` | - |
| `secondary/200` | - | `#80CBC4` | `#002926` | - |
| `secondary/300` | - | `#4DB6AC` | `#6FC7C0` | - |
| `secondary/400` | - | `#26A69A` | `#5AB8B0` | - |
| **`secondary/500`** | **`secondary`** | **`#009688`** | **`#4DB6AC`** | **Ações secundárias** |
| `secondary/600` | - | `#00897B` | `#26A69A` | - |
| `secondary/700` | `secondaryFixed` | `#00796B` | `#00897B` | Texto em container |
| `secondary/800` | - | `#00695C` | `#00796B` | - |
| `secondary/900` | `onSecondaryContainer` | `#004D40` | `#E0F2F1` | Texto sobre secondary |

### 2.3 Paleta Terciária (Lavanda - Gravidez/Especial)

| Token Figma | Compose Token | Light Value | Dark Value | Uso |
|-------------|---------------|-------------|------------|-----|
| `tertiary/50` | `tertiaryContainer` | `#F3E5F5` | `#3B1A3F` | Container terciário |
| `tertiary/100` | - | `#E1BEE7` | `#311535` | - |
| **`tertiary/500`** | **`tertiary`** | **`#9C27B0`** | **`#CE93D8`** | **Gravidez, modo parceiro** |
| `tertiary/900` | `onTertiaryContainer` | `#4A148C` | `#F3E5F5` | Texto sobre tertiary |

### 2.4 Cores Semânticas (Status)

| Token Figma | Compose Token | Light Value | Dark Value | Uso |
|-------------|---------------|-------------|------------|-----|
| `success/500` | `success` | `#2E7D32` | `#66BB6A` | Sucesso, confirmação |
| `success/100` | `successContainer` | `#C8E6C9` | `#1B5E20` | Container sucesso |
| `warning/500` | `warning` | `#F57F17` | `#FFB300` | Aviso, atenção |
| `warning/100` | `warningContainer` | `#FFF9C4` | `#F57F17` | Container aviso |
| `error/500` | `error` | `#C62828` | `#EF5350` | Erro, destrutivo |
| `error/100` | `errorContainer` | `#FFCDD2` | `#B71C1C` | Container erro |
| `info/500` | `info` | `#0277BD` | `#64B5F6` | Informação neutra |
| `info/100` | `infoContainer` | `#B3E5FC` | `#01579B` | Container info |

### 2.5 Cores de Fase do Ciclo

| Fase | Token | Light | Dark | Uso |
|------|-------|-------|------|-----|
| Menstrual | `phaseMenstrual` | `#E91E63` | `#F06292` | Período, sangramento |
| Folicular | `phaseFollicular` | `#009688` | `#4DB6AC` | Pós-menstruação |
| Ovulatório | `phaseOvulatory` | `#FF9800` | `#FFB74D` | Janela fértil, ovulação |
| Lútea | `phaseLuteal` | `#9C27B0` | `#CE93D8` | Pré-menstruação, TPM |

### 2.6 Cores de Sintoma (Severidade)

| Severidade | Token | Light | Dark |
|------------|-------|-------|------|
| Leve | `severityMild` | `#4CAF50` | `#81C784` |
| Moderado | `severityModerate` | `#FF9800` | `#FFB74D` |
| Intenso | `severitySevere` | `#F44336` | `#EF5350` |

### 2.7 Neutros e Superfícies

| Token Figma | Compose Token | Light Value | Dark Value |
|-------------|---------------|-------------|------------|
| `neutral/0` | `surface` | `#FFFFFF` | `#1C1B1F` |
| `neutral/50` | `surfaceVariant` | `#FAFAFA` | `#2E2D32` |
| `neutral/100` | `surfaceContainerLow` | `#F5F5F5` | `#3D3C41` |
| `neutral/200` | `surfaceContainer` | `#EEEEEE` | `#4E4D53` |
| `neutral/300` | `surfaceContainerHigh` | `#E0E0E0` | `#605F65` |
| `neutral/400` | `surfaceContainerHighest` | `#BDBDBD` | `#75747B` |
| `neutral/500` | `outline` | `#9E9E9E` | `#93929A` |
| `neutral/600` | `outlineVariant` | `#757575` | `#CAC4D0` |
| `neutral/700` | `onSurfaceVariant` | `#616161` | `#D6D3DC` |
| `neutral/800` | `onSurface` | `#424242` | `#E6E1E5` |
| `neutral/900` | `onSurface` | `#212121` | `#F4EFF4` |
| `neutral/950` | - | `#121212` | `#FFFFFF` |

### 2.8 Implementação Compose (Color.kt)

```kotlin
// Theme/Color.kt
val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE91E63),
    primaryContainer = Color(0xFFFCE4EC),
    onPrimary = Color.White,
    onPrimaryContainer = Color(0xFF880E4F),

    secondary = Color(0xFF009688),
    secondaryContainer = Color(0xFFE0F2F1),
    onSecondary = Color.White,
    onSecondaryContainer = Color(0xFF004D40),

    tertiary = Color(0xFF9C27B0),
    tertiaryContainer = Color(0xFFF3E5F5),
    onTertiary = Color.White,
    onTertiaryContainer = Color(0xFF4A148C),

    error = Color(0xFFC62828),
    errorContainer = Color(0xFFFFCDD2),
    onError = Color.White,
    onErrorContainer = Color(0xFFB71C1C),

    surface = Color.White,
    surfaceVariant = Color(0xFFFAFAFA),
    surfaceContainer = Color(0xFFEEEEEE),
    surfaceContainerHigh = Color(0xFFE0E0E0),
    surfaceContainerHighest = Color(0xFFBDBDBD),

    onSurface = Color(0xFF212121),
    onSurfaceVariant = Color(0xFF616161),

    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFF757575),

    inverseSurface = Color(0xFF1C1B1F),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFF06292),

    // Custom cycle phase colors
    phaseMenstrual = Color(0xFFE91E63),
    phaseFollicular = Color(0xFF009688),
    phaseOvulatory = Color(0xFFFF9800),
    phaseLuteal = Color(0xFF9C27B0),

    // Symptom severity
    severityMild = Color(0xFF4CAF50),
    severityModerate = Color(0xFFFF9800),
    severitySevere = Color(0xFFF44336),
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF06292),
    primaryContainer = Color(0xFF4A1A2E),
    onPrimary = Color(0xFF560027),
    onPrimaryContainer = Color(0xFFFCE4EC),

    secondary = Color(0xFF4DB6AC),
    secondaryContainer = Color(0xFF003D39),
    onSecondary = Color(0xFF001F1D),
    onSecondaryContainer = Color(0xFFE0F2F1),

    tertiary = Color(0xFFCE93D8),
    tertiaryContainer = Color(0xFF3B1A3F),
    onTertiary = Color(0xFF230027),
    onTertiaryContainer = Color(0xFFF3E5F5),

    error = Color(0xFFEF5350),
    errorContainer = Color(0xFFB71C1C),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFCDD2),

    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF2E2D32),
    surfaceContainer = Color(0xFF3D3C41),
    surfaceContainerHigh = Color(0xFF4E4D53),
    surfaceContainerHighest = Color(0xFF605F65),

    onSurface = Color(0xFFF4EFF4),
    onSurfaceVariant = Color(0xFFD6D3DC),

    outline = Color(0xFF93929A),
    outlineVariant = Color(0xFF49484E),

    inverseSurface = Color(0xFFFAFAFA),
    inverseOnSurface = Color(0xFF1C1B1F),
    inversePrimary = Color(0xFFE91E63),

    phaseMenstrual = Color(0xFFF06292),
    phaseFollicular = Color(0xFF4DB6AC),
    phaseOvulatory = Color(0xFFFFB74D),
    phaseLuteal = Color(0xFFCE93D8),

    severityMild = Color(0xFF81C784),
    severityModerate = Color(0xFFFFB74D),
    severitySevere = Color(0xFFEF5350),
)
```

---

## 3. Tipografia (Type Tokens)

### 3.1 Escala Tipográfica

| Token Figma | Compose Style | Fonte | Tamanho | Peso | Line Height | Letter Spacing |
|-------------|---------------|-------|---------|------|-------------|----------------|
| `displayLarge` | `displayLarge` | Inter | 57sp | 400 | 64sp | -0.25sp |
| `displayMedium` | `displayMedium` | Inter | 45sp | 400 | 52sp | 0sp |
| `displaySmall` | `displaySmall` | Inter | 36sp | 400 | 44sp | 0sp |
| `headlineLarge` | `headlineLarge` | Inter | 32sp | 600 | 40sp | 0sp |
| `headlineMedium` | `headlineMedium` | Inter | 28sp | 600 | 36sp | 0sp |
| `headlineSmall` | `headlineSmall` | Inter | 24sp | 600 | 32sp | 0sp |
| `titleLarge` | `titleLarge` | Inter | 22sp | 500 | 28sp | 0sp |
| `titleMedium` | `titleMedium` | Inter | 16sp | 500 | 24sp | 0.15sp |
| `titleSmall` | `titleSmall` | Inter | 14sp | 500 | 20sp | 0.1sp |
| `bodyLarge` | `bodyLarge` | Inter | 16sp | 400 | 24sp | 0.5sp |
| `bodyMedium` | `bodyMedium` | Inter | 14sp | 400 | 20sp | 0.25sp |
| `bodySmall` | `bodySmall` | Inter | 12sp | 400 | 16sp | 0.4sp |
| `labelLarge` | `labelLarge` | Inter | 14sp | 500 | 20sp | 0.1sp |
| `labelMedium` | `labelMedium` | Inter | 12sp | 500 | 16sp | 0.5sp |
| `labelSmall` | `labelSmall` | Inter | 11sp | 500 | 16sp | 0.5sp |

### 3.2 Fonte Personalizada (Inter Variable)

```kotlin
// Theme/Type.kt
val interFontFamily = FontFamily(
    Font(R.font.inter_variable, FontWeight.Normal),
    Font(R.font.inter_variable, FontWeight.Medium),
    Font(R.font.inter_variable, FontWeight.SemiBold),
    Font(R.font.inter_variable, FontWeight.Bold),
)

val Typography = Typography(
    fontFamily = interFontFamily,
    displayLarge = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = -0.25.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    // ... demais estilos
)
```

### 3.3 Escalabilidade (Accessibility)

```kotlin
// Suporte a escalas de fonte do sistema (até 200%)
val fontScale = LocalConfiguration.current.fontScale
// Compose automaticamente escala sp baseado na configuração do usuário
```

---

## 4. Espaçamento (Spacing Tokens)

### 4.1 Escala Base (4dp)

| Token | Valor | Uso |
|-------|-------|-----|
| `space/0` | 0dp | - |
| `space/1` | 4dp | Padding mínimo, gap pequeno |
| `space/2` | 8dp | Padding padrão, gap médio |
| `space/3` | 12dp | Padding confortável |
| `space/4` | 16dp | Padding grande, margens de tela |
| `space/5` | 20dp | Seções |
| `space/6` | 24dp | Margens grandes, containers |
| `space/8` | 32dp | Hero sections |
| `space/10` | 40dp | Espaçamento extra grande |
| `space/12` | 48dp | Full bleed |

### 4.2 Implementação

```kotlin
// Theme/Spacing.kt
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}
```

---

## 5. Formas (Shape Tokens)

### 5.1 Border Radius

| Token Figma | Compose Token | Valor | Uso |
|-------------|---------------|-------|-----|
| `shape/none` | `Shape.None` | 0dp | Imagens full bleed |
| `shape/xs` | `CornerSize(4.dp)` | 4dp | Chips, badges, inputs |
| `shape/sm` | `CornerSize(8.dp)` | 8dp | Botões, cards pequenos |
| `shape/md` | `CornerSize(12.dp)` | 12dp | Cards padrão, dialogs |
| `shape/lg` | `CornerSize(16.dp)` | 16dp | Cards grandes, sheets |
| `shape/xl` | `CornerSize(24.dp)` | 24dp | Hero cards, onboarding |
| `shape/full` | `CornerSize(50%)` | 50% | Avatares, FABs, pills |
| `shape/pill` | `CornerSize(1000.dp)` | 9999dp | Pills, tags |

### 5.2 Implementação Material3

```kotlin
// Theme/Shape.kt
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Chips, badges
    small = RoundedCornerShape(8.dp),        // Buttons
    medium = RoundedCornerShape(12.dp),      // Cards
    large = RoundedCornerShape(16.dp),       // Large cards
    extraLarge = RoundedCornerShape(24.dp),  // Hero
)
```

---

## 6. Elevação e Sombras (Elevation Tokens)

### 6.1 Níveis de Elevação

| Nível | Token | Sombra (Light) | Sombra (Dark) | Uso |
|-------|-------|----------------|---------------|-----|
| 0 | `Level0` | None | None | Superfície base |
| 1 | `Level1` | `0 1px 2px rgba(0,0,0,0.05)` | `0 1px 2px rgba(0,0,0,0.3)` | Cards flat |
| 2 | `Level2` | `0 2px 4px rgba(0,0,0,0.08)` | `0 2px 4px rgba(0,0,0,0.4)` | Cards elevated |
| 3 | `Level3` | `0 4px 8px rgba(0,0,0,0.1)` | `0 4px 8px rgba(0,0,0,0.5)` | Dialogs, bottom sheets |
| 4 | `Level4` | `0 8px 16px rgba(0,0,0,0.12)` | `0 8px 16px rgba(0,0,0,0.6)` | Modals, menus |
| 5 | `Level5` | `0 16px 24px rgba(0,0,0,0.15)` | `0 16px 24px rgba(0,0,0,0.7)` | Toasts, snackbar |

---

## 7. Componentes (Component Tokens)

### 7.1 Botões

| Variante | Container Color | Content Color | Border | Elevação | Estado Hover | Estado Pressed | Estado Disabled |
|----------|-----------------|---------------|--------|----------|--------------|----------------|-----------------|
| **Primary** | `primary` | `onPrimary` | None | Level1 | `primaryContainer` | `primary/700` | `surfaceContainerHigh` / `onSurface` 38% |
| **Secondary** | `secondaryContainer` | `onSecondaryContainer` | None | Level1 | `secondaryContainer` hover | `secondary` pressed | `surfaceContainerHigh` / `onSurface` 38% |
| **Tertiary** | `tertiaryContainer` | `onTertiaryContainer` | None | Level0 | `tertiaryContainer` hover | `tertiary` pressed | `surfaceContainerHigh` / `onSurface` 38% |
| **Outlined** | `transparent` | `primary` | `outline` 1dp | Level0 | `primaryContainer` 10% | `primaryContainer` 20% | `outline` 38% / `onSurface` 38% |
| **Text** | `transparent` | `primary` | None | Level0 | `primaryContainer` 10% | `primaryContainer` 20% | `onSurface` 38% |

### 7.2 Cards

| Variante | Container | Border | Elevação | Padding |
|----------|-----------|--------|----------|---------|
| **Elevated** | `surface` | None | Level1 | 16dp |
| **Filled** | `surfaceVariant` | None | Level0 | 16dp |
| **Outlined** | `surface` | `outlineVariant` 1dp | Level0 | 16dp |

### 7.3 Chips

| Tipo | Container | Content | Shape | Ícone |
|------|-----------|---------|-------|-------|
| **Filter** | `surfaceContainerHigh` | `onSurfaceVariant` | Pill | Leading (check) |
| **Input** | `surfaceContainerHigh` | `onSurfaceVariant` | Pill | Trailing (close) |
| **Choice** | `primaryContainer` / `surfaceContainerHigh` | `onPrimaryContainer` / `onSurfaceVariant` | Pill | Leading (radio) |
| **Action** | `primaryContainer` | `onPrimaryContainer` | Pill | Leading/Trailing |
| **Suggestion** | `surfaceContainerHigh` | `onSurfaceVariant` | Pill | Leading |

### 7.4 Text Fields

| Estado | Container | Border | Label | Placeholder | Helper Text |
|--------|-----------|--------|-------|-------------|-------------|
| **Enabled** | `surface` | `outline` | `onSurfaceVariant` | `onSurfaceVariant` 60% | `onSurfaceVariant` |
| **Focused** | `surface` | `primary` 2dp | `primary` | `onSurfaceVariant` 60% | `onSurfaceVariant` |
| **Error** | `surface` | `error` 2dp | `error` | `onSurfaceVariant` 60% | `error` |
| **Disabled** | `surfaceContainerHigh` | `outline` 38% | `onSurface` 38% | `onSurface` 38% | `onSurface` 38% |

---

## 8. Movimento (Motion Tokens)

### 8.1 Durações

| Token | Valor | Uso |
|-------|-------|-----|
| `duration/xs` | 50ms | Micro-interações (ripple) |
| `duration/sm` | 150ms | Transições simples (fade, scale) |
| `duration/md` | 250ms | Navegação, dialogs, sheets |
| `duration/lg` | 350ms | Transições complexas (shared element) |
| `duration/xl` | 500ms | Onboarding, splash |

### 8.2 Easings

| Token | Curva | Uso |
|-------|-------|-----|
| `easing/standard` | `cubic-bezier(0.4, 0, 0.2, 1)` | Padrão Material |
| `easing/accelerate` | `cubic-bezier(0.4, 0, 1, 1)` | Saída de tela |
| `easing/decelerate` | `cubic-bezier(0, 0, 0.2, 1)` | Entrada de tela |
| `easing/emphasized` | `cubic-bezier(0.2, 0, 0, 1)` | Transições hero |

### 8.3 Implementação Compose

```kotlin
// Theme/Motion.kt
object Motion {
    val durationXs = 50.milliseconds
    val durationSm = 150.milliseconds
    val durationMd = 250.milliseconds
    val durationLg = 350.milliseconds
    val durationXl = 500.milliseconds

    val easingStandard = Easing(0.4f, 0f, 0.2f, 1f)
    val easingAccelerate = Easing(0.4f, 0f, 1f, 1f)
    val easingDecelerate = Easing(0f, 0f, 0.2f, 1f)
    val easingEmphasized = Easing(0.2f, 0f, 0f, 1f)
}
```

---

## 9. Ícones (Icon Tokens)

### 9.1 Sistema de Ícones
- **Base**: Material Icons Outlined (24dp)
- **Preenchidos**: Material Icons Filled para estados ativos
- **Personalizados**: Ciclo, gravidez, parceiro (SVG 24x24)

### 9.2 Tamanhos
| Token | Valor | Uso |
|-------|-------|-----|
| `icon/xs` | 16dp | Inline, chips |
| `icon/sm` | 20dp | Botões com ícone, tabs |
| `icon/md` | 24dp | Padrão, app bar |
| `icon/lg` | 28dp | FAB, destaque |
| `icon/xl` | 32dp | Hero, empty states |

---

## 10. Breakpoints (Responsive)

| Token | Valor | Dispositivo | Colunas Grid |
|-------|-------|-------------|--------------|
| `breakpoint/sm` | < 600dp | Phone portrait | 4 |
| `breakpoint/md` | 600-839dp | Phone landscape / Tablet small | 8 |
| `breakpoint/lg` | 840-1199dp | Tablet | 12 |
| `breakpoint/xl` | ≥ 1200dp | Desktop / Tablet large | 12 |

### 10.1 Implementação WindowSizeClass

```kotlin
// Theme/WindowSize.kt
val windowSizeClass = calculateWindowSizeClass(LocalConfiguration.current)

@Composable
fun ProvideWindowSizeClass(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        content()
    }
}
```

---

## 11. Acessibilidade (A11y Tokens)

### 11.1 Contraste Mínimo (WCAG 2.1 AA)
- **Texto normal (≥ 14pt)**: 4.5:1
- **Texto grande (≥ 18pt ou ≥ 14pt bold)**: 3:1
- **UI Components**: 3:1

### 11.2 Tokens de Acessibilidade

| Token | Valor | Descrição |
|-------|-------|-----------|
| `touchTarget/min` | 48dp | Área mínima de toque |
| `touchTarget/comfortable` | 56dp | Área confortável |
| `focusRing/width` | 2dp | Anel de foco |
| `focusRing/color` | `primary` | Cor do foco |
| `motion/reduce` | `false` | Respeitar "Reduzir movimento" |
| `contrast/high` | `false` | Modo alto contraste do sistema |

### 11.3 Semântica Compose

```kotlin
// Exemplo de botão acessível
Button(
    onClick = { /* action */ },
    modifier = Modifier
        .fillMaxWidth()
        .minimumInteractiveComponentSize() // 48dp
        .semantics {
            role = Role.Button
            stateDescription = "Botão de salvar período"
        }
) {
    Text("Salvar")
}
```

---

## 12. Temas Especiais

### 12.1 Modo Gravidez (Tertiary Theme)
- Primary → `tertiary` (Lavanda)
- Phase colors adaptados para gravidez
- Ícones de bebê, marcos

### 12.2 Modo Parceiro (Secondary Theme)
- Accent → `secondary` (Teal)
- Cards de compartilhamento com borda teal

### 12.3 Modo Perimenopausa (Future)
- Paleta quente (âmbar/laranja)
- Métricas específicas (irregularidade, ondas de calor)

---

## 13. Implementação no Projeto

### 13.1 Estrutura de Arquivos

```
ui/theme/
├── Color.kt          # ColorScheme light/dark + custom colors
├── Type.kt           # Typography + Inter font
├── Shape.kt          # Shapes
├── Spacing.kt        # Spacing tokens
├── Motion.kt         # Duration + Easing
├── Theme.kt          # Theme composable principal
├── Components.kt     # Component customizations
└── A11y.kt           # Accessibility helpers
```

### 13.2 Theme.kt Principal

```kotlin
@Composable
fun CycleTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    val context = LocalContext.current

    // Dynamic color (Android 12+)
    val dynamicColor = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    val scheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicColor else colorScheme

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

### 13.3 Uso em Composables

```kotlin
// Cores customizadas via CompositionLocal
object CycleTrackColors {
    val PhaseMenstrual = staticCompositionLocalOf { Color(0xFFE91E63) }
    val PhaseFollicular = staticCompositionLocalOf { Color(0xFF009688) }
    // ...
}

@Composable
fun CyclePhaseChip(phase: CyclePhase) {
    val color = when (phase) {
        CyclePhase.MENSTRUAL -> CycleTrackColors.PhaseMenstrual.current
        CyclePhase.FOLLICULAR -> CycleTrackColors.PhaseFollicular.current
        // ...
    }
    Chip(
        colors = ChipDefaults.colors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        )
    ) {
        Text(phase.displayName)
    }
}
```

---

## 14. Checklist de Qualidade

### 14.1 Pré-Release
- [ ] Todos os tokens têm versão light/dark
- [ ] Contraste AA verificado (Color Contrast Analyzer)
- [ ] Touch targets ≥ 48dp
- [ ] Escalabilidade de fonte testada (100%-200%)
- [ ] TalkBack/VoiceOver testado
- [ ] Reduzir movimento respeitado
- [ ] Alto contraste do sistema testado
- [ ] RTL testado (árabe/hebraico futuro)

### 14.2 Documentação Figma
- [ ] Variáveis de cor publicadas
- [ ] Estilos de texto publicados
- [ ] Efeitos de sombra publicados
- [ ] Componentes variantados (auto-layout)
- [ ] Documentação de handoff (Inspect)

---

## 15. Versionamento

| Versão | Data | Alterações |
|--------|------|------------|
| 1.0 | 17/11/2026 | Versão inicial - tokens core, tipografia, espaçamento, componentes base |

---

## 16. Referências

- [Material Design 3](https://m3.material.io/)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility)
- [LGPD - Art. 9, §1º](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/L13709.htm) - Linguagem simples
- [Human Interface Guidelines - Apple](https://developer.apple.com/design/human-interface-guidelines/)

---

*Este documento é vivo e deve ser atualizado a cada release. Última sincronização Figma: 17/11/2026*