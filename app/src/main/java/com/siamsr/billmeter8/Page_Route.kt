package com.siamsr.billmeter8

import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.util.*

class Page_Route : AppCompatActivity() {

    internal var myDB  = DBHelper(this)
    private var db: SQLiteDatabase? = null

    internal var listView: ListView? = null
    private var txRoute: String? = null
    var mCursor: SQLiteCursor? = null
    var USERID: String? = null
    internal var curSumRoute: Cursor? = null
    internal var tRoute: String? = null
    internal var rCount: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_route)
    }

    override fun onStart() {
        super.onStart()
        db = myDB!!.writableDatabase

        USERID = intent.getStringExtra("userid")
        listView = findViewById(R.id.list) as ListView
        mCursor = db!!.rawQuery("select mtrrdroute,count(*) from PWADB group by mtrrdroute", null) as SQLiteCursor
        val dirArray = ArrayList<String>()
        mCursor!!.moveToFirst()

        while (!mCursor!!.isAfterLast) {
            rCount = "0"
            tRoute = mCursor!!.getString(0)
            curSumRoute = myDB.select_SumRoute(tRoute = mCursor!!.getString(0))
            if (curSumRoute != null) {
                rCount = curSumRoute!!.getString(curSumRoute!!.getColumnIndex("sumRoute"))
            } else {
                rCount = "0"
            }
            dirArray.add("สาย : " + mCursor!!.getString(0) +
                    "  | จำนวน : " + rCount + " / "
                    + mCursor!!.getString(1) + "\n")

            mCursor!!.moveToNext()

        }

        val adapterDir = ArrayAdapter(this, R.layout.list_item, dirArray)
        listView!!.adapter = adapterDir
        myDB!!.close()
        db!!.close()


        val etRoute = findViewById(R.id.et_find) as EditText
        etRoute.setText("")
        etRoute.setRawInputType(Configuration.KEYBOARD_12KEY)
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // TODO Auto-generated method stub
            val item = parent.getItemAtPosition(position)
            etRoute.setText(item.toString().substring(6, 12))
        }

        val selectbn = findViewById<Button>(R.id.bn_findmeter)
        selectbn.setOnClickListener {
            txRoute = etRoute.text.toString()
            if (txRoute !== "") {
                val chRoute = Intent(applicationContext, Page_ReadMeter::class.java)
                chRoute.putExtra("TRoute", txRoute)
                chRoute.putExtra("userid", USERID)
                startActivity(chRoute)
            } else {
                Toast.makeText(this, "Not found Route!",
                        Toast.LENGTH_LONG).show()
            }
        }
    }







}
