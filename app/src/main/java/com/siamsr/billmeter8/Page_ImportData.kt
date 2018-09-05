package com.siamsr.billmeter8

import android.app.ProgressDialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.RingtoneManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.format.Time
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.properties.Delegates

class Page_ImportData : AppCompatActivity() {

    companion object {

        lateinit var pwaDB :DBHelper
        private val DB_PATH = "/data/data/com.siamsr.billmeter8/databases/"
        private val DB_NAME = "PWA"
        internal var tvempid: TextView?=null
        internal var tvempfname: TextView?=null
        internal var tvemplname: TextView?=null
        internal var tvwwcode: TextView?=null
        internal var tvstatus: TextView?=null
        internal var progressDialog: ProgressDialog? = null
        //Thread thread;
        private val handler = Handler()
        internal var USERID: String? = null
        private var myDataBase: SQLiteDatabase  by Delegates.notNull()
        internal var Import_PATH = "/PWA/DOWNLOAD/PwaData.xml"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_importdata)


        pwaDB = DBHelper(this)

        tvempid = findViewById(R.id.tvempid) as TextView
        tvempfname = findViewById(R.id.tvempname) as TextView
        tvemplname = findViewById(R.id.tvempsurname) as TextView
        tvwwcode = findViewById(R.id.tvempwwcode) as TextView
        tvstatus = findViewById(R.id.tv_status) as TextView

        tvstatus?.text = "สถานะ : เตรียมพร้อม"

        val bnimport :Button = findViewById(R.id.btnImportData)
        bnimport.setOnClickListener {
            // TODO Auto-generated method stub

            try {

                val sd = Environment.getExternalStorageDirectory()
                val data = Environment.getDataDirectory()
                if (sd.canWrite()) {
                    Log.d("C", "BackupDBbegin2")
                    val currentDBPath = "//data//$packageName//databases//PWA"
                    val backupDBPath = "/PWA/DATABASE/PwaDB.sqlite"
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

                month = month + 1

                val `MONTH$`: String
                if (month <= 9) {
                    `MONTH$` = "0$month"
                } else {
                    `MONTH$` = "" + month
                }    //Set month to MM


                val YYYYDDMM = "" + day + `MONTH$` + year
                val FileDate = YYYYDDMM + "-" + time.format("%H%M")


                val path = sd.toString() + "/PWA/BACKUP/" + "PWAData-" + FileDate + ".xml"
                val pathIMG = sd.toString() + "/PWA/BACKUP/" + "PWAImage-" + FileDate + ".xml"
                Log.d("D", "STARTxml")
                val databaseDump = DatabaseDump(pwaDB.readableDatabase, path)
                databaseDump.exportData()
//                val databaseIMGDump = DatabaseIMGDump(pwaDB!!.readableDatabase, pathIMG)
//                databaseIMGDump.exportData()
//                Log.d("E", "Endxml")

                val ExStroage = Environment.getExternalStorageDirectory()

                Import_PATH = ExStroage.toString() + Import_PATH
                if ((!isFileExsist(Import_PATH)!!)!!) {
                    Log.e("blubb", "File not exists")
                    tvstatus?.text = "สถานะ : ไม่พบข้อมูลเส้นทาง  PwaData.xml"
                } else {

                    importDB()

                }

                getEmpInfo()
            } catch (e: Exception) {
                // TODO: handle exception

            }
        }

        getEmpInfo()

    }

    fun isFileExsist(filepath: String): Boolean? {

        try {
            val file = File(filepath)
            return file.exists()
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            return false
        }

    }

    fun importDB() {
        Toast.makeText(this, "สำรองข้อมูลเรียบร้อย .. เตรียมนำเข้าข้อมูล ..", Toast.LENGTH_SHORT).show()
        tvstatus?.text = "สถานะ : กำลังนำเข้าข้อมูล..."
        progressDialog = ProgressDialog.show(this, "", "กำลังนำเข้าข้อมูล...", true)
        val runnable = Runnable {
            // TODO Auto-generated method stub
            try {
                //Open the database
                Thread.sleep(5000)
                handler.post {
                    // Handler thread
                    val myPath = DB_PATH + DB_NAME
                    myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)
                    Log.d("ex", "1")

                    pwaDB!!.onUpgrade(myDataBase, 1, 2)
                    tvstatus?.text = "สถานะ : นำเข้าข้อมูลเรียบร้อยแล้ว"
                    showNotificationUP()
                    Log.d("ex", "2")

                    //Toast.makeText(this, "นำเข้าข้อมูลเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                }


            } catch (e: Exception) {
                // TODO: handle exception
                tvstatus?.text = "สถานะ : นำเข้าข้อมูลไม่สำเร็จ"
                e.printStackTrace()
                //Toast.makeText(this, "เกิดข้อผิดพลาดในการในเข้าข้อมูล !!", Toast.LENGTH_SHORT).show();
            }

            progressDialog!!.dismiss()
        }
        Thread(runnable).start()


    }


    fun getEmpInfo() {

        val curEmp: Cursor?
        curEmp = pwaDB!!.select_EmpData()

        if (curEmp != null && curEmp.moveToFirst()) {
            tvempid?.text = curEmp.getString(curEmp.getColumnIndex("empid"))
            tvempfname?.text = curEmp.getString(curEmp.getColumnIndex("empname"))
            tvemplname?.text = curEmp.getString(curEmp.getColumnIndex("emplastname"))
            tvwwcode?.text = curEmp.getString(curEmp.getColumnIndex("wwcode"))
        }
    }

    private fun showNotificationUP() {

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
    }


}
