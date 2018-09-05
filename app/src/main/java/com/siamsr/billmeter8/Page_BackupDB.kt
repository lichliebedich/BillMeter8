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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.properties.Delegates

class Page_BackupDB : AppCompatActivity() {

    internal var dialog: ProgressDialog? = null
    internal var myDB: DBHelper  by Delegates.notNull()
    private var db: SQLiteDatabase? = null
    internal var tvStatus: TextView?= null
    internal var USERID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_backupdb)

        myDB = DBHelper(this)
        db = myDB!!.writableDatabase
        getEmpInfo()

        tvStatus = findViewById(R.id.tvstatus)
        tvStatus?.text = "สถานะ : เตรียมพร้อม"
        val btnBackup : Button = findViewById(R.id.btnbackupdb)
        btnBackup.setOnClickListener {
            // TODO Auto-generated method stub
            val dialog= ProgressDialog(this)
            dialog.setMessage("กำลังสำรองข้อมูล")
            dialog.setTitle("กรุณารอสักครู่...")
            dialog.setCancelable(false)
            dialog.isIndeterminate=true
            dialog.show()

            val thread = Thread(Runnable {


                Log.d("C", "BackupDB")
                try {
                    Thread.sleep(5000)
                    Check_and_Create_folder()
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

                    month = month + 1

                    val `MONTH$`: String
                    if (month <= 9) {
                        `MONTH$` = "0$month"
                    } else {
                        `MONTH$` = "" + month
                    }    //Set month to MM


                    val YYYYDDMM = "" + day + `MONTH$` + year
                    val FileDate = YYYYDDMM + "-" + time.format("%H%M")


                    val path = sd.toString() + "/PWA/BACKUP/" + "PWAData" + USERID + "-" + FileDate + ".xml"
                    val pathIMG = sd.toString() + "/PWA/BACKUP/" + "PWAImage" + USERID + "-" + FileDate + ".xml"
                    Log.d("D", "STARTxml")
                    val databaseDump = DatabaseDump(myDB!!.readableDatabase, path)
                    databaseDump.exportData()
//                    val databaseIMGDump = DatabaseIMGDump(myDB!!.readableDatabase, pathIMG)
//                    databaseIMGDump.exportData()
                    Log.d("E", "Endxml")

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                dialog.dismiss()
            })

            thread.start()
            tvStatus?.text = "สถานะ : สำรองข้อมูลเรียบร้อยแล้ว"
        }

    }


    fun Check_and_Create_folder() {
        val sd = Environment.getExternalStorageDirectory()
        var P_Directory: Boolean
        val p_root = sd.toString() + "/PWA"
        val p_backup = sd.toString() + "/PWA/BACKUP"
        val p_database = sd.toString() + "/PWA/DATABASE"

        val P_Path = File(p_root)
        if (!P_Path.exists()) {
            P_Directory = P_Path.mkdir()
            if (P_Directory) {
                Log.d("", "Path")
            }
        }
        val P_PathBackup = File(p_backup)
        if (!P_PathBackup.exists()) {
            P_Directory = P_PathBackup.mkdir()
            if (P_Directory) {
                Log.d("", "BackupPath")
            }
        }
        val P_PathDatabase = File(p_database)
        if (!P_PathDatabase.exists()) {
            P_Directory = P_PathDatabase.mkdir()
            if (P_Directory) {
                Log.d("", "DatabasePath")
            }
        }

    }

    private fun getEmpInfo() {
        val curEmp: Cursor?
        curEmp = myDB!!.select_EmpData()

        if (curEmp != null) {
            if (curEmp.moveToFirst()) {
                USERID = curEmp.getString(curEmp.getColumnIndex("empid"))

            }
        }
    }
}
