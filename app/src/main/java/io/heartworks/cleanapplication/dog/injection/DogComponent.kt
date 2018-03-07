package io.heartworks.cleanapplication.dog.injection

import dagger.Component
import io.heartworks.cleanapplication.dog.view.DogsListActivity
import io.heartworks.cleanapplication.dog.presentation.DogsListPresenter
import io.heartworks.cleanapplication.dog.view.DogsListFragment
import io.heartworks.cleanapplication.injection.AppModule
import io.heartworks.cleanapplication.injection.DatabaseModule
import io.heartworks.cleanapplication.injection.NetworkModule
import io.heartworks.cleanapplication.injection.PresentationModule
import javax.inject.Singleton

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * This dagger component provides all required dependencies for the dog domain to work.
 */
@Singleton
@Component(modules = [
  (AppModule::class),
  (DogModule::class),
  (NetworkModule::class),
  (DatabaseModule::class),
  (PresentationModule::class)
])
interface DogComponent {
  fun inject(activity: DogsListActivity)
  fun inject(fragment: DogsListFragment)
  fun inject(presenter: DogsListPresenter)
}