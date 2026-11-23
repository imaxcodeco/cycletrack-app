# Napkin Runbook

## Curation Rules
- Re-prioritize on every read.
- Keep recurring, high-value notes only.
- Max 10 items per category.
- Each item includes date + "Do instead:".

## Build & Toolchain (Highest Priority)
1. **[2026-11-18] JDK 17 fixo em `C:\Users\Ian Max\.jdk17\jdk-17.0.21+9`**
   Do instead: usar `scripts\build.bat` (já fixa JAVA_HOME) em vez de chamar gradlew direto.
2. **[2026-11-18] cmd.exe trunca comandos multilinha**
   Do instead: gravar scripts `.ps1`/`.bat` e rodar via `powershell -ExecutionPolicy Bypass -File`.
3. **[2026-11-18] Path com espaços quebra Push-Location / Move-Item (8.3)**
   Do instead: usar `gradle -p "<dir>"`, `Copy-Item -LiteralPath`, sempre com path literal entre aspas.
4. **[2026-11-18] Realm 10.19.0 = API Java (`io.realm.*`), não Kotlin**
   Do instead: usar `realm.where(X::class.java)`, `RealmQuery.findAll()`, `realm.copyFromRealm(...)`; nunca `io.realm.kotlin.*`.
5. **[2026-11-18] `RealmResults` é thread-confined**
   Do instead: sempre devolver cópias detachadas (`realm.copyFromRealm`) e usar `Realm.getDefaultInstance()` dentro do `withContext(Dispatchers.IO)`.
6. **[2026-11-18] API Java do Realm não compara campo-com-campo**
   Do instead: filtrar em Kotlin (ex.: `updatedAt > lastSyncedAt`) em vez de `greaterThan("a", "b")`.

## Gradle / Plugins
1. **[2026-11-18] Plugin Realm correto = `id("realm-android")`**
   Do instead: manter `useModule("io.realm:realm-gradle-plugin:10.19.0")` em `settings.gradle.kts`.
2. **[2026-11-18] KSP não resolve no plugin Realm 10.x**
   Do instead: usar `kapt` para Hilt/Room/Moshi.
3. **[2026-11-18] Repositórios centralizados em settings.gradle.kts**
   Do instead: nunca adicionar bloco `allprojects { repositories }` (FAIL_ON_PROJECT_REPOS).
4. **[2026-11-18] Nomes de recurso Android não podem ser palavras reservadas Java**
   Do instead: usar prefixo (`action_continue`) e rodar `scripts\check-strings.ps1`.

## Android Resources / Manifest
1. **[2026-11-18] Material 3 XML exige material:1.12.0 e parents corretos**
   Do instead: validar parents/attrs com `scripts\validate-themes.ps1` antes do build.
2. **[2026-11-18] BOM UTF-8 em XML/Kotlin quebra o compilador**
   Do instead: rodar `scripts\strip-bom.ps1` e salvar sempre UTF-8 sem BOM.

## User Directives
1. **[2026-11-18] Sempre responder em pt-BR**
   Do instead: escrever toda a comunicação, comentários e mensagens de log em português.
2. **[2026-11-18] "prossiga" = continuar a tarefa pendente**
   Do instead: retomar o próximo item da lista sem pedir confirmação.
