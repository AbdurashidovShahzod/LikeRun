package uz.unzosoft.likerun.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import uz.unzosoft.likerun.other.Constants.TIME_UPDATE_INTERVAL
import uz.unzosoft.likerun.other.TrackingUtility
import uz.unzosoft.likerun.ui.MainActivity
import java.util.concurrent.Executor


typealias PolyLine = MutableList<LatLng>
typealias PolyLines = MutableList<PolyLine>

class TrackingService : LifecycleService() {

    private var isFirstRun = true
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSecond = MutableLiveData<Long>()

    companion object {
        val timeRunMillis = MutableLiveData<Long>()
        val isTrackingMutableLiveData = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<PolyLines>()
    }

    private fun postInitialValues() {
        isTrackingMutableLiveData.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    private var icTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        isTrackingMutableLiveData.postValue(true)
        timeStarted = System.currentTimeMillis()
        icTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTrackingMutableLiveData.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunMillis.postValue(timeRun + lapTime)
                if (timeRunMillis.value!! > lastSecondTimeStamp + 1000L) {
                    timeRunInSecond.postValue(timeRunInSecond.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIME_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService() {
        isTrackingMutableLiveData.postValue(false)
        icTimerEnabled = false
    }

    override fun onCreate() {
        postInitialValues()
        super.onCreate()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTrackingMutableLiveData.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        Timber.d("Resuming service...")
                    } else {
                        Timber.d("Resuming service...")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Pause service")
                    pauseService()
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
                        Timber.d("New location:${location.latitude},${location.longitude}")
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

        isTrackingMutableLiveData.postValue(true)
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