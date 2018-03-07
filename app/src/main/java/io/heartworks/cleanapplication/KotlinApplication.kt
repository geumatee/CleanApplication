package io.heartworks.cleanapplication

import android.app.Application
import io.heartworks.cleanapplication.injection.AppModule
import io.heartworks.cleanapplication.injection.ApplicationComponent
import io.heartworks.cleanapplication.injection.DaggerApplicationComponent
import io.realm.Realm
import io.realm.RealmConfiguration



/**
 * Created by geuma on 3/5/2018.
 */
class KotlinApplication : Application() {

  companion object {
    /**
     * The ApplicationComponent for the dependency injection context.
     */
    @JvmStatic private lateinit var appComponent: ApplicationComponent

    /**
     * The AppModule. which was created during the setup
     */
    @JvmStatic private lateinit var appModule: AppModule

    /**
     * Returns the appModule for this application.
     * Use this, if you have a dependency to the AppModule in you Components.
     */
    fun module(): AppModule {
      return appModule
    }

    /**
     * Provides the created ApplicationComponent for this app.
     */
    fun component(): ApplicationComponent {
      return appComponent
    }
  }

  /**
   * Lazy initialized ApplicationComponent
   */
  private val component: ApplicationComponent by lazy {
    appModule = AppModule(this)
    DaggerApplicationComponent.builder()
        .appModule(appModule)
        .build()
  }

  override fun onCreate() {
    super.onCreate()

    // Inject application dependencies
    component.inject(this)
    appComponent = component

    Realm.init(this)
    val realmConfiguration = RealmConfiguration.Builder()
        .deleteRealmIfMigrationNeeded()
        .build()
    Realm.setDefaultConfiguration(realmConfiguration)
  }
}