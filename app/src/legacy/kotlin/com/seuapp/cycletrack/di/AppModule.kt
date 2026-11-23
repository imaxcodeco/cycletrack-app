package com.seuapp.cycletrack.di

import android.content.Context
import com.seuapp.cycletrack.data.local.realm.RealmRepository
import com.seuapp.cycletrack.data.local.realm.dao.*
import com.seuapp.cycletrack.data.local.room.AppDatabase
import com.seuapp.cycletrack.data.local.room.dao.AnalyticsEventDao
import com.seuapp.cycletrack.data.local.room.dao.UserSessionDao
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import com.seuapp.cycletrack.domain.algorithm.CyclePredictionEngine
import com.seuapp.cycletrack.domain.repository.*
import com.seuapp.cycletrack.domain.usecase.auth.AuthUseCases
import com.seuapp.cycletrack.domain.usecase.cycle.CycleUseCases
import com.seuapp.cycletrack.domain.usecase.export.ExportUseCases
import com.seuapp.cycletrack.domain.usecase.partner.PartnerUseCases
import com.seuapp.cycletrack.domain.usecase.pregnancy.PregnancyUseCases
import com.seuapp.cycletrack.domain.usecase.prediction.PredictionUseCases
import com.seuapp.cycletrack.domain.usecase.subscription.SubscriptionUseCases
import com.seuapp.cycletrack.domain.usecase.symptom.SymptomUseCases
import com.seuapp.cycletrack.domain.usecase.export.ExportUseCases
import com.seuapp.cycletrack.data.local.realm.entities.*
import io.realm.Realm
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import com.seuapp.cycletrack.domain.model.AnalyticsEvent
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.PartnerConnection
import com.seuapp.cycletrack.domain.model.PregnancyAppointment
import com.seuapp.cycletrack.domain.model.PregnancyOutcome
import com.seuapp.cycletrack.domain.model.PregnancyTest
import com.seuapp.cycletrack.domain.model.Subscription
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.model.UserSession
import com.seuapp.cycletrack.domain.repository.Content
import com.seuapp.cycletrack.domain.repository.Notification

/**
 * Módulo Koin para injeção de dependência da camada de domínio.
 * Clean-room - separa domínio dos frameworks (Hilt para Android, Koin para Domain).
 */
val appModule = module {
    // Realm Repository & DAOs
    single<RealmRepository> { RealmRepository.getInstance() }
    
    // DAOs são obtidos via RealmRepository
    // Cycle
    factory<CycleRepository> { 
        RealmCycleRepository(get<RealmRepository>().cycles) 
    }
    
    // Symptom
    factory<SymptomRepository> { 
        RealmSymptomRepository(get<RealmRepository>().symptoms) 
    }
    
    // UserProfile
    factory<UserProfileRepository> { 
        RealmUserProfileRepository(get<RealmRepository>().userProfiles) 
    }
    
    // Prediction
    factory<PredictionRepository> { 
        RealmPredictionRepository(get<RealmRepository>().predictions) 
    }
    
    // Pregnancy
    factory<PregnancyRepository> { 
        RealmPregnancyRepository(get<RealmRepository>().pregnancies) 
    }
    
    // Partner
    factory<PartnerRepository> { 
        RealmPartnerRepository(get<RealmRepository>().partners) 
    }
    
    // Subscription
    factory<SubscriptionRepository> { 
        RealmSubscriptionRepository(get<RealmRepository>().subscriptions) 
    }
    
    // Content
    factory<ContentRepository> { 
        RealmContentRepository(get<RealmRepository>().content) 
    }
    
    // HealthDataPoint
    factory<HealthDataPointRepository> { 
        RealmHealthDataPointRepository(get<RealmRepository>().healthDataPoints) 
    }
    
    // Notification
    factory<NotificationRepository> { 
        RealmNotificationRepository(get<RealmRepository>().notifications) 
    }
    
    // Export
    factory<ExportRepository> { 
        RealmExportRepository(get<RealmRepository>().exports) 
    }

    // Room Database & DAOs
    single<AppDatabase> { AppDatabase.getInstance(androidContext()) }
    single<AnalyticsEventDao> { get<AppDatabase>().analyticsEventDao() }
    single<UserSessionDao> { get<AppDatabase>().userSessionDao() }
    factory<AnalyticsRepository> { RoomAnalyticsRepository(get<AnalyticsEventDao>(), get<UserSessionDao>()) }

    // DataStore Preferences
    single<PreferencesManager> { PreferencesManager.getInstance(androidContext()) }

    // Domain Algorithm
    single<CyclePredictionEngine> { CyclePredictionEngine }

    // Use Cases
    factory<AuthUseCases> { AuthUseCases(get<UserProfileRepository>()) }
    factory<CycleUseCases> { 
        CycleUseCases(
            cycleRepository = get(),
            predictionRepository = get(),
            userProfileRepository = get()
        ) 
    }
    factory<SymptomUseCases> { 
        SymptomUseCases(
            symptomRepository = get(),
            cycleRepository = get()
        ) 
    }
    factory<PredictionUseCases> { 
        PredictionUseCases(get<PredictionRepository>()) 
    }
    factory<PregnancyUseCases> { 
        PregnancyUseCases(get<PregnancyRepository>()) 
    }
    factory<PartnerUseCases> { 
        PartnerUseCases(get<PartnerRepository>()) 
    }
    factory<SubscriptionUseCases> { 
        SubscriptionUseCases(
            subscriptionRepository = get(),
            userProfileRepository = get(),
            billingClient = get() // Provided by Hilt
        ) 
    }
    factory<ExportUseCases> { 
        ExportUseCases(
            exportRepository = get(),
            cycleRepository = get(),
            symptomRepository = get(),
            predictionRepository = get(),
            pregnancyRepository = get(),
            partnerRepository = get(),
            healthDataRepository = get(),
            userProfileRepository = get(),
            subscriptionRepository = get()
        ) 
    }
}

/**
 * Inicializa Koin (chamado no Application.onCreate).
 */
fun startKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(appModule)
    }
}

/**
 * Implementações Realm das interfaces de repositório.
 * Estas classes adaptam os DAOs Realm para as interfaces de domínio.
 */

class RealmCycleRepository(private val dao: CycleDao) : CycleRepository {
    override fun getCycles(userId: String) = dao.getCycles(userId).asFlow()
    override suspend fun getCycle(cycleId: String) = dao.getCycle(cycleId)
    override suspend fun getLatestCycle(userId: String) = dao.getLatestCycle(userId)
    override suspend fun getCyclesInRange(userId: String, startDate: Long, endDate: Long) = 
        dao.getCyclesInRange(userId, startDate, endDate)
    override suspend fun getPredictedCycles(userId: String) = dao.getPredictedCycles(userId)
    override suspend fun getCyclesNeedingSync(userId: String) = dao.getCyclesNeedingSync(userId)
    override suspend fun upsertCycle(cycle: Cycle) = dao.upsertCycle(cycle.toRealm())
    override suspend fun upsertCycles(cycles: List<Cycle>) = dao.upsertCycles(cycles.map { it.toRealm() })
    override suspend fun deleteCycle(cycleId: String) = dao.deleteCycle(cycleId)
    override suspend fun hardDeleteCycle(cycleId: String) = dao.hardDeleteCycle(cycleId)
    override suspend fun getCycleStats(userId: String) = dao.getCycleStats(userId)
    override suspend fun getCycleCount(userId: String) = dao.getCycleCount(userId)
    override suspend fun hasCycleOnDate(userId: String, date: Long) = dao.hasCycleOnDate(userId, date)
    override suspend fun getCyclesForExport(userId: String, startDate: Long, endDate: Long, anonymize: Boolean) =
        dao.getCyclesInRange(userId, startDate, endDate).map { it.toDomain() }
}

class RealmSymptomRepository(private val dao: SymptomDao) : SymptomRepository {
    override fun getSymptoms(userId: String) = dao.getSymptoms(userId).asFlow()
    override suspend fun getSymptomsForCycle(cycleId: String) = dao.getSymptomsForCycle(cycleId)
    override suspend fun getSymptomsForDate(userId: String, date: Long) = dao.getSymptomsForDate(userId, date)
    override suspend fun getSymptomsInRange(userId: String, startDate: Long, endDate: Long) = 
        dao.getSymptomsInRange(userId, startDate, endDate)
    override suspend fun getSymptomsByCategory(userId: String, category: String) = 
        dao.getSymptomsByCategory(userId, category)
    override suspend fun getSymptomsNeedingSync(userId: String) = dao.getSymptomsNeedingSync(userId)
    override suspend fun upsertSymptom(symptom: Symptom) = dao.upsertSymptom(symptom.toRealm())
    override suspend fun upsertSymptoms(symptoms: List<Symptom>) = dao.upsertSymptoms(symptoms.map { it.toRealm() })
    override suspend fun deleteSymptom(symptomId: String) = dao.deleteSymptom(symptomId)
    override suspend fun hardDeleteSymptom(symptomId: String) = dao.hardDeleteSymptom(symptomId)
    override suspend fun getSymptomFrequency(userId: String, startDate: Long, endDate: Long) = 
        dao.getSymptomFrequency(userId, startDate, endDate)
    override suspend fun getSymptomSeverityDistribution(userId: String, symptomName: String, startDate: Long, endDate: Long) = 
        dao.getSymptomSeverityDistribution(userId, symptomName, startDate, endDate)
    override suspend fun getMostCommonSymptoms(userId: String, limit: Int, startDate: Long, endDate: Long) = 
        dao.getMostCommonSymptoms(userId, limit, startDate, endDate)
    override suspend fun getSymptomCount(userId: String) = dao.getSymptomCount(userId)
    override suspend fun hasSymptomOnDate(userId: String, date: Long, symptomName: String) = 
        dao.hasSymptomOnDate(userId, date, symptomName)
    override suspend fun getSymptomsForExport(userId: String, startDate: Long, endDate: Long, anonymize: Boolean) =
        dao.getSymptomsInRange(userId, startDate, endDate).map { it.toDomain() }
}

class RealmUserProfileRepository(private val dao: UserProfileDao) : UserProfileRepository {
    override suspend fun getProfile(userId: String) = dao.getProfile(userId)?.toDomain()
    override suspend fun getProfileByEmail(email: String) = dao.getProfileByEmail(email)?.toDomain()
    override suspend fun upsertProfile(profile: UserProfile) = dao.upsertProfile(profile.toRealm())
    override suspend fun updateProfile(userId: String, updateBlock: (UserProfile) -> Unit) = 
        dao.updateProfile(userId) { realmProfile ->
            val domain = realmProfile.toDomain()
            updateBlock(domain)
            realmProfile.updateFromDomain(domain)
        }
    override suspend fun deleteProfile(userId: String) = dao.deleteProfile(userId)
    override suspend fun hardDeleteProfile(userId: String) = dao.hardDeleteProfile(userId)
    override suspend fun updateSubscriptionStatus(userId: String, status: String, productId: String, expiresAt: Long, autoRenew: Boolean, platform: String) =
        dao.updateSubscriptionStatus(userId, status, productId, expiresAt, autoRenew, platform)
    override suspend fun updatePartnerInfo(userId: String, partnerId: String, shareLevel: String, invitationSent: Boolean) =
        dao.updatePartnerInfo(userId, partnerId, shareLevel, invitationSent)
    override suspend fun updateLastLogin(userId: String) = dao.updateLastLogin(userId)
    override suspend fun updateCycleCharacteristics(userId: String, averageCycleLength: Int, averagePeriodLength: Int, averageLutealPhaseLength: Int, cycleRegularity: String, lastPeriodDate: Long) =
        dao.updateCycleCharacteristics(userId, averageCycleLength, averagePeriodLength, averageLutealPhaseLength, cycleRegularity, lastPeriodDate)
    override suspend fun updatePregnancyStatus(userId: String, status: String, dueDate: Long, startDate: Long) =
        dao.updatePregnancyStatus(userId, status, dueDate, startDate)
    override suspend fun updateNotificationPreferences(userId: String, periodReminderEnabled: Boolean?, periodReminderDaysBefore: Int?, fertileWindowReminderEnabled: Boolean?, ovulationReminderEnabled: Boolean?, pillReminderEnabled: Boolean?, pillReminderTime: String?) =
        dao.updateNotificationPreferences(userId, periodReminderEnabled, periodReminderDaysBefore, fertileWindowReminderEnabled, ovulationReminderEnabled, pillReminderEnabled, pillReminderTime)
    override suspend fun updateDisplayPreferences(userId: String, themeMode: String?, language: String?, temperatureUnit: String?, weightUnit: String?, heightUnit: String?, firstDayOfWeek: Int?, calendarViewMode: String?) =
        dao.updateDisplayPreferences(userId, themeMode, language, temperatureUnit, weightUnit, heightUnit, firstDayOfWeek, calendarViewMode)
    override suspend fun updateConsentPreferences(userId: String, analyticsOptIn: Boolean?, crashReportingOptIn: Boolean?, marketingOptIn: Boolean?, healthConnectEnabled: Boolean?, dataSharingConsentVersion: String?) =
        dao.updateConsentPreferences(userId, analyticsOptIn, crashReportingOptIn, marketingOptIn, healthConnectEnabled, dataSharingConsentVersion)
    override suspend fun updateSecuritySettings(userId: String, biometricEnabled: Boolean?, autoLockEnabled: Boolean?, autoLockTimeoutMinutes: Int?) =
        dao.updateSecuritySettings(userId, biometricEnabled, autoLockEnabled, autoLockTimeoutMinutes)
    override suspend fun profileExists(userId: String) = dao.profileExists(userId)
    override suspend fun emailExists(email: String) = dao.emailExists(email)
    override suspend fun getSubscriptionDisplayStatus(userId: String) = dao.getSubscriptionDisplayStatus(userId)
    override suspend fun getProfileForExport(userId: String, anonymize: Boolean) = dao.getProfile(userId)?.toDomain()
}

class RealmPredictionRepository(private val dao: PredictionDao) : PredictionRepository {
    override suspend fun getPredictions(userId: String) = dao.getPredictions(userId)
    override suspend fun getPredictionsByType(userId: String, type: com.seuapp.cycletrack.domain.model.PredictionType) = 
        dao.getPredictionsByType(userId, type.key)
    override suspend fun getUpcomingPredictions(userId: String, fromDate: Long) = 
        dao.getUpcomingPredictions(userId, fromDate)
    override suspend fun getNextPeriodStart(userId: String) = dao.getNextPeriodStart(userId)?.toDomain()
    override suspend fun getNextOvulation(userId: String) = dao.getNextOvulation(userId)?.toDomain()
    override suspend fun getNextFertileWindow(userId: String) = 
        dao.getNextFertileWindow(userId).map { (first, second) -> Pair(first?.toDomain(), second?.toDomain()) }
    override suspend fun getPredictionsNeedingNotification(userId: String, beforeMillis: Long) = 
        dao.getPredictionsNeedingNotification(userId, beforeMillis)
    override suspend fun getExpiredPredictions(userId: String, beforeDate: Long) = 
        dao.getExpiredPredictions(userId, beforeDate)
    override suspend fun upsertPrediction(prediction: com.seuapp.cycletrack.domain.model.Prediction) = 
        dao.upsertPrediction(prediction.toRealm())
    override suspend fun upsertPredictions(predictions: List<com.seuapp.cycletrack.domain.model.Prediction>) = 
        dao.upsertPredictions(predictions.map { it.toRealm() })
    override suspend fun deletePrediction(predictionId: String) = dao.deletePrediction(predictionId)
    override suspend fun deleteAllPredictions(userId: String) = dao.deleteAllPredictions(userId)
    override suspend fun markAsNotified(predictionId: String) = dao.markAsNotified(predictionId)
    override suspend fun updateActualDate(predictionId: String, actualDate: Long) = dao.updateActualDate(predictionId, actualDate)
    override suspend fun getAccuracyStats(userId: String, type: com.seuapp.cycletrack.domain.model.PredictionType) = 
        dao.getAccuracyStats(userId, type.key)
    override suspend fun regeneratePredictions(userId: String) = dao.regeneratePredictions(userId)
    override suspend fun getPredictionsForExport(userId: String, startDate: Long, endDate: Long, anonymize: Boolean) =
        dao.getPredictions(userId).filter { it.predictedDate >= startDate && it.predictedDate <= endDate }.map { it.toDomain() }
}

class RealmPregnancyRepository(private val dao: PregnancyDao) : PregnancyRepository {
    override suspend fun getActivePregnancy(userId: String) = dao.getActivePregnancy(userId)?.toDomain()
    override suspend fun getPregnancies(userId: String) = dao.getPregnancies(userId).map { it.toDomain() }
    override suspend fun getPregnancy(pregnancyId: String) = dao.getPregnancy(pregnancyId)?.toDomain()
    override suspend fun upsertPregnancy(pregnancy: com.seuapp.cycletrack.domain.model.Pregnancy) = 
        dao.upsertPregnancy(pregnancy.toRealm())
    override suspend fun deletePregnancy(pregnancyId: String) = dao.deletePregnancy(pregnancyId)
    override suspend fun hardDeletePregnancy(pregnancyId: String) = dao.hardDeletePregnancy(pregnancyId)
    override suspend fun updateCurrentWeek(pregnancyId: String) = dao.updateCurrentWeek(pregnancyId)
    override suspend fun getAppointments(pregnancyId: String) = dao.getAppointments(pregnancyId).map { it.toDomain() }
    override suspend fun getUpcomingAppointments(pregnancyId: String, fromDate: Long) = 
        dao.getUpcomingAppointments(pregnancyId, fromDate).map { it.toDomain() }
    override suspend fun upsertAppointment(appointment: PregnancyAppointment) = 
        dao.upsertAppointment(appointment.toRealm())
    override suspend fun deleteAppointment(appointmentId: String) = dao.deleteAppointment(appointmentId)
    override suspend fun getTests(pregnancyId: String) = dao.getTests(pregnancyId).map { it.toDomain() }
    override suspend fun upsertTest(test: PregnancyTest) = dao.upsertTest(test.toRealm())
    override suspend fun deleteTest(testId: String) = dao.deleteTest(testId)
    override suspend fun completePregnancy(pregnancyId: String, outcome: PregnancyOutcome, outcomeDate: Long, outcomeNotes: String, babyName: String, babyGender: String, babyWeightGrams: Int, babyHeightCm: Float, babyApgarScore: Int) =
        dao.completePregnancy(pregnancyId, outcome.key, outcomeDate, outcomeNotes, babyName, babyGender, babyWeightGrams, babyHeightCm, babyApgarScore)
    override suspend fun markPregnancyLost(pregnancyId: String, outcome: PregnancyOutcome, outcomeDate: Long, notes: String) =
        dao.markPregnancyLost(pregnancyId, outcome.key, outcomeDate, notes)
    override suspend fun getPregnancyForExport(userId: String, pregnancyId: String, anonymize: Boolean) = 
        dao.getPregnancy(pregnancyId)?.toDomain()
}

class RealmPartnerRepository(private val dao: PartnerDao) : PartnerRepository {
    override suspend fun getActivePartner(userId: String) = dao.getActivePartner(userId)?.toDomain()
    override suspend fun getPartners(userId: String) = dao.getPartners(userId).map { it.toDomain() }
    override suspend fun getPartner(partnerId: String) = dao.getPartner(partnerId)?.toDomain()
    override suspend fun getPartnerByInvitationCode(code: String) = dao.getPartnerByInvitationCode(code)?.toDomain()
    override suspend fun getPendingInvitationsSent(userId: String) = dao.getPendingInvitationsSent(userId).map { it.toDomain() }
    override suspend fun getPendingInvitationsReceived(email: String) = dao.getPendingInvitationsReceived(email).map { it.toDomain() }
    override suspend fun upsertPartner(partner: PartnerConnection) = dao.upsertPartner(partner.toRealm())
    override suspend fun createInvitation(userId: String, partnerEmail: String, partnerDisplayName: String, shareLevel: com.seuapp.cycletrack.domain.model.PartnerShareLevel) = 
        dao.createInvitation(userId, partnerEmail, partnerDisplayName, shareLevel.key)
    override suspend fun acceptInvitation(invitationCode: String, partnerUserId: String, partnerDisplayName: String, partnerAvatarUrl: String) = 
        dao.acceptInvitation(invitationCode, partnerUserId, partnerDisplayName, partnerAvatarUrl)?.toDomain()
    override suspend fun declineInvitation(invitationCode: String) = dao.declineInvitation(invitationCode)
    override suspend fun revokePartner(userId: String, partnerId: String) = dao.revokePartner(userId, partnerId)
    override suspend fun updateSharingPreferences(partnerId: String, shareCycle: Boolean?, sharePeriodDates: Boolean?, shareOvulation: Boolean?, shareFertileWindow: Boolean?, sharePms: Boolean?, shareSymptoms: Boolean?, sharePredictions: Boolean?, sharePregnancy: Boolean?, shareInsights: Boolean?, shareMood: Boolean?, shareWeight: Boolean?, shareNotes: Boolean?) =
        dao.updateSharingPreferences(partnerId, shareCycle, sharePeriodDates, shareOvulation, shareFertileWindow, sharePms, shareSymptoms, sharePredictions, sharePregnancy, shareInsights, shareMood, shareWeight, shareNotes)
    override suspend fun updateNotificationPreferences(partnerId: String, notifyPeriodStart: Boolean?, notifyPeriodEnd: Boolean?, notifyFertileWindow: Boolean?, notifyOvulation: Boolean?, notifyPms: Boolean?, notifyPregnancy: Boolean?, notifySymptoms: Boolean?) =
        dao.updateNotificationPreferences(partnerId, notifyPeriodStart, notifyPeriodEnd, notifyFertileWindow, notifyOvulation, notifyPms, notifyPregnancy, notifySymptoms)
    override suspend fun updatePartnerSharingPreferences(partnerId: String, shareCycle: Boolean?, sharePeriodDates: Boolean?, shareOvulation: Boolean?, shareFertileWindow: Boolean?, sharePms: Boolean?, shareSymptoms: Boolean?, sharePredictions: Boolean?, sharePregnancy: Boolean?, shareInsights: Boolean?, shareMood: Boolean?, shareWeight: Boolean?, shareNotes: Boolean?) =
        dao.updatePartnerSharingPreferences(partnerId, shareCycle, sharePeriodDates, shareOvulation, shareFertileWindow, sharePms, shareSymptoms, sharePredictions, sharePregnancy, shareInsights, shareMood, shareWeight, shareNotes)
    override suspend fun updatePartnerDisplayInfo(partnerId: String, displayName: String?, avatarUrl: String?) = 
        dao.updatePartnerDisplayInfo(partnerId, displayName, avatarUrl)
    override suspend fun deletePartner(partnerId: String) = dao.deletePartner(partnerId)
    override suspend fun hardDeletePartner(partnerId: String) = dao.hardDeletePartner(partnerId)
    override suspend fun getPartnerSharedData(partnerId: String) = dao.getPartnerSharedData(partnerId)
    override suspend fun isInvitationExpired(invitationCode: String) = dao.isInvitationExpired(invitationCode)
    override suspend fun getPartnerForExport(userId: String, partnerId: String, anonymize: Boolean) = 
        dao.getPartner(partnerId)?.toDomain()
}

class RealmSubscriptionRepository(private val dao: SubscriptionDao) : SubscriptionRepository {
    override suspend fun getActiveSubscription(userId: String) = dao.getActiveSubscription(userId)?.toDomain()
    override suspend fun getSubscriptions(userId: String) = dao.getSubscriptions(userId).map { it.toDomain() }
    override suspend fun getSubscriptionByPurchaseToken(purchaseToken: String) = dao.getSubscriptionByPurchaseToken(purchaseToken)?.toDomain()
    override suspend fun getSubscriptionByOrderId(orderId: String) = dao.getSubscriptionByOrderId(orderId)?.toDomain()
    override suspend fun getSubscription(subscriptionId: String) = dao.getSubscription(subscriptionId)?.toDomain()
    override suspend fun getSubscriptionsNeedingAcknowledgment(userId: String) = dao.getSubscriptionsNeedingAcknowledgment(userId).map { it.toDomain() }
    override suspend fun getExpiredSubscriptions(userId: String, beforeDate: Long) = dao.getExpiredSubscriptions(userId, beforeDate).map { it.toDomain() }
    override suspend fun getSubscriptionsInGracePeriod(userId: String) = dao.getSubscriptionsInGracePeriod(userId).map { it.toDomain() }
    override suspend fun getSubscriptionsOnAccountHold(userId: String) = dao.getSubscriptionsOnAccountHold(userId).map { it.toDomain() }
    override suspend fun upsertSubscription(subscription: Subscription) = dao.upsertSubscription(subscription.toRealm())
    override suspend fun createSubscription(userId: String, productId: String, purchaseToken: String, orderId: String, purchaseTime: Long, expiryTime: Long, priceAmountMicros: Long, priceCurrencyCode: String, priceFormatted: String, isTrial: Boolean, trialStartTime: Long, trialEndTime: Long, platform: String, platformData: String) = 
        dao.createSubscription(userId, productId, purchaseToken, orderId, purchaseTime, expiryTime, priceAmountMicros, priceCurrencyCode, priceFormatted, isTrial, trialStartTime, trialEndTime, platform, platformData)
    override suspend fun updateStatus(subscriptionId: String, status: com.seuapp.cycletrack.domain.model.SubscriptionStatus) = 
        dao.updateStatus(subscriptionId, status.key)
    override suspend fun updateExpiryTime(subscriptionId: String, expiryTime: Long) = dao.updateExpiryTime(subscriptionId, expiryTime)
    override suspend fun updateAutoRenewing(subscriptionId: String, autoRenewing: Boolean) = dao.updateAutoRenewing(subscriptionId, autoRenewing)
    override suspend fun markAcknowledged(subscriptionId: String) = dao.markAcknowledged(subscriptionId)
    override suspend fun updateGracePeriodExpiry(subscriptionId: String, gracePeriodExpiryTime: Long) = dao.updateGracePeriodExpiry(subscriptionId, gracePeriodExpiryTime)
    override suspend fun updateAccountHoldExpiry(subscriptionId: String, accountHoldExpiryTime: Long) = dao.updateAccountHoldExpiry(subscriptionId, accountHoldExpiryTime)
    override suspend fun recordNotification(subscriptionId: String, notificationType: String) = dao.recordNotification(subscriptionId, notificationType)
    override suspend fun processNotification(purchaseToken: String, notificationType: String, expiryTime: Long?, autoRenewing: Boolean?, gracePeriodExpiryTime: Long?, accountHoldExpiryTime: Long?) = 
        dao.processNotification(purchaseToken, notificationType, expiryTime, autoRenewing, gracePeriodExpiryTime, accountHoldExpiryTime)
    override suspend fun deleteSubscription(subscriptionId: String) = dao.deleteSubscription(subscriptionId)
    override suspend fun hardDeleteSubscription(subscriptionId: String) = dao.hardDeleteSubscription(subscriptionId)
    override suspend fun hasActivePremium(userId: String) = dao.hasActivePremium(userId)
    override suspend fun isInTrial(userId: String) = dao.isInTrial(userId)
    override suspend fun getDisplayStatus(userId: String) = dao.getDisplayStatus(userId)
    override suspend fun getSubscriptionForExport(userId: String, subscriptionId: String, anonymize: Boolean) = 
        dao.getSubscription(subscriptionId)?.toDomain()
}

class RealmContentRepository(private val dao: ContentDao) : ContentRepository {
    override suspend fun getContent(language: String, region: String, includeExpired: Boolean) = 
        dao.getContent(language, region, includeExpired).map { it.toDomain() }
    override suspend fun getContentByCategory(category: String, language: String, region: String) = 
        dao.getContentByCategory(category, language, region).map { it.toDomain() }
    override suspend fun getContentById(contentId: String) = dao.getContentById(contentId)?.toDomain()
    override suspend fun getContentForCyclePhase(phase: String, language: String, region: String) = 
        dao.getContentForCyclePhase(phase, language, region).map { it.toDomain() }
    override suspend fun getContentForPregnancyWeek(week: Int, language: String, region: String) = 
        dao.getContentForPregnancyWeek(week, language, region).map { it.toDomain() }
    override suspend fun getPremiumContent(language: String, region: String) = 
        dao.getPremiumContent(language, region).map { it.toDomain() }
    override suspend fun getBookmarkedContent(language: String, region: String) = 
        dao.getBookmarkedContent(language, region).map { it.toDomain() }
    override suspend fun getUnreadContent(language: String, region: String) = 
        dao.getUnreadContent(language, region).map { it.toDomain() }
    override suspend fun upsertContent(content: com.seuapp.cycletrack.domain.model.Content) = 
        dao.upsertContent(content.toRealm())
    override suspend fun upsertContentList(contents: List<com.seuapp.cycletrack.domain.model.Content>) = 
        dao.upsertContentList(contents.map { it.toRealm() })
    override suspend fun toggleBookmark(contentId: String) = dao.toggleBookmark(contentId)
    override suspend fun markAsRead(contentId: String, progress: Float) = dao.markAsRead(contentId, progress)
    override suspend fun incrementViewCount(contentId: String) = dao.incrementViewCount(contentId)
    override suspend fun incrementLikeCount(contentId: String) = dao.incrementLikeCount(contentId)
    override suspend fun incrementShareCount(contentId: String) = dao.incrementShareCount(contentId)
    override suspend fun deleteExpiredContent() = dao.deleteExpiredContent()
    override suspend fun searchContent(query: String, language: String, region: String, limit: Int) = 
        dao.searchContent(query, language, region, limit).map { it.toDomain() }
}

class RealmHealthDataPointRepository(private val dao: HealthDataPointDao) : HealthDataPointRepository {
    override suspend fun getHealthDataPoints(userId: String) = dao.getHealthDataPoints(userId).map { it.toDomain() }
    override suspend fun getHealthDataPointsByType(userId: String, dataType: String) = 
        dao.getHealthDataPointsByType(userId, dataType).map { it.toDomain() }
    override suspend fun getHealthDataPointsInRange(userId: String, startDate: Long, endDate: Long) = 
        dao.getHealthDataPointsInRange(userId, startDate, endDate).map { it.toDomain() }
    override suspend fun getHealthDataPointsByTypeAndRange(userId: String, dataType: String, startDate: Long, endDate: Long) = 
        dao.getHealthDataPointsByTypeAndRange(userId, dataType, startDate, endDate).map { it.toDomain() }
    override suspend fun getPendingSyncToHealthConnect(userId: String) = 
        dao.getPendingSyncToHealthConnect(userId).map { it.toDomain() }
    override suspend fun getFromHealthConnectNeedingMerge(userId: String) = 
        dao.getFromHealthConnectNeedingMerge(userId).map { it.toDomain() }
    override suspend fun getHealthDataPoint(dataPointId: String) = dao.getHealthDataPoint(dataPointId)?.toDomain()
    override suspend fun getByHealthConnectRecordId(userId: String, recordId: String) = 
        dao.getByHealthConnectRecordId(userId, recordId)?.toDomain()
    override suspend fun upsertHealthDataPoint(dataPoint: HealthDataPoint) = 
        dao.upsertHealthDataPoint(dataPoint.toRealm())
    override suspend fun upsertHealthDataPoints(dataPoints: List<HealthDataPoint>) = 
        dao.upsertHealthDataPoints(dataPoints.map { it.toRealm() })
    override suspend fun updateSyncStatus(dataPointId: String, status: String, error: String?, conflictResolution: String?) = 
        dao.updateSyncStatus(dataPointId, status, error, conflictResolution)
    override suspend fun bulkUpdateSyncStatus(dataPointIds: List<String>, status: String) = 
        dao.bulkUpdateSyncStatus(dataPointIds, status)
    override suspend fun resolveConflictLocalWins(dataPointId: String) = dao.resolveConflictLocalWins(dataPointId)
    override suspend fun resolveConflictRemoteWins(dataPointId: String, remoteData: HealthDataPoint) = 
        dao.resolveConflictRemoteWins(dataPointId, remoteData.toRealm())
    override suspend fun deleteHealthDataPoint(dataPointId: String) = dao.deleteHealthDataPoint(dataPointId)
    override suspend fun hardDeleteHealthDataPoint(dataPointId: String) = dao.hardDeleteHealthDataPoint(dataPointId)
    override suspend fun hardDeleteAllForUser(userId: String) = dao.hardDeleteAllForUser(userId)
    override suspend fun getMenstrualFlows(userId: String, startDate: Long, endDate: Long) = 
        dao.getMenstrualFlows(userId, startDate, endDate).map { it.toDomain() }
    override suspend fun getBasalBodyTemperatures(userId: String, startDate: Long, endDate: Long) = 
        dao.getBasalBodyTemperatures(userId, startDate, endDate).map { it.toDomain() }
    override suspend fun getOvulationTests(userId: String, startDate: Long, endDate: Long) = 
        dao.getOvulationTests(userId, startDate, endDate).map { it.toDomain() }
    override suspend fun getCountByType(userId: String) = dao.getCountByType(userId)
    override suspend fun getHealthDataForExport(userId: String, startDate: Long, endDate: Long, anonymize: Boolean) = 
        dao.getHealthDataPointsInRange(userId, startDate, endDate).map { it.toDomain() }
}

class RealmNotificationRepository(private val dao: NotificationDao) : NotificationRepository {
    override suspend fun getNotifications(userId: String) = dao.getNotifications(userId).map { it.toDomain() }
    override suspend fun getPendingNotifications(userId: String, beforeTime: Long) = 
        dao.getPendingNotifications(userId, beforeTime).map { it.toDomain() }
    override suspend fun getNotificationsByType(userId: String, type: String) = 
        dao.getNotificationsByType(userId, type).map { it.toDomain() }
    override suspend fun getNotificationsByStatus(userId: String, status: String) = 
        dao.getNotificationsByStatus(userId, status).map { it.toDomain() }
    override suspend fun getRecurringNotifications(userId: String) = 
        dao.getRecurringNotifications(userId).map { it.toDomain() }
    override suspend fun getNotificationsNeedingRetry(beforeTime: Long) = 
        dao.getNotificationsNeedingRetry(beforeTime).map { it.toDomain() }
    override suspend fun getNotification(notificationId: String) = dao.getNotification(notificationId)?.toDomain()
    override suspend fun upsertNotification(notification: com.seuapp.cycletrack.domain.model.Notification) = 
        dao.upsertNotification(notification.toRealm())
    override suspend fun upsertNotifications(notifications: List<com.seuapp.cycletrack.domain.model.Notification>) = 
        dao.upsertNotifications(notifications.map { it.toRealm() })
    override suspend fun markAsSent(notificationId: String) = dao.markAsSent(notificationId)
    override suspend fun markAsDelivered(notificationId: String) = dao.markAsDelivered(notificationId)
    override suspend fun markAsOpened(notificationId: String) = dao.markAsOpened(notificationId)
    override suspend fun markAsDismissed(notificationId: String) = dao.markAsDismissed(notificationId)
    override suspend fun markAsFailed(notificationId: String, reason: String) = dao.markAsFailed(notificationId, reason)
    override suspend fun cancelNotification(notificationId: String) = dao.cancelNotification(notificationId)
    override suspend fun cancelNotificationsByType(userId: String, type: String) = dao.cancelNotificationsByType(userId, type)
    override suspend fun cleanupOldNotifications(olderThan: Long) = dao.cleanupOldNotifications(olderThan)
    override suspend fun deleteFailedNotifications() = dao.deleteFailedNotifications()
    override suspend fun deleteNotification(notificationId: String) = dao.deleteNotification(notificationId)
    override suspend fun hardDeleteNotification(notificationId: String) = dao.hardDeleteNotification(notificationId)
    override suspend fun getNotificationStats(userId: String) = dao.getNotificationStats(userId)
}

class RealmExportRepository(private val dao: ExportDao) : ExportRepository {
    override suspend fun getExports(userId: String) = dao.getExports(userId).map { it.toDomain() }
    override suspend fun getExportsByStatus(userId: String, status: String) = 
        dao.getExportsByStatus(userId, status).map { it.toDomain() }
    override suspend fun getPendingExports() = dao.getPendingExports().map { it.toDomain() }
    override suspend fun getExport(exportId: String) = dao.getExport(exportId)?.toDomain()
    override suspend fun createExport(userId: String, format: String, includeCycles: Boolean, includeSymptoms: Boolean, includePredictions: Boolean, includePregnancy: Boolean, includePartnerData: Boolean, includeHealthConnectData: Boolean, includeSettings: Boolean, includeContent: Boolean, dateRangeStart: Long, dateRangeEnd: Long, anonymize: Boolean, requestedFrom: String, ipAddress: String, userAgent: String, legalBasis: String, retentionDays: Int) = 
        dao.createExport(userId, format, includeCycles, includeSymptoms, includePredictions, includePregnancy, includePartnerData, includeHealthConnectData, includeSettings, includeContent, dateRangeStart, dateRangeEnd, anonymize, requestedFrom, ipAddress, userAgent, legalBasis, retentionDays)
    override suspend fun markAsProcessing(exportId: String) = dao.markAsProcessing(exportId)
    override suspend fun updateProgress(exportId: String, progress: Int) = dao.updateProgress(exportId, progress)
    override suspend fun markAsCompleted(exportId: String, filePath: String, fileSize: Long, fileHash: String, downloadUrl: String) = 
        dao.markAsCompleted(exportId, filePath, fileSize, fileHash, downloadUrl)
    override suspend fun markAsFailed(exportId: String, errorMessage: String) = dao.markAsFailed(exportId, errorMessage)
    override suspend fun markAsExpired(exportId: String) = dao.markAsExpired(exportId)
    override suspend fun getExpiredExports() = dao.getExpiredExports().map { it.toDomain() }
    override suspend fun cleanupExpiredExports() = dao.cleanupExpiredExports()
    override suspend fun deleteExport(exportId: String) = dao.deleteExport(exportId)
    override suspend fun getExportStats(userId: String) = dao.getExportStats(userId)
}

class RoomAnalyticsRepository(
    private val eventDao: AnalyticsEventDao,
    private val sessionDao: UserSessionDao
) : AnalyticsRepository {
    override suspend fun logEvent(event: AnalyticsEvent) = eventDao.insert(event)
    override suspend fun logEvents(events: List<AnalyticsEvent>) = eventDao.insertAll(events)
    override suspend fun getPendingEvents(limit: Int) = eventDao.getPendingEvents(limit)
    override suspend fun getFailedEvents(maxRetries: Int, limit: Int) = eventDao.getFailedEvents(maxRetries, limit)
    override suspend fun markEventsAsSent(eventIds: List<Long>) = eventDao.markAsSent(eventIds, System.currentTimeMillis())
    override suspend fun markEventsAsFailed(eventIds: List<Long>) = eventDao.markAsFailed(eventIds)
    override suspend fun deleteOldSentEvents(beforeTime: Long) = eventDao.deleteOldSentEvents(beforeTime)
    override suspend fun deleteAllEventsForUser(userId: String) = eventDao.deleteAllForUser(userId)
    override suspend fun countPendingEvents() = eventDao.countPendingEvents()
    override suspend fun startSession(session: UserSession) = sessionDao.insert(session)
    override suspend fun endSession(sessionId: String, endTime: Long, durationMs: Long, screenViews: Int, interactions: Int, crashes: Int, anrs: Int, wasBackgrounded: Boolean, backgroundCount: Int, totalBackgroundTimeMs: Long) = 
        sessionDao.updateSession(sessionId, endTime, durationMs, screenViews, interactions, crashes, anrs, wasBackgrounded, backgroundCount, totalBackgroundTimeMs)
    override suspend fun getPendingSessions(limit: Int) = sessionDao.getPendingSessions(limit)
    override suspend fun getFailedSessions(limit: Int) = sessionDao.getFailedSessions(limit)
    override suspend fun markSessionsAsSent(sessionIds: List<Long>) = sessionDao.markAsSent(sessionIds, System.currentTimeMillis())
    override suspend fun markSessionsAsFailed(sessionIds: List<Long>) = sessionDao.markAsFailed(sessionIds)
    override suspend fun deleteOldSentSessions(beforeTime: Long) = sessionDao.deleteOldSentSessions(beforeTime)
    override suspend fun deleteAllSessionsForUser(userId: String) = sessionDao.deleteAllForUser(userId)
    override suspend fun getSessionStats(userId: String, since: Long) = sessionDao.getSessionStats(userId, since)
    override suspend fun getActiveUsersCount(since: Long) = sessionDao.getActiveUsersCount(since)
    override suspend fun getAverageSessionDuration(since: Long) = sessionDao.getAverageSessionDuration(since)
    override fun getEventsFlow(userId: String, limit: Int) = eventDao.getEventsFlow(userId, limit)
    override fun getPendingEventsFlow(limit: Int) = eventDao.getPendingEventsFlow(limit)
    override fun getSessionsFlow(userId: String, limit: Int) = sessionDao.getSessionsFlow(userId, limit)
    override fun getPendingSessionsFlow(limit: Int) = sessionDao.getPendingSessionsFlow(limit)
}