package com.egemeninceler.donempro

import android.util.JsonReader
import android.util.Log
import com.egemeninceler.donempro.Model.Model
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*
import kotlin.reflect.typeOf

class ClientHandler(private val dataInputStream: DataInputStream, private val dataOutputStream: DataOutputStream) : Thread() {
    override fun run() {
        while (true) {
            try {
                println("datainputstream ${dataInputStream.available()}")
                if(dataInputStream.available() > 0){

                    //Log.i(TAG, "Received: " + dataInputStream.readUTF())
                    println("res")
                    val response = dataInputStream.bufferedReader(Charset.forName("utf-8"))
                    println("response: " +response.readLine())


//                    dataOutputStream.writeUTF("Hello Client")
//                    sleep(2000L)
                }
            } catch (e: IOException) {
                println("debug")
                e.printStackTrace()
                try {
//                    dataInputStream.close()
//                    dataOutputStream.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            } catch (e: InterruptedException) {
                println("debug2")
                e.printStackTrace()
                try {
//                    dataInputStream.close()
//                    dataOutputStream.close()
                } catch (ex: IOException) {
                    println("debug3")
                    ex.printStackTrace()
                }
            }
        }
    }

    companion object {
        private val TAG = ClientHandler::class.java.simpleName
    }

}