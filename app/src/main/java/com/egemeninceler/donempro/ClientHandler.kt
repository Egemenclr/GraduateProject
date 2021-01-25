package com.egemeninceler.donempro

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.egemeninceler.donempro.Model.Model
import java.io.DataInputStream
import java.io.IOException
import java.nio.charset.Charset

class ClientHandler(
    private val dataInputStream: DataInputStream? = null,
    private val context: Context? = null
) : Thread() {
    @Volatile
    public var deneme = arrayOf(-1, "egemen")


    override fun run() {
        while (true) {
            try {

//                println("datainputstream ${dataInputStream!!.available()}")
                if (dataInputStream!!.available() > 0) {

//                    Log.i(TAG, "Received: " + dataInputStream.readUTF())
                    println("res")
                    var response  = dataInputStream.readUTF()
//                    val response = dataInputStream.bufferedReader(Charset.forName("utf-8"))

                    deneme = arrayOf(250, 434, "cat", 97.76)
                    var m = Model(
                        deneme[0] as Int, deneme[1] as Int,
                        deneme[2] as String, deneme[3] as Double
                    )

                    var intent = Intent("com.android.activiy.send_data")
                    intent.putExtra("model", dataInputStream.readUTF())
                    try {
                        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


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

    fun getList(): Array<out Any> {
        return deneme
    }

    companion object {
        private val TAG = ClientHandler::class.java.simpleName
    }

}