package com.siamsr.billmeter8

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.text.format.Time
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DBHelper(context:Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)  {

    companion object {

        // Database Version
        val DATABASE_VERSION = 1

        // Database Named
        val DATABASE_NAME = "PWA"

        var CISData: List<XMLParserObject>? = null
        var db: SQLiteDatabase? = null

        // Table Name
        private val TABLE_CISDB = "PWADB"
        private val TABLE_CONSTANT = "CONSTANT"
        private val TABLE_DBST42 = "DBST42"
        private val TABLE_DBST06 = "DBST06"
        private val TABLE_DBST51 = "DBST51"
        private val TABLE_DBST52 = "DBST52"
        private val TABLE_DBST53 = "DBST53"
        private val TABLE_DBST54 = "DBST54"
        private val TABLE_CISIMG = "PWAIMG"
        private val TABLE_CISEMP = "PWAEMP"

        // CISIMG Table Columns names
        private val COL_WWCODE = "wwcode"
        private val COL_ROUTE = "mtrrdroute"
        private val COL_SEQ = "mtrseq"
        private val COL_CUSTCODE = "custcode"
        private val COL_USERID = "userid"
        private val COL_DATE = "datepic"
        private val COL_IMAGE = "image"

        private val CREATE_INDEX_CISDB = "CREATE UNIQUE INDEX [CUST_ROUTE] ON [PWADB](" +
                "[mtrrdroute]  ASC," +
                "[custcode]  ASC)"

        private val CREATE_TABLE_CISDB = "CREATE TABLE PWADB (" +
                "precode TEXT NOT NULL," +
                "wwcode TEXT (7) NOT NULL," +
                "mtrrdroute TEXT (6) NULL," +
                "mtrseq INTEGER NULL," +
                "custcode TEXT (7) NULL," +
                "usertype TEXT (3) NULL," +
                "oldtype TEXT (3) NULL," +
                "custstat TEXT (1) NULL," +
                "location TEXT (40) NULL," +
                "custname TEXT (80) NULL," +
                "custaddr TEXT (100) NULL," +
                "mtrmkcode TEXT (2) NULL," +
                "metersize TEXT (2) NULL," +
                "meterno TEXT (20) NULL," + //แก้ไขจาก10เป็น20

                "prsmtrstat TEXT(1) NULL," +
                "lstmtrddt TEXT(6) NULL," +
                "lstmtrcnt INTEGER NULL," +
                "revym TEXT (4) NULL," +
                "novat TEXT (1) NULL," +
                "avgwtuse INTEGER NULL," +
                "discntcode TEXT (3) NULL," +
                "invoicecnt TEXT (13) NULL," +
                "invflag TEXT (1) NULL," +
                "debmonth INTEGER NULL," +
                "debamt INTEGER(15,2) NULL," +
                "remwtusg INTEGER NULL," +
                "noofhouse INTEGER NULL," +
                "pwa_flag TEXT NULL," + //เพิ่มส่วนลดประชารัฐ
                "meterest INTEGER NULL," +
                "smcnt INTEGER NULL," +
                "mincharge TEXT(1) NULL," +
                "lstwtusg INTEGER NULL," +
                "subdiscn INTEGER NULL," +
                "readflag TEXT (1) NULL," +
                "newread TEXT (1) NULL," +
                "prsmtrcnt INTEGER NULL," +
                "nortrfwt INTEGER(10,2) NULL," +
                "discntamt INTEGER(10,2) NULL," +
                "NetTrfWt INTEGER(10,2) NuLL," +
                "srvfee INTEGER(10,2) NULL," +
                "vat INTEGER(10,2) NULL," +
                "tottrfwt INTEGER(15,2) NULL," +
                "comment TEXT (2) NULL," +
                "commentdec TEXT (14) NULL," +
                "billflag TEXT (1) NULL," +
                "billsend TEXT (1) NULL," +
                "hln TEXT(1) NULL," +
                "prswtusg INTEGER NULL," +
                "latitude TEXT(20) NULL," +
                "lontitude TEXT(20) NULL," +
                "prsmtrrddt TEXT (6) NULL," +
                "time TEXT (4) NULL," +
                "readcount INTEGER NULL," +
                "printcount INTEGER Null," +
                "usgcalmthd TEXT (1) Null," +
                "userid TEXT (4) NULL," +
                "chkdigit TEXT (2) NULL," +
                "controlmtr TEXT (1) NULL," +
                "bigmtrno TEXT(7) NULL," +
                "tbseq INTEGER NULL," +
                "readdate TEXT NULL," +
                "regisno TEXT (13) NULL," +
                "custbrn TEXT (5) NULL," +
                "calwtusg INTEGER NULL," +
                "distempamt INTEGER(6,2) NULL," + //เพิ่มส่วนลดประชารัฐ

                "distempnet INTEGER(6,2) NULL," + //เพิ่มส่วนลดประชารัฐ

                "distempvat INTEGER(6,2) NULL" + //เพิ่มส่วนลดประชารัฐ

                ")"
        private val CREATE_TABLE_CONSTANT = "CREATE TABLE CONSTANT (" +
                "precode TEXT NOT NULL," +
                "wwcode TEXT (7) NOT NULL," +
                "wwnamet TEXT (65) NULL," +
                "wwtel TEXT (50) NULL," +
                "ba TEXT (4) NULL," +
                "mvamt1 INTEGER (10,2) NULL," +
                "mvamt2 INTEGER (10,2) NULL," +
                "mvamt3 INTEGER (10,2) NULL," +
                "dispwapro	INTEGER (6,2) NULL" +
                ")"
        private val CREATE_TABLE_DBST42 = "CREATE TABLE DBST42(" +
                "metersize TEXT (2) NOT NULL," +
                "srvfee INTEGER(10,2) NULL" +
                ")"
        private val CREATE_TABLE_DBST06 = "CREATE TABLE DBST06(" +
                "DISCNTCODE TEXT (3) NOT NULL," +
                "DISCNTMEAN TEXT (60) NULL," +
                "DISCNTTYPE TEXT (1) NULL," +
                "DISCNTSRVF TEXT (1) NULL," +
                "DISCNTPCEN FLOAT NULL," +
                "DISCNTNUMR FLOAT NULL," +
                "DISCNTDNOM FLOAT NULL," +
                "DISCNTUNIT FLOAT NULL," +
                "DISCNTBAHT FLOAT NULL" +
                ")"
        private val CREATE_TABLE_DBST51 = "CREATE TABLE DBST51 (" +
                "wttrfrt FLOAT NULL," +
                "acwttrfamt FLOAT NULL," +
                "lowusgran FLOAT NULL," +
                "highusgran FLOAT NULL " +
                ")"
        private val CREATE_TABLE_DBST52 = "CREATE TABLE DBST52 (" +
                "wttrfrt FLOAT NULL," +
                "acwttrfamt FLOAT NULL," +
                "lowusgran FLOAT NULL," +
                "highusgran FLOAT NULL " +
                ")"
        private val CREATE_TABLE_DBST53 = "CREATE TABLE DBST53 (" +
                "wttrfrt FLOAT NULL," +
                "acwttrfamt FLOAT NULL," +
                "lowusgran FLOAT NULL," +
                "highusgran FLOAT NULL " +
                ")"
        private val CREATE_TABLE_DBST54 = "CREATE TABLE DBST54 (" +
                "wttrfrt FLOAT NULL," +
                "acwttrfamt FLOAT NULL," +
                "lowusgran FLOAT NULL," +
                "highusgran FLOAT NULL " +
                ")"
        private val CREATE_TABLE_CISIMG = "CREATE TABLE PWAIMG(" +
                "wwcode TEXT (7) NOT NULL," +
                "mtrrdroute TEXT (6) NOT NULL," +
                "mtrseq INTEGER NULL," +
                "custcode TEXT (7) NULL," +
                "userid TEXT(4) NOT NULL," +
                "datepic TEXT(8) NOT NULL," +
                "image BLOB NOT NULL" +
                ")"
        private val CREATE_TABLE_CISEMP = "CREATE TABLE PWAEMP(" +
                "precode TEXT NOT NULL," +
                "wwcode TEXT (7) NOT NULL," +
                "empid TEXT (4) NOT NULL," +
                "empname TEXT (50) NOT NULL," +
                "emplastname TEXT (50) NULL," +
                "empmobile TEXT (10) NULL," +
                "empposition TEXT (2) NULL " +
                ")"

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_CISDB")
        db.execSQL("DROP INDEX IF EXISTS CUST_ROUTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONSTANT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DBST06")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DBST42")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DBST51")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DBST52")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DBST53")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DBST54")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CISEMP")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CISIMG")
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase) {

        // creating required tables

        db.execSQL(CREATE_TABLE_CISDB)
        db.execSQL(CREATE_INDEX_CISDB)
        db.execSQL(CREATE_TABLE_CONSTANT)
        db.execSQL(CREATE_TABLE_DBST06)
        db.execSQL(CREATE_TABLE_DBST42)
        db.execSQL(CREATE_TABLE_DBST51)
        db.execSQL(CREATE_TABLE_DBST52)
        db.execSQL(CREATE_TABLE_DBST53)
        db.execSQL(CREATE_TABLE_DBST54)
        db.execSQL(CREATE_TABLE_CISIMG)
        db.execSQL(CREATE_TABLE_CISEMP)

        // Insert data from XML
        val parser = XMLParser()
        try {
            Log.d("xml", "open")
            //CISData = parser.parse(mContext.getAssets().open("CISData.xml"));
            val file = File(Environment.getExternalStorageDirectory().toString() + "/PWA/DOWNLOAD/PwaData.xml")

            // create an input stream to be read by the stream reader.
            val fis = FileInputStream(file)
            CISData = parser.parse(fis)
            for (CISData in CISData!!) {
                if (CISData.getprecode() === "DATA") {
                    Log.d("insertdata", "OK")
                    val values = ContentValues()
                    values.put("precode", CISData.getprecode())
                    values.put("wwcode", CISData.getwwcode())
                    values.put("mtrrdroute", CISData.getmtrrdroute())
                    values.put("mtrseq", CISData.getmtrseq())
                    values.put("custcode", CISData.getcustcode())
                    values.put("usertype", CISData.getusertype())
                    values.put("oldtype", CISData.getoldtype())
                    values.put("custstat", CISData.getcuststat())
                    values.put("location", CISData.getlocation())
                    values.put("custname", CISData.getcustname())
                    values.put("custaddr", CISData.getcustaddr())
                    values.put("mtrmkcode", CISData.getmtrmkcode())
                    values.put("metersize", CISData.getmetersize())
                    values.put("meterno", CISData.getmeterno())
                    values.put("prsmtrstat", CISData.getprsmtrstat())
                    values.put("lstmtrddt", CISData.getlstmtrddt())
                    values.put("lstmtrcnt", CISData.getlstmtrcnt())
                    values.put("revym", CISData.getrevym())
                    values.put("novat", CISData.getnovat())
                    values.put("avgwtuse", CISData.getavgwtuse())
                    values.put("discntcode", CISData.getdiscntcode())
                    values.put("invoicecnt", CISData.getinvoicecnt())
                    values.put("invflag", CISData.getinvflag())
                    values.put("debmonth", CISData.getdebmonth())
                    values.put("debamt", CISData.getdebamt())
                    values.put("remwtusg", CISData.getremwtusg())
                    values.put("noofhouse", CISData.getnoofhouse())
                    values.put("pwa_flag", CISData.getpwaflag())
                    values.put("meterest", CISData.getmeterest())
                    values.put("smcnt", CISData.getsmcnt())
                    values.put("mincharge", CISData.getmincharge())
                    values.put("lstwtusg", CISData.getlstwtusg())
                    values.put("subdiscn", CISData.getsubdiscn())
                    values.put("readflag", CISData.getreadflag())
                    values.put("newread", CISData.getnewread())
                    values.put("prsmtrcnt", CISData.getprsmtrcnt())
                    values.put("nortrfwt", CISData.getnortrfwt())
                    values.put("discntamt", CISData.getdiscntamt())
                    values.put("srvfee", CISData.getsrvfee())
                    values.put("vat", CISData.getvat())
                    values.put("tottrfwt", CISData.gettottrfwt())
                    values.put("comment", CISData.getcomment())
                    values.put("commentdec", CISData.getcommentdec())
                    values.put("billflag", CISData.getbillflag())
                    values.put("billsend", CISData.getbillsend())
                    values.put("hln", CISData.gethln())
                    values.put("prswtusg", CISData.getprswtusg())
                    values.put("latitude", CISData.getlatitude())
                    values.put("lontitude", CISData.getlontitude())
                    values.put("prsmtrrddt", CISData.getprsmtrrddt())
                    values.put("time", CISData.gettime())
                    values.put("readcount", CISData.getreadcount())
                    values.put("printcount", CISData.getprintcount())
                    values.put("usgcalmthd", CISData.getusgcalmthd())
                    values.put("userid", CISData.getuserid())
                    values.put("chkdigit", CISData.getchkdigit())
                    values.put("controlmtr", CISData.getcontrolmtr())
                    values.put("bigmtrno", CISData.getbigmtrno())
                    values.put("regisno", CISData.getregisno())
                    values.put("custbrn", CISData.getcustbrn())
                    values.put("tbseq", CISData.gettbseq())
                    //					Log.d("xmldup", String.valueOf(CISData.getdupamt()));
                    Log.d("xml", "import")
                    db.insert(TABLE_CISDB, null, values)
                    Log.d("xml", "successok")

                } else if (CISData.getprecode() === "CON") {
                    val valuesConstant = ContentValues()
                    valuesConstant.put("precode", CISData.getprecode())
                    valuesConstant.put("wwcode", CISData.getwwcode())
                    valuesConstant.put("wwnamet", CISData.getwwnamet())
                    valuesConstant.put("wwtel", CISData.getwwtel())
                    valuesConstant.put("ba", CISData.getba())
                    valuesConstant.put("mvamt1", CISData.getmvamt1())
                    valuesConstant.put("mvamt2", CISData.getmvamt2())
                    valuesConstant.put("mvamt3", CISData.getmvamt3())
                    valuesConstant.put("dispwapro", CISData.getdispwapro())
                    Log.d("xml", "importconstant")
                    db.insert(TABLE_CONSTANT, null, valuesConstant)
                    Log.d("xml", "ConstantOk")
                } else if (CISData.getwwcode() === "D51") {
                    val valuesDbst = ContentValues()
                    valuesDbst.put("wttrfrt", CISData.getwttrfrt())
                    valuesDbst.put("acwttrfamt", CISData.getacwttrfamt())
                    valuesDbst.put("lowusgran", CISData.getlowusgran())
                    valuesDbst.put("highusgran", CISData.gethighusgran())
                    Log.d("xml", "importdbst51")
                    db.insert(TABLE_DBST51, null, valuesDbst)
                    Log.d("xml", "DBST51")
                } else if (CISData.getwwcode() === "D52") {
                    val valuesDbst = ContentValues()
                    valuesDbst.put("wttrfrt", CISData.getwttrfrt())
                    valuesDbst.put("acwttrfamt", CISData.getacwttrfamt())
                    valuesDbst.put("lowusgran", CISData.getlowusgran())
                    valuesDbst.put("highusgran", CISData.gethighusgran())
                    Log.d("xml", "importdbst52")
                    db.insert(TABLE_DBST52, null, valuesDbst)
                    Log.d("xml", "DBST52")
                } else if (CISData.getwwcode() === "D53") {
                    val valuesDbst = ContentValues()
                    valuesDbst.put("wttrfrt", CISData.getwttrfrt())
                    valuesDbst.put("acwttrfamt", CISData.getacwttrfamt())
                    valuesDbst.put("lowusgran", CISData.getlowusgran())
                    valuesDbst.put("highusgran", CISData.gethighusgran())
                    Log.d("xml", "importdbst53")
                    db.insert(TABLE_DBST53, null, valuesDbst)
                    Log.d("xml", "DBST53")
                } else if (CISData.getwwcode() === "D54") {
                    val valuesDbst = ContentValues()
                    valuesDbst.put("wttrfrt", CISData.getwttrfrt())
                    valuesDbst.put("acwttrfamt", CISData.getacwttrfamt())
                    valuesDbst.put("lowusgran", CISData.getlowusgran())
                    valuesDbst.put("highusgran", CISData.gethighusgran())
                    Log.d("xml", "importdbst54")
                    db.insert(TABLE_DBST54, null, valuesDbst)
                    Log.d("xml", "DBST54")
                } else if (CISData.getwwcode() === "D06") {
                    val valuesDbst = ContentValues()
                    valuesDbst.put("DISCNTCODE", CISData.getdiscntcode())
                    valuesDbst.put("DISCNTMEAN", CISData.getdiscntmean())
                    valuesDbst.put("DISCNTTYPE", CISData.getdiscnttype())
                    valuesDbst.put("DISCNTSRVF", CISData.getdiscntsrvf())
                    valuesDbst.put("DISCNTPCEN", CISData.getdiscntpcen())
                    valuesDbst.put("DISCNTNUMR", CISData.getdiscntnumr())
                    valuesDbst.put("DISCNTDNOM", CISData.getdiscntdnom())
                    valuesDbst.put("DISCNTUNIT", CISData.getdiscntunit())
                    valuesDbst.put("DISCNTBAHT", CISData.getdiscntbaht())
                    Log.d("xml", "importdbst06")
                    db.insert(TABLE_DBST06, null, valuesDbst)
                    Log.d("xml", "DBST06")
                } else if (CISData.getwwcode() === "D42") {
                    val valuesDbst = ContentValues()
                    valuesDbst.put("metersize", CISData.getmetersize())
                    valuesDbst.put("srvfee", CISData.getsrvfee())
                    Log.d("xml", "importdbst42")
                    db.insert(TABLE_DBST42, null, valuesDbst)
                    Log.d("xml", "DBST42")
                } else if (CISData.getprecode() === "EMP") {
                    val valuesEmp = ContentValues()
                    valuesEmp.put("precode", CISData.getprecode())
                    valuesEmp.put("wwcode", CISData.getwwcode())
                    valuesEmp.put("empid", CISData.getempid())
                    valuesEmp.put("empname", CISData.getempname())
                    valuesEmp.put("emplastname", CISData.getemplastname())
                    valuesEmp.put("empmobile", "")
                    valuesEmp.put("empposition", CISData.getempposition())
                    Log.d("xml", "importEmp")
                    db.insert(TABLE_CISEMP, null, valuesEmp)
                    Log.d("xml", "EMP")
                }

            }
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        Log.d("CREATE TABLE", "Create Table Successfully.")
    }

    fun select_CISData(mRoute: String): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select * from PWADB where mtrrdroute = '$mRoute' and readflag = '0' order by mtrseq", null)
        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }

    fun select_CISDataRead(mRoute: String): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select * from PWADB where mtrrdroute = '$mRoute' and readflag = '1' order by mtrseq", null)
        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }

    fun select_FindCust(StrFind: String): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select * from PWADB where custcode = '$StrFind'  order by mtrseq", null)
        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }

    fun select_FindMeter(StrFind: String, strRoute: String): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select * from PWADB where meterno like '%$StrFind%' and mtrrdroute = '$strRoute'  order by mtrseq", null)
        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }

    fun select_FindAddr(StrFind: String, strRoute: String): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select * from PWADB where custaddr like '%$StrFind%'  and mtrrdroute = '$strRoute' order by mtrseq", null)
        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }



    fun select_FindSeq(StrFind: String, strRoute: String): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select * from PWADB where mtrseq = '$StrFind' and mtrrdroute = '$strRoute' order by mtrseq", null)
        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }


    fun select_ConstantData(): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select * from CONSTANT", null)
        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }


    fun curEmp(): Cursor {


        db = readableDatabase

        val SQL = "select * from msEmployee Order by empid"
        val mCursor = db!!.rawQuery(SQL, null)
        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }

        } catch (e: Exception) {
            // TODO: handle exception
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }


    fun select_ShowRoute(): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select wwcode,mtrrdroute," + "count(mtrrdroute) as total from PWADB group by wwcode,mtrrdroute", null)

        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor

    }

    fun select_SumRoute(tRoute: String): Cursor? {
        var sql: String
        db = writableDatabase
        sql = " SELECT COUNT(wwcode) AS sumRoute "
        sql += " FROM PWADB "
        sql += " WHERE mtrrdroute = '$tRoute' "
        sql += " AND READFLAG = '1'"
        val mCursor = db!!.rawQuery(sql, null)

        try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor

    }

    fun select_EmpData(): Cursor {

        db = writableDatabase
        val mCursor = db!!.rawQuery("select * from PWAEMP", null)
       try {
            if (mCursor.count > 0) {
                mCursor.moveToFirst()
            }


        } catch (e: Exception) {
            println(e.message)
        } finally {
            db!!.close()
        }
        return mCursor
    }

    fun curDbst42(): Cursor? {
        try {
            val db: SQLiteDatabase
            db = this.readableDatabase

            val SQL = "select * from Dbst42"

            return db.rawQuery(SQL, null)
        } catch (e: Exception) {
            // TODO: handle exception
            return null
        }

    }

    fun curDbst51(): Cursor? {
        try {
            val db: SQLiteDatabase
            db = this.readableDatabase

            val SQL = "select * from Dbst51 Order by LowUsgRan"

            return db.rawQuery(SQL, null)
        } catch (e: Exception) {
            // TODO: handle exception
            return null
        }

    }

    fun curDbst52(): Cursor? {
        try {
            val db: SQLiteDatabase
            db = this.readableDatabase

            val SQL = "select * from Dbst52 Order by LowUsgRan"

            return db.rawQuery(SQL, null)
        } catch (e: Exception) {
            // TODO: handle exception
            return null
        }

    }

    fun curDbst53(): Cursor? {
        try {
            val db: SQLiteDatabase
            db = this.readableDatabase

            val SQL = "select * from Dbst53 Order by LowUsgRan"

            return db.rawQuery(SQL, null)
        } catch (e: Exception) {
            // TODO: handle exception
            return null
        }

    }

    fun curDbst54(): Cursor? {
        try {
            val db: SQLiteDatabase
            db = this.readableDatabase

            val SQL = "select * from Dbst54 Order by LowUsgRan"

            return db.rawQuery(SQL, null)
        } catch (e: Exception) {
            // TODO: handle exception
            return null
        }

    }

    fun curDbst06(): Cursor? {
        try {
            val db: SQLiteDatabase
            db = this.readableDatabase

            val SQL = "select * from Dbst06  order by discntcode"

            return db.rawQuery(SQL, null)
        } catch (e: Exception) {
            // TODO: handle exception
            return null
        }

    }

    fun getReadCount(Cust_Code: String): Int {

        val cdb: SQLiteDatabase
        cdb = this.readableDatabase
        val SQL = "select readcount from PWADB where custcode = '$Cust_Code'"
        val c = cdb.rawQuery(SQL, null)
        try {

            c.moveToFirst()
            val x = c.getInt(c.getColumnIndex("readcount"))

            c.close()
            return x
        } catch (e: Exception) {
            // TODO: handle exception
            Log.d("Error", e.toString())

            c.close()
            return 0

        }

    }

    fun updateData(CustCode: String, OkPrint: Boolean, CustStat: String?,
                   ComMent: String?, ComMentDec: String?, NorTrfwt: Double, SrvFee: Double,
                   Vat: Double, TotTrfwt: Double, PrsMtrCnt: Double, PrsWtUsg: Double, DiscntAmt: Double,
                   NetTrfWt: Double, Usgcalmthd: String?, USER: String?, ChkDigit: String?, BillSend: String?,
                   Latitude: String?, Lontitude: String?, HLN: String?, CalWtUsg: Double, distempamt: Double, distempnet: Double, distempvat: Double) {




        var sql :String
        var BillSend: String?
        var custStat: String? = CustStat

        db = writableDatabase

        val BillFlag: String
        val c = Calendar.getInstance()
        val time = Time()
        time.setToNow()

        var rCount = 0
        rCount = getReadCount(CustCode)
        rCount = rCount + 1
        Log.d("ReadCount", rCount.toString())

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


        //580605
        val YYYYDDMM = "" + (year + 543 - 2500) + String.format("%02d", day) + `MONTH$`    //Put date ints into string DD/MM/YYYY

        println("Time==> " + String.format("%02d", time.minute))
        println("Current time => " + c.time)
        //--580605
        //--2/2/2558 17:19
        val df = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val formattedDate = df.format(c.time)
        val formattedDate2 = day.toString() + "/" + `MONTH$` + "/" + (year + 543) + " " + time.hour + ":" + String.format("%02d", time.minute)

        if (OkPrint) {
            BillFlag = "Y"
        } else {
            BillFlag = "N"
        }

        if (CustStat.equals("", ignoreCase = true)) {
            custStat = "1"
        }
        sql = ""
        sql += " UPDATE PWADB "
        sql += " SET readflag = '1',"
        sql += "custstat = '$CustStat',"
        sql += " nortrfwt = '" + String.format("%.2f", NorTrfwt) + "',"
        sql += " srvfee = '" + String.format("%.2f", SrvFee) + "',"
        sql += " vat = '" + String.format("%.2f", Vat) + "',"
        sql += " tottrfwt = '" + String.format("%.2f", TotTrfwt) + "',"
        sql += " prsmtrcnt = '$PrsMtrCnt',"
        sql += " prswtusg = '$PrsWtUsg',"
        sql += " discntamt = '" + String.format("%.2f", DiscntAmt) + "',"
        sql += " NetTrfWt = '" + String.format("%.2f", NetTrfWt) + "',"


        if (ComMent!!.length > 2) {
            sql += " comment ='" + ComMent.substring(0, 2) + "',"
        } else {
            sql += " comment ='$ComMent',"
        }

        if (ComMentDec!!.length > 10) {
            sql += " commentdec ='" + ComMentDec.substring(0, 10) + "',"
        } else {
            sql += " commentdec ='$ComMentDec',"
        }

        BillSend = "1"
        sql += " billflag  = '$BillFlag',"
        sql += " billsend = '$BillSend',"
        sql += " hln = '$HLN',"
        sql += " latitude  = '$Latitude',"
        sql += " lontitude = '$Lontitude',"
        sql += " prsmtrrddt = '$YYYYDDMM'," //ปีวันเดือน
        sql += " time = '" + time.hour + String.format("%02d", time.minute) + "',"
        sql += " readcount = '$rCount',"
        sql += " printcount = '1',"
        sql += " usgcalmthd = '$Usgcalmthd',"

        var tmp_prsmtrstat = ""

        if (ComMent.equals("01", ignoreCase = true) || ComMent.equals("02", ignoreCase = true) || ComMent.equals("03", ignoreCase = true)) {
            tmp_prsmtrstat = "2"
        } else if (ComMent.equals("15", ignoreCase = true)) {
            tmp_prsmtrstat = "3"
        } else if (ComMent.equals("28", ignoreCase = true)) {
            tmp_prsmtrstat = "4"
        } else {
            tmp_prsmtrstat = "1"
        }

        sql += " prsmtrstat = '$tmp_prsmtrstat',"
        sql += " userid = '$USER',"
        sql += " chkdigit = '$ChkDigit',"
        sql += " newread = '1',"
        sql += " readdate = '$formattedDate2',"
        //		sql += " dupamt = '" + dupamt + "',";
        //		sql += " dupnet = '" + dupnet + "',";
        //		sql += " dupvat = '" + dupvat + "',";
        //		sql += " debtamt = '" + debtamt + "',";
        //		sql += " debtnet = '" + debtnet + "',";
        //		sql += " debtvat = '" + debtvat + "',";
        sql += " calwtusg = '$CalWtUsg',"
        sql += " distempamt = '$distempamt',"
        sql += " distempnet = '$distempnet',"
        sql += " distempvat = '$distempvat'"
        sql += " WHERE custcode = '" + CustCode.trim { it <= ' ' } + "'"
        Log.d("", sql)
        db!!.execSQL(sql)
        db!!.close()
    }


    fun insertIMG(wwcode: String, route: String, mtrseq: Int, custcode: String,
                  userid: String,img : ByteArray) {


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

        //2015086ปีเดือนวัน
        val db = this.writableDatabase
        val datepic = year.toString() + `MONTH$` + day

        val values = ContentValues()
        values.put(COL_WWCODE, wwcode)
        values.put(COL_ROUTE, route)
        values.put(COL_SEQ, mtrseq)
        values.put(COL_CUSTCODE, custcode)
        values.put(COL_USERID, userid)
        values.put(COL_DATE, datepic)
        values.put(COL_IMAGE, img)

        // Inserting Row
        db.insert(TABLE_CISIMG, null, values)
        db.close() // Closing database connection
    }


    val countDbst42: Int
        get() {
            try {
                val db: SQLiteDatabase
                db = this.readableDatabase

                val SQL = "select * from Dbst42 "
                val cursor = db.rawQuery(SQL, null)
                val x = cursor.count
                db.close()
                return x
            } catch (e: Exception) {
                return 0
            }

        }

    // TODO: handle exception
    val countDbst51: Int
        get() {
            try {
                val db: SQLiteDatabase
                db = this.readableDatabase

                val SQL = "select * from Dbst51 Order by LowUsgRan"
                val cursor = db.rawQuery(SQL, null)
                val x = cursor.count
                db.close()
                return x
            } catch (e: Exception) {
                return 0
            }

        }

    // TODO: handle exception
    val countDbst52: Int
        get() {
            try {
                val db: SQLiteDatabase
                db = this.readableDatabase

                val SQL = "select * from Dbst52 Order by LowUsgRan"
                val cursor = db.rawQuery(SQL, null)
                val x = cursor.count
                db.close()
                return x
            } catch (e: Exception) {
                return 0
            }

        }

    // TODO: handle exception
    val countDbst53: Int
        get() {
            try {
                val db: SQLiteDatabase
                db = this.readableDatabase

                val SQL = "select * from Dbst53 Order by LowUsgRan"
                val cursor = db.rawQuery(SQL, null)
                val x = cursor.count
                db.close()
                return x
            } catch (e: Exception) {
                return 0
            }

        }

    // TODO: handle exception
    val countDbst54: Int
        get() {
            try {
                val db: SQLiteDatabase
                db = this.readableDatabase

                val SQL = "select * from Dbst54 Order by LowUsgRan"
                val cursor = db.rawQuery(SQL, null)
                val x = cursor.count
                db.close()
                return x
            } catch (e: Exception) {
                return 0
            }

        }

    // TODO: handle exception
    val countDbst06: Int
        get() {
            try {
                val db: SQLiteDatabase
                db = this.readableDatabase

                val SQL = "select * from Dbst06"
                val cursor = db.rawQuery(SQL, null)
                val x = cursor.count
                db.close()
                return x
            } catch (e: Exception) {
                return 0
            }

        }

    val disPwaPro: String
        get() {
            var DisPwaPro = ""
            val cdb: SQLiteDatabase
            cdb = this.readableDatabase
            val SQL = "select dispwapro from Constant "
            val c = cdb.rawQuery(SQL, null)
            try {

                c.moveToFirst()
                DisPwaPro = c.getString(c.getColumnIndex("dispwapro"))
                c.close()

                return DisPwaPro
            } catch (e: Exception) {
                Log.d("Error", e.toString())

                c.close()
                return DisPwaPro

            }

        }

}



