package com.egemeninceler.donempro

import android.graphics.Bitmap
import com.egemeninceler.donempro.util.rotate90FImage
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.Socket

class Client(address: String, port: Int) {
    private var connection: Socket = Socket(address, port)
    init {

//        println("Connected to server at $address port $port")

        connection.soTimeout = 10000
        println("timeout:" +connection.soTimeout)
    }

    private val writer: OutputStream = connection.getOutputStream()

    fun write(bytes: ByteArray) {
        println(bytes)
        val baos = ByteArrayOutputStream()
//        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val bitmap = rotate90FImage(bytes)
        resizeImage(bitmap!!).compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val byteArray = baos.toByteArray()


        writer.write(byteArray)
        writer.write("sended".toByteArray())
        writer.flush()
        baos.flush()
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