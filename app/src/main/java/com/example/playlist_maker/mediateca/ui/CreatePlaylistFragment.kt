package com.example.playlist_maker.mediateca.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentCreatePlaylistBinding
import com.example.playlist_maker.mediateca.ui.view_model.CreatePlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentCreatePlaylistBinding
    private val viewModel: CreatePlaylistViewModel by viewModel()

    private var isPlaylistFlag = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                binding.cover.setImageURI(uri) // Установить изображение в ImageView
                binding.cover.setBackgroundResource(0)
                viewModel.setImagePath(uri)

                // Сохранить разрешения для URI
                try {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.e("CreatePlaylistFragment", "Failed to take persistable URI permission: $uri", e)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupTextListeners()
        observeChanges()
    }

    private fun setupClickListeners() {
        binding.cover.setOnClickListener {
            // Открыть Intent для выбора изображения
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
        }

        binding.backArrow.setOnClickListener {
            handleBackPress()
        }

        binding.btnCreate.setOnClickListener {
            lifecycleScope.launch {
                if (viewModel.createPlaylist()) {
                    val message = getString(R.string.playlist_created_message, viewModel.getPlaylistTitle())
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    val flag = arguments?.getBoolean("flagKey", false) ?: false
                    if (flag) {
                        requireActivity().finish()
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun setupTextListeners() {
        binding.name.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnCreate.isEnabled = !s.isNullOrBlank()
                viewModel.setPlaylistTitle(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.description.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPlaylistDescription(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf()
        )

        binding.name.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = binding.name.editText?.text.toString()
                val hintColor = if (text.isNotBlank()) Color.BLUE else Color.GRAY
                val colors = intArrayOf(
                    Color.BLUE,
                    Color.GRAY,
                    hintColor
                )
                val colorStateList = ColorStateList(states, colors)
                binding.name.setBoxStrokeColorStateList(colorStateList)
                binding.name.defaultHintTextColor = ColorStateList.valueOf(hintColor)
            }
        }

        binding.description.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = binding.description.editText?.text.toString()
                val hintColor = if (text.isNotBlank()) Color.BLUE else Color.GRAY
                val colors = intArrayOf(
                    Color.BLUE,
                    Color.GRAY,
                    hintColor
                )
                val colorStateList = ColorStateList(states, colors)
                binding.description.setBoxStrokeColorStateList(colorStateList)
                binding.description.defaultHintTextColor = ColorStateList.valueOf(hintColor)
            }
        }
    }

    private fun observeChanges() {
        viewModel.hasChanges.observe(viewLifecycleOwner) { hasChanges ->
            // Обновить UI при необходимости
        }
    }

    private fun handleBackPress() {
        viewModel.hasChanges.value?.let { hasChanges ->
            if (hasChanges) {
                showExitConfirmationDialog()
            } else {
                val flag = arguments?.getBoolean("flagKey", false) ?: false
                if (flag) {
                    requireActivity().finish()
                } else {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_finish_creation)
            .setMessage(R.string.dialog_message_unsaved_data)
            .setPositiveButton(R.string.dialog_button_finish) { _, _ ->
                val flag = arguments?.getBoolean("flagKey", false) ?: false
                if (flag) {
                    requireActivity().finish()
                } else {
                    findNavController().navigateUp()
                }
            }
            .setNegativeButton(R.string.dialog_button_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }
}

