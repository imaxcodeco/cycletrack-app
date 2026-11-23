package com.seuapp.cycletrack.domain.model

import com.seuapp.cycletrack.data.local.realm.entities.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import com.seuapp.cycletrack.domain.model.HealthDataPoint
import com.seuapp.cycletrack.domain.repository.Content
import com.seuapp.cycletrack.domain.repository.Notification
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Mappers entre modelos de domínio e entidades Realm.
 * Clean-room - conversão bidirecional sem dependências cruzadas.
 */

// ============ Cycle ============
fun Cycle.toRealm(): CycleRealm {
    val realm = CycleRealm()
    realm.id = id
    realm.userId = userId
    realm.startDate = startDate.toEpochMilliseconds()
    endDate?.let { realm.endDate = it.toEpochMilliseconds() }
    realm.cycleLength = cycleLength
    realm.periodLength = periodLength
    realm.lutealPhaseLength = lutealPhaseLength
    ovulationDate?.let { realm.ovulationDate = it.toEpochMilliseconds() }
    fertileWindowStart?.let { realm.fertileWindowStart = it.toEpochMilliseconds() }
    fertileWindowEnd?.let { realm.fertileWindowEnd = it.toEpochMilliseconds() }
    realm.isPredicted = isPredicted
    realm.confidenceScore = confidenceScore
    realm.source = source
    realm.notes = notes
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun CycleRealm.toDomain(): Cycle {
    return Cycle(
        id = id,
        userId = userId,
        startDate = LocalDate.fromEpochDays(startDate / (1000 * 60 * 60 * 24)),
        endDate = if (endDate > 0) LocalDate.fromEpochDays(endDate / (1000 * 60 * 60 * 24)) else null,
        cycleLength = cycleLength,
        periodLength = periodLength,
        lutealPhaseLength = lutealPhaseLength,
        ovulationDate = if (ovulationDate > 0) LocalDate.fromEpochDays(ovulationDate / (1000 * 60 * 60 * 24)) else null,
        fertileWindowStart = if (fertileWindowStart > 0) LocalDate.fromEpochDays(fertileWindowStart / (1000 * 60 * 60 * 24)) else null,
        fertileWindowEnd = if (fertileWindowEnd > 0) LocalDate.fromEpochDays(fertileWindowEnd / (1000 * 60 * 60 * 24)) else null,
        isPredicted = isPredicted,
        confidenceScore = confidenceScore,
        source = source,
        notes = notes,
        symptoms = symptoms.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}

fun CycleRealm.updateFromDomain(domain: Cycle) {
    this.startDate = domain.startDate.toEpochMilliseconds()
    domain.endDate?.let { this.endDate = it.toEpochMilliseconds() }
    this.cycleLength = domain.cycleLength
    this.periodLength = domain.periodLength
    this.lutealPhaseLength = domain.lutealPhaseLength
    domain.ovulationDate?.let { this.ovulationDate = it.toEpochMilliseconds() }
    domain.fertileWindowStart?.let { this.fertileWindowStart = it.toEpochMilliseconds() }
    domain.fertileWindowEnd?.let { this.fertileWindowEnd = it.toEpochMilliseconds() }
    this.isPredicted = domain.isPredicted
    this.confidenceScore = domain.confidenceScore
    this.source = domain.source
    this.notes = domain.notes
    this.updatedAt = System.currentTimeMillis()
    this.version++
}

// ============ Symptom ============
fun Symptom.toRealm(): SymptomRealm {
    val realm = SymptomRealm()
    realm.id = id
    realm.userId = userId
    realm.cycleId = cycleId
    realm.date = date.toEpochMilliseconds()
    realm.category = category
    realm.name = name
    realm.severity = severity.value
    realm.notes = notes
    realm.source = source
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun SymptomRealm.toDomain(): Symptom {
    return Symptom(
        id = id,
        userId = userId,
        cycleId = cycleId,
        date = LocalDate.fromEpochDays(date / (1000 * 60 * 60 * 24)),
        category = category,
        name = name,
        severity = SymptomSeverity.values().first { it.value == severity },
        notes = notes,
        source = source,
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}

// ============ UserProfile ============
fun UserProfile.toRealm(): UserProfileRealm {
    val realm = UserProfileRealm()
    realm.id = id
    realm.email = email
    realm.displayName = displayName
    dateOfBirth?.let { realm.dateOfBirth = it.toEpochMilliseconds() }
    realm.heightCm = heightCm
    realm.weightKg = weightKg
    realm.gender = gender.key
    realm.averageCycleLength = averageCycleLength
    realm.averagePeriodLength = averagePeriodLength
    realm.averageLutealPhaseLength = averageLutealPhaseLength
    realm.cycleRegularity = cycleRegularity.key
    realm.contraceptionMethod = contraceptionMethod.key
    contraceptionStartDate?.let { realm.contraceptionStartDate = it.toEpochMilliseconds() }
    lastPeriodDate?.let { realm.lastPeriodDate = it.toEpochMilliseconds() }
    realm.periodDurationDays = periodDurationDays
    realm.pregnancyStatus = pregnancyStatus.key
    pregnancyDueDate?.let { realm.pregnancyDueDate = it.toEpochMilliseconds() }
    pregnancyStartDate?.let { realm.pregnancyStartDate = it.toEpochMilliseconds() }
    realm.childrenCount = childrenCount
    lastPregnancyOutcome?.let { realm.lastPregnancyOutcome = it.key }
    realm.hasPcos = hasPcos
    realm.hasEndometriosis = hasEndometriosis
    realm.hasThyroidCondition = hasThyroidCondition
    realm.hasDiabetes = hasDiabetes
    realm.medicalConditions.addAll(medicalConditions)
    realm.periodReminderEnabled = periodReminderEnabled
    realm.periodReminderDaysBefore = periodReminderDaysBefore
    realm.fertileWindowReminderEnabled = fertileWindowReminderEnabled
    realm.ovulationReminderEnabled = ovulationReminderEnabled
    realm.pillReminderEnabled = pillReminderEnabled
    realm.pillReminderTime = pillReminderTime
    realm.themeMode = themeMode.key
    realm.language = language
    realm.temperatureUnit = temperatureUnit.key
    realm.weightUnit = weightUnit.key
    realm.heightUnit = heightUnit.key
    realm.firstDayOfWeek = firstDayOfWeek
    realm.calendarViewMode = calendarViewMode.key
    realm.analyticsOptIn = analyticsOptIn
    realm.crashReportingOptIn = crashReportingOptIn
    realm.marketingOptIn = marketingOptIn
    realm.healthConnectEnabled = healthConnectEnabled
    realm.healthConnectPermissionsGranted = healthConnectPermissionsGranted
    realm.dataSharingConsentVersion = dataSharingConsentVersion
    realm.dataSharingConsentDate = dataSharingConsentDate
    realm.isAnonymousMode = isAnonymousMode
    realm.accountCreatedAt = accountCreatedAt
    realm.lastLoginAt = lastLoginAt
    realm.emailVerified = emailVerified
    realm.phoneVerified = phoneVerified
    realm.biometricEnabled = biometricEnabled
    realm.autoLockEnabled = autoLockEnabled
    realm.autoLockTimeoutMinutes = autoLockTimeoutMinutes
    realm.subscriptionStatus = subscriptionStatus.key
    realm.subscriptionProductId = subscriptionProductId
    realm.subscriptionExpiresAt = subscriptionExpiresAt
    realm.subscriptionAutoRenew = subscriptionAutoRenew
    realm.subscriptionPlatform = subscriptionPlatform
    realm.trialUsed = trialUsed
    realm.partnerModeEnabled = partnerModeEnabled
    realm.partnerId = partnerId
    realm.partnerShareLevel = partnerShareLevel.key
    realm.partnerInvitationSent = partnerInvitationSent
    realm.partnerInvitationAcceptedAt = partnerInvitationAcceptedAt
    realm.autoSyncEnabled = autoSyncEnabled
    realm.syncFrequencyMinutes = syncFrequencyMinutes
    realm.lastBackupAt = lastBackupAt
    realm.backupFrequencyDays = backupFrequencyDays
    realm.exportFormat = exportFormat.key
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun UserProfileRealm.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        email = email,
        displayName = displayName,
        dateOfBirth = if (dateOfBirth > 0) LocalDate.fromEpochDays(dateOfBirth / (1000 * 60 * 60 * 24)) else null,
        heightCm = heightCm,
        weightKg = weightKg,
        gender = Gender.values().first { it.key == gender },
        averageCycleLength = averageCycleLength,
        averagePeriodLength = averagePeriodLength,
        averageLutealPhaseLength = averageLutealPhaseLength,
        cycleRegularity = CycleRegularity.values().first { it.key == cycleRegularity },
        contraceptionMethod = ContraceptionMethod.values().first { it.key == contraceptionMethod },
        contraceptionStartDate = if (contraceptionStartDate > 0) LocalDate.fromEpochDays(contraceptionStartDate / (1000 * 60 * 60 * 24)) else null,
        lastPeriodDate = if (lastPeriodDate > 0) LocalDate.fromEpochDays(lastPeriodDate / (1000 * 60 * 60 * 24)) else null,
        periodDurationDays = periodDurationDays,
        pregnancyStatus = PregnancyStatus.values().first { it.key == pregnancyStatus },
        pregnancyDueDate = if (pregnancyDueDate > 0) LocalDate.fromEpochDays(pregnancyDueDate / (1000 * 60 * 60 * 24)) else null,
        pregnancyStartDate = if (pregnancyStartDate > 0) LocalDate.fromEpochDays(pregnancyStartDate / (1000 * 60 * 60 * 24)) else null,
        childrenCount = childrenCount,
        lastPregnancyOutcome = lastPregnancyOutcome?.let { PregnancyOutcome.values().first { it.key == it } },
        hasPcos = hasPcos,
        hasEndometriosis = hasEndometriosis,
        hasThyroidCondition = hasThyroidCondition,
        hasDiabetes = hasDiabetes,
        medicalConditions = medicalConditions.toList(),
        periodReminderEnabled = periodReminderEnabled,
        periodReminderDaysBefore = periodReminderDaysBefore,
        fertileWindowReminderEnabled = fertileWindowReminderEnabled,
        ovulationReminderEnabled = ovulationReminderEnabled,
        pillReminderEnabled = pillReminderEnabled,
        pillReminderTime = pillReminderTime,
        themeMode = ThemeMode.values().first { it.key == themeMode },
        language = language,
        temperatureUnit = TemperatureUnit.values().first { it.key == temperatureUnit },
        weightUnit = WeightUnit.values().first { it.key == weightUnit },
        heightUnit = HeightUnit.values().first { it.key == heightUnit },
        firstDayOfWeek = firstDayOfWeek,
        calendarViewMode = CalendarViewMode.values().first { it.key == calendarViewMode },
        analyticsOptIn = analyticsOptIn,
        crashReportingOptIn = crashReportingOptIn,
        marketingOptIn = marketingOptIn,
        healthConnectEnabled = healthConnectEnabled,
        healthConnectPermissionsGranted = healthConnectPermissionsGranted,
        dataSharingConsentVersion = dataSharingConsentVersion,
        dataSharingConsentDate = dataSharingConsentDate,
        isAnonymousMode = isAnonymousMode,
        accountCreatedAt = accountCreatedAt,
        lastLoginAt = lastLoginAt,
        emailVerified = emailVerified,
        phoneVerified = phoneVerified,
        biometricEnabled = biometricEnabled,
        autoLockEnabled = autoLockEnabled,
        autoLockTimeoutMinutes = autoLockTimeoutMinutes,
        subscriptionStatus = SubscriptionStatus.values().first { it.key == subscriptionStatus },
        subscriptionProductId = subscriptionProductId,
        subscriptionExpiresAt = subscriptionExpiresAt,
        subscriptionAutoRenew = subscriptionAutoRenew,
        subscriptionPlatform = subscriptionPlatform,
        trialUsed = trialUsed,
        partnerModeEnabled = partnerModeEnabled,
        partnerId = partnerId,
        partnerShareLevel = PartnerShareLevel.values().first { it.key == partnerShareLevel },
        partnerInvitationSent = partnerInvitationSent,
        partnerInvitationAcceptedAt = partnerInvitationAcceptedAt,
        autoSyncEnabled = autoSyncEnabled,
        syncFrequencyMinutes = syncFrequencyMinutes,
        lastBackupAt = lastBackupAt,
        backupFrequencyDays = backupFrequencyDays,
        exportFormat = ExportFormat.values().first { it.key == exportFormat },
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}

fun UserProfileRealm.updateFromDomain(domain: UserProfile) {
    this.displayName = domain.displayName
    domain.dateOfBirth?.let { this.dateOfBirth = it.toEpochMilliseconds() }
    this.heightCm = domain.heightCm
    this.weightKg = domain.weightKg
    this.gender = domain.gender.key
    this.averageCycleLength = domain.averageCycleLength
    this.averagePeriodLength = domain.averagePeriodLength
    this.averageLutealPhaseLength = domain.averageLutealPhaseLength
    this.cycleRegularity = domain.cycleRegularity.key
    this.contraceptionMethod = domain.contraceptionMethod.key
    domain.contraceptionStartDate?.let { this.contraceptionStartDate = it.toEpochMilliseconds() }
    domain.lastPeriodDate?.let { this.lastPeriodDate = it.toEpochMilliseconds() }
    this.periodDurationDays = domain.periodDurationDays
    this.pregnancyStatus = domain.pregnancyStatus.key
    domain.pregnancyDueDate?.let { this.pregnancyDueDate = it.toEpochMilliseconds() }
    domain.pregnancyStartDate?.let { this.pregnancyStartDate = it.toEpochMilliseconds() }
    this.childrenCount = domain.childrenCount
    domain.lastPregnancyOutcome?.let { this.lastPregnancyOutcome = it.key }
    this.hasPcos = domain.hasPcos
    this.hasEndometriosis = domain.hasEndometriosis
    this.hasThyroidCondition = domain.hasThyroidCondition
    this.hasDiabetes = domain.hasDiabetes
    this.medicalConditions.clear()
    this.medicalConditions.addAll(domain.medicalConditions)
    this.periodReminderEnabled = domain.periodReminderEnabled
    this.periodReminderDaysBefore = domain.periodReminderDaysBefore
    this.fertileWindowReminderEnabled = domain.fertileWindowReminderEnabled
    this.ovulationReminderEnabled = domain.ovulationReminderEnabled
    this.pillReminderEnabled = domain.pillReminderEnabled
    this.pillReminderTime = domain.pillReminderTime
    this.themeMode = domain.themeMode.key
    this.language = domain.language
    this.temperatureUnit = domain.temperatureUnit.key
    this.weightUnit = domain.weightUnit.key
    this.heightUnit = domain.heightUnit.key
    this.firstDayOfWeek = domain.firstDayOfWeek
    this.calendarViewMode = domain.calendarViewMode.key
    this.analyticsOptIn = domain.analyticsOptIn
    this.crashReportingOptIn = domain.crashReportingOptIn
    this.marketingOptIn = domain.marketingOptIn
    this.healthConnectEnabled = domain.healthConnectEnabled
    this.healthConnectPermissionsGranted = domain.healthConnectPermissionsGranted
    this.dataSharingConsentVersion = domain.dataSharingConsentVersion
    this.dataSharingConsentDate = domain.dataSharingConsentDate
    this.isAnonymousMode = domain.isAnonymousMode
    this.lastLoginAt = domain.lastLoginAt
    this.emailVerified = domain.emailVerified
    this.phoneVerified = domain.phoneVerified
    this.biometricEnabled = domain.biometricEnabled
    this.autoLockEnabled = domain.autoLockEnabled
    this.autoLockTimeoutMinutes = domain.autoLockTimeoutMinutes
    this.subscriptionStatus = domain.subscriptionStatus.key
    this.subscriptionProductId = domain.subscriptionProductId
    this.subscriptionExpiresAt = domain.subscriptionExpiresAt
    this.subscriptionAutoRenew = domain.subscriptionAutoRenew
    this.subscriptionPlatform = domain.subscriptionPlatform
    this.trialUsed = domain.trialUsed
    this.partnerModeEnabled = domain.partnerModeEnabled
    this.partnerId = domain.partnerId
    this.partnerShareLevel = domain.partnerShareLevel.key
    this.partnerInvitationSent = domain.partnerInvitationSent
    this.partnerInvitationAcceptedAt = domain.partnerInvitationAcceptedAt
    this.autoSyncEnabled = domain.autoSyncEnabled
    this.syncFrequencyMinutes = domain.syncFrequencyMinutes
    this.lastBackupAt = domain.lastBackupAt
    this.backupFrequencyDays = domain.backupFrequencyDays
    this.exportFormat = domain.exportFormat.key
    this.updatedAt = System.currentTimeMillis()
    this.version++
}

// ============ Prediction ============
fun com.seuapp.cycletrack.domain.model.Prediction.toRealm(): PredictionRealm {
    val realm = PredictionRealm()
    realm.id = id
    realm.userId = userId
    realm.predictionType = type.key
    realm.predictedDate = predictedDate.toEpochMilliseconds()
    realm.confidenceScore = confidenceScore
    realm.algorithmVersion = algorithmVersion
    realm.cycleCountUsed = cycleCountUsed
    realm.standardDeviation = standardDeviation
    realm.metadata = metadata
    realm.source = source.key
    realm.isNotified = isNotified
    realm.notifiedAt = notifiedAt
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.expiresAt = expiresAt
    actualDate?.let { realm.actualDate = it.toEpochMilliseconds() }
    realm.accuracyDays = accuracyDays
    return realm
}

fun PredictionRealm.toDomain(): com.seuapp.cycletrack.domain.model.Prediction {
    return com.seuapp.cycletrack.domain.model.Prediction(
        id = id,
        userId = userId,
        type = com.seuapp.cycletrack.domain.model.PredictionType.values().first { it.key == predictionType },
        predictedDate = LocalDate.fromEpochDays(predictedDate / (1000 * 60 * 60 * 24)),
        confidenceScore = confidenceScore,
        algorithmVersion = algorithmVersion,
        cycleCountUsed = cycleCountUsed,
        standardDeviation = standardDeviation,
        metadata = metadata,
        source = com.seuapp.cycletrack.domain.model.PredictionSource.values().first { it.key == source },
        isNotified = isNotified,
        notifiedAt = notifiedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        expiresAt = expiresAt,
        actualDate = if (actualDate > 0) LocalDate.fromEpochDays(actualDate / (1000 * 60 * 60 * 24)) else null,
        accuracyDays = accuracyDays
    )
}

// ============ Pregnancy ============
fun com.seuapp.cycletrack.domain.model.Pregnancy.toRealm(): PregnancyRealm {
    val realm = PregnancyRealm()
    realm.id = id
    realm.userId = userId
    realm.lmpDate = lmpDate.toEpochMilliseconds()
    conceptionDate?.let { realm.conceptionDate = it.toEpochMilliseconds() }
    realm.dueDate = dueDate.toEpochMilliseconds()
    actualBirthDate?.let { realm.actualBirthDate = it.toEpochMilliseconds() }
    realm.currentWeek = currentWeek
    realm.currentDay = currentDay
    realm.trimester = trimester
    realm.status = status.key
    outcome?.let { realm.outcome = it.key }
    outcomeDate?.let { realm.outcomeDate = it.toEpochMilliseconds() }
    realm.outcomeNotes = outcomeNotes
    realm.prePregnancyWeightKg = prePregnancyWeightKg
    realm.currentWeightKg = currentWeightKg
    realm.totalWeightGainKg = totalWeightGainKg
    realm.bloodPressureSystolic = bloodPressureSystolic
    realm.bloodPressureDiastolic = bloodPressureDiastolic
    realm.glucoseLevel = glucoseLevel
    realm.appointments.addAll(appointments.map { it.toRealm() })
    realm.tests.addAll(tests.map { it.toRealm() })
    realm.symptoms.addAll(symptoms.map { it.toRealm() })
    realm.babyName = babyName
    realm.babyGender = babyGender.key
    realm.babyWeightGrams = babyWeightGrams
    realm.babyHeightCm = babyHeightCm
    realm.babyApagarScore = babyApgarScore
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun PregnancyRealm.toDomain(): com.seuapp.cycletrack.domain.model.Pregnancy {
    return com.seuapp.cycletrack.domain.model.Pregnancy(
        id = id,
        userId = userId,
        lmpDate = LocalDate.fromEpochDays(lmpDate / (1000 * 60 * 60 * 24)),
        conceptionDate = if (conceptionDate > 0) LocalDate.fromEpochDays(conceptionDate / (1000 * 60 * 60 * 24)) else null,
        dueDate = LocalDate.fromEpochDays(dueDate / (1000 * 60 * 60 * 24)),
        actualBirthDate = if (actualBirthDate > 0) LocalDate.fromEpochDays(actualBirthDate / (1000 * 60 * 60 * 24)) else null,
        currentWeek = currentWeek,
        currentDay = currentDay,
        trimester = trimester,
        status = com.seuapp.cycletrack.domain.model.PregnancyStatus.values().first { it.key == status },
        outcome = outcome?.let { com.seuapp.cycletrack.domain.model.PregnancyOutcome.values().first { it.key == it } },
        outcomeDate = if (outcomeDate > 0) LocalDate.fromEpochDays(outcomeDate / (1000 * 60 * 60 * 24)) else null,
        outcomeNotes = outcomeNotes,
        prePregnancyWeightKg = prePregnancyWeightKg,
        currentWeightKg = currentWeightKg,
        totalWeightGainKg = totalWeightGainKg,
        bloodPressureSystolic = bloodPressureSystolic,
        bloodPressureDiastolic = bloodPressureDiastolic,
        glucoseLevel = glucoseLevel,
        appointments = appointments.map { it.toDomain() },
        tests = tests.map { it.toDomain() },
        symptoms = symptoms.map { it.toDomain() },
        babyName = babyName,
        babyGender = com.seuapp.cycletrack.domain.model.BabyGender.values().first { it.key == babyGender },
        babyWeightGrams = babyWeightGrams,
        babyHeightCm = babyHeightCm,
        babyApgarScore = babyApgarScore,
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}

fun PregnancyAppointment.toRealm(): PregnancyAppointmentRealm {
    val realm = PregnancyAppointmentRealm()
    realm.id = id
    realm.pregnancyId = pregnancyId
    realm.title = title
    realm.description = description
    realm.appointmentDate = appointmentDate.toEpochMilliseconds()
    realm.type = type.key
    realm.location = location
    realm.doctorName = doctorName
    realm.notes = notes
    realm.completed = completed
    realm.reminderEnabled = reminderEnabled
    realm.reminderMinutesBefore = reminderMinutesBefore
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    return realm
}

fun PregnancyAppointmentRealm.toDomain(): PregnancyAppointment {
    return PregnancyAppointment(
        id = id,
        pregnancyId = pregnancyId,
        title = title,
        description = description,
        appointmentDate = LocalDate.fromEpochDays(appointmentDate / (1000 * 60 * 60 * 24)),
        type = AppointmentType.values().first { it.key == type },
        location = location,
        doctorName = doctorName,
        notes = notes,
        completed = completed,
        reminderEnabled = reminderEnabled,
        reminderMinutesBefore = reminderMinutesBefore,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun PregnancyTest.toRealm(): PregnancyTestRealm {
    val realm = PregnancyTestRealm()
    realm.id = id
    realm.pregnancyId = pregnancyId
    realm.testType = testType.key
    realm.testDate = testDate.toEpochMilliseconds()
    realm.resultValue = resultValue
    realm.resultUnit = resultUnit
    realm.referenceRange = referenceRange
    realm.isNormal = isNormal
    realm.notes = notes
    realm.createdAt = createdAt
    return realm
}

fun PregnancyTestRealm.toDomain(): PregnancyTest {
    return PregnancyTest(
        id = id,
        pregnancyId = pregnancyId,
        testType = TestType.values().first { it.key == testType },
        testDate = LocalDate.fromEpochDays(testDate / (1000 * 60 * 60 * 24)),
        resultValue = resultValue,
        resultUnit = resultUnit,
        referenceRange = referenceRange,
        isNormal = isNormal,
        notes = notes,
        createdAt = createdAt
    )
}

// ============ Partner ============
fun PartnerConnection.toRealm(): PartnerRealm {
    val realm = PartnerRealm()
    realm.id = id
    realm.userId = userId
    realm.partnerUserId = partnerUserId
    realm.invitationCode = invitationCode
    realm.partnerEmail = partnerEmail
    realm.partnerDisplayName = partnerDisplayName
    realm.partnerAvatarUrl = partnerAvatarUrl
    realm.status = status.key
    realm.invitedAt = invitedAt
    realm.acceptedAt = acceptedAt
    realm.revokedAt = revokedAt
    realm.shareCycle = shareCycle
    realm.sharePeriodDates = sharePeriodDates
    realm.shareOvulation = shareOvulation
    realm.shareFertileWindow = shareFertileWindow
    realm.sharePms = sharePms
    realm.shareSymptoms = shareSymptoms
    realm.sharePredictions = sharePredictions
    realm.sharePregnancy = sharePregnancy
    realm.shareInsights = shareInsights
    realm.shareMood = shareMood
    realm.shareWeight = shareWeight
    realm.shareNotes = shareNotes
    realm.notifyPartnerPeriodStart = notifyPartnerPeriodStart
    realm.notifyPartnerPeriodEnd = notifyPartnerPeriodEnd
    realm.notifyPartnerFertileWindow = notifyPartnerFertileWindow
    realm.notifyPartnerOvulation = notifyPartnerOvulation
    realm.notifyPartnerPms = notifyPartnerPms
    realm.notifyPartnerPregnancy = notifyPartnerPregnancy
    realm.notifyPartnerSymptoms = notifyPartnerSymptoms
    realm.partnerShareCycle = partnerShareCycle
    realm.partnerSharePeriodDates = partnerSharePeriodDates
    realm.partnerShareOvulation = partnerShareOvulation
    realm.partnerShareFertileWindow = partnerShareFertileWindow
    realm.partnerSharePms = partnerSharePms
    realm.partnerShareSymptoms = partnerShareSymptoms
    realm.partnerSharePredictions = partnerSharePredictions
    realm.partnerSharePregnancy = partnerSharePregnancy
    realm.partnerShareInsights = partnerShareInsights
    realm.partnerShareMood = partnerShareMood
    realm.partnerShareWeight = partnerShareWeight
    realm.partnerShareNotes = partnerShareNotes
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun PartnerRealm.toDomain(): PartnerConnection {
    return PartnerConnection(
        id = id,
        userId = userId,
        partnerUserId = partnerUserId,
        invitationCode = invitationCode,
        partnerEmail = partnerEmail,
        partnerDisplayName = partnerDisplayName,
        partnerAvatarUrl = partnerAvatarUrl,
        status = ConnectionStatus.values().first { it.key == status },
        invitedAt = invitedAt,
        acceptedAt = acceptedAt,
        revokedAt = revokedAt,
        shareCycle = shareCycle,
        sharePeriodDates = sharePeriodDates,
        shareOvulation = shareOvulation,
        shareFertileWindow = shareFertileWindow,
        sharePms = sharePms,
        shareSymptoms = shareSymptoms,
        sharePredictions = sharePredictions,
        sharePregnancy = sharePregnancy,
        shareInsights = shareInsights,
        shareMood = shareMood,
        shareWeight = shareWeight,
        shareNotes = shareNotes,
        notifyPartnerPeriodStart = notifyPartnerPeriodStart,
        notifyPartnerPeriodEnd = notifyPartnerPeriodEnd,
        notifyPartnerFertileWindow = notifyPartnerFertileWindow,
        notifyPartnerOvulation = notifyPartnerOvulation,
        notifyPartnerPms = notifyPartnerPms,
        notifyPartnerPregnancy = notifyPartnerPregnancy,
        notifyPartnerSymptoms = notifyPartnerSymptoms,
        partnerShareCycle = partnerShareCycle,
        partnerSharePeriodDates = partnerSharePeriodDates,
        partnerShareOvulation = partnerShareOvulation,
        partnerShareFertileWindow = partnerShareFertileWindow,
        partnerSharePms = partnerSharePms,
        partnerShareSymptoms = partnerShareSymptoms,
        partnerSharePredictions = partnerSharePredictions,
        partnerSharePregnancy = partnerSharePregnancy,
        partnerShareInsights = partnerShareInsights,
        partnerShareMood = partnerShareMood,
        partnerShareWeight = partnerShareWeight,
        partnerShareNotes = partnerShareNotes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}

// ============ Subscription ============
fun Subscription.toRealm(): SubscriptionRealm {
    val realm = SubscriptionRealm()
    realm.id = id
    realm.userId = userId
    realm.productId = productId
    realm.productType = productType
    realm.basePlanId = basePlanId
    realm.offerId = offerId
    realm.purchaseToken = purchaseToken
    realm.orderId = orderId
    realm.packageName = packageName
    realm.status = status.key
    realm.purchaseState = purchaseState.value
    realm.purchaseTime = purchaseTime
    realm.expiryTime = expiryTime
    realm.autoRenewing = autoRenewing
    realm.gracePeriodExpiryTime = gracePeriodExpiryTime
    realm.accountHoldExpiryTime = accountHoldExpiryTime
    realm.isTrial = isTrial
    realm.trialStartTime = trialStartTime
    realm.trialEndTime = trialEndTime
    realm.priceAmountMicros = priceAmountMicros
    realm.priceCurrencyCode = priceCurrencyCode
    realm.priceFormatted = priceFormatted
    realm.platform = platform.key
    realm.platformData = platformData
    realm.acknowledged = acknowledged
    realm.acknowledgedAt = acknowledgedAt
    realm.lastNotificationType = lastNotificationType
    realm.lastNotificationTime = lastNotificationTime
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun SubscriptionRealm.toDomain(): Subscription {
    return Subscription(
        id = id,
        userId = userId,
        productId = productId,
        productType = productType,
        basePlanId = basePlanId,
        offerId = offerId,
        purchaseToken = purchaseToken,
        orderId = orderId,
        packageName = packageName,
        status = SubscriptionStatus.values().first { it.key == status },
        purchaseState = PurchaseState.values().first { it.value == purchaseState },
        purchaseTime = purchaseTime,
        expiryTime = expiryTime,
        autoRenewing = autoRenewing,
        gracePeriodExpiryTime = gracePeriodExpiryTime,
        accountHoldExpiryTime = accountHoldExpiryTime,
        isTrial = isTrial,
        trialStartTime = trialStartTime,
        trialEndTime = trialEndTime,
        priceAmountMicros = priceAmountMicros,
        priceCurrencyCode = priceCurrencyCode,
        priceFormatted = priceFormatted,
        platform = SubscriptionPlatform.values().first { it.key == platform },
        platformData = platformData,
        acknowledged = acknowledged,
        acknowledgedAt = acknowledgedAt,
        lastNotificationType = lastNotificationType,
        lastNotificationTime = lastNotificationTime,
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}

// ============ Content ============
fun com.seuapp.cycletrack.domain.model.Content.toRealm(): ContentRealm {
    val realm = ContentRealm()
    realm.id = id
    realm.category = category
    realm.tags = tags
    realm.title = title
    realm.subtitle = subtitle
    realm.body = body
    realm.summary = summary
    realm.author = author
    realm.sourceUrl = sourceUrl
    realm.imageUrl = imageUrl
    realm.videoUrl = videoUrl
    realm.durationMinutes = durationMinutes
    realm.targetCyclePhase = targetCyclePhase
    realm.targetPregnancyWeekMin = targetPregnancyWeekMin
    realm.targetPregnancyWeekMax = targetPregnancyWeekMax
    realm.targetConditions = targetConditions
    realm.language = language
    realm.region = region
    realm.isPremium = isPremium
    realm.minAppVersion = minAppVersion
    realm.maxAppVersion = maxAppVersion
    realm.viewCount = viewCount
    realm.likeCount = likeCount
    realm.bookmarkCount = bookmarkCount
    realm.shareCount = shareCount
    realm.isBookmarked = isBookmarked
    realm.isRead = isRead
    realm.readProgress = readProgress
    realm.lastReadAt = lastReadAt
    realm.publishedAt = publishedAt
    realm.expiresAt = expiresAt
    realm.version = version
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    return realm
}

fun ContentRealm.toDomain(): com.seuapp.cycletrack.domain.model.Content {
    return com.seuapp.cycletrack.domain.model.Content(
        id = id,
        category = category,
        tags = tags,
        title = title,
        subtitle = subtitle,
        body = body,
        summary = summary,
        author = author,
        sourceUrl = sourceUrl,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        durationMinutes = durationMinutes,
        targetCyclePhase = targetCyclePhase,
        targetPregnancyWeekMin = targetPregnancyWeekMin,
        targetPregnancyWeekMax = targetPregnancyWeekMax,
        targetConditions = targetConditions,
        language = language,
        region = region,
        isPremium = isPremium,
        minAppVersion = minAppVersion,
        maxAppVersion = maxAppVersion,
        viewCount = viewCount,
        likeCount = likeCount,
        bookmarkCount = bookmarkCount,
        shareCount = shareCount,
        isBookmarked = isBookmarked,
        isRead = isRead,
        readProgress = readProgress,
        lastReadAt = lastReadAt,
        publishedAt = publishedAt,
        expiresAt = expiresAt,
        version = version,
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt
    )
}

// ============ HealthDataPoint ============
fun HealthDataPoint.toRealm(): HealthDataPointRealm {
    val realm = HealthDataPointRealm()
    realm.id = id
    realm.userId = userId
    realm.dataType = dataType
    realm.startTime = startTime
    realm.endTime = endTime
    realm.timeZoneOffset = timeZoneOffset
    realm.value = value
    realm.numericValue = numericValue
    realm.unit = unit
    realm.source = source
    realm.sourcePackageName = sourcePackageName
    realm.sourceRecordId = sourceRecordId
    realm.flowLevel = flowLevel
    realm.testResult = testResult
    realm.temperatureCelsius = temperatureCelsius
    realm.mucusConsistency = mucusConsistency
    realm.pregnancyTestResult = pregnancyTestResult
    realm.syncStatus = syncStatus
    realm.lastSyncAttempt = lastSyncAttempt
    realm.syncError = syncError
    realm.conflictResolution = conflictResolution
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun HealthDataPointRealm.toDomain(): HealthDataPoint {
    return HealthDataPoint(
        id = id,
        userId = userId,
        dataType = dataType,
        startTime = startTime,
        endTime = endTime,
        timeZoneOffset = timeZoneOffset,
        value = value,
        numericValue = numericValue,
        unit = unit,
        source = source,
        sourcePackageName = sourcePackageName,
        sourceRecordId = sourceRecordId,
        flowLevel = flowLevel,
        testResult = testResult,
        temperatureCelsius = temperatureCelsius,
        mucusConsistency = mucusConsistency,
        pregnancyTestResult = pregnancyTestResult,
        syncStatus = syncStatus,
        lastSyncAttempt = lastSyncAttempt,
        syncError = syncError,
        conflictResolution = conflictResolution,
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}

// ============ Notification ============
fun com.seuapp.cycletrack.domain.model.Notification.toRealm(): NotificationRealm {
    val realm = NotificationRealm()
    realm.id = id
    realm.userId = userId
    realm.type = type
    realm.scheduledTime = scheduledTime
    realm.title = title
    realm.body = body
    realm.icon = icon
    realm.color = color
    realm.channelId = channelId
    realm.priority = priority
    realm.category = category
    realm.action1Label = action1Label
    realm.action1IntentAction = action1IntentAction
    realm.action2Label = action2Label
    realm.action2IntentAction = action2IntentAction
    realm.deepLink = deepLink
    realm.isRecurring = isRecurring
    realm.recurrenceRule = recurrenceRule
    realm.recurrenceEndDate = recurrenceEndDate
    realm.targetCycleId = targetCycleId
    realm.targetPregnancyId = targetPregnancyId
    realm.targetPartnerId = targetPartnerId
    realm.status = status
    realm.sentAt = sentAt
    realm.deliveredAt = deliveredAt
    realm.openedAt = openedAt
    realm.dismissedAt = dismissedAt
    realm.failureReason = failureReason
    realm.retryCount = retryCount
    realm.maxRetries = maxRetries
    realm.nextRetryAt = nextRetryAt
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun NotificationRealm.toDomain(): com.seuapp.cycletrack.domain.model.Notification {
    return com.seuapp.cycletrack.domain.model.Notification(
        id = id,
        userId = userId,
        type = type,
        scheduledTime = scheduledTime,
        title = title,
        body = body,
        icon = icon,
        color = color,
        channelId = channelId,
        priority = priority,
        category = category,
        action1Label = action1Label,
        action1IntentAction = action1IntentAction,
        action2Label = action2Label,
        action2IntentAction = action2IntentAction,
        deepLink = deepLink,
        isRecurring = isRecurring,
        recurrenceRule = recurrenceRule,
        recurrenceEndDate = recurrenceEndDate,
        targetCycleId = targetCycleId,
        targetPregnancyId = targetPregnancyId,
        targetPartnerId = targetPartnerId,
        status = status,
        sentAt = sentAt,
        deliveredAt = deliveredAt,
        openedAt = openedAt,
        dismissedAt = dismissedAt,
        failureReason = failureReason,
        retryCount = retryCount,
        maxRetries = maxRetries,
        nextRetryAt = nextRetryAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}

// ============ Export ============
fun com.seuapp.cycletrack.domain.model.ExportRequest.toRealm(): ExportRealm {
    val realm = ExportRealm()
    realm.id = id
    realm.userId = userId
    realm.format = format
    realm.includeCycles = includeCycles
    realm.includeSymptoms = includeSymptoms
    realm.includePredictions = includePredictions
    realm.includePregnancy = includePregnancy
    realm.includePartnerData = includePartnerData
    realm.includeHealthConnectData = includeHealthConnectData
    realm.includeSettings = includeSettings
    realm.includeContent = includeContent
    realm.dateRangeStart = dateRangeStart
    realm.dateRangeEnd = dateRangeEnd
    realm.anonymize = anonymize
    realm.status = status
    realm.progress = progress
    realm.filePath = filePath
    realm.fileSize = fileSize
    realm.fileHash = fileHash
    realm.downloadUrl = downloadUrl
    realm.expiresAt = expiresAt
    realm.errorMessage = errorMessage
    realm.retryCount = retryCount
    realm.requestedAt = requestedAt
    realm.startedAt = startedAt
    realm.completedAt = completedAt
    realm.requestedFrom = requestedFrom
    realm.ipAddress = ipAddress
    realm.userAgent = userAgent
    realm.legalBasis = legalBasis
    realm.retentionDays = retentionDays
    realm.createdAt = createdAt
    realm.updatedAt = updatedAt
    realm.serverId = serverId
    realm.lastSyncedAt = lastSyncedAt
    realm.version = version
    return realm
}

fun ExportRealm.toDomain(): com.seuapp.cycletrack.domain.model.ExportRequest {
    return com.seuapp.cycletrack.domain.model.ExportRequest(
        id = id,
        userId = userId,
        format = format,
        includeCycles = includeCycles,
        includeSymptoms = includeSymptoms,
        includePredictions = includePredictions,
        includePregnancy = includePregnancy,
        includePartnerData = includePartnerData,
        includeHealthConnectData = includeHealthConnectData,
        includeSettings = includeSettings,
        includeContent = includeContent,
        dateRangeStart = dateRangeStart,
        dateRangeEnd = dateRangeEnd,
        anonymize = anonymize,
        status = status,
        progress = progress,
        filePath = filePath,
        fileSize = fileSize,
        fileHash = fileHash,
        downloadUrl = downloadUrl,
        expiresAt = expiresAt,
        errorMessage = errorMessage,
        retryCount = retryCount,
        requestedAt = requestedAt,
        startedAt = startedAt,
        completedAt = completedAt,
        requestedFrom = requestedFrom,
        ipAddress = ipAddress,
        userAgent = userAgent,
        legalBasis = legalBasis,
        retentionDays = retentionDays,
        createdAt = createdAt,
        updatedAt = updatedAt,
        serverId = serverId,
        lastSyncedAt = lastSyncedAt,
        version = version
    )
}