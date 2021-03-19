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
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.egemeninceler.donempro.ViewModel.MainViewModel
import com.egemeninceler.donempro.util.rotate90FImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.timer

typealias LumaListener = (byte: ByteArray) -> Unit

class MainActivity : AppCompatActivity() {
    //Camera variable initialization
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    var client: Client? = null
    lateinit var frameLayout: FrameLayout
    lateinit var linearLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        /*
        var textView = TextView(this)
        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(254.0.toFloat().toInt(), 341.0.toFloat().toInt(), 10, 10)
        textView.layoutParams = params
        textView.text = "laptop"

        frameLayout.addView(textView)
         */





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(applicationContext, ServerService::class.java))
        } else {
            startService(Intent(applicationContext, ServerService::class.java))

        }
        print("aa")
    }

    override fun onResume() {
        super.onResume()

        cameraExecutor = Executors.newSingleThreadExecutor()
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver
            , IntentFilter("com.android.activiy.send_data")
        )

    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Extract data included in the Intent
            var message = intent.getStringExtra("model")
            println("Got message: $message")
//            Log.d("receiver", "Content: ${message?.type}")
            if (message != null) {
                println("mesaj bos degil")

                //message = message.replace('\n', ' ')
                //message.split('\n')
                message = message.replace('[', ' ')
                message = message.replace(']', ' ')
                var items = message.split(',')
                println()

                //Toast.makeText(applicationContext, stringArray, Toast.LENGTH_SHORT).show()
                runOnUiThread {
                    try {

                        if (items.size > 1) {
                            var size = items.size - 1
                            var temp = 1


                            for (i in 0..size step 3) {
                                if (temp == 1) {
                                    var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                                    params.setMargins(items[i].toFloat().toInt(), items[i+1].toFloat().toInt(), 10, 10)
                                    bulunan1.layoutParams = params
                                    bulunan1.text = items[i+2]
                                    bulunan1.visibility = View.VISIBLE

                                }
                                else if (temp == 2) {
                                    var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                                    params.setMargins(items[i].toFloat().toInt(), items[i+1].toFloat().toInt(), 10, 10)
                                    bulunan2.layoutParams = params
                                    bulunan2.text = items[i+2]
                                    bulunan2.visibility = View.VISIBLE

                                }
                                temp += 1
                            }


                        }
                    } catch (exception: java.lang.Exception) {
                        Log.e("Error", exception.toString())
                        println(exception)
                        println()
                    }


                }
            }

        }

    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
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
                            val instance = ClientHandler(context = baseContext)
                        }

                        // Socket connection and send data.
                        GlobalScope.launch {

                            try {
                                val address = "192.168.1.105"
                                val port = 12400

                                client = Client(address, port)
                                client?.write(it)

//                                val socket = Socket("192.168.1.104", 12400)
//                                val scanner = Scanner(socket.getInputStream())
//                                val printWriter = PrintWriter(socket.getOutputStream())
//                                while (scanner.hasNextLine()) {
//                                    Log.d("ege", "${ scanner.nextLine() }")
//                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    })
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )


            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))


    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        //Request camera permisson
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
//        val direct = ByteBuffer.allocateDirect(1024)
//        val cleanerField: Field = direct.javaClass.getDeclaredField("cleaner")
//        cleanerField.isAccessible = true
//
//        direct.clear()

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

        //Thread.sleep(250)
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