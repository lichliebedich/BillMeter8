package com.siamsr.billmeter8

import android.database.sqlite.SQLiteDatabase
import android.util.Base64
import android.util.Log
import java.io.*

class DatabaseIMGDump(private val mDb: SQLiteDatabase, destXml: String) {

    companion object {
        private val CLOSING_WITH_TICK = ">"
        private val START_DB = "<export-database name='"
        private val END_DB = "</>"
        private val START_TABLE = "<table name='"
        private val END_TABLE = "</table>"
        private val START_ROW = "<row>"
        private val END_ROW = "</row>"
        private val START_COL = "<"
        private val END_COL = "</col>"
    }

    private var mDestXmlFilename = "/CIS/exportimg.xml"
    private var mExporter: Exporter? = null

    init {
        mDestXmlFilename = destXml

        try {
            // create a file on sdcard1 to export the
            // database contents to
            val myFile = File(mDestXmlFilename)
            myFile.createNewFile()

            val fOut = FileOutputStream(myFile)
            val bos = BufferedOutputStream(fOut)

            mExporter = Exporter(bos)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun exportData() {

        try {
            // mExporter.startDbExport(mDb.getPath());

            // get the tables out of the given sqlite database
            val sql = "SELECT * FROM sqlite_master"

            val cur = mDb.rawQuery(sql, arrayOfNulls(0))
            cur.moveToFirst()

            var tableName: String
            while (cur.position < cur.count) {
                tableName = cur.getString(cur.getColumnIndex("name"))
                Log.d("table", tableName)
                // don't process these two tables since they are used
                // for metadata
                //if (!tableName.equals("android_metadata")
                //&& !tableName.equals("sqlite_sequence")) {
                if (tableName == "PWAIMG") {

                    Log.d(">>", tableName)
                    exportTable(tableName)
                }
                //Log.d(">>", cur.getString(cur.getColumnIndex("custcode")));
                cur.moveToNext()
            }
            // mExporter.endDbExport();
            mExporter!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    private fun exportTable(tableName: String) {
        mExporter!!.startTable(tableName)

        // get everything from the table
        val sql = "SELECT * FROM PWAIMG"
        val cur = mDb.rawQuery(sql, arrayOfNulls(0))
        val numcols = cur.columnCount
        Log.d("จำนวนคอลัมภ์", numcols.toString())
        cur.moveToFirst()

        // move through the table, creating rows
        // and adding each column with name and value
        // to the row
        while (cur.position < cur.count) {
            mExporter!!.startRow()
            var name: String
            var `val`: String
            var valimg: ByteArray
            //ไม่เอาคอลัมภ์แรกDATA
            for (idx in 0 until numcols) {
                if (idx == numcols - 1) {
                    //Log.d(">>","ฟิลล์รูปภาพ");

                    name = cur.getColumnName(idx)
                    valimg = cur.getBlob(idx)
                    val encodedImage = Base64.encodeToString(valimg, Base64.DEFAULT)
                    // Log.d("", encodedImage.toString());
                    mExporter!!.addColumn(name, encodedImage)
                } else {
                    // Log.d("จำนวนคอลัมภ์",String.valueOf(idx));
                    name = cur.getColumnName(idx)
                    `val` = cur.getString(idx)

                    mExporter!!.addColumn(name, `val`)
                }
            }//ปิดfor

            mExporter!!.endRow()
            cur.moveToNext()
        }//ปิดwhile

        cur.close()

        mExporter!!.endTable()
    }

    internal inner class Exporter @JvmOverloads constructor(private val mbufferos: BufferedOutputStream? = BufferedOutputStream(FileOutputStream(mDestXmlFilename))) {

        @Throws(IOException::class)
        fun close() {
            mbufferos?.close()
        }

        @Throws(IOException::class)
        fun startDbExport(dbName: String) {
            val stg = "<table>"
            mbufferos!!.write(stg.toByteArray())
        }

        @Throws(IOException::class)
        fun endDbExport() {
            mbufferos!!.write(END_DB.toByteArray())
        }

        @Throws(IOException::class)
        fun startTable(tableName: String) {
            val stg = "<$tableName>"
            mbufferos!!.write(stg.toByteArray())
        }

        @Throws(IOException::class)
        fun endTable() {
            val stg = "</PWAIMG>"
            mbufferos!!.write(stg.toByteArray())
        }

        @Throws(IOException::class)
        fun startRow() {
            mbufferos!!.write(START_ROW.toByteArray())
        }

        @Throws(IOException::class)
        fun endRow() {
            mbufferos!!.write(END_ROW.toByteArray())
        }

        @Throws(IOException::class)
        fun addColumn(name: String, `val`: String) {

            val stg = START_COL + name + CLOSING_WITH_TICK + `val` + "</" + name + ">"
            mbufferos!!.write(stg.toByteArray())
        }

        @Throws(IOException::class)
        fun addColumnIMG(name: String, `val`: ByteArray) {

            val stg = START_COL + name + CLOSING_WITH_TICK + `val` + "</" + name + ">"
            mbufferos!!.write(stg.toByteArray())
        }


    }
}