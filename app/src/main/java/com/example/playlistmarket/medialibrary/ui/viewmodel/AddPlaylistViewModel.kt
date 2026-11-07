package com.example.playlistmarket.medialibrary.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmarket.medialibrary.domain.api.PlaylistInteractor
import com.example.playlistmarket.medialibrary.domain.models.Playlist
import com.example.playlistmarket.medialibrary.domain.models.PlaylistPattern
import com.example.playlistmarket.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AddPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    application: Application
): AndroidViewModel(application) {

    private val playlistPattern: PlaylistPattern = PlaylistPattern()
    private var playlist: Playlist? = null

    private val loadImgJob: Job? = null

    private val stateViewModel = MutableLiveData<AddPlaylistState>(AddPlaylistState.EnteringDataState())

    fun observeStateViewModel(): LiveData<AddPlaylistState> = stateViewModel


    fun createPlaylistCancel() {
        if (checkPattern()) stateViewModel.postValue(AddPlaylistState.WarningState)
        else stateViewModel.postValue(AddPlaylistState.CloseState)
    }

    private fun checkPattern(): Boolean =
            playlistPattern.name.isNotBlank() ||
            playlistPattern.description.isNotBlank() ||
            playlistPattern.coverUrl.isNotBlank()

    private fun checkPatternToCratePlaylist(): Boolean = playlistPattern.name.isNotBlank()

    fun updateName(name: String) {
        playlistPattern.name = name
        if (checkPatternToCratePlaylist()) stateViewModel.postValue(AddPlaylistState.EnteringDataState(true))
        else stateViewModel.postValue(AddPlaylistState.EnteringDataState(false))
    }

    fun updateDescription(description: String) {
        playlistPattern.description = description
    }

    fun updateCoverUrl(url: Uri) {
        playlistPattern.coverUrl = url.toString()
    }

    fun createPlaylist(){
        viewModelScope.launch {
            val ExternalUriImg = playlistPattern.coverUrl
            if (ExternalUriImg.isNotBlank()) {
                playlistPattern.coverUrl = saveImageToPrivateStorage().toString()
            } else playlistPattern.coverUrl = ""
            playlist = playlistInteractor.createPlaylist(playlistPattern)
            stateViewModel.postValue(AddPlaylistState.PlaylistCreatedState(playlist!!))
        }
    }

    fun forcedCancel() {
        stateViewModel.postValue(AddPlaylistState.CloseState)
    }

    private suspend fun saveImageToPrivateStorage(): Uri? {
        return withContext(Dispatchers.IO) {
            val context = getApplication<Application>()

            // 1. Создаем правильный путь к папке
            val filePath = File(context.filesDir, NAME_IMG_DIR_IN_PRIVATE_DIR)
            if (!filePath.exists()) {
                val isCreated = filePath.mkdirs()
                Log.d("SAVE_IMAGE", "Папка создана: $isCreated, путь: ${filePath.absolutePath}")
            }

            // 2. Генерируем уникальное имя для файла
            val fileName = "${UUID.randomUUID()}.jpeg"
            val file = File(filePath, fileName)

            try {
                // Переводим строковый URL в Uri
                val sourceUri = playlistPattern.coverUrl.toUri()
                Log.d("SAVE_IMAGE", "Пытаемся открыть поток для Uri: $sourceUri")

                // 3. Открываем поток чтения
                context.contentResolver.openInputStream(sourceUri).use { inputStream ->
                    if (inputStream == null) {
                        Log.e("SAVE_IMAGE", "Ошибка: openInputStream вернул NULL! Доступ к Uri запрещен системой.")
                        return@withContext null
                    }

                    // 4. Открываем поток записи и сжимаем картинку
                    FileOutputStream(file).use { outputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap == null) {
                            Log.e("SAVE_IMAGE", "Ошибка: Не удалось декодировать Bitmap из потока.")
                            return@withContext null
                        }

                        val isCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                        Log.d("SAVE_IMAGE", "Сжатие картинки прошло: $isCompressed")
                    }
                }

                Log.d("SAVE_IMAGE", "Файл УСПЕШНО сохранен! Размер: ${file.length()} байт. Путь: ${file.absolutePath}")
                // Возвращаем Uri созданного нами локального приватного файла
                Uri.fromFile(file)

            } catch (e: Exception) {
                Log.e("SAVE_IMAGE", "КРИТИЧЕСКАЯ ОШИБКА при сохранении файла на диск: ", e)
                null // Возвращаем null, чтобы приложение не падало
            }
        }
    }

    companion object {
        val NAME_IMG_DIR_IN_PRIVATE_DIR = "playlistCovers"
    }


}