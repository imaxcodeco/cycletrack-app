# CycleTrack v1.0 — Play Store Launch Checklist

## Status: Ready for Build

✅ Code preparation complete
- Premium/Billing removed
- Health Connect removed
- Firebase Analytics removed (Auth kept)
- Mandatory login gate + onboarding persistence
- Logout fixed (race condition resolved)
- Local-first architecture (all data on device)
- applicationId: `com.lamax.cycletrack`
- Google Play Services reduced

✅ Legal & compliance
- Política de Privacidade v2.0 (accurate, LGPD/GDPR compliant)
- Termos de Serviço v2.0 (medical disclaimer, no-sync promise)
- Hosted at: https://imaxcodeco.github.io/cycletrack-app/

✅ CI/CD ready
- GitHub Actions workflow (`.github/workflows/release.yml`)
- Keystore + signing credentials in GitHub Secrets
- Trigger: push tag `v1.0.0` → builds AAB automatically

---

## Build Release AAB

### Option 1: Automatic (Recommended)
```bash
git tag v1.0.0
git push origin v1.0.0
# GitHub Actions builds AAB, uploads to Releases (draft)
# Wait ~15 min for build
# Download AAB from https://github.com/imaxcodeco/cycletrack-app/releases
```

### Option 2: Manual (Requires 8GB+ free RAM)
```bash
export KEYSTORE_PATH=keystore/release.keystore
export KEYSTORE_PASSWORD=$(grep "^Senha" keystore/README.md | sed 's/.*: //')
export KEY_ALIAS=cycletrack-release-key
export KEY_PASSWORD=$KEYSTORE_PASSWORD
./gradlew bundleRelease --no-daemon
# Output: app/release/outputs/bundle/release/app-release.aab
```

---

## Play Console Setup

### 1. Create Developer Account
- Go to: https://play.google.com/console
- Register: $25 one-time
- Accepts: Google/credit card

### 2. Create App
- App name: **CycleTrack**
- Package name: **com.lamax.cycletrack** (already set)
- App type: Health & fitness
- Category: Health & wellness

### 3. App Details (Store Listing)
- **Title:** CycleTrack
- **Short description:** (60 chars max)
  > Rastreie seu ciclo menstrual com precisão. Sem publicidades, sem compartilhamento de dados.
- **Full description:** (4000 chars max)
  > Ciclos menstruais rastreados com segurança no seu aparelho.
  > 
  > • Registre menstruação, sintomas, temperatura e humor
  > • Previsões de próximo período e janela fértil
  > • Lembretes configuráveis
  > • Modo parceiro (compartilhamento opcional)
  > • Sem publicidades, sem rastreamento
  > • Seus dados ficam no aparelho — nunca sincronizados
  > 
  > Conformidade: LGPD, GDPR
- **Screenshots:** (Min 2, max 8)
  - Use do app real ou mockups
  - Formato: PNG/JPG, 1080×1920 max
- **Logo:** 512×512 PNG (upload store listing section)
- **Feature graphic:** 1024×500 PNG
- **Category:** Health & fitness
- **Content rating:** Health (not explicit)
- **Target audience:** 13+

### 4. Data Safety & Privacy
- Go to: **Data Safety** section
- **Policy URL:** https://imaxcodeco.github.io/cycletrack-app/
- **Data collection:**
  - User data: Email (Firebase Auth)
  - Device/app data: None (local-only)
  - Advertising: None
  - Sensitive health data: Yes (cycle tracking)
  - Encryption: AES-256 at rest, TLS in transit
  - Deletion: User can delete all via Settings

### 5. Content Rating Questionnaire
- **Medical/health:** Health app? Yes
- **Alcohol/tobacco/drugs:** No
- **Violence/hate speech:** No
- **Sexual content:** No
- **Profanity:** No
- Rating: **12+** (or 13+ if strict)

### 6. Pricing & Distribution
- **Pricing:** Free
- **Countries:** Select all or target Brazil + interested regions
- **Device categories:** Android phones ✓, Android tablets ✓

### 7. Upload Release AAB
- Section: **Testing** → **Internal testing**
  - Upload AAB first to test
  - Add testers (email list)
  - Test for 1-2 days minimum
- Once stable: **Production** → Upload final AAB

---

## Quality Checks (Before Upload)

- [ ] Run `./gradlew detekt` — no errors
- [ ] Run `./gradlew ktlintCheck` — no style issues
- [ ] Run `./gradlew testDebugUnitTest` — all pass
- [ ] Manual test on emulator:
  - Signup → verify Firebase creates account
  - Login → verify session persists across app restart
  - Logout → verify back to login screen (no auto-login)
  - Logging → register cycle, verify saved to Realm
  - Offline: app works without internet
- [ ] Check APK/AAB is signed correctly:
  ```bash
  keytool -printcert -jarfile app/release/outputs/bundle/release/app-release.aab
  ```

---

## Post-Launch

### Monitor
- Play Console: Crashes, ANRs, ratings
- GitHub: Issues, feature requests
- Error tracking: Sentry (if enabled)

### v1.1 Roadmap (Future)
- Health Connect integration (optional premium)
- Cloud backup (opt-in, explicit consent)
- More prediction algorithms
- Partner mode refinement
- Pregnancy tracking enhancements

---

## Contacts & Support

- **Privacy inquiries:** ianmaxcodeco@gmail.com
- **Bug reports:** GitHub Issues
- **Legal:** See Política de Privacidade + Termos (hosted)

---

**Last updated:** 2026-11-25  
**Status:** Ready for Play Console submission
