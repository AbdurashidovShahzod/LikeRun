package uz.unzosoft.likerun.other

import android.app.Activity
import android.os.Build
import android.view.View
import uz.unzosoft.likerun.R

object Constants {
    const val RUNNING_DATABASE_NAME = "running_db"
    const val REQUEST_CODE_LOCATION_PERMISSIONS = 0

    fun statusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            activity.window.statusBarColor = activity.resources.getColor(R.color.transparent)
        }
    }
}