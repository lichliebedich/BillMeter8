package com.siamsr.billmeter8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class Page_Menu : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {

        when (v.id) {
            R.id.btnRoute -> {
                val i = Intent(this,Page_Route::class.java)
                i.putExtra("userid", USERID)
                startActivity(i)
            }
            R.id.btnImportData -> {
                val i1 = Intent(this, Page_ImportData::class.java)
                startActivity(i1)
            }
            R.id.btnuploadonline -> {
                val i2 = Intent(this, Page_UploadOnline::class.java)
                startActivity(i2)
            }
            R.id.btnMtr -> {
                val i3 = Intent(this, Page_ShowRoute::class.java)
                startActivity(i3)
            }
            R.id.btnbackupdb -> {
                val i4 = Intent(this, Page_BackupDB::class.java)
                startActivity(i4)
            }
        }
    }

    var USERID:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu)



        USERID = intent.getStringExtra("userid")
        val btnRoute = findViewById(R.id.btnRoute) as Button
        btnRoute.setOnClickListener(this)
        val btnImport = findViewById(R.id.btnImportData) as Button
        btnImport.setOnClickListener(this)
        val btnuploadonline = findViewById(R.id.btnuploadonline) as Button
        btnuploadonline.setOnClickListener(this)
        val btnmtr = findViewById(R.id.btnMtr) as Button
        btnmtr.setOnClickListener(this)
        val btnbackup = findViewById(R.id.btnbackupdb) as Button
        btnbackup.setOnClickListener(this)
        val tvShowId = findViewById(R.id.text_title) as TextView
        tvShowId.text = "[เข้าสู่ระบบโดย: $USERID]"
    }
}
