package io.heartworks.cleanapplication.utils

import io.realm.Realm
import io.realm.RealmModel

/**
 * Created by geuma on 3/7/2018.
 */


fun <E : RealmModel> Class<E>.findInRealm(): E? {
  val realm = Realm.getDefaultInstance()
  val data: E? = realm.where(this).findFirst()
  realm.close()
  return data
}

fun <E : RealmModel> Class<E>.writeToRealm(listener: (E) -> E) {
  val realm = Realm.getDefaultInstance()
  var data = this.findInRealm()
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
  realm.close()
}