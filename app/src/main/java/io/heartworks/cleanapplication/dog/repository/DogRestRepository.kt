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
import io.realm.RealmModel
import io.realm.RealmQuery
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
          return@onErrorReturn data
        }
        .flatMapPublisher<RealmList<Dog>?> { dogsResponse: DogsResponse ->
          if (dogsResponse.error == null) {
            writeToRealm(DogsResponse::class.java,
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
    val cache = Flowable.just(findInRealm(DogsResponse::class.java)?.data)
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

  private fun <E : RealmModel> writeToRealm(clazz: Class<E>, listener: (E) -> E) {
    val realm = Realm.getDefaultInstance()
    var data = findInRealm(clazz)
    realm.executeTransaction {
      if (data == null) {
        data = it.createObject(clazz)
      }
      data = listener.invoke(data!!)
      if (data != null) {
        it.insertOrUpdate(data!!)
      }
      it.insertOrUpdate(data!!)
    }
    realm.close()
  }

  private fun <E : RealmModel> findInRealm(clazz: Class<E>): E? {
    val realm = Realm.getDefaultInstance()
    val data: E? = realm.where(clazz).findFirst()
    realm.close()
    return data
  }
}