package io.heartworks.cleanapplication.injection

import javax.inject.Qualifier

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * This annotation marks a dagger injection as application-wide
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForApplication