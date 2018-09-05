package com.siamsr.billmeter8

import android.app.ProgressDialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.format.Time
import android.util.Log
import android.widget.Button
import android.widget.TextView
import it.sauronsoftware.ftp4j.FTPClient
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.properties.Delegates

class Page_UploadOnline : AppCompatActivity() {

    internal var myDB: DBHelper  by Delegates.notNull()
    private var db: SQLiteDatabase? = null

    internal var txtstat: TextView? = null
    internal var dialog: ProgressDialog? = null

    internal var FTPHOST = "ftp.siamrajdataservice.com"
    internal var FTPUSER = "php_billing"
    internal var FTPPASS = "asbi12"
//    internal var SRFTPHOST = "203.151.43.168"
//    internal var SRFTPUSER = "ftpadmin"
//    internal var SRFTPPASS = "siamrajadmin"
    internal var FTPPORT = 21

    internal var USERID: String = ""
    internal var WWCODE: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_uploadonline)

        myDB = DBHelper(this)
        db=myDB.writableDatabase

        var btnUpload  = findViewById<Button> (R.id.btnupload)
        getEmpInfo()


        btnUpload.setOnClickListener {
            val dialog= ProgressDialog(this)
            dialog.setMessage("กำลังส่งข้อมูล")
            dialog.setTitle("กรุณารอสักครู่...")
            dialog.setCancelable(false)
            dialog.isIndeterminate=true
            dialog.show()
            val thread = Thread(Runnable {
                // TODO Auto-generated method stub
                try {

                    Thread.sleep(5000)

                    val sd = Environment.getExternalStorageDirectory()
                    val data = Environment.getDataDirectory()
                    if (sd.canWrite()) {
                        Log.d("C", "BackupDBbegin2")
                        val currentDBPath = "//data//$packageName//databases//PWA"
                        val backupDBPath = "/PWA/DATABASE/PWADB.sqlite"
                        // String backupDBPath = path+"CIS.sqlite";

                        val currentDB = File(data, currentDBPath)
                        val backupDB = File(sd, backupDBPath)

                        if (currentDB.exists()) {
                            Log.d("C", "BackupDBOK")
                            val src = FileInputStream(currentDB).channel
                            val dst = FileOutputStream(backupDB).channel
                            dst.transferFrom(src, 0, src.size())
                            src.close()
                            dst.close()
                        }
                    }
                    val c = Calendar.getInstance()
                    val time = Time()
                    time.setToNow()


                    val day = c.get(Calendar.DATE)
                    var month = c.get(Calendar.MONTH)  //0 = JAN / 11 = DEC
                    val year = c.get(Calendar.YEAR)

                    month = month + 1 //Set int to correct month numeral, i.e 0 = JAN therefore set to 1.

                    val `MONTH$`: String
                    if (month <= 9) {
                        `MONTH$` = "0$month"
                    } else {
                        `MONTH$` = "" + month
                    }    //Set month to MM


                    val YYYYDDMM = "" + day + `MONTH$` + year    //Put date ints into string DD/MM/YYYY
                    val FileDate = YYYYDDMM + "-" + time.format("%H%M")


                    val UPDataPath = sd.toString() + "/PWA/UPLOAD/" + "PWAData" + USERID + "-" + FileDate + ".xml"
                    val UPImgPath = sd.toString() + "/PWA/UPLOAD/" + "PWAImage" + USERID + "-" + FileDate + ".xml"
                    Log.d("D", "STARTxml")
                    val databaseDump = DatabaseDump(myDB.readableDatabase, UPDataPath)
                    databaseDump.exportData()
                    val databaseIMGDump = DatabaseIMGDump(myDB.readableDatabase, UPImgPath)
                    databaseIMGDump.exportData()
                    Log.d("E", "Endxml")
                    val FileUpPath = "/PWA/UPLOAD/PWAData$USERID-$FileDate.xml"
                    val FileImgPath = "/PWA/UPLOAD/PWAImage$USERID-$FileDate.xml"
                    val uploadFile = File(Environment.getExternalStorageDirectory(), FileUpPath)
                    val uploadImg = File(Environment.getExternalStorageDirectory(), FileImgPath)
                    Log.d("ftp", "start")
                    uploadfile(uploadFile)
                    uploadfile(uploadImg)
                    Log.d("ftp", "success")

                    //txtstat!!.text ="สถานะ :อัพโหลดข้อมูลเรียบร้อยแล้ว"

                } catch (e: Exception) {
                    // TODO: handle exception
                    e.printStackTrace()
                    txtstat!!.text = "สถานะ :อัพโหลดข้อมูลล้มเหลว"
                }

                dialog.dismiss()

            })
            thread.start()

            Log.d("ftp", "finish")
        }

    }


    private fun uploadfile(upFile: File) {
        // TODO Auto-generated method stub

        val ftpclient = FTPClient()


        val remoteFile = "/PWA/8/HHTOPC/$WWCODE/$USERID"
        Log.d("FTP", remoteFile)
        try {
            ftpclient.connect(FTPHOST, 21)
            ftpclient.login(FTPUSER, FTPPASS)
            ftpclient.type = FTPClient.TYPE_BINARY

            ftpclient.changeDirectory(remoteFile)
            ftpclient.upload(upFile)

        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            try {
                txtstat?.text = "สถานะ :อัพโหลดไม่สำเร็จ"
                ftpclient.disconnect(true)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }

        }

    }

    private fun getEmpInfo() {
        val curEmp: Cursor?
        curEmp = myDB.select_EmpData()

        if (curEmp != null && curEmp.moveToFirst()) {
            USERID = curEmp.getString(curEmp.getColumnIndex("empid"))
            WWCODE = curEmp.getString(curEmp.getColumnIndex("wwcode"))

        }
    }
}
