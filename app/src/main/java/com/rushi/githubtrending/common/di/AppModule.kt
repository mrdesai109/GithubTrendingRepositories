package com.rushi.githubtrending.common.di

import android.content.Context
import com.rushi.githubtrending.common.Constants
import com.rushi.githubtrending.data.remote.GithubAPI
import com.rushi.githubtrending.data.repository.MainRepositoryImpl
import com.rushi.githubtrending.domain.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit() : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Singleton
    @Provides
    fun provideGithubAPI(retrofit: Retrofit) : GithubAPI {
        return retrofit.create(GithubAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideMainRepository(githubAPI: GithubAPI) : MainRepository{
        return MainRepositoryImpl(githubAPI = githubAPI);
    }

    @Singleton
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}