package com.example.daytonaassignment.di.modules

import android.app.usage.UsageEvents.Event.NONE
import com.example.daytonaassignment.BuildConfig
import com.example.daytonaassignment.data.remote.config.BaseUrl
import com.mingle.chatapp.data.remote.config.ApiManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Singleton


@Module
class NetworkResolver {

    @Singleton
    @Provides
    fun providesApiKey() : String = "AIzaSyDw7Px0jGAugn5OaUITiRGb8QdKs_Ynstk"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() : HttpLoggingInterceptor = HttpLoggingInterceptor()

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor) : OkHttpClient {

        val okHttpClient = OkHttpClient.Builder()
          /*  .addInterceptor(httpLoggingInterceptor.apply {
              //  HttpLoggingInterceptor.Level.BODY
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })*/
            .build()

        return okHttpClient
    }

    @Singleton
    @Provides
    fun providesRetrofitBuilder(okHttpClient: OkHttpClient) : Retrofit = Retrofit.Builder()
        .baseUrl(BaseUrl.base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun providesRetrofit(retrofit: Retrofit) = ApiManager(retrofit)


}