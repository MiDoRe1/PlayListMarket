package com.example.playlistmarket.medialibrary.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmarket.R
import com.example.playlistmarket.core.ui.BindingFragment
import com.example.playlistmarket.databinding.FragmentPlaylistAddBinding
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import android.provider.Settings
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmarket.medialibrary.ui.viewmodel.AddPlaylistState
import com.example.playlistmarket.medialibrary.ui.viewmodel.AddPlaylistViewModel
import com.example.playlistmarket.medialibrary.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmarket.utils.dpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class AddPlaylistFragment : BindingFragment<FragmentPlaylistAddBinding>() {

    private var playList = object {
        var coverName: String? = null
        var name: String? = null
        var description: String? = null
    }

    private val viewModel: AddPlaylistViewModel by viewModel()


    private val pickMedia = registerForActivityResult<PickVisualMediaRequest, Uri?>(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateCoverUrl(uri)
            loadImgToPlaceHolder(uri)
        } else {
            showDefaultPlaceholder()
        }
    }

    private val callbackToExit = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.createPlaylistCancel()
        //showWarningToast()
           // disableCallback()
        }
    }


    private fun showWarningMessage() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?") // Заголовок диалога
            .setMessage("Все несохраненные данные будут потеряны") // Описание диалога
            .setNeutralButton("Отмена") { dialog, which -> // Добавляет кнопку «Отмена»
                dialog.dismiss()
            }
            .setPositiveButton("Завершить") { dialog, which -> // Добавляет кнопку «Да»
                viewModel.forcedCancel()
                callbackToExit.isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .show()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistAddBinding {
        return FragmentPlaylistAddBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtonCreatePlaylist()
        initButtonAddPhoto()
        initEditTextPlaylistTitle()
        initEditTextPlaylistDescription()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callbackToExit)
        binding.toolbarHeader.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.observeStateViewModel().observe(viewLifecycleOwner) {
            state ->
            when(state) {
                is AddPlaylistState.CloseState -> forcedClose()
                is AddPlaylistState.EnteringDataState -> changeCreateButtonEnableTo(state.isEnough)
                is AddPlaylistState.WarningState -> showWarningMessage()
                is AddPlaylistState.PlaylistCreatedState -> {
                    showToastMessage("Плейлист ${state.playlist.name} создан")
                    forcedClose()
                }
            }
        }

    }

    private fun forcedClose() {
        callbackToExit.isEnabled = false
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun changeCreateButtonEnableTo(value: Boolean) {
        binding.buttonMakePlaylist.isEnabled = value
    }

    private fun initButtonCreatePlaylist() {
        binding.buttonMakePlaylist.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    fun initButtonAddPhoto() {
        binding.imageviewPlaylistImg.setOnClickListener {
            addPhoto()
        }
    }

    private fun addPhoto() {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )

    }
    private fun showDefaultPlaceholder() {
        binding.imageviewPlaylistImg.scaleType = ImageView.ScaleType.CENTER_INSIDE
        binding.imageviewPlaylistImg.setImageResource(R.drawable.img_add_photo_placeholder)
    }

    private fun loadImgToPlaceHolder(uri: Uri) {
        try {
            binding.imageviewPlaylistImg.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.imageviewPlaylistImg.setImageURI(uri)
            Glide.with(requireContext())
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8F,requireContext())))
                .into(binding.imageviewPlaylistImg)
        } catch (e: Exception) {
            showDefaultPlaceholder()
        }

    }


    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun initEditTextPlaylistTitle() {
        binding.editTextPlaylistTitle.addTextChangedListener(
            afterTextChanged = {
                text ->
                viewModel.updateName(text.toString())
            }
        )
    }

    private fun initEditTextPlaylistDescription() {
        binding.editTextPlaylistDescription.addTextChangedListener(
            afterTextChanged = {
                text ->
                viewModel.updateDescription(text.toString())
            }
        )
    }
}