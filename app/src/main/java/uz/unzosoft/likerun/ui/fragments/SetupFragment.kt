package uz.unzosoft.likerun.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import uz.unzosoft.likerun.R
import uz.unzosoft.likerun.databinding.FragmentSetupBinding

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    lateinit var binding: FragmentSetupBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvContinue.setOnClickListener {
        findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }
    }
}