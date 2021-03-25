package com.egemeninceler.donempro

import android.graphics.Bitmap

import com.egemeninceler.donempro.util.rotate90FImage
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.Socket

class Client(address: String, port: Int){
    var connection: Socket = Socket(address, port)

    fun write(bytes: ByteArray) {
        try {
            connection.soTimeout = 10000
            var writer: OutputStream = connection.getOutputStream()
            val baos = ByteArrayOutputStream()
            val bitmap = rotate90FImage(bytes)
            resizeImage(bitmap!!).compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val byteArray = baos.toByteArray()

            Thread.sleep(250)
            writer.write(byteArray)
            writer.write("sended".toByteArray())
            writer.flush()
            baos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resizeImage(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 480, 600, true)
    }

    fun shutdown() {
        if (connection.isConnected) {
            connection.close()
        }
    }
}