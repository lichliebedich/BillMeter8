package com.siamsr.billmeter8

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.*

class DatabaseDump(private val mDb: SQLiteDatabase?, destXml: String) {

    private val CLOSING_WITH_TICK = ">"
    private val START_DB = "<export-database name='"
    private val END_DB = "</>"
    private val START_TABLE = "<table name='"
    private val END_TABLE = "</table>"
    private val START_ROW = "<row>"
    private val END_ROW = "</row>"
    private val START_COL = "<"
    private val END_COL = "</col>"
    private var mDestXmlFilename = "/sdcard/export.xml"
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

            val cur = mDb!!.rawQuery(sql, arrayOfNulls(0))
            cur.moveToFirst()

            var tableName: String
            while (cur.position < cur.count) {
                tableName = cur.getString(cur.getColumnIndex("name"))
                Log.d("table", tableName)
                if (tableName == "PWADB") {


                    exportTable(tableName)
                }

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
        val sql = "select * from PWADB where readflag = 1"
        val cur = mDb!!.rawQuery(sql, arrayOfNulls(0))
        val numcols = cur.columnCount

        cur.moveToFirst()

        // move through the table, creating rows
        // and adding each column with name and value
        // to the row
        while (cur.position < cur.count) {
            mExporter!!.startRow()
            var name: String
            var `val`: String
            var val2: Double

            for (idx in 1 until numcols) {
                name = cur.getColumnName(idx)
                `val` = cur.getString(idx)
                val2 = cur.getDouble(idx)
                //Log.d("idx", String.valueOf(idx));
                //Log.d("val", String.valueOf(val));
                //Log.d("val2", String.valueOf(val2));
                if (idx >= 35 && idx <= 41) {
                    `val` = val2.toString()
                }
                mExporter!!.addColumn(name, `val`)
            }
            mExporter!!.endRow()
            cur.moveToNext()
        }

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
            val stg = "</PWADB>"
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



    }
}
