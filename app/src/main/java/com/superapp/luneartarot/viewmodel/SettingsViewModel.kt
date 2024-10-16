package com.superapp.luneartarot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superapp.luneartarot.data.local.PreferencesManager
import com.superapp.luneartarot.workers.DailyCardNotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)

    private val _isMusicEnabled = MutableStateFlow(true)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled

    private val _isVibrationEnabled = MutableStateFlow(true)
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled

    private val _isNotificationEnabled = MutableStateFlow(true)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled

    var onMusicEnabledChanged: ((Boolean) -> Unit)? = null

    init {
        viewModelScope.launch {
            preferencesManager.isMusicEnabled.collect {
                _isMusicEnabled.value = it
                onMusicEnabledChanged?.invoke(it)
            }
        }
        viewModelScope.launch {
            preferencesManager.isVibrationEnabled.collect {
                _isVibrationEnabled.value = it
            }
        }
        viewModelScope.launch {
            preferencesManager.isNotificationEnabled.collect {
                _isNotificationEnabled.value = it
            }
        }
    }

    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setMusicEnabled(enabled)
            _isMusicEnabled.value = enabled
            onMusicEnabledChanged?.invoke(enabled)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setVibrationEnabled(enabled)
            _isVibrationEnabled.value = enabled
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationEnabled(enabled)
            _isNotificationEnabled.value = enabled

            if (enabled) {
                DailyCardNotificationWorker.schedule(getApplication())
            } else {
                DailyCardNotificationWorker.cancel(getApplication())
            }
        }
    }
}