package io.heartworks.cleanapplication.dog.data

import io.realm.RealmObject

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * The model class to store dog data in.
 *
 * @param id the id of the dog image
 * @param url the url to the image
 * @param time the timestamp when the images was shot
 * @param format the format the picture is stored in
 */
open class Dog : RealmObject() {
  var id: String? = null
  var url: String? = null
  var time: String? = null
  var format: String? = null
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Dog

    if (id != other.id) return false
    if (url != other.url) return false
    if (time != other.time) return false
    if (format != other.format) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id?.hashCode() ?: 0
    result = 31 * result + (url?.hashCode() ?: 0)
    result = 31 * result + (time?.hashCode() ?: 0)
    result = 31 * result + (format?.hashCode() ?: 0)
    return result
  }
}