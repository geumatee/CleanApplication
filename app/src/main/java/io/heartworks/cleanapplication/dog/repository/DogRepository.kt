package io.heartworks.cleanapplication.dog.repository

import android.support.annotation.IntRange
import io.heartworks.cleanapplication.dog.data.Dog
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by geuma on 3/5/2018.
 */

/**
 * The dog repository provides endpoints for the caller to interact with dog data.
 */
interface DogRepository {
  /**
   * Loads a random collection of dogs.
   * @param max the maximum amount of loaded dogs
   * @return a Single which emits a List of dogs
   */
  fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Flowable<List<Dog>>
}