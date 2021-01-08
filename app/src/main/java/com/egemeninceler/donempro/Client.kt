package com.egemeninceler.donempro

import android.graphics.Bitmap
import com.egemeninceler.donempro.util.rotate90FImage
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*

class Client(address: String, port: Int) {
    var connection: Socket = Socket(address, port)

    init {

        println("Connected to server at $address port $port")

    }


    fun write(bytes: ByteArray) {

        try {

            connection.soTimeout = 10000
            var writer: OutputStream = connection.getOutputStream()


            println(bytes)
            val baos = ByteArrayOutputStream()
//        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val bitmap = rotate90FImage(bytes)
            resizeImage(bitmap!!).compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val byteArray = baos.toByteArray()

            println("byteArray: " + byteArray)

            writer.write(byteArray)
            writer.write("sended".toByteArray())
            writer.flush()
            baos.flush()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun read() {
//
//        val client: Socket = client
//        val reader: Scanner = Scanner(client.getInputStream())
//        val text = reader.nextLine()
//        println(text)
//    }

    fun resizeImage(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 480, 600, true)

    }

    fun shutdown() {
        println("aaa: ${connection.isConnected}")
        if (connection.isConnected){
            connection.close()
        }

    }
}