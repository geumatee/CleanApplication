package io.heartworks.cleanapplication.dog.repository

import android.annotation.SuppressLint
import android.support.annotation.IntRange
import android.util.Log
import io.heartworks.cleanapplication.dog.data.Dog
import io.heartworks.cleanapplication.dog.networking.DogApi
import io.heartworks.cleanapplication.dog.networking.DogsResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import org.reactivestreams.Publisher

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
          data
        }
        .flatMapPublisher<RealmList<Dog>?> { dogsResponse: DogsResponse ->
          //          if (dogsResponse.error != null) {
//            throw RuntimeException(dogsResponse.error)
//          }
          if (dogsResponse.error == null) {
            writeToRealm(dogsResponse)
          }
          Flowable.just(dogsResponse.data)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .filter {
          it.isNotEmpty()
        }
    val cache = Flowable.just(findInRealm()?.data)
                        .filter {
                          it.isNotEmpty()
                        }
    return Flowable.merge(api, cache)
        .flatMap {
          val list = mutableListOf<Dog>()
          list.addAll(it)
          Flowable.just(list.toList())
        }
        .distinct()

  }

  private fun writeToRealm(data: DogsResponse) {
    val realm = Realm.getDefaultInstance()
    var dogsResponse = findInRealm()
    realm.executeTransaction {
      if (dogsResponse == null || dogsResponse?.data?.size == 0) {
        dogsResponse = it.createObject(DogsResponse::class.java)
      }
      dogsResponse?.data?.clear()
      dogsResponse?.data?.addAll(data.data)
      dogsResponse?.count = data.count
      if (dogsResponse != null) {
        it.insertOrUpdate(dogsResponse!!)
      }

      it.insertOrUpdate(data)
    }
    realm.close()
  }

  private fun findInRealm(): DogsResponse? {
    val realm = Realm.getDefaultInstance()
    var data: DogsResponse? = realm.where(DogsResponse::class.java).findFirst()
    realm.close()
    if (data == null) {
      data = DogsResponse()
    }
    return data
  }
}