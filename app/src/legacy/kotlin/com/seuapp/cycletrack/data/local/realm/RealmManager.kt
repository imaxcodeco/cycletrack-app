package com.seuapp.cycletrack.data.local.realm

import io.realm.RealmMigration
import timber.log.Timber

/**
 * Realm migration manager.
 * Clean-room implementation with versioned schema migrations.
 *
 * O schema v1 (todas as tabelas do_cycle_track) e criado automaticamente pelo Realm a
 * partir das classes de entidade anotadas com `@RealmClass`, portanto a migracao de v0
 * para v1 e um no-op. Futuras versoes do schema (v2, v3, ...) devem ser adicionadas
 * aqui, incrementando o `schemaVersion` em `CycleTrackApplication.initializeRealm()`.
 */
object RealmManager {

    val migration: RealmMigration = RealmMigration { _, oldVersion, newVersion ->
        Timber.i("Realm migration from v$oldVersion to v$newVersion")

        when (oldVersion) {
            0 -> {
                // Schema inicial (v1) criado automaticamente pelo Realm.
                Timber.i("Realm schema v1 inicializado")
            }
            else -> {
                // Futuras migracoes entram aqui (ex.: 1 -> { migrateToV2(realm) }).
                Timber.w("Nenhuma migracao definida para v$oldVersion -> v$newVersion")
            }
        }
    }
}
