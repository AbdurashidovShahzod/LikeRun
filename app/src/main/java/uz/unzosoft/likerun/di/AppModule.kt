package uz.unzosoft.likerun.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.unzosoft.likerun.db.database.RunningDatabase
import uz.unzosoft.likerun.other.Constants
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        Constants.RUNNING_DATABASE_NAME
    ).build()


    @Provides
    @Singleton
    fun provideRunDao(db: RunningDatabase) = db.getRunDao()


}