package com.diyartaikenov.pickamovie.di

import com.diyartaikenov.pickamovie.repository.network.MoviesApi
import com.diyartaikenov.pickamovie.repository.network.MoviesNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    fun provideMoviesApi(moviesNetwork: MoviesNetwork): MoviesApi {
        return moviesNetwork.moviesApi
    }
}