package io.heartworks.cleanapplication.dog.view

import io.heartworks.cleanapplication.dog.viewmodel.DogsListViewModel
import net.grandcentrix.thirtyinch.TiView
import io.reactivex.Observable

/**
 * Created by geuma on 3/5/2018.
 */

/**
 * This view connects the view implementation with a presenter.
 */
interface DogsListView : TiView {

  /**
   * Emits items when the user clicks the reload button
   */
  fun getViewModel(): DogsListViewModel

  /**
   * Provides the viewmodel for the presenter
   */
  fun onReloadClick(): Observable<Any>
}