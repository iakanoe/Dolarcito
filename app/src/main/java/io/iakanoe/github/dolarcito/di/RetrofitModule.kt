package io.iakanoe.github.dolarcito.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.iakanoe.github.dolarcito.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val newRequest = chain.request().newBuilder()
                .addHeader("Auth-Client", BuildConfig.API_KEY)
                .build()

            return chain.proceed(newRequest)
        }
    }

    private fun createClient() = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()

    @Provides
    @Singleton
    fun providesRetrofit(
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://dolarito.ar/")
        .client(createClient())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}