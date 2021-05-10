package uz.unzosoft.likerun.di

import android.content.Context
import android.provider.DocumentsContract
import androidx.room.Room
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uz.unzosoft.likerun.db.RunningDatabase


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    fun provideRunningDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    )
}