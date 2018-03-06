package io.heartworks.cleanapplication.dog.repository

import android.support.annotation.IntRange
import android.annotation.SuppressLint
import io.heartworks.cleanapplication.dog.data.Dog
import io.heartworks.cleanapplication.dog.networking.DogApi
import io.heartworks.cleanapplication.dog.networking.DogsResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * The dog repository provides endpoints for the caller to interact with dog data.
 * This implementation's data source is a rest service.
 */
class DogRestRepository(private val dogApi: DogApi) : DogRepository {

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
    return dogApi.getRandom(max)
        .subscribeOn(Schedulers.io())
        .flatMapPublisher { dogsResponse: DogsResponse ->
          if (dogsResponse.error != null) {
            throw RuntimeException(dogsResponse.error)
          }
          Observable.just(dogsResponse.data).toFlowable(BackpressureStrategy.LATEST)
        }
  }
}