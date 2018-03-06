package io.heartworks.cleanapplication.injection

import dagger.Component
import dagger.Provides
import io.heartworks.cleanapplication.KotlinApplication

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * This component should be used to inject application scoped dependencies.
 */
@ForApplication
@Component(modules = [(AppModule::class)])
interface ApplicationComponent {

  fun inject(application: KotlinApplication)
}