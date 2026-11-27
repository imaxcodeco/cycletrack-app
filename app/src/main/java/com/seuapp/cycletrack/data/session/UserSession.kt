package com.seuapp.cycletrack.data.session

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte única do identificador do usuário autenticado.
 *
 * Todos os dados locais (ciclos, sintomas, perfil, parceiro) são particionados por este
 * id. Antes, um id fixo ("user_123") era usado em todo o app, de modo que contas
 * diferentes no mesmo aparelho compartilhavam os mesmos dados de saúde — uma falha de
 * privacidade. Agora o id é o UID do Firebase do usuário logado.
 *
 * As telas de dados ficam atrás do gate de login (ver CycleTrackApp), então
 * [currentUserId] retorna o UID real; o fallback [ANONYMOUS] é apenas defensivo e não
 * deve ocorrer em uso normal.
 */
@Singleton
class UserSession @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {
    fun currentUserId(): String = firebaseAuth.currentUser?.uid ?: ANONYMOUS

    companion object {
        const val ANONYMOUS = "anonymous"
    }
}
