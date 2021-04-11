package com.egemeninceler.donempro

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.egemeninceler.donempro.Service.ServerService
import com.egemeninceler.donempro.util.rotate90FImage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.imageView
import kotlinx.android.synthetic.main.bottom_sheet_persistent.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (byte: ByteArray) -> Unit

class MainActivity : AppCompatActivity() {
    //Camera variable initialization
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    var client: Client? = null
    lateinit var frameLayout: CoordinatorLayout
    lateinit var items: List<String>
    var isCheck = false
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frameLayout = findViewById<CoordinatorLayout>(R.id.coordinator)

        // Arka plandan gelen cevapları dinleyen servisin başlatılması.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(applicationContext, ServerService::class.java))
        } else {
            startService(Intent(applicationContext, ServerService::class.java))
        }
        var bottomSheet = findViewById<View>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object:
            BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == 5){
                    isCheck = false
                    startCamera()
                }
            }
        })

        // Buton tıklama işlemleri
        bulunan1.setOnClickListener {
            isCheck = true
            if (client != null) {
                client!!.shutdown()
            }
            cameraExecutor.shutdown()
            bottomSheet.visibility = View.VISIBLE
            
            bulunan1.visibility = View.INVISIBLE
            bulunan2.visibility = View.INVISIBLE
            var view = findViewById<WebView>(R.id.webView)
            view.getSettings().setJavaScriptEnabled(true)
            var button = findViewById<Button>(R.id.detailButton)
            button.visibility = View.VISIBLE
            view.loadUrl("https://www.google.com/search?q=" + items[2] + "&tbm=shop")
        }
        bulunan2.setOnClickListener {
            isCheck = true
            if (client != null) {
                client!!.shutdown()

            }
            cameraExecutor.shutdown()

            bottomSheet.visibility = View.VISIBLE
            bulunan1.visibility = View.INVISIBLE
            bulunan2.visibility = View.INVISIBLE
            var view = findViewById<WebView>(R.id.webView)
            view.getSettings().setJavaScriptEnabled(true)
            view.loadUrl("https://www.google.com/search?q=" + items[5] + "&tbm=shop")
        }
        detailButton.setOnClickListener {
            var intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Kameranın başlatılması.
        cameraExecutor = Executors.newSingleThreadExecutor()
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        // Servisten gelen cevapları broadcast üzerinden dinleme.
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver
            , IntentFilter("com.android.activiy.send_data")
        )
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var message = intent.getStringExtra("model")
            // Gelen metnin parse edilmesi ve ekranda gösterme işlemleri.
            if (message != null) {
                message = message.replace('[', ' ')
                message = message.replace(']', ' ')
                items = message.split(',')

                runOnUiThread {
                    try {
                        if (items.size > 1 && !isCheck) {
                            bulunan1.visibility = View.INVISIBLE
                            bulunan2.visibility = View.INVISIBLE
                            var size = items.size - 1
                            var temp = 1
                            for (i in 0..size step 3) {
                                if (temp == 1) {
                                    setTextView(bulunan1, items, i)
                                } else if (temp == 2) {
                                    setTextView(bulunan2, items, i)
                                }
                                temp += 1
                            }
                        }
                        else{
                            bulunan1.visibility = View.INVISIBLE
                            bulunan2.visibility = View.INVISIBLE
                        }
                    } catch (exception: java.lang.Exception) {
                        Log.e("Error", exception.toString())
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        isCheck = false
        bottomSheet.visibility = View.INVISIBLE
        startCamera()

    }

    /**
     * Gelen cevaba göre butonun gösterilmesi.
     */
    private fun setTextView(textView: Button, items: List<String>, i: Int) {
        var params = CoordinatorLayout.LayoutParams(
            50,
            50
        )
        var customWidth = (items[i].toFloat().toInt()) * 0.45
        var customHeight = (items[i+1].toFloat().toInt()) * 0.60
        params.setMargins(items[i].toFloat().toInt() + customWidth.toInt(), items[i+1].toFloat().toInt() +customHeight.toInt(), 0, 0)

        textView.layoutParams = params
        textView.visibility = View.VISIBLE
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()
            // Camerax imageAnalyzer
            // Take frame from camerax
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer {
                        runOnUiThread {
                            //Rotate and set image to frame that comming from analyzer
                            imageView.setImageBitmap(rotate90FImage(it))
                        }
                        // Socket connection and send data.
                        GlobalScope.launch {
                            try {
                                val address = getString(R.string.sendIP)
                                val port = getString(R.string.sendPort).toInt()
                                client = Client(address, port)
                                client?.write(it)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        //Request camera permission
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (client != null) {
            client!!.shutdown()
        }
        cameraExecutor.shutdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        if (client != null) {
            client!!.shutdown()
        }
    }

    companion object {
        private const val TAG = "CameraX"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

//Camerax listener.
private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy) {
        // Take frame from camera
        val yBuffer = image.planes[0].buffer // Y
        val vuBuffer = image.planes[2].buffer // VU
        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()
        val nv21 = ByteArray(ySize + vuSize)
        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        yBuffer.clear()
        vuBuffer.clear()

        listener(imageBytes)
        image.close()
    }


}