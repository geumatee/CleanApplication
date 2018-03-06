package io.heartworks.cleanapplication.dog.data

import android.os.Parcel
import com.playmoweb.store2realm.model.HasId
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
open class Dog : RealmObject(), HasId {
  var id: String = ""
  var url: String? = null
  var time: String? = null
  var format: String? = null

  override fun getUniqueIdentifierName(): String = "id"

  override fun getUniqueIdentifier(): Int = id.hashCode()
}
//data class Dog(val id: String, val url: String, val time: String, val format: String)