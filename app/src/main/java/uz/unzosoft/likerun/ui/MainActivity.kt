package uz.unzosoft.likerun.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import uz.unzosoft.likerun.R
import uz.unzosoft.likerun.databinding.ActivityMainBinding
import uz.unzosoft.likerun.db.dao.RunDAO
import uz.unzosoft.likerun.other.Constants
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constants.statusBar(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootView)

        setSupportActionBar(binding.toolbar)
        binding.apply {
            bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

            navHostFragment.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->

                    when (destination.id) {
                        R.id.settingsFragment, R.id.runFragment, R.id.statisticFragment ->
                            bottomNavigationView.visibility = View.VISIBLE
                        else -> bottomNavigationView.visibility = View.GONE
                    }
                }
        }
    }
}