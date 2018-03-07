package io.heartworks.cleanapplication.utils

import io.realm.Realm
import io.realm.RealmModel

/**
 * Created by geuma on 3/7/2018.
 */


fun <E : RealmModel> Class<E>.findInRealm(realm: Realm): E? {
  return realm.where(this).findFirst()
}

fun <E : RealmModel> Class<E>.writeToRealm(realm: Realm, listener: (E) -> E) {
  var data = this.findInRealm(realm)
  realm.executeTransaction {
    if (data == null) {
      data = it.createObject(this)
    }
    data = listener.invoke(data!!)
    if (data != null) {
      it.insertOrUpdate(data!!)
    }
    it.insertOrUpdate(data!!)
  }
}