package de.damien.frontend.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.damien.frontend.common.Constants
import de.damien.frontend.data.remote.FunnyPlacesApi
import de.damien.frontend.data.repositorys.FunnyPlacesRepositoryImpl
import de.damien.frontend.domain.repositorys.FunnyPlacesRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFunnyPlacesApi(): FunnyPlacesApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FunnyPlacesApi::class.java)
    }

    @Provides
    @Singleton
    fun providePlaceRepository(api: FunnyPlacesApi): FunnyPlacesRepository {
        return FunnyPlacesRepositoryImpl(api)
    }

}