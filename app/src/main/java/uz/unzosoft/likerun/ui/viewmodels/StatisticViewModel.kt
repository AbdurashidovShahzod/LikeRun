package uz.unzosoft.likerun.ui.viewmodels

import androidx.fragment.app.viewModels
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.unzosoft.likerun.repositories.MainRepository
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    var mainRepository: MainRepository,
) : ViewModel() {

}











