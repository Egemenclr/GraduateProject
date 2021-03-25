package com.egemeninceler.donempro

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.*

class ClientHandler(
    private val dataInputStream: DataInputStream? = null,
    private val context: Context? = null
) : Thread() {
    var geldiMi = false

    override fun run() {
        while (true) {
            try {
                if (dataInputStream!!.available() >= 0) {
                    var socketReadStream    = BufferedReader(InputStreamReader(dataInputStream))
                    var line = socketReadStream.readLine()

                    while(line != null ) {
                        geldiMi = true
                        var intent = Intent("com.android.activiy.send_data")
                        intent.putExtra("model", line.toString())

                        try {
                            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        line = socketReadStream.readLine()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                try {
                    dataInputStream?.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                try {
                    dataInputStream?.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
    }
}