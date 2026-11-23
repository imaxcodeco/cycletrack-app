package com.seuapp.cycletrack.data.local.realm

import io.realm.Realm
import io.realm.RealmModel

/**
 * Extensoes de apoio para a API Java do Realm.
 *
 * A API Java e thread-confined: `RealmResults` e uma view viva que nao pode ser lida
 * em outra thread. Por isso, todos os resultados que saem dos DAOs sao convertidos em
 * copias detached (nao gerenciadas) antes de retornar ao chamador.
 */

/**
 * Copia detached de uma colecao de objetos gerenciados.
 */
fun <T : RealmModel> Iterable<T>.detached(): List<T> =
    Realm.getDefaultInstance().copyFromRealm(this)

/**
 * Copia detached de um objeto gerenciado (ou null).
 */
fun <T : RealmModel> T?.detached(): T? = this?.let { Realm.getDefaultInstance().copyFromRealm(it) }
