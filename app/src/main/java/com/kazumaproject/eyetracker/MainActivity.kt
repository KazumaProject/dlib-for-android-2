package com.kazumaproject.eyetracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kazumaproject.eyetracker.databinding.ActivityMainBinding
import com.kazumaproject.eyetracker.ui.eye_detect.EyeDetectFragment
import com.kazumaproject.eyetracker.util.FileUtil
import com.tzutalin.dlib.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object{
        private const val REQUEST_CODE_PERMISSIONS = 77
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
        )
    }

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val targetPath = Constants.getFaceShapeModelPath()
        if (!File(targetPath).exists()){
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,"Copying necessary files...",Toast.LENGTH_SHORT).show()
                }
                FileUtil.copyFileFromRawToOthers(
                    this@MainActivity,
                    R.raw.shape_predictor_68_face_landmarks,
                    targetPath
                )
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,"Copied shape_predictor_68_face_landmarks",Toast.LENGTH_SHORT).show()
                }
            }
        }

        when{
            !allPermissionsGranted() ->{
                ActivityCompat.requestPermissions(this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }

            !Environment.isExternalStorageManager() ->{
                try {
                    val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                    startActivity(intent)
                }catch (e : Exception){
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    Timber.d("Error: $e")
                }
                this.finish()
            }

            else ->{
                startEyeDetectFragment()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionsGranted()) {
                this.finish()
            } else {
                startEyeDetectFragment()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startEyeDetectFragment(){
        val cameraCaptureFragment = EyeDetectFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(binding.fragmentHostView.id,cameraCaptureFragment)
        transaction.commit()
    }

    private fun moveFiles(){

    }

}