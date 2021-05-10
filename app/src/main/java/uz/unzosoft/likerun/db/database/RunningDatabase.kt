package uz.unzosoft.likerun.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.unzosoft.likerun.db.converters.Converters
import uz.unzosoft.likerun.db.dao.RunDAO
import uz.unzosoft.likerun.db.entities.Run


@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDAO
}