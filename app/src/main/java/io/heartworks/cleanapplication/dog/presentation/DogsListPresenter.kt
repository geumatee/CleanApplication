package io.heartworks.cleanapplication.dog.presentation

import android.util.Log
import com.playmoweb.store2store.utils.Filter
import com.playmoweb.store2store.utils.FilterType
import io.heartworks.cleanapplication.config.PresenterConfig
import io.heartworks.cleanapplication.dog.data.Dog
import io.heartworks.cleanapplication.dog.repository.DogRepository
import io.heartworks.cleanapplication.dog.store.DogService
import io.heartworks.cleanapplication.dog.view.DogsListView
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toSingle
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.rx2.RxTiPresenterDisposableHandler
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * This presenter takes care of the DogList logic for the DogsListView.
 */
class DogsListPresenter : TiPresenter<DogsListView>() {

  companion object {
    /**
     * TAG for the Android logger
     */
    @JvmStatic
    val TAG = DogsListPresenter::class.java.simpleName!!
  }

  /**
   * Injected
   * A config for this presenter.
   */
  @Inject
  lateinit var presenterConfig: PresenterConfig

  /**
   * Injected
   * A DogRepository implementation
   */
  @Inject
  lateinit var repository: DogRepository

  @Inject
  lateinit var service: DogService

  /**
   * Unsubscribes rx subscriptions when needed
   */
  private val rxHandler = RxTiPresenterDisposableHandler(this)

  /**
   * Internal presenter cache to store dog data
   */
  private val dogCache: MutableList<Dog> = mutableListOf()

  /**
   * Called when the view was attached to this presenter (when it is available)
   */
  override fun onAttachView(view: DogsListView) {
    super.onAttachView(view)

    // Listen to view based events
    subscribeToView(view)

    if (dogCache.isEmpty()) {
      // load the dog data
      loadDogs(view)
    } else {
      renderDogs(view, dogCache)
    }
  }

  /**
   * Subscribes to every view Observable.
   */
  private fun subscribeToView(view: DogsListView) {
    // Reacts to the reload click and gets some new dogs - yay!
    rxHandler.manageViewDisposable(view.onReloadClick()
        // clickDebounce will provide a buffer if the user plays monkey on the reload button
        .debounce(presenterConfig.clickDebounce, TimeUnit.MILLISECONDS)
        // Cheap way to trigger a reload of the doggies
        .subscribe({ loadDogs(view) })
    )
  }

  /**
   * Creates the dog loading logic wrapped in a Single.
   * Will also tell the view to show the loading indicator
   */
  private fun createDogLoader(): Flowable<List<Dog>> {
    return Flowable.fromCallable { view!!.getViewModel().setLoading(true) }
        .subscribeOn(AndroidSchedulers.mainThread())
        .flatMap<List<Dog>> { _ -> repository.getRandomDogs(10) }
        .map { dogs ->
          dogCache.clear()
          dogCache.addAll(dogs)
          return@map dogs
        }

//    return Flowable.fromCallable { view!!.getViewModel().setLoading(true) }
//        .subscribeOn(AndroidSchedulers.mainThread())
//        .flatMap<List<Dog>> { _ ->
//          service.all.map { data ->
//            data.get()
//          }
//              .map { t: MutableList<Dog> ->
//                t.toList()
//              }
//        }
//        .map { dogs ->
//          dogCache.clear()
//          dogCache.addAll(dogs)
//          return@map dogs
//        }
  }

  /**
   * Loads the dogs from the repository and sets the result in the viewmodel.
   * Also disables the loading indicator in the view.
   */
  private fun loadDogs(view: DogsListView) {
    rxHandler.manageDisposable(createDogLoader()
        .observeOn(AndroidSchedulers.mainThread())
        .toObservable()
//        .doOnError { t ->  Log.e(TAG, "Could not load cute little doggy pictures.", t) }
        .onErrorReturn { throwable ->
          Log.e(TAG, "Could not load cute little doggy pictures.", throwable)
          return@onErrorReturn dogCache
        }
        .subscribe { dogs ->
          renderDogs(view, dogs)
        }
    )
  }

  private fun renderDogs(view: DogsListView, dogs: List<Dog>) {
    val viewModel = view.getViewModel()
    viewModel.setDogs(dogs)
    viewModel.setLoading(false)
  }
}