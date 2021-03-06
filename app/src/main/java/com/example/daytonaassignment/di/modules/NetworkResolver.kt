package com.example.daytonaassignment.di.modules

import android.util.Log
import com.example.daytonaassignment.data.remote.config.BaseUrl
import com.mingle.chatapp.data.remote.config.ApiManager
import com.mmi.services.account.MapmyIndiaAccountManager
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
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

      /*  val okHttpClient = OkHttpClient.Builder()
          *//*  .addInterceptor(httpLoggingInterceptor.apply {
              //  HttpLoggingInterceptor.Level.BODY
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })*//*
            .build()*/

        val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(object :
            Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                  val token = MapmyIndiaAccountManager.getInstance().accessToken
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .header("Authorization", String.format("bearer %s", token))
                    .build()

                Log.d("*** AUTH = ", String.format("bearer %s", token))

                return chain.proceed(newRequest)
            }
        }).build()

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