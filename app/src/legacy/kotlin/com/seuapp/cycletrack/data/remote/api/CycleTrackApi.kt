package com.seuapp.cycletrack.data.remote.api

import com.seuapp.cycletrack.data.remote.dto.request.*
import com.seuapp.cycletrack.data.remote.dto.response.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface da API REST do CycleTrack.
 * Clean-room - contrato próprio, não copia Flo.
 */
interface CycleTrackApi {

    // ============ Auth ============
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") authHeader: String): Response<EmptyResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<EmptyResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<EmptyResponse>

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<EmptyResponse>

    @POST("auth/resend-verification")
    suspend fun resendVerification(@Header("Authorization") authHeader: String): Response<EmptyResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") authHeader: String): Response<UserProfileResponse>

    // ============ Cycles ============
    @GET("cycles")
    suspend fun getCycles(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<CycleListResponse>

    @POST("cycles")
    suspend fun createCycle(
        @Header("Authorization") authHeader: String,
        @Body request: CreateCycleRequest
    ): Response<CycleResponse>

    @GET("cycles/{cycleId}")
    suspend fun getCycle(
        @Header("Authorization") authHeader: String,
        @Path("cycleId") cycleId: String
    ): Response<CycleResponse>

    @PUT("cycles/{cycleId}")
    suspend fun updateCycle(
        @Header("Authorization") authHeader: String,
        @Path("cycleId") cycleId: String,
        @Body request: UpdateCycleRequest
    ): Response<CycleResponse>

    @DELETE("cycles/{cycleId}")
    suspend fun deleteCycle(
        @Header("Authorization") authHeader: String,
        @Path("cycleId") cycleId: String
    ): Response<EmptyResponse>

    // ============ Symptoms ============
    @GET("symptoms")
    suspend fun getSymptoms(
        @Header("Authorization") authHeader: String,
        @Query("cycle_id") cycleId: String? = null,
        @Query("date") date: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("category") category: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<SymptomListResponse>

    @POST("symptoms")
    suspend fun createSymptom(
        @Header("Authorization") authHeader: String,
        @Body request: CreateSymptomRequest
    ): Response<SymptomResponse>

    @POST("symptoms/batch")
    suspend fun createSymptomsBatch(
        @Header("Authorization") authHeader: String,
        @Body request: CreateSymptomsBatchRequest
    ): Response<SymptomListResponse>

    @PUT("symptoms/{symptomId}")
    suspend fun updateSymptom(
        @Header("Authorization") authHeader: String,
        @Path("symptomId") symptomId: String,
        @Body request: UpdateSymptomRequest
    ): Response<SymptomResponse>

    @DELETE("symptoms/{symptomId}")
    suspend fun deleteSymptom(
        @Header("Authorization") authHeader: String,
        @Path("symptomId") symptomId: String
    ): Response<EmptyResponse>

    // ============ Predictions ============
    @GET("predictions")
    suspend fun getPredictions(
        @Header("Authorization") authHeader: String,
        @Query("type") type: String? = null,
        @Query("upcoming") upcoming: Boolean = true,
        @Query("limit") limit: Int = 50
    ): Response<PredictionListResponse>

    @POST("predictions/regenerate")
    suspend fun regeneratePredictions(
        @Header("Authorization") authHeader: String
    ): Response<PredictionListResponse>

    @PUT("predictions/{predictionId}/actual")
    suspend fun updatePredictionActual(
        @Header("Authorization") authHeader: String,
        @Path("predictionId") predictionId: String,
        @Body request: UpdatePredictionActualRequest
    ): Response<PredictionResponse>

    // ============ Pregnancy ============
    @GET("pregnancies")
    suspend fun getPregnancies(
        @Header("Authorization") authHeader: String
    ): Response<PregnancyListResponse>

    @POST("pregnancies")
    suspend fun createPregnancy(
        @Header("Authorization") authHeader: String,
        @Body request: CreatePregnancyRequest
    ): Response<PregnancyResponse>

    @GET("pregnancies/{pregnancyId}")
    suspend fun getPregnancy(
        @Header("Authorization") authHeader: String,
        @Path("pregnancyId") pregnancyId: String
    ): Response<PregnancyResponse>

    @PUT("pregnancies/{pregnancyId}")
    suspend fun updatePregnancy(
        @Header("Authorization") authHeader: String,
        @Path("pregnancyId") pregnancyId: String,
        @Body request: UpdatePregnancyRequest
    ): Response<PregnancyResponse>

    @DELETE("pregnancies/{pregnancyId}")
    suspend fun deletePregnancy(
        @Header("Authorization") authHeader: String,
        @Path("pregnancyId") pregnancyId: String
    ): Response<EmptyResponse>

    @POST("pregnancies/{pregnancyId}/complete")
    suspend fun completePregnancy(
        @Header("Authorization") authHeader: String,
        @Path("pregnancyId") pregnancyId: String,
        @Body request: CompletePregnancyRequest
    ): Response<PregnancyResponse>

    @GET("pregnancies/{pregnancyId}/appointments")
    suspend fun getAppointments(
        @Header("Authorization") authHeader: String,
        @Path("pregnancyId") pregnancyId: String
    ): Response<AppointmentListResponse>

    @POST("pregnancies/{pregnancyId}/appointments")
    suspend fun createAppointment(
        @Header("Authorization") authHeader: String,
        @Path("pregnancyId") pregnancyId: String,
        @Body request: CreateAppointmentRequest
    ): Response<AppointmentResponse>

    @PUT("appointments/{appointmentId}")
    suspend fun updateAppointment(
        @Header("Authorization") authHeader: String,
        @Path("appointmentId") appointmentId: String,
        @Body request: UpdateAppointmentRequest
    ): Response<AppointmentResponse>

    @DELETE("appointments/{appointmentId}")
    suspend fun deleteAppointment(
        @Header("Authorization") authHeader: String,
        @Path("appointmentId") appointmentId: String
    ): Response<EmptyResponse>

    @GET("pregnancies/{pregnancyId}/tests")
    suspend fun getTests(
        @Header("Authorization") authHeader: String,
        @Path("pregnancyId") pregnancyId: String
    ): Response<TestListResponse>

    @POST("pregnancies/{pregnancyId}/tests")
    suspend fun createTest(
        @Header("Authorization") authHeader: String,
        @Path("pregnancyId") pregnancyId: String,
        @Body request: CreateTestRequest
    ): Response<TestResponse>

    // ============ Partner ============
    @GET("partner")
    suspend fun getPartnerConnection(
        @Header("Authorization") authHeader: String
    ): Response<PartnerConnectionResponse>

    @POST("partner/invite")
    suspend fun invitePartner(
        @Header("Authorization") authHeader: String,
        @Body request: InvitePartnerRequest
    ): Response<PartnerConnectionResponse>

    @POST("partner/accept")
    suspend fun acceptInvitation(
        @Header("Authorization") authHeader: String,
        @Body request: AcceptInvitationRequest
    ): Response<PartnerConnectionResponse>

    @POST("partner/decline")
    suspend fun declineInvitation(
        @Header("Authorization") authHeader: String,
        @Body request: DeclineInvitationRequest
    ): Response<EmptyResponse>

    @POST("partner/revoke")
    suspend fun revokePartner(
        @Header("Authorization") authHeader: String
    ): Response<EmptyResponse>

    @PUT("partner/sharing")
    suspend fun updateSharingPreferences(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateSharingPreferencesRequest
    ): Response<PartnerConnectionResponse>

    @PUT("partner/notifications")
    suspend fun updatePartnerNotifications(
        @Header("Authorization") authHeader: String,
        @Body request: UpdatePartnerNotificationsRequest
    ): Response<PartnerConnectionResponse>

    @GET("partner/shared-data")
    suspend fun getPartnerSharedData(
        @Header("Authorization") authHeader: String
    ): Response<PartnerSharedDataResponse>

    // ============ Subscription ============
    @GET("subscription/status")
    suspend fun getSubscriptionStatus(
        @Header("Authorization") authHeader: String
    ): Response<SubscriptionStatusResponse>

    @GET("subscription/products")
    suspend fun getSubscriptionProducts(
        @Header("Authorization") authHeader: String
    ): Response<SubscriptionProductsResponse>

    @POST("subscription/purchase")
    suspend fun recordPurchase(
        @Header("Authorization") authHeader: String,
        @Body request: RecordPurchaseRequest
    ): Response<SubscriptionResponse>

    @POST("subscription/acknowledge")
    suspend fun acknowledgePurchase(
        @Header("Authorization") authHeader: String,
        @Body request: AcknowledgePurchaseRequest
    ): Response<EmptyResponse>

    @POST("subscription/cancel")
    suspend fun cancelSubscription(
        @Header("Authorization") authHeader: String
    ): Response<SubscriptionResponse>

    @POST("subscription/restore")
    suspend fun restorePurchases(
        @Header("Authorization") authHeader: String
    ): Response<SubscriptionListResponse>

    // ============ Health Connect ============
    @POST("health-connect/sync")
    suspend fun syncHealthData(
        @Header("Authorization") authHeader: String,
        @Body request: SyncHealthDataRequest
    ): Response<SyncHealthDataResponse>

    @GET("health-connect/permissions")
    suspend fun getHealthConnectPermissions(
        @Header("Authorization") authHeader: String
    ): Response<HealthConnectPermissionsResponse>

    // ============ Content ============
    @GET("content")
    suspend fun getContent(
        @Header("Authorization") authHeader: String,
        @Query("category") category: String? = null,
        @Query("cycle_phase") cyclePhase: String? = null,
        @Query("pregnancy_week") pregnancyWeek: Int? = null,
        @Query("language") language: String = "pt_BR",
        @Query("region") region: String = "BR",
        @Query("premium") premium: Boolean = false,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<ContentListResponse>

    @GET("content/{contentId}")
    suspend fun getContentById(
        @Header("Authorization") authHeader: String,
        @Path("contentId") contentId: String
    ): Response<ContentResponse>

    @POST("content/{contentId}/view")
    suspend fun recordContentView(
        @Header("Authorization") authHeader: String,
        @Path("contentId") contentId: String
    ): Response<EmptyResponse>

    @POST("content/{contentId}/bookmark")
    suspend fun toggleBookmark(
        @Header("Authorization") authHeader: String,
        @Path("contentId") contentId: String
    ): Response<ContentResponse>

    @POST("content/{contentId}/read")
    suspend fun markContentRead(
        @Header("Authorization") authHeader: String,
        @Path("contentId") contentId: String,
        @Body request: MarkReadRequest
    ): Response<ContentResponse>

    // ============ Profile/Settings ============
    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateProfileRequest
    ): Response<UserProfileResponse>

    @PUT("profile/cycle-characteristics")
    suspend fun updateCycleCharacteristics(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateCycleCharacteristicsRequest
    ): Response<UserProfileResponse>

    @PUT("profile/contraception")
    suspend fun updateContraception(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateContraceptionRequest
    ): Response<UserProfileResponse>

    @PUT("profile/pregnancy-status")
    suspend fun updatePregnancyStatus(
        @Header("Authorization") authHeader: String,
        @Body request: UpdatePregnancyStatusRequest
    ): Response<UserProfileResponse>

    @PUT("profile/notifications")
    suspend fun updateNotificationPreferences(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateNotificationPreferencesRequest
    ): Response<UserProfileResponse>

    @PUT("profile/display")
    suspend fun updateDisplayPreferences(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateDisplayPreferencesRequest
    ): Response<UserProfileResponse>

    @PUT("profile/consent")
    suspend fun updateConsentPreferences(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateConsentPreferencesRequest
    ): Response<UserProfileResponse>

    @PUT("profile/security")
    suspend fun updateSecuritySettings(
        @Header("Authorization") authHeader: String,
        @Body request: UpdateSecuritySettingsRequest
    ): Response<UserProfileResponse>

    // ============ Export (LGPD/GDPR) ============
    @POST("export")
    suspend fun requestExport(
        @Header("Authorization") authHeader: String,
        @Body request: ExportRequest
    ): Response<ExportResponse>

    @GET("export/{exportId}")
    suspend fun getExportStatus(
        @Header("Authorization") authHeader: String,
        @Path("exportId") exportId: String
    ): Response<ExportResponse>

    @GET("export/{exportId}/download")
    suspend fun downloadExport(
        @Header("Authorization") authHeader: String,
        @Path("exportId") exportId: String
    ): Response<ExportDownloadResponse>

    @DELETE("export/{exportId}")
    suspend fun deleteExport(
        @Header("Authorization") authHeader: String,
        @Path("exportId") exportId: String
    ): Response<EmptyResponse>

    // ============ Analytics ============
    @POST("analytics/events")
    suspend fun sendAnalyticsEvents(
        @Header("Authorization") authHeader: String,
        @Body request: AnalyticsEventsRequest
    ): Response<EmptyResponse>

    @POST("analytics/sessions")
    suspend fun sendAnalyticsSessions(
        @Header("Authorization") authHeader: String,
        @Body request: AnalyticsSessionsRequest
    ): Response<EmptyResponse>

    // ============ Notifications ============
    @POST("notifications/register-token")
    suspend fun registerNotificationToken(
        @Header("Authorization") authHeader: String,
        @Body request: RegisterTokenRequest
    ): Response<EmptyResponse>

    @DELETE("notifications/token")
    suspend fun unregisterNotificationToken(
        @Header("Authorization") authHeader: String
    ): Response<EmptyResponse>

    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") authHeader: String,
        @Query("status") status: String? = null,
        @Query("type") type: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<NotificationListResponse>

    @PUT("notifications/{notificationId}/read")
    suspend fun markNotificationRead(
        @Header("Authorization") authHeader: String,
        @Path("notificationId") notificationId: String
    ): Response<EmptyResponse>

    // ============ Device/Config ============
    @GET("config/app")
    suspend fun getAppConfig(): Response<AppConfigResponse>

    @GET("config/feature-flags")
    suspend fun getFeatureFlags(): Response<FeatureFlagsResponse>

    @POST("device/register")
    suspend fun registerDevice(
        @Header("Authorization") authHeader: String,
        @Body request: RegisterDeviceRequest
    ): Response<EmptyResponse>
}

/**
 * Response wrapper vazio.
 */
data class EmptyResponse(
    val success: Boolean = true,
    val message: String = ""
)