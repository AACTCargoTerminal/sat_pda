package com.aact.sat_pda.screen.camera

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentCameraBinding
import com.aact.sat_pda.screen.BaseFragment
import java.io.File

class CAMERA_Fragment : BaseFragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val cameraModel: CAMERA_Model by lazy { CAMERA_Model() }
    private var imageCapture: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cameraModel


        item.disableEditText(binding.mawb.editText)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.getSettings().setLoadWithOverviewMode(true)
        binding.webView.getSettings().setUseWideViewPort(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera()



        cameraModel.cameraList.observe(viewLifecycleOwner) {
            binding.list.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = cameraModel.cameraHeader,
                bodyList = it,
                selectValue = cameraModel.cameraSelect.value,
                height = 350
            ) {
                cameraModel.cameraSelect.value =
                    Util.validSelectTable(cameraModel.cameraSelect.value, it)
            }
            binding.list.custItem.addView(table)
        }

        cameraModel.cameraSelect.observe(viewLifecycleOwner) {
            val sid = it["EDM_SID"]
            val fileName = it["SOURCE_FILE_NAME"]

            if (sid.isNullOrEmpty()) {

                collapseView(binding.webView, {}, {
                    expand(binding.previewView, {}, {})
                })
            } else {

                collapseView(binding.previewView, {}, {
                    expand(binding.webView, {if (!fileName.isNullOrEmpty()) {
                        val arr1 = fileName.split("_")
                        val yyyymm = arr1[1].substring(0, 6)
                        val url =
                            "http://${Common.selectServer.value}/content/images_pda/$yyyymm/$fileName"
                        binding.webView.loadUrl(url)
                    }}, {

                    })
                })
            }
        }


    }

    override fun onStart() {
        super.onStart()
        val route = Common.route.value
        if (route.params.size > 0) {

            val temp_param = route.params

            temp_param.forEach {
                when (it.first) {
                    "CARGO_CONTROL_SID" -> cameraModel.cargoControlSid = it.second
                    "MAWB" -> cameraModel.mawb.value = it.second
                    "DAMAGE_EXPORT_SID" -> cameraModel.damageExportSid = it.second
                }
            }
        }

        cameraModel.getList()

        for(item in route.bottomItems()){
            when(item){
                is BottomItem.Save -> {
                    item.onClick ={
                        takePhoto()
                    }
                }

                is BottomItem.Delete -> {
                    item.onClick = fun() {
                        val edmSid =
                            cameraModel.cameraSelect.value
                                ?.get("EDM_SID") ?: ""

                        if (edmSid.isEmpty()) {
                            Common.sendError("선택한 항목이 없습니다.")
                            return
                        }

                        cameraModel.deletePicture()
                    }
                }

                else -> null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            createTempFile(requireContext())
        ).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let {
                        cameraModel.parseAndUpload(requireContext(), it)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Common.sendError("저장중 에러발생 : "+ exception.message)
                }
            }
        )
    }

    private fun createTempFile(context: Context): File {
        val dir = context.cacheDir
        return File.createTempFile("IMG_", ".jpg", dir)
    }

    fun expand(view: View, start: () -> Unit, end: () -> Unit) {
        val targetHeight = dpToPx(400)

        view.measure(
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animator = ValueAnimator.ofInt(0, targetHeight).apply {
            duration = 200L
            addUpdateListener {
                view.layoutParams.height = it.animatedValue as Int
                view.requestLayout()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    start()
                }

                override fun onAnimationEnd(animation: Animator) {
                    end()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        animator.start()
    }

    fun collapseView(view: View, start: () -> Unit, end: () -> Unit) {
        val initialHeight = view.height

        val animator = ValueAnimator.ofInt(initialHeight, 0).apply {
            duration = 200L
            addUpdateListener {
                view.layoutParams.height = it.animatedValue as Int
                view.requestLayout()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    start()
                }

                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    end()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        animator.start()
    }
}