package uz.unzosoft.likerun.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import uz.unzosoft.likerun.R
import uz.unzosoft.likerun.databinding.FragmentTrackingBinding
import uz.unzosoft.likerun.other.Constants.ACTION_PAUSE_SERVICE
import uz.unzosoft.likerun.other.Constants.ACTION_START_OR_RESUME_SERVICE
import uz.unzosoft.likerun.other.Constants.MAP_ZOOM
import uz.unzosoft.likerun.other.Constants.POLYLINE_COLOR
import uz.unzosoft.likerun.other.Constants.POLYLINE_WIDTH
import uz.unzosoft.likerun.services.PolyLine
import uz.unzosoft.likerun.services.TrackingService
import uz.unzosoft.likerun.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    lateinit var binding: FragmentTrackingBinding
    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null
    private var icTracking = false
    private var pathPoints = mutableListOf<PolyLine>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            btnToggleRun.setOnClickListener {
                toggleRun()
            }
            mapView.onCreate(savedInstanceState)
        }
        binding?.mapView.getMapAsync {
            map = it
            addAllPolyline()
        }
        subscribeToObservers()
    }


    private fun subscribeToObservers() {
        TrackingService.isTrackingMutableLiveData.observe(viewLifecycleOwner,
            Observer {
                updateTracking(it)
            })
        TrackingService.pathPoints.observe(viewLifecycleOwner,
            Observer {
                pathPoints = it
                addLatestPolyline()
                moveCameraToUser()

            })
    }


    private fun toggleRun() {
        if (icTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }

    }

    private fun updateTracking(isTracking: Boolean) {
        this.icTracking = isTracking
        if (!icTracking) {
            binding.btnToggleRun.text = getString(R.string.start)
            binding.btnFinishRun.visibility = View.VISIBLE
        } else {
            binding.btnToggleRun.text = getString(R.string.stop)
            binding.btnFinishRun.visibility = View.GONE
        }

    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory
                    .newLatLngZoom(pathPoints.last().last(), MAP_ZOOM)
            )
        }
    }


    private fun addAllPolyline() {
        for (polyline in pathPoints) {
            val polyLineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polyLineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            var lastLatLng = pathPoints.last().last()
            val polyLineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polyLineOptions)
        }
    }


    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }


    override fun onResume() {
        super.onResume()
        binding?.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding?.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding?.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding?.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding?.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapView.onSaveInstanceState(outState)
    }
}