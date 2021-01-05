package com.egemeninceler.donempro

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.delay
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class Client(address: String, port: Int) {
    private val connection: Socket = Socket(address, port)
    private var connected: Boolean = true

//    val server = ServerSocket(9999)
//    val client = server.accept()


    init {
        println("Connected to server at $address on port $port")


    }


    private val writer: OutputStream = connection.getOutputStream()

    fun write(bytes: ByteArray) {
        val baos = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        resizeImage(bitmap).compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val byteArray = baos.toByteArray()

        writer.write(byteArray)
        writer.flush()
    }

//    fun read() {
//        println("Connected to server at: ${server.inetAddress}")
//        println("Connected to server at: ${server.channel}")
//
//        val client: Socket = client
//        val reader: Scanner = Scanner(client.getInputStream())
//        val text = reader.nextLine()
//        println(text)
//    }

    fun resizeImage(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 640, 480, true)

    }
}