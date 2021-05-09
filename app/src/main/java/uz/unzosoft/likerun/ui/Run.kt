package uz.unzosoft.likerun.ui

import android.graphics.Bitmap
import androidx.room.Entity
import java.sql.Timestamp

@Entity(tableName = "running_table")
data class Run(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKHM: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L
)
