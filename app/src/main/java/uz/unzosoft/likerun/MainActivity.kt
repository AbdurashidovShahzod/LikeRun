package uz.unzosoft.likerun

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import uz.unzosoft.likerun.databinding.ActivityMainBinding
import uz.unzosoft.likerun.db.RunDAO
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var dao: RunDAO
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootView)
        Log.d("daoRun", "${dao.hashCode()}")

    }


    private fun transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = resources.getColor(R.color.transparent)
        }
    }
}