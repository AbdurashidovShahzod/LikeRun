package uz.unzosoft.likerun.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import uz.unzosoft.likerun.R
import uz.unzosoft.likerun.ui.viewmodels.MainViewModel
import uz.unzosoft.likerun.ui.viewmodels.StatisticViewModel

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticViewModel by viewModels()
}