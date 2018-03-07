package io.heartworks.cleanapplication.dog.networking

import io.heartworks.cleanapplication.dog.data.Dog
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * This POJO holds the response data for the DogApi requests
 *
 * @param data the actual array of dog objects
 * @param count the amount of dog objects in this response
 * @param error an optional error if the service threw one
 */
open class DogsResponse: RealmObject() {
  var data: RealmList<Dog> = RealmList()
  var count: Int = 0
  var error: String? = null
}