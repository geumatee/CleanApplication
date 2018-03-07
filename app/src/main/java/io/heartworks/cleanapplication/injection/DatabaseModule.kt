package io.heartworks.cleanapplication.injection

import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Singleton

/**
 * Created by geuma on 3/7/2018.
 */
@Module
class DatabaseModule {
  @Provides
  fun provideRealm() : Realm = Realm.getDefaultInstance()
}