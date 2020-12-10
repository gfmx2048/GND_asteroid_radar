package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

// Singleton pattern in Kotlin: https://kotlinlang.org/docs/reference/object-declarations.html#object-declarations
object RetrofitClient {

    private val okHttpClientInterceptor: OkHttpClient.Builder
        get() {
            val okHttpBuilder = OkHttpClient.Builder()

            okHttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
            okHttpBuilder.readTimeout(40, TimeUnit.SECONDS)
            okHttpBuilder.writeTimeout(40, TimeUnit.SECONDS)

            okHttpBuilder.addInterceptor { chain ->
                val request = chain.request()
                val newRequest = request.newBuilder()
                    .addHeader("Content-Type", "application/json")
                chain.proceed(newRequest.build())
            }

            if (BuildConfig.DEBUG) {
                //create http instance for logging
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okHttpBuilder.addNetworkInterceptor(loggingInterceptor)
                ////////////////////////////////////////////
            }

            return okHttpBuilder
        }

    /**
     * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
     * full Kotlin compatibility.
     */
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    val instance: AsteroidApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClientInterceptor.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        //create retrofit client
        return@lazy retrofit.create(AsteroidApi::class.java)
    }

}