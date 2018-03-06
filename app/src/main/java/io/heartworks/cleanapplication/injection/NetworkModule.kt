package io.heartworks.cleanapplication.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import io.heartworks.cleanapplication.KotlinApplication
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import java.io.IOException
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.*


/**
 * Created by geuma on 3/5/2018.
 */
/**
 * This module provides retrofit and handles its creation.
 */
@Module
class NetworkModule {

  /**
   * Creates an instance of Retrofit for this app
   */
  @Provides
  @Singleton
  fun provideRetrofit(application: KotlinApplication): Retrofit {
    val cacheSize = 10 * 1024 * 1024 // 10 MB
    val cache = Cache(application.applicationContext.cacheDir, cacheSize.toLong())

    val okHttpClient = OkHttpClient.Builder()
//        .addNetworkInterceptor(ResponseCachingInterceptor())
        .addInterceptor(OfflineResponseCacheInterceptor(application))
        .cache(cache)
        .build()

    return Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.thedogapi.co.uk")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
  }

  private class ResponseCachingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
      val response = chain.proceed(chain.request())
      return response.newBuilder()
          .removeHeader("Pragma")
          .removeHeader("Access-Control-Allow-Origin")
          .removeHeader("Vary")
          .removeHeader("Cache-Control")
          .header("Cache-Control", "public, max-age=60")
          .build()
    }
  }

  private class OfflineResponseCacheInterceptor(val application: KotlinApplication) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
      var request = chain.request()
      val networkInfo = (application.applicationContext.getSystemService(
          Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
      if (networkInfo == null) {
        request = request.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Access-Control-Allow-Origin")
            .removeHeader("Vary")
            .removeHeader("Cache-Control")
            .header("Cache-Control",
                "public, only-if-cached, max-stale= 60")
            .build()
      }
      return chain.proceed(request)
    }
  }
}