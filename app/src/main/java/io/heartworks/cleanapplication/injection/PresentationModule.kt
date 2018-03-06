package io.heartworks.cleanapplication.injection

import dagger.Module
import dagger.Provides
import io.heartworks.cleanapplication.config.PresenterConfig
import javax.inject.Singleton

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * This module provides presenter dependencies.
 */
@Module
class PresentationModule {

  @Provides
  @Singleton
  fun providePresenterConfig(): PresenterConfig {
    return PresenterConfig()
  }
}