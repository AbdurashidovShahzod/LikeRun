package uz.unzosoft.likerun.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber
import uz.unzosoft.likerun.R
import uz.unzosoft.likerun.other.Constants.ACTION_PAUSE_SERVICE
import uz.unzosoft.likerun.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import uz.unzosoft.likerun.other.Constants.ACTION_START_OR_RESUME_SERVICE
import uz.unzosoft.likerun.other.Constants.ACTION_STOP_SERVICE
import uz.unzosoft.likerun.other.Constants.FASTEST_UPDATE_INTERVAL
import uz.unzosoft.likerun.other.Constants.LOCATION_UPDATE_INTERVAL
import uz.unzosoft.likerun.other.Constants.NOTIFICATION_CHANNEL_ID
import uz.unzosoft.likerun.other.Constants.NOTIFICATION_CHANNEL_NAME
import uz.unzosoft.likerun.other.Constants.NOTIFICATION_ID
import uz.unzosoft.likerun.other.TrackingUtility
import uz.unzosoft.likerun.ui.MainActivity


typealias PolyLine = MutableList<LatLng>
typealias PolyLines = MutableList<PolyLine>

class TrackingService : LifecycleService() {

    private var isFirstRun = true
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTrackingMutableLiveData = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<PolyLines>()
    }

    private fun postInitialValues() {
        isTrackingMutableLiveData.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        Timber.d("Resuming service...")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Pause service")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            if (isTrackingMutableLiveData.value!!) {
                result.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {
        addEmptyPolyline()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }


        val notificationBuilder = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("LikeRun")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT

        }, FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )
        notificationManager.createNotificationChannel(channel)
    }
}