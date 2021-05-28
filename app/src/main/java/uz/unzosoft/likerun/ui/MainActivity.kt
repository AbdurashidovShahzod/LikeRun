package uz.unzosoft.likerun.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import uz.unzosoft.likerun.R
import uz.unzosoft.likerun.databinding.ActivityMainBinding
import uz.unzosoft.likerun.db.dao.RunDAO
import uz.unzosoft.likerun.other.Constants
import uz.unzosoft.likerun.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constants.statusBar(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootView)
        navigateToTrackingFragmentIfNeeded(intent)

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragment.findNavController().navigate(R.id.action_global_tracking_fragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_container)
            .navigateUp()
    }

}