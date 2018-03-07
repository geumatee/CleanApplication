package io.heartworks.cleanapplication.dog.repository

import android.annotation.SuppressLint
import android.support.annotation.IntRange
import android.util.Log
import io.heartworks.cleanapplication.dog.data.Dog
import io.heartworks.cleanapplication.dog.networking.DogApi
import io.heartworks.cleanapplication.dog.networking.DogsResponse
import io.heartworks.cleanapplication.utils.findInRealm
import io.heartworks.cleanapplication.utils.writeToRealm
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import java.util.Locale.filter

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * The dog repository provides endpoints for the caller to interact with dog data.
 * This implementation's data source is a rest service.
 */
class DogRestRepository(private val dogApi: DogApi, private val realm: Realm) : DogRepository {

  /**
   * Loads a random collection of dogs.
   * This implementation loads the dogs from a rest service.
   *
   * @param max the maximum amount of loaded dogs
   * @return a Single which emits a List of dogs
   */
  @SuppressLint("Range")
  override fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Flowable<List<Dog>> {
    // Query the service
    val api = dogApi.getRandom(max)
        .subscribeOn(Schedulers.io())
        .onErrorReturn {
          Log.e("getRandomDogs", it.message)
          val data = DogsResponse()
          data.error = it.message
          return@onErrorReturn data
        }
        .flatMapPublisher<RealmList<Dog>?> { dogsResponse: DogsResponse ->
          if (dogsResponse.error == null) {
            DogsResponse::class.java.writeToRealm(
                {
                  it ->
                  it.data.clear()
                  it.data.addAll(dogsResponse.data)
                  it.count = dogsResponse.count
                  return@writeToRealm it
                }
            )
          }
          return@flatMapPublisher Flowable.just(dogsResponse.data)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .filter {
          it.isNotEmpty()
        }
    val cache = Flowable.just(DogsResponse::class.java.findInRealm()?.data)
                        .filter {
                          it.isNotEmpty()
                        }
    return Flowable.merge(api, cache)
        .flatMap {
          val list = mutableListOf<Dog>()
          list.addAll(it)
          return@flatMap Flowable.just(list.toList())
        }
        .distinct()
  }
}