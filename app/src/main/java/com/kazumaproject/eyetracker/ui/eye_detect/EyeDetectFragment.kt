package com.kazumaproject.eyetracker.ui.eye_detect

import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.camera.camera2.internal.compat.workaround.TargetAspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.kazumaproject.eyetracker.R
import com.kazumaproject.eyetracker.databinding.FragmentEyeDetectBinding
import com.kazumaproject.eyetracker.detection.FaceDetection
import com.kazumaproject.eyetracker.ui.BaseFragment
import com.kazumaproject.eyetracker.util.AppPreferences
import com.tzutalin.dlib.Constants
import com.tzutalin.dlib.FaceDet
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class EyeDetectFragment : BaseFragment(R.layout.fragment_eye_detect) {

    private var _binding : FragmentEyeDetectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EyeDetectViewModel by viewModels()

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var faceDet : FaceDet

    private lateinit var overlaySurfaceView: OverlaySurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        faceDet = FaceDet(Constants.getFaceShapeModelPath())
        AppPreferences.init(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEyeDetectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.whichCameraUsed.observe(viewLifecycleOwner){}
        viewModel.drawMode.observe(viewLifecycleOwner){}
        viewModel.colorMode.observe(viewLifecycleOwner){}
        viewModel.isShowMenu.observe(viewLifecycleOwner){}
        viewModel.updateDrawMode(AppPreferences.currentMode)
        viewModel.updateColorMode(AppPreferences.currentColorMode)
        viewModel.updateIsShowMenu(false)
        viewModel.drawMode.value?.let { mode ->
            when(mode){
                0 -> binding.mode.selectButton(binding.btn1)
                1 -> binding.mode.selectButton(binding.btn2)
                else -> binding.mode.selectButton(binding.btn3)
            }
        }
        viewModel.colorMode.value?.let { mode ->
            when(mode){
                0 -> binding.colorMode.selectButton(binding.color1)
                1 -> binding.colorMode.selectButton(binding.color2)
                else -> binding.colorMode.selectButton(binding.color3)
            }
        }
        setCameraFrontOrBack()
        overlaySurfaceView = OverlaySurfaceView(binding.resultView)
        binding.mode.setOnSelectListener {
            when(it.id){
                binding.btn1.id ->{
                    viewModel.updateDrawMode(0)
                    AppPreferences.currentMode = 0
                    startCamera()
                    binding.settingMenuParent.visibility = View.GONE
                    viewModel.updateIsShowMenu(false)
                }
                binding.btn2.id ->{
                    viewModel.updateDrawMode(1)
                    AppPreferences.currentMode = 1
                    startCamera()
                    binding.settingMenuParent.visibility = View.GONE
                    viewModel.updateIsShowMenu(false)
                }
                binding.btn3.id ->{
                    viewModel.updateDrawMode(2)
                    AppPreferences.currentMode = 2
                    startCamera()
                    binding.settingMenuParent.visibility = View.GONE
                    viewModel.updateIsShowMenu(false)
                }

            }
        }
        binding.colorMode.setOnSelectListener {
            when(it.id){
                binding.color1.id ->{
                    viewModel.updateColorMode(0)
                    AppPreferences.currentColorMode = 0
                    startCamera()
                    binding.settingMenuParent.visibility = View.GONE
                    viewModel.updateIsShowMenu(false)
                }
                binding.color2.id ->{
                    viewModel.updateColorMode(1)
                    AppPreferences.currentColorMode = 1
                    startCamera()
                    binding.settingMenuParent.visibility = View.GONE
                    viewModel.updateIsShowMenu(false)
                }
                binding.color3.id ->{
                    viewModel.updateColorMode(2)
                    AppPreferences.currentColorMode = 2
                    startCamera()
                    binding.settingMenuParent.visibility = View.GONE
                    viewModel.updateIsShowMenu(false)
                }
            }
        }
        binding.settingBtn.setOnClickListener {
            viewModel.isShowMenu.value?.let { isShow ->
                if (isShow){
                    binding.settingMenuParent.visibility = View.GONE
                    viewModel.updateIsShowMenu(false)
                } else {
                    binding.settingMenuParent.visibility = View.VISIBLE
                    viewModel.updateIsShowMenu(true)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!requireActivity().isChangingConfigurations){
            _binding = null
            cameraExecutor.shutdown()
            faceDet.release()
        }
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext().applicationContext)

        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //Front Camera by default
            val cameraSelector = if (AppPreferences.currentCameraState) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            val orientation = if (Configuration.ORIENTATION_PORTRAIT == resources.configuration.orientation) 1 else 0

            val aspectRatio = if (orientation == 0) TargetAspectRatio.RATIO_16_9 else TargetAspectRatio.RATIO_4_3

            val scaleType = if (viewModel.whichCameraUsed.value == true) PreviewView.ScaleType.FIT_CENTER else PreviewView.ScaleType.FILL_CENTER

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.cameraView.surfaceProvider) }

            binding.cameraView.scaleType = scaleType

            val displayMetrics = DisplayMetrics()
            (requireActivity().windowManager as WindowManager).defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels

            Timber.d("f w: $width   $height")

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetRotation(binding.cameraView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetAspectRatio(aspectRatio)
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        FaceDetection(
                            faceDet,
                            viewModel.whichCameraUsed.value?:false
                        ){ list ->
                            Timber.d("detected: $list")
                            viewModel.drawMode.value?.let { mode ->
                                viewModel.colorMode.value?.let { colorMode->
                                    when(mode){
                                        0 ->{
                                            overlaySurfaceView.drawLandMarks(list,viewModel.whichCameraUsed.value?:false,colorMode, orientation)
                                        }
                                        1 ->{
                                            overlaySurfaceView.drawEyes(list,viewModel.whichCameraUsed.value?:false, colorMode,orientation)
                                        }
                                        2 ->{
                                            overlaySurfaceView.drawAroundEye(list,viewModel.whichCameraUsed.value?:false, colorMode,orientation)
                                        }
                                        3 ->{
                                            overlaySurfaceView.clearView()
                                        }
                                    }
                                }
                            }

                        }
                    )
                }

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            } catch (exc: Exception) {
                Timber.e("ERROR: Camera", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext().applicationContext))
    }

    private fun setCameraFrontOrBack(){
        binding.toggleFrontBackCameraImg.setOnClickListener {
            AppPreferences.currentCameraState = !AppPreferences.currentCameraState
            startCamera()
        }
    }


}