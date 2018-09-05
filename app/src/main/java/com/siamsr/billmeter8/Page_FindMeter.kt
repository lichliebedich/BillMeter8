package com.siamsr.billmeter8

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import java.util.ArrayList

class Page_FindMeter : AppCompatActivity() {

    companion object {
        lateinit var etfind: TextView
        lateinit var bnfind: Button
        lateinit var myDB: DBHelper
        lateinit var db: SQLiteDatabase
        lateinit var listView: ListView
        lateinit var mCursor: Cursor
        lateinit var fCursor:Cursor
        lateinit var tRoute: String
        lateinit var tCustcode:String
        lateinit var tFind:String
        lateinit var R0: RadioButton
        lateinit var R1: RadioButton
        lateinit var R2: RadioButton
        lateinit var R3 :RadioButton
         var idfind: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_findmeter)

        try {
            myDB = DBHelper(this)
            db = myDB.writableDatabase
            idfind = 1
            etfind = findViewById<View>(R.id.et_find) as TextView
            R0 = findViewById<View>(R.id.radio0) as RadioButton
            R1 = findViewById<View>(R.id.radio1) as RadioButton
            R2 = findViewById<View>(R.id.radio2) as RadioButton
            R3 = findViewById<View>(R.id.radio3) as RadioButton
            R0.setOnClickListener(radio_listener)
            R1.setOnClickListener(radio_listener)
            R2.setOnClickListener(radio_listener)
            R3.setOnClickListener(radio_listener)

            tRoute = intent.getStringExtra("tRoute")

            bnfind = findViewById<View>(R.id.bn_findmeter) as Button
            bnfind.setOnClickListener {
                // TODO Auto-generated method stub
                tFind = etfind.text.toString().trim { it <= ' ' }
                val i = Intent()
                i.putExtra("tFind", tFind)
                i.putExtra("idfind", idfind)
                setResult(-1, i)
                finish()
            }




            listView = findViewById(R.id.list) as ListView
            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                // TODO Auto-generated method stub
                val item = parent.getItemAtPosition(position)
                etfind.text = item.toString().substring(0, 7)
            }

            mCursor = db.rawQuery("select mtrrdroute,custcode,meterno,custaddr " +
                    "from PWADB where mtrrdroute = '" + tRoute + "'", null) as SQLiteCursor
            val dirArray = ArrayList<String>()
            mCursor.moveToFirst()

            while (!mCursor.isAfterLast) {


                dirArray.add(mCursor.getString(1) + " | " + mCursor.getString(2) + " | " + mCursor.getString(3).substring(0, 15) + "\n")

                mCursor.moveToNext()

            }

            val adapterDir = ArrayAdapter(this, R.layout.list_item2, dirArray)
            listView.adapter = adapterDir
            myDB.close()
            db.close()

        } catch (e: Exception) {
            // TODO: handle exception

        }


    }

    private val radio_listener = View.OnClickListener { v ->
        when (v.id) {
            R.id.radio0 -> {
                Log.d("0", "0")
                idfind = 1
            }
            R.id.radio1 -> {
                Log.d("1", "1")
                idfind = 2
            }
            R.id.radio2 -> {
                Log.d("2", "2")
                idfind = 3
            }
            R.id.radio3 -> {
                Log.d("3", "3")
                idfind = 4
            }
            else -> {
            }
        }
    }
}
