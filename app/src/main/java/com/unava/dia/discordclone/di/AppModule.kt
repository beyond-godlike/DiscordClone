package com.unava.dia.discordclone.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.firebase.database.FirebaseDatabase
import com.unava.dia.discordclone.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.unava.dia.discordclone.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Provides
    fun provideTheFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(
            KEY_FIRST_TIME_TOGGLE, true
        )
    @Provides
    fun provideFirebaseDatabase() = FirebaseDatabase.getInstance("https://discordclone-dd303-default-rtdb.europe-west1.firebasedatabase.app/")
}