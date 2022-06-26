package com.kazumaproject.eyetracker.ui.eye_detect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EyeDetectViewModel: ViewModel() {

    /**
     * value for front or back camera
     * false == front camera
     * true == back camera
     * default value is front camera
     **/
    val whichCameraUsed: LiveData<Boolean>
        get() = _whichCameraUsed
    private val _whichCameraUsed = MutableLiveData(false)

    val drawMode: LiveData<Int>
        get() = _drawMode
    private val _drawMode = MutableLiveData(0)

    val colorMode: LiveData<Int>
        get() = _colorMode
    private val _colorMode = MutableLiveData(0)

    val isShowMenu: LiveData<Boolean>
        get() = _isShowMenu
    private val _isShowMenu = MutableLiveData(false)

    fun updateWhichCameraUsed(value: Boolean){
        _whichCameraUsed.value = value
    }

    fun updateDrawMode(value: Int){
        _drawMode.value = value
    }

    fun updateColorMode(value: Int){
        _colorMode.value = value
    }

    fun updateIsShowMenu(value: Boolean){
        _isShowMenu.value = value
    }

}