package io.heartworks.cleanapplication.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import io.heartworks.cleanapplication.KotlinApplication
import javax.inject.Singleton

/**
 * Created by geuma on 3/5/2018.
 */

/**
 * This module should be used to inject application scoped dependencies.
 */
@Module
class AppModule(private val application: KotlinApplication) {

  @Singleton
  @Provides
  fun provideApplication(): KotlinApplication = application

  @Singleton
  @Provides fun provideApplicationContext(): Context {
    return application
  }
}