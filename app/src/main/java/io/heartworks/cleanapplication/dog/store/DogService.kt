package io.heartworks.cleanapplication.dog.store

import android.os.Parcel
import android.os.Parcelable
import com.playmoweb.store2store.store.StoreService
import io.heartworks.cleanapplication.dog.data.Dog
import com.playmoweb.store2store.utils.SortingMode
import io.reactivex.Flowable
import com.playmoweb.store2store.store.StoreDao
import com.playmoweb.store2realm.service.BaseRealmService
import com.playmoweb.store2store.store.Optional
import com.playmoweb.store2store.utils.Filter
import io.heartworks.cleanapplication.dog.networking.DogApi
import io.heartworks.cleanapplication.dog.networking.DogsResponse
import io.reactivex.schedulers.Schedulers



/**
 * Created by geuma on 3/5/2018.
 */
class DogService constructor(dogApi: DogApi) : StoreService<Dog>(Dog::class.java, DogDao(dogApi)) {

  init {
    this.syncWith(BaseRealmService<Dog>(Dog::class.java)) // sync here with realm
  }

  class DogDao(private val dogApi: DogApi) : StoreDao<Dog>() {
    override fun getAll(filter: Filter?,
        sortingMode: SortingMode?): Flowable<Optional<MutableList<Dog>>> {
      val limit = 10
      return wrapOptional(dogApi.getRandom(10 )
          .subscribeOn(Schedulers.io())
          .map { dogsResponse: DogsResponse ->
            // if there was an error, throw an exception
            if (dogsResponse.error != null) {
              throw RuntimeException(dogsResponse.error)
            }

            // Return the list of dogs
            return@map dogsResponse.data.toMutableList()
          }.toFlowable())
    }
  }
}