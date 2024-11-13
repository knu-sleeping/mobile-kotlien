package com.dacslab.android.sleeping.model.di

import android.content.Context
import com.dacslab.android.sleeping.BuildConfig
import com.dacslab.android.sleeping.model.network.AuthApiService
import com.dacslab.android.sleeping.model.network.AuthInterceptor
import com.dacslab.android.sleeping.model.network.UserApiService
import com.dacslab.android.sleeping.model.repository.AuthRepository
import com.dacslab.android.sleeping.model.repository.AuthRepositoryImpl
import com.dacslab.android.sleeping.model.repository.UserRepository
import com.dacslab.android.sleeping.model.repository.UserRepositoryImpl
import com.dacslab.android.sleeping.model.source.local.AuthLocalDataSource
import com.dacslab.android.sleeping.model.source.remote.AuthRemoteDataSource
import com.dacslab.android.sleeping.model.source.remote.UserRemoteDataSource
import com.dacslab.android.sleeping.viewmodel.SharedSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthLocalDataSource(@ApplicationContext context: Context): AuthLocalDataSource {
        return AuthLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authLocalDataSource: AuthLocalDataSource
    ): AuthInterceptor {
        return AuthInterceptor(authLocalDataSource)
    }

    @Provides
    @Singleton
    fun provideAuthOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(authOkHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(authOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        authApiService: AuthApiService
    ): AuthRemoteDataSource {
        return AuthRemoteDataSource(authApiService)
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        userApiService: UserApiService
    ): UserRemoteDataSource {
        return UserRemoteDataSource(userApiService)
    }

    @Provides
    @Singleton
    fun provideProductionAuthRepository(
        authLocalDataSource: AuthLocalDataSource,
        authRemoteDataSource: AuthRemoteDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authLocalDataSource, authRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideProductionUserRepository(
        userRemoteDataSource: UserRemoteDataSource
    ): UserRepository {
        return UserRepositoryImpl(userRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideSharedSessionManager(): SharedSessionManager {
        return SharedSessionManager()
    }
}
