package io.heartworks.cleanapplication.dog.injection

import dagger.Module
import dagger.Provides
import io.heartworks.cleanapplication.dog.networking.DogApi
import io.heartworks.cleanapplication.dog.repository.DogRepository
import io.heartworks.cleanapplication.dog.repository.DogRestRepository
import io.heartworks.cleanapplication.dog.store.DogService
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by geuma on 3/5/2018.
 *//**
 * The DogModule injects a DogRepository implementation into the target.
 */

@Module
class DogModule {

  /**
   * @param retrofit required to create the repository implementation for the DogRepository
   * @return an implementation of the DogRepository
   */
  @Provides
  @Singleton
  fun provideDogsRepository(retrofit: Retrofit): DogRepository {
    // You can decide by whatever params which repo you want to inject

    return DogRestRepository(retrofit.create(DogApi::class.java))
  }

  @Provides
  @Singleton
  fun provideDogsService(retrofit: Retrofit): DogService {
    return DogService(retrofit.create(DogApi::class.java))
  }
}