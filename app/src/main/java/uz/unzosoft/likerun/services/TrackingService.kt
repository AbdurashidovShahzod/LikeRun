package uz.unzosoft.likerun.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import timber.log.Timber
import uz.unzosoft.likerun.other.Constants.ACTION_PAUSE_SERVICE
import uz.unzosoft.likerun.other.Constants.ACTION_START_OR_RESUME_SERVICE
import uz.unzosoft.likerun.other.Constants.ACTION_STOP_SERVICE

class TrackingService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> Timber.d("Started or resume service")
                ACTION_PAUSE_SERVICE -> Timber.d("Pause service")
                ACTION_STOP_SERVICE -> Timber.d("Stop service")

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}