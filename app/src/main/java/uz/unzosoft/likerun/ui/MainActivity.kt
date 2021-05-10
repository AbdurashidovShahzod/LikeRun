package uz.unzosoft.likerun.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import uz.unzosoft.likerun.databinding.ActivityMainBinding
import uz.unzosoft.likerun.db.dao.RunDAO
import uz.unzosoft.likerun.other.Constants
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var dao: RunDAO
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constants.statusBar(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootView)
    }

}