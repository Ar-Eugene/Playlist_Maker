package com.example.playlist_maker.settings.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.example.playlist_maker.databinding.FragmentSettingsBinding
import com.example.playlist_maker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var mySwitch: SwitchMaterial
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mySwitch = binding.mySwitch

        val shareAppImageView = binding.share
        shareAppImageView.setOnClickListener {
            startActivity(settingsViewModel.getShareIntent(requireContext()))
        }

        val writeToSupportImageView = binding.writeToSupport
        writeToSupportImageView.setOnClickListener {
            startActivity(settingsViewModel.getEmailIntent(requireContext()))
        }

        val agreementImageView = binding.agreementLayout
        agreementImageView.setOnClickListener {
            startActivity(settingsViewModel.getAgreementIntent(requireContext()))
        }
        initializationSwitch(mySwitch)
        observeViewModel(mySwitch)
    }

    private fun observeViewModel(mySwitch: SwitchMaterial) {
        settingsViewModel.isDarkThemeLiveData.observe(viewLifecycleOwner) { isDarkTheme ->
            mySwitch.isChecked = isDarkTheme
        }
    }

    private fun initializationSwitch(mySwitch: SwitchMaterial) {
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleTheme(isChecked)
        }
    }
}

