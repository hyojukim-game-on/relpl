package com.gdd.relpl.module

import android.content.Context
import com.gdd.presentation.base.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    @Provides
    @Singleton
    fun providePrefManager(
        @ApplicationContext context: Context
    ): PrefManager {
        return PrefManager(context)
    }
}