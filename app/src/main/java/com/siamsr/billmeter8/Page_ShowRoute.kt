package com.siamsr.billmeter8

import android.app.AlertDialog
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

class Page_ShowRoute : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bn_moveN -> Nextdata()
            R.id.bn_moveP -> Previousdata()
            R.id.bn_moveF -> Firstdata()
            R.id.bn_moveL -> Lastdata()
        }
    }

    internal var cur: Cursor? = null
    internal var curSumRoute: Cursor? = null
    internal var bn_next: ImageButton? = null
    internal var bn_prev: ImageButton? = null
    internal var bn_first: ImageButton? = null
    internal var bn_last: ImageButton? = null
    private val db = DBHelper(this)
    internal var tvwwcode: TextView? = null
    internal var tvwwTname: TextView? = null
    internal var tvRoute: TextView? = null
    internal var tvamount: TextView? = null
    internal var tvread: TextView? = null
    internal var wwTname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_dashboard)

        tvwwcode = findViewById(R.id.tvwwcode) as TextView
        tvwwTname = findViewById(R.id.tvwwTname) as TextView
        tvRoute = findViewById(R.id.tvroute) as TextView
        tvamount = findViewById(R.id.tvamount) as TextView
        tvread = findViewById(R.id.tvread) as TextView
        bn_next = findViewById(R.id.bn_moveN) as ImageButton
        bn_prev = findViewById(R.id.bn_moveP) as ImageButton
        bn_first = findViewById(R.id.bn_moveF) as ImageButton
        bn_last = findViewById(R.id.bn_moveL) as ImageButton

        bn_next?.setOnClickListener(this)
        bn_prev?.setOnClickListener(this)
        bn_first?.setOnClickListener(this)
        bn_last?.setOnClickListener(this)

        showRoute()
    }

    private fun showRoute() {
        // TODO Auto-generated method stub

        try {

            cur = db.select_ShowRoute()
            if (cur != null) {
                if (cur!!.moveToFirst()) {
                    setData(cur!!)
                }
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }

    }
    private fun setData(curRoute: Cursor) {
        // TODO Auto-generated method stub
        val tRoute: String
        val rCount: String
        if (curRoute.isLast) {
            showDialog("ข้อมูลสุดท้าย !!")
        }
        tvwwcode?.text = curRoute.getString(curRoute.getColumnIndex("wwcode"))
        tvRoute?.text = curRoute.getString(curRoute.getColumnIndex("mtrrdroute"))
        tvamount?.text = curRoute.getString(curRoute.getColumnIndex("total"))
        getTname()
        tRoute = curRoute.getString(curRoute.getColumnIndex("mtrrdroute"))
        curSumRoute = db.select_SumRoute(tRoute)
        if (curSumRoute != null) {
            rCount = curSumRoute!!.getString(curSumRoute!!.getColumnIndex("sumRoute"))
            tvread?.text = rCount
        }

    }

    private fun getTname() {
        val wwcode: String
        wwcode = tvwwcode?.text.toString()
        // TODO Auto-generated method stub
        when (wwcode) {
            "5532011" -> wwTname = "อุบลราชธานี"
            "5532012" -> wwTname = "พิบูลมังสาหาร"
            "5532013" -> wwTname = "เดชอุดม"
            "5532014" -> wwTname = "เขมราฐ"
            "5532015" -> wwTname = "อำนาจเจริญ"
            "1131" -> wwTname = "เลิงนกทา"
            "1132" -> wwTname = "มหาชนะชัย"
            "5532019" -> wwTname = "บุรีรัมย์"
            "5532020" -> wwTname = "สตึก"
            "5532021" -> wwTname = "ลำปลายมาศ"
            "5532022" -> wwTname = "นางรอง"
            "5532023" -> wwTname = "ละหานทราย"
            "1141" -> wwTname = "สุรินทร์"
            "1142" -> wwTname = "ศีขรภูมิ"
            "1143" -> wwTname = "รัตนบุรี"
            "1246" -> wwTname = "สังขะ"
            "1144" -> wwTname = "ศรีสะเกษ"
            "1145" -> wwTname = "กันทรลักษ์"
            "5532030" -> wwTname = "มุกดาหาร"
            "1130" -> wwTname = "ยโสธร"
            else -> {
            }
        }
        tvwwTname?.text = wwTname
    }

    fun Nextdata() {


        if (cur!!.moveToNext()) {
            setData(cur!!)
        }

    }

    fun Previousdata() {

        if (cur!!.moveToPrevious()) {
            setData(cur!!)
        }
    }

    fun Firstdata() {

        if (cur!!.moveToFirst()) {
            setData(cur!!)
        }
    }

    fun Lastdata() {
        if (cur!!.moveToLast()) {

            setData(cur!!)
        }
    }


    private fun showDialog(message: String) {

        val alt_bld = AlertDialog.Builder(this)
        alt_bld.setMessage(message)
        alt_bld.setTitle("แจ้งเตือน")
        alt_bld.setPositiveButton("ตกลง", null)
        alt_bld.show()

    }



}
