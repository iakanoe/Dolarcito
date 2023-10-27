package io.iakanoe.github.dolarcito.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.iakanoe.github.dolarcito.gateway.DolarcitoApiGateway
import io.iakanoe.github.dolarcito.gateway.SettingsGateway
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GatewayModule {

    @Provides
    @Singleton
    fun providesDolarcitoApiGateway(
        retrofit: Retrofit
    ) = retrofit.create<DolarcitoApiGateway>()

    @Provides
    @Singleton
    fun providesSettingsGateway(
        @ApplicationContext context: Context,
        gson: Gson
    ) = SettingsGateway(context, gson)

}