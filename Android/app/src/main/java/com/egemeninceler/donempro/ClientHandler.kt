package com.egemeninceler.donempro

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.egemeninceler.donempro.Model.Model
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ClientHandler(
    private val dataInputStream: DataInputStream? = null,
    private val context: Context? = null
) : Thread() {


    var geldiMi = false

    //var dataInputStream = socket?.getInputStream()

    override fun run() {

        while (true) {
            try {


//                println("datainputstream ${dataInputStream!!.available()}")
                if (dataInputStream!!.available() >= 0) {

                    var socketReadStream    = BufferedReader(InputStreamReader(dataInputStream))
                    var line = socketReadStream.readLine()


                    while(line != null ) {
                        geldiMi = true
                        var intent = Intent("com.android.activiy.send_data")
                        intent.putExtra("model", line.toString())
                        //intent.putExtra("model", deneme)

                        try {
                            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        line = socketReadStream.readLine()


                    }



//                    dataOutputStream.writeUTF("Hello Client")
//                    sleep(2000L)
                }else{
                    print("bosss")
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

    fun returnValue(){
        println("itemG:" + geldiMi)
        while (!geldiMi){
            println("itemW: bekleniyor")
        }
        geldiMi = false
        return
    }

    companion object {
        private val TAG = ClientHandler::class.java.simpleName
    }

}