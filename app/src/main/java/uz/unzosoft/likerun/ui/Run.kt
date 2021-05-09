package uz.unzosoft.likerun.ui

import android.graphics.Bitmap
import androidx.room.Entity
import java.sql.Timestamp

@Entity(tableName = "running_table")
data class Run(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,

    )
