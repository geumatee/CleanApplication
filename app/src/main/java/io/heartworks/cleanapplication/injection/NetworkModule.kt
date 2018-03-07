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
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.*
import io.realm.RealmList
import io.realm.RealmObject
import retrofit2.converter.gson.GsonConverterFactory


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
//        .addInterceptor(OfflineResponseCacheInterceptor(application))
        .cache(cache)
        .build()

    val gson = GsonBuilder()
        .setExclusionStrategies(object : ExclusionStrategy {
          override fun shouldSkipField(f: FieldAttributes): Boolean {
            return f.getDeclaringClass().equals(RealmObject::class.java) // is this still needed?
          }

          override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return false
          }
        })
        .registerTypeAdapter(object : TypeToken<RealmList<Int>>() {

        }.type, object : TypeAdapter<RealmList<Int>>() {

          @Throws(IOException::class)
          override fun write(out: JsonWriter, value: RealmList<Int>) {
            // Ignore for now
          }

          @Throws(IOException::class)
          override fun read(`in`: JsonReader): RealmList<Int> {
            val list = RealmList<Int>()
            `in`.beginArray()
            while (`in`.hasNext()) {
              list.add(`in`.nextInt())
            }
            `in`.endArray()
            return list
          }
        })
        .create()

    return Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.thedogapi.co.uk")
        .addConverterFactory(GsonConverterFactory.create(gson))
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