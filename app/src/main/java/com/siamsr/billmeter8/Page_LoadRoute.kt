package com.siamsr.billmeter8

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.*
import java.net.InetAddress

class Page_LoadRoute : AppCompatActivity() {

    companion object {
        private val TAG = "PermissionDemo"
        private val RECORD_REQUEST_CODE = 101
        var wwcode = ""
        var emp_id = ""
        var dialog: ProgressDialog? = null
        var txtstat: TextView? = null
        val REFRESH_SCREEN = 1
        internal var FTPHOST = "ftp.siamrajdataservice.com"
        internal var FTPUSER = "php_billing"
        internal var FTPPASS = "asbi12"
//        internal var FTPHOST = "203.151.43.168"
//        internal var FTPUSER = "ftpadmin"
//        internal var FTPPASS = "siamrajadmin"
        internal var FTPPORT = 21

    }

    var isInternetAvailable: Boolean? = null
        get() {


            try {
                val ipAddr = InetAddress.getByName("google.com")

                return !ipAddr.equals("")

            } catch (e: Exception) {
                return false
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_loadroute)

        setupPermissions()

        val spin1 : Spinner = findViewById(R.id.spinner1)
        val btn1 : Button = findViewById(R.id.btn_find)
        txtstat = findViewById(R.id.txtstat)


        txtstat?.text = "เตรียมพร้อม"
        // Perform action on click
        btn1.setOnClickListener {
            //            val title = "Downloading Data"
//            val msg = "Please Wait..."
//            dialog = ProgressDialog.show(this@ftpload, title, msg)
            val dialog= ProgressDialog(this)
            dialog.setMessage("Please wait")
            dialog.setTitle("Loading")
            dialog.setCancelable(false)
            dialog.isIndeterminate=true
            dialog.show()
            Log.d("test", "test")
            val txtemp = findViewById(R.id.txtemp) as EditText
            emp_id = txtemp.text.toString()
            val tempwwcode = spin1.selectedItemPosition
            when (tempwwcode) {
                0 -> wwcode = "0000000"
                1 -> wwcode = "5532011"
                2 -> wwcode = "5532012"
                3 -> wwcode = "5532013"
                4 -> wwcode = "5532014"
                5 -> wwcode = "5532015"
                6 -> wwcode = "1130"
                7 -> wwcode = "1131"
                8 -> wwcode = "1132"
                9 -> wwcode = "5532019"
                10 -> wwcode = "5532020"
                11 -> wwcode = "5532021"
                12 -> wwcode = "5532022"
                13 -> wwcode = "5532023"
                14 -> wwcode = "1141"
                15 -> wwcode = "1142"
                16 -> wwcode = "1143"
                17 -> wwcode = "1246"
                18 -> wwcode = "1144"
                19 -> wwcode = "1145"
                20 -> wwcode = "5532030"
                21 -> wwcode = "1039"
                22 -> wwcode = "1038"
                23 -> wwcode = "5512019"
                24 -> wwcode = "5512020"
                25 -> wwcode = "1120"
                else -> {
                }
            }//5532016ยโสธร
            //5532017เลิงนกทา
            //5532018มหาชนะชัย
            //5532024สุรินทร์
            //5532025ศีขรภูมิ
            //5532026รัตนบุรี
            //5532027สังขะ
            //5532028ศรีสะเกษ
            //5532029กัลทรลักษ์
            //ขาณุ
            //กำแพง
            //ตาก
            //แม่สอด
            isInternetAvailable
            if (isInternetAvailable == false) {
                txtstat?.text = "ไม่สามารถเชื่อมต่ออินเทอร์เน็ตได้ กรุณาตรวจสอบ"
                //Toast.makeText(getBaseContext(), "CONNECT FAILED", Toast.LENGTH_SHORT).show();
            } else {
                txtstat?.text = "เชื่อมต่ออินเทอร์เน็ตเรียบร้อยแล้ว"
                //Toast.makeText(getBaseContext(), "CONNECT PASSED", Toast.LENGTH_SHORT).show();
            }

            val thread = Thread(Runnable {
                // TODO Auto-generated method stub
                try {
                    Log.d("test2", "test2")
                    val ftpClient = FTPClient()
                    try {

                        ftpClient.connect(FTPHOST, FTPPORT)
                        ftpClient.login(FTPUSER, FTPPASS)
                        ftpClient.enterLocalPassiveMode()
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

                        val remoteFile2 = "/PWA/8/PCTOHH/$wwcode/$emp_id/PwaData.xml"
                        Log.d("str", remoteFile2)
                        val downloadFile2 = File(Environment.getExternalStorageDirectory(),
                                "/PWA/DOWNLOAD/PwaData.xml")
                        val outputStream2 = BufferedOutputStream(FileOutputStream(downloadFile2))
                        val inputStream : InputStream = ftpClient.retrieveFileStream(remoteFile2)
                        val bytesArray = ByteArray(4096)
                        var bytesRead = inputStream.read(bytesArray)
                        Log.d("begin2", "OK")
                        while (bytesRead != -1) {
                            outputStream2.write(bytesArray, 0, bytesRead)
                            bytesRead = inputStream.read(bytesArray)

                        }
                        println("File #3 has been downloaded successfully.")
                        val success = ftpClient.completePendingCommand()
                        if (success) {
                            println("File #4 has been downloaded successfully.")
                        }
                        outputStream2.close()
                        inputStream.close()
                        Log.d("Success", remoteFile2)
                    } catch (ex: IOException) {
                        println("Error: " + ex.message)
                        ex.printStackTrace()
                    } finally {
                        try {
                            if (ftpClient.isConnected) {
                                ftpClient.logout()
                                ftpClient.disconnect()
                            }
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                        }

                    }
                } catch (e: Exception) {
                    // TODO: handle exception
                    e.printStackTrace()
                }

                dialog.dismiss()

                if (isInternetAvailable == false) {

                } else {
                    hRefresh.sendEmptyMessage(REFRESH_SCREEN)
                }
            })
            thread.start()
        }

    }



    @SuppressLint("HandlerLeak")
    var hRefresh: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                REFRESH_SCREEN -> txtstat?.text = "Download ข้อมูลเรียบร้อยแล้ว"
                else -> {
                }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET),
                RECORD_REQUEST_CODE)
    }
}
