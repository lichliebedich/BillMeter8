package com.siamsr.billmeter8

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.text.format.Time
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.tscdll.TSCActivity
import honeywell.printer.DocumentEZ
import honeywell.printer.ParametersEZ
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
class Page_ReadMeter: AppCompatActivity(), View.OnClickListener {

    private var docEZ: DocumentEZ? = null
    private var paramEZ: ParametersEZ? = null
    internal var printData = byteArrayOf(0)
    internal var tsc = TSCActivity()

    private var db = DBHelper(this)

    companion object {

        private val CAMERA_REQUEST = 1
        private val RESULT_OK = -1
        private val FIND_OK = 3
        private val RESULT_REQUEST = 2

        var SendAVGSt: Boolean = false
        private val REQUEST_ENABLE_BT = 2
        val MESSAGE_STATE_CHANGE = 1
        val MESSAGE_DEVICE_NAME = 4
        val MESSAGE_TOAST = 5
        val DEVICE_NAME = "device_name"
        val TOAST = "toast"

        var cur: Cursor? = null
        var bn_next: ImageButton? = null
        var bn_prev: ImageButton? = null
        internal var bn_first: ImageButton? = null
        internal var bn_last: ImageButton? = null
        internal var bn_notread: Button?  = null
        internal var bn_readagain: Button?  = null
        internal var bn_reprint: Button?  = null
        internal var bn_find: Button?  = null
        internal var bnCap: Button? = null
        internal var bnOk: Button?  = null
        var ckAvg: CheckBox?=null
        var tv1: TextView?  = null
        var tv2: TextView? = null
        var tv3: TextView? = null
        var tv4: TextView? = null
        var tv5: TextView? = null
        var tv6: TextView? = null
        var tv7: TextView? = null
        var tv8: TextView? = null
        var tv9: TextView? = null
        var tv10: TextView? = null
        var tv11: TextView? = null
        var tv12: TextView?= null
        var tvdupamt: TextView? = null
        var tv_latlon: TextView? = null
        internal var etRead: EditText? = null
        internal var imageFileUri: Uri? = null
        private var chRoute: String? = null
        private var lat: Double = 0.toDouble()
        private var lon: Double = 0.toDouble()
        private var dupamt: Double = 0.toDouble()
        private var dupnet: Double = 0.toDouble()
        private var dupvat: Double = 0.toDouble()
        private var debtamt: Double = 0.toDouble()
        private var debtnet: Double = 0.toDouble()
        private var debtvat: Double = 0.toDouble()
        private var tmpdupamt: Double = 0.toDouble()
        private var distempamt: Double = 0.toDouble()
        private var distempnet: Double = 0.toDouble()
        private var distempvat: Double = 0.toDouble()
        private var locationManager: LocationManager? = null


        private var stBarCode:String?=null
        private var wwcode: String? = null
        private var Mtrrdroute: String? = null
        private var Mtrseq: Int = 0
        private var usertype: String? = null// ประเภทผู้ใช้น้ำ
        private var CustCode: String? = null //ชื่อผู้ใช้น้ำ
        private var DisCntCode: String? = null// ประเภทส่วนลด
        private var MeterNo: String? = null
        private var MeterSize: String? = null
        private var MtRmkCode: String? = null // ขนาดมิเตอร์
        private var controlmtr: String? = null
        private var ReadFlag: Int = 0
        private var LstMtrCnt: Double = 0.toDouble() // เลขมิเตอร์ก่อนจด
        private var PrsMtrCnt: Double = 0.toDouble() //เลขมิเตอร์หลังจด
        private var Est: Double = 0.toDouble() //ค่าประมาณการ
        private var PrsWtUsg: Double = 0.toDouble() // หน่วยที่ใช้น้ำ PrsMtrCnt - LstMtrCnt
        private var AvgWtUsg: Int = 0
        private var Smcnt: Double = 0.toDouble()//หน่วยน้ำมาตรย่อย
        private val BigPrsWtUsg: Double = 0.toDouble() //รวมมาตรย่อย
        private var NoofHouse: Double = 0.toDouble()// บ้านเคหะ
        private var PwaFlag: String? = null//ส่วนลดประชารัฐ
        private var OldMtrUsg: Double = 0.toDouble() // หน่วยน้ำค้างมิเตอร์ก่อนเปลี่ยน
        private var debmonth: Int = 0
        private var debamt: Double = 0.toDouble()
        private val Unitdiscnt: Double = 0.toDouble() //ส่วนลดหน่วยน้ำมาตรใหญ่
        private val Discntbaht: Double = 0.toDouble() //ส่วนลดค่าน้ำทหาร
        private var LowPrice: Double = 0.toDouble() // ค่าน้ำขั้นต่ำตามประเภทผู้ใช้น้ำ
        private var Invoicecnt: String? = null
        private var NorTrfwt: Double = 0.toDouble()//ค่าใช้น้ำปกติ
        private var DiscntAmt: Double = 0.toDouble()// ส่วนลด ดูจาก DisCntCode
        private var NetTrfWt: Double = 0.toDouble() // ค่าใช้จ่ายจริง
        private var Vat: Double = 0.toDouble() // ภาษี (NetTrfWt + SrvFee) * 0.7
        private var SrvFee: Double = 0.toDouble() // ค่าน้ำขั้นต่ำ คิดตามขนาดมิเตอร์
        private var TotTrfwt: Double = 0.toDouble() // ค่าน้ำสุทธิ NetTrfWt + Vat + SrvFee
        private var ChkDigit: String? = null// chkDigit ที่พิมพ์ใน Barcode
        private val tmpChkDigit: Int = 0// chkDigit ที่พิมพ์ใน Barcode
        private var Str_report = "Null"
        private var discUnit: Double = 0.toDouble()
        private var discBaht: Double = 0.toDouble()
        private var discNom: Double = 0.toDouble()
        private var discPcen: Double = 0.toDouble()
        private var discNum: Double = 0.toDouble()
        private var Comment: String? = null
        var USERID: String? = null
        var HLN: String? = null
        var ComMentDec: String? = null
        var BillSend: String? = null
        var OkRead: Boolean = false
        var OkPrint: Boolean = false
        var Usgcalmthd = "2"
        var CustStat: String? = null
        var invflag: String? = null
        var ServiceFlag = "1"
        private var CalWtUsg: Double = 0.toDouble()
        var SendOutSt: Boolean = false
        var SendNotIn: Boolean = false
        var ChkNearMT: Boolean = false

        private val MIN1 = 4//หน่วยน้ำขั้นต่ำประเภท1
        private val MIN2 = 9//หน่วยน้ำขั้นต่ำประเภท2
        private val MIN3 = 15//หน่วยน้ำขั้นต่ำประเภท3


        internal var bn_value: String? = null
        internal var chkRead: Int = 0

        private var iposition: Int = 0
        private var rposition: Int = 0
        private var printaddress = ""

        private var mBluetoothAdapter: BluetoothAdapter? = null
        private var mCommandService: bluetoothCommandService? = null
        internal var Statust_Connect = false
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_readmeter)

        var tsc = TSCActivity()

        initWidget()
        chRoute = intent.getStringExtra("TRoute")
        USERID = intent.getStringExtra("userid")

        Log.d("USERID",USERID.toString())

        showData(chRoute)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        Comment = "00"
        etRead?.setText("")
        etRead?.requestFocus()
        etRead?.setSelectAllOnFocus(true)

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etRead, InputMethodManager.SHOW_IMPLICIT)


        bn_next?.setOnClickListener(this)
        bn_prev?.setOnClickListener(this)
        bn_first?.setOnClickListener(this)
        bn_last?.setOnClickListener(this)

        bnOk?.setOnClickListener(this)
        bn_readagain?.setOnClickListener(this)
        bn_notread?.setOnClickListener(this)


        ckAvg?.setOnClickListener {

            if (ckAvg!!.isChecked) {
                CustStat = "3"
                SendAVGSt = true
                bn_notread?.visibility = View.INVISIBLE
                Usgcalmthd = "3"
                etRead?.requestFocus()
            } else {
                CustStat = "1"
                etRead?.requestFocus()
                SendAVGSt = false
                bn_notread?.visibility = View.VISIBLE
                Usgcalmthd = "2"
            }
        }


        bn_reprint?.setOnClickListener {
            // TODO Auto-generated method stub
            if (Str_report=="") {
                showDialog("No data to Print")
            } else {
                val t = Thread(GotoPrint(Str_report))
                t.start()
            }
        }

        bn_find?.setOnClickListener {
            // TODO Auto-generated method stub
            FindMeter()
        }


        // Create persistent LocationManager reference
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)

    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            lat = location.latitude
            lon = location.longitude
            tv_latlon!!.setText(lat.toString() + "," + lon)

        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun FindMeter() {
        // TODO Auto-generated method stub
        val findMeter = Intent(applicationContext, Page_FindMeter::class.java)
        findMeter.putExtra("tRoute", chRoute)
        startActivityForResult(findMeter, FIND_OK)
    }

    private fun initWidget() {

        //bnCap = (Button)findViewById(R.id.bn_cap);
        bnOk = findViewById(R.id.bn_ok) as Button
        bn_next = findViewById(R.id.bn_moveN) as ImageButton
        bn_prev = findViewById(R.id.bn_moveP) as ImageButton
        bn_first = findViewById(R.id.bn_moveF) as ImageButton
        bn_last = findViewById(R.id.bn_moveL) as ImageButton
        bn_notread = findViewById(R.id.bn_notread) as Button
        bn_readagain = findViewById(R.id.bn_readagain) as Button
        bn_reprint = findViewById(R.id.bn_printagain) as Button
        bn_find = findViewById(R.id.bn_findmeter) as Button

        tv1 = findViewById(R.id.tx_route2) as TextView
        tv2 = findViewById(R.id.tx_seq) as TextView
        tv3 = findViewById(R.id.tx_custcode) as TextView
        tv4 = findViewById(R.id.tx_custname) as TextView
        tv5 = findViewById(R.id.tx_custaddr) as TextView
        tv6 = findViewById(R.id.tx_disccode) as TextView
        tv7 = findViewById(R.id.tx_usetype) as TextView
        tv8 = findViewById(R.id.tx_meterno2) as TextView
        tv9 = findViewById(R.id.tx_mtrmkcode) as TextView
        tv10 = findViewById(R.id.tx_readflag) as TextView
        tv11 = findViewById(R.id.tx_lstcnt) as TextView
        tv12 = findViewById(R.id.tx_metersize) as TextView

        ckAvg = findViewById(R.id.ck_avg) as CheckBox
        tvdupamt = findViewById(R.id.tx_dupamt) as TextView
        tv_latlon = findViewById(R.id.tv_latlon) as TextView
        chkRead = 1
        etRead = findViewById(R.id.et_readmeter) as EditText
        etRead!!.setRawInputType(Configuration.KEYBOARD_12KEY)
        etRead!!.setOnKeyListener(OnKeyListener { v, keyCode, event ->
            // If the event is a key-down event on the "enter" button
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                calMeter()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun showData(route: String?) {
        Log.d("เส้นทางที่แสดง",route)
        try {
            if (iposition >= 0) {
                cur = db.select_CISData(route!!)

                val i = cur!!.count

                if (i > 0) {
                    if (cur != null) {
                        if (cur!!.moveToFirst()) {
                            cur!!.moveToPosition(iposition)
                            setData(cur!!)
                        }

                    }
                } else {
                    showDialog("สายการจดหน่วยนี้อ่านข้อมูลครบหมดแล้ว")
                    return
                }

            }
        } catch (e: Exception) {
            println(e)
            throw e

        }

    }

    fun setData(c: Cursor) {

        Log.d("setData",  c.getString(c.getColumnIndex("custcode")))
        val cgetposition: Int
        if (c.isLast) {
            Toast.makeText(this, "ข้อมูลสุดท้าย !! ", Toast.LENGTH_SHORT).show()
        }
        cgetposition = c.position
        iposition = c.position
        Log.d("Position", cgetposition.toString())
        dupnet = 0.0
        dupvat = 0.0
        dupamt = 0.0
        debtnet = 0.0
        debtvat = 0.0
        debtamt = 0.0
        tmpdupamt = 0.0
        tv1?.text = c.getString(c.getColumnIndex("mtrrdroute"))
        tv2?.text = c.getString(c.getColumnIndex("mtrseq"))
        tv3?.text = c.getString(c.getColumnIndex("custcode"))
        tv4?.text = c.getString(c.getColumnIndex("custname"))
        tv5?.text = c.getString(c.getColumnIndex("custaddr"))
        tv6?.text = c.getString(c.getColumnIndex("discntcode"))
        tv7?.text = c.getString(c.getColumnIndex("usertype"))
        tv8?.text = c.getString(c.getColumnIndex("meterno"))
        tv9?.text = c.getString(c.getColumnIndex("mtrmkcode"))
        tv11?.text = c.getString(c.getColumnIndex("lstmtrcnt"))
        tv12?.text = c.getString(c.getColumnIndex("metersize"))

        wwcode = c.getString(c.getColumnIndex("wwcode"))
        CustCode = c.getString(c.getColumnIndex("custcode"))
        Mtrrdroute = c.getString(c.getColumnIndex("mtrrdroute"))
        Mtrseq = c.getInt(c.getColumnIndex("mtrseq"))
        MeterSize = c.getString(c.getColumnIndex("metersize"))
        Invoicecnt = c.getString(c.getColumnIndex("invoicecnt"))
        MeterNo = c.getString(c.getColumnIndex("meterno"))
        ReadFlag = c.getInt(c.getColumnIndex("readflag"))
        Smcnt = c.getDouble(c.getColumnIndex("smcnt"))
        CustStat = "1"
        when (MeterSize) {
            "01" -> tv12?.text = "1/2 นิ้ว"
            "02" -> tv12?.text = "3/4 นิ้ว"
            "03" -> tv12?.text = "1 นิ้ว"
            "04" -> tv12?.text = "1 1/2 นิ้ว"
            "05" -> tv12?.text = "2 นิ้ว"
            "06" -> tv12?.text = "2 1/2 นิ้ว"
            "07" -> tv12?.text = "3 นิ้ว"
            "08" -> tv12?.text = "4 นิ้ว"
            "09" -> tv12?.text = "6 นิ้ว"
            "10" -> tv12?.text = "8และมากกว่า "
        }

        MtRmkCode = c.getString(c.getColumnIndex("mtrmkcode"))
        when (MtRmkCode) {
            "01" -> tv9?.text = "เค็นท์"
            "02" -> tv9?.text = "อาซาฮี"
            "03" -> tv9?.text = "อาชิโต้ไก"
            "04" -> tv9?.text = "เอส.ที.ซี.อี."
            "07" -> tv9?.text = "ออสเซล"
            "11" -> tv9?.text = "แมคคาลินา"
            "14" -> tv9?.text = "ไทยอาชิ, ไทยอิชิ"
            "15" -> tv9?.text = "ไวลท์แม็ค"
            "17" -> tv9?.text = "สลิมเบอร์เกอร์"
        }

        ckAvg?.isChecked = false
        bn_notread?.visibility = View.VISIBLE
        SendAVGSt = false
        usertype = c.getString(c.getColumnIndex("usertype"))
        LstMtrCnt = c.getDouble(c.getColumnIndex("lstmtrcnt"))
        controlmtr = c.getString(c.getColumnIndex("controlmtr"))
        Est = c.getDouble(c.getColumnIndex("meterest"))
        debmonth = c.getInt(c.getColumnIndex("debmonth"))
        debamt = c.getDouble(c.getColumnIndex("debamt"))
        NoofHouse = c.getInt(c.getColumnIndex("noofhouse")).toDouble()
        PwaFlag = c.getString(c.getColumnIndex("pwa_flag")) //ส่วนลดประชารัฐ
        Log.d("PwaFlag====>", PwaFlag)
        DisCntCode = c.getString(c.getColumnIndex("discntcode")).trim { it <= ' ' }
        if (DisCntCode!!.equals("", ignoreCase = true)) {
            DisCntCode = "0"
        }
        OldMtrUsg = c.getDouble(c.getColumnIndex("remwtusg"))
        invflag = c.getString(c.getColumnIndex("invflag"))
        //		dupamt =  c.getDouble(c.getColumnIndex(("dupamt")));
        //		tmpdupamt =  c.getDouble(c.getColumnIndex(("tempdupamt")));
        //		tvdupamt.setText(String.valueOf(tmpdupamt));
        AvgWtUsg = cur!!.getInt(cur!!.getColumnIndex("avgwtuse"))
        //		ServiceFlag = cur.getString(c.getColumnIndex("service_flag"));

        ChkDigit = "00"

        if (ReadFlag == 1) {
            tv10?.text = "อ่านแล้ว"
        } else {
            tv10?.text = "ยังไม่อ่าน"
        }

        etRead?.setText("")
        etRead?.requestFocus()
        etRead?.setSelectAllOnFocus(true)
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(etRead, InputMethodManager.SHOW_IMPLICIT);
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

    private fun calMeter() {
        Log.d("กดเข้าอ่านปกติ", PrsWtUsg.toString())
        Comment = "00"
        SendOutSt = false

        if (cur != null) {
            AvgWtUsg = cur!!.getInt(cur!!.getColumnIndex("avgwtuse"))
            Smcnt = cur!!.getInt(cur!!.getColumnIndex("smcnt")).toDouble()

        }
        val text = etRead?.text.toString().trim { it <= ' ' }
        PrsMtrCnt = java.lang.Double.parseDouble(text)
        if (text.equals("", ignoreCase = true)) {
            Toast.makeText(this, "กรุณาใส่เลขมาตร !! ", Toast.LENGTH_SHORT).show()
        } else {

            val readPlus = Intent(applicationContext, Page_ReadMeterPlus::class.java)

            if (ckAvg?.isChecked!!) {
                PrsMtrCnt = java.lang.Double.parseDouble(text)
                PrsWtUsg = AvgWtUsg.toDouble()
                CalWtUsg = PrsWtUsg
                SendAVGSt = true
            } else {
                PrsMtrCnt = java.lang.Double.parseDouble(text)
                getPrsWtUsg()
            }

            PrsMtrCnt = java.lang.Double.parseDouble(text)

            if (PrsWtUsg < 0) {

                Toast.makeText(this, "หน่วยน้ำติดลบ กรุณาตรวจสอบ", Toast.LENGTH_SHORT).show()
                showNotification()
                return
            } else {
                Log.d("3e", "3e")
                HLN = "N"
                val Percent: Double?
                val Npercent: Double?
                val LoLimit: Double?
                val HiLimit: Double?

                if (AvgWtUsg == 0) {
                    Log.d("1", "1")
                    if (PrsWtUsg > 0) {
                        HLN = "H"
                    } else {
                        HLN = "N"
                    }
                } else if (AvgWtUsg > 0) {
                    Log.d("2", "2")
                    Log.d("AVG >0 ==>", AvgWtUsg.toString())
                    if (PrsWtUsg <= 20) {
                        Npercent = 0.5
                        Log.d("ค่า SMCNT ==>", Smcnt.toString())
                        LoLimit = AvgWtUsg - Npercent * AvgWtUsg
                        HiLimit = AvgWtUsg + Npercent * AvgWtUsg
                        Log.d("ค่า LO ==>", LoLimit.toString())
                        Log.d("ค่า HI ==>", HiLimit.toString())
                        if (PrsWtUsg - Smcnt >= LoLimit && PrsWtUsg - Smcnt <= HiLimit) {
                            HLN = "N"

                        } else if (PrsWtUsg - Smcnt < LoLimit) {
                            HLN = "L"

                        } else if (PrsWtUsg - Smcnt > HiLimit) {
                            HLN = "H"

                        }
                    } else if (PrsWtUsg <= 50) {
                        Log.d("3", "3")
                        Npercent = 0.35

                        LoLimit = AvgWtUsg - Npercent * AvgWtUsg
                        HiLimit = AvgWtUsg + Npercent * AvgWtUsg

                        if (PrsWtUsg - Smcnt >= LoLimit && PrsWtUsg - Smcnt <= HiLimit) {
                            HLN = "N"

                        } else if (PrsWtUsg - Smcnt < LoLimit) {
                            HLN = "L"

                        } else if (PrsWtUsg - Smcnt > HiLimit) {
                            HLN = "H"

                        }
                    } else if (PrsWtUsg <= 100) {
                        Log.d("4", "4")
                        Percent = Math.abs(PrsWtUsg - Smcnt - AvgWtUsg)

                        if (Percent <= 10) {
                            HLN = "N"

                        } else if (PrsWtUsg - Smcnt < AvgWtUsg) {
                            HLN = "L"

                        } else if (PrsWtUsg - Smcnt > AvgWtUsg) {
                            HLN = "H"

                        }
                    } else if (PrsWtUsg > 100) {
                        Percent = Math.abs(PrsWtUsg - Smcnt - AvgWtUsg) / (PrsWtUsg - Smcnt)
                        //Toast.makeText(this,"avg "+ String.valueOf(AvgWtUsg),Toast.LENGTH_LONG).show();
                        //Toast.makeText(this,"น้ำสูง "+ String.valueOf(PrsWtUsg),Toast.LENGTH_LONG).show();
                        //Toast.makeText(this,"Percent "+ String.valueOf(Percent),Toast.LENGTH_LONG).show();
                        if (Percent < 0.2) {
                            //Toast.makeText(this,"เข้าเคส "+ String.valueOf(Percent),Toast.LENGTH_LONG).show();
                            HLN = "N"

                        } else if (PrsWtUsg - Smcnt < AvgWtUsg) {
                            HLN = "L"

                        } else if (PrsWtUsg - Smcnt > AvgWtUsg) {
                            HLN = "H"

                        }
                    }
                }


                Log.d("5", "5")

                //เพิ่มเงื่อนไขสำหรับตรวจสอบค่าน้ำสูงเกิน 25/09/2557 เพิ่มน้ำสูงตามช่วงเฉลี่ย (20/11/27)
                when (usertype!!.subSequence(0, 1)) {
                    "1" ->

                        if (PrsWtUsg > AvgWtUsg * 3 && TotTrfwt >= 3000) {
                            Comment = "27"//ใช้น้ำมากผิดปกติ (27)
                            HLN = "H"
                            readPlus.putExtra("Tmsg", true)
                            readPlus.putExtra("Bmsg", false)
                            //SendOutSt = false;
                        } else {
                            readPlus.putExtra("Tmsg", false)
                            readPlus.putExtra("Bmsg", true)

                        }
                    "2" -> if (PrsWtUsg > AvgWtUsg * 3 && TotTrfwt >= 50000) {
                        Comment = "27"//ใช้น้ำมากผิดปกติ (27)
                        HLN = "H"
                        readPlus.putExtra("Tmsg", true)
                        readPlus.putExtra("Bmsg", false)

                    } else {
                        readPlus.putExtra("Tmsg", false)
                        readPlus.putExtra("Bmsg", true)

                    }
                    "3" -> if (PrsWtUsg > AvgWtUsg * 2 && TotTrfwt >= 100000) {
                        Comment = "27"//ใช้น้ำมากผิดปกติ (27)
                        HLN = "H"
                        readPlus.putExtra("Tmsg", true)
                        readPlus.putExtra("Bmsg", false)

                    } else {
                        readPlus.putExtra("Tmsg", false)
                        readPlus.putExtra("Bmsg", true)

                    }
                }

                Log.d("6", "6")

                if (MeterSize!!.equals("01", ignoreCase = true) && PrsWtUsg >= 1500) {
                    Comment = "27"//ใช้น้ำมากผิดปกติ (27)
                    HLN = "H"
                    readPlus.putExtra("Tmsg", true)
                    readPlus.putExtra("Bmsg", false)
                } else if (MeterSize!!.equals("02", ignoreCase = true) && PrsWtUsg >= 3000) {
                    Comment = "27"//ใช้น้ำมากผิดปกติ (27)
                    HLN = "H"
                    readPlus.putExtra("Tmsg", true)
                    readPlus.putExtra("Bmsg", false)
                } else if (MeterSize!!.equals("03", ignoreCase = true) && PrsWtUsg >= 4000) {
                    Comment = "27"//ใช้น้ำมากผิดปกติ (27)
                    HLN = "H"
                    readPlus.putExtra("Tmsg", true)
                    readPlus.putExtra("Bmsg", false)
                }

                if (HLN!!.equals("L", ignoreCase = true)) {

                    Comment = "26" //ใช้น้ำน้อยผิดปกติ (26);
                } else if (HLN!!.equals("H", ignoreCase = true)) {
                    Comment = "27"//ใช้น้ำมากผิดปกติ (27);
                } else if (HLN!!.equals("N", ignoreCase = true)) {

                    Comment = "00"
                }

                if (PrsMtrCnt < LstMtrCnt) {//' จดหลัง น้อยกว่า จดก่อน
                    //showNotification();
                    if (ChkNearMT) {
                        Comment = "18"
                        SendOutSt = true
                    } else {
                        Comment = "19"//"เลขที่อ่านได้น้อยกว่าเลขมาตรครั้งก่อน (19)"
                        Toast.makeText(this, "มาตรถอยหลัง..!! โปรดตรวจสอบ", Toast.LENGTH_SHORT).show()
                        readPlus.putExtra("Tmsg", true)
                        readPlus.putExtra("Bmsg", false)
                        SendOutSt = false
                    }
                }
                Log.d("7", "7")


                if (USERID.toString().equals("8899")){
                    Toast.makeText(this, "รหัสพนักงาน QC 8899", Toast.LENGTH_SHORT).show()
                    readPlus.putExtra("Tmsg", false)
                    readPlus.putExtra("Bmsg", true)
                }

                Log.d("8", USERID.toString())
                readPlus.putExtra("userid", USERID)
                readPlus.putExtra("Twwcode", wwcode)
                readPlus.putExtra("MtrSeq", Mtrseq)
                readPlus.putExtra("TCustCode", CustCode)
                readPlus.putExtra("TRoute", chRoute)
                readPlus.putExtra("TMeterno", MeterNo)
                readPlus.putExtra("TPrsmtrcnt", PrsMtrCnt)
                readPlus.putExtra("TPrswtusg", PrsWtUsg)
                readPlus.putExtra("THln", HLN)
                readPlus.putExtra("TSmcnt", Smcnt)
                readPlus.putExtra("TComment", Comment)
                readPlus.putExtra("SentAVG", SendAVGSt)
                readPlus.putExtra("SendOutSt", SendOutSt)
                readPlus.putExtra("OkRead", false)
                readPlus.putExtra("OkPrint", false)
                readPlus.putExtra("SendNotIn", false)


                startActivityForResult(readPlus, RESULT_REQUEST)

            }
        }

    }


    //Set SoundUPDown
    private fun showNotification() {

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
    }

    fun getPrsWtUsg() {

        if (LstMtrCnt == Est) {
            // การคำนวณปกติ
            PrsWtUsg = PrsMtrCnt - LstMtrCnt + OldMtrUsg
            CalWtUsg = PrsWtUsg
            if (PrsMtrCnt < LstMtrCnt) {// มิเตอร์หมุนครบรอบ
                val nLstMtrCnt: Int
                nLstMtrCnt = LstMtrCnt.toInt()
                ChkNearMT = true
                if (nLstMtrCnt.toString().length == 7) {
                    //1000000 ขนาดมิเตอร์เป็น 1 เลขที่สูงสุดที่หมุนได้คือ 999999
                    //ถ้าจะให้ดีต้องมีการ Comment ด้วยว่ามิเตอร์หมุนครบรอบ
                    if (LstMtrCnt < 9000000) {
                        ChkNearMT = false
                    }
                    PrsWtUsg = PrsMtrCnt + 10000000 - LstMtrCnt + OldMtrUsg
                    CalWtUsg = PrsWtUsg
                } else if (nLstMtrCnt.toString().length == 6) {
                    //1000000 ขนาดมิเตอร์เป็น 1 เลขที่สูงสุดที่หมุนได้คือ 999999
                    //ถ้าจะให้ดีต้องมีการ Comment ด้วยว่ามิเตอร์หมุนครบรอบ
                    if (LstMtrCnt < 900000) {
                        ChkNearMT = false
                    }
                    PrsWtUsg = PrsMtrCnt + 1000000 - LstMtrCnt + OldMtrUsg
                    CalWtUsg = PrsWtUsg
                } else if (nLstMtrCnt.toString().length == 5) {
                    //100000 ขนาดมิเตอร์เป็น 1 เลขที่สูงสุดที่หมุนได้คือ 99999
                    //ถ้าจะให้ดีต้องมีการ Comment ด้วยว่ามิเตอร์หมุนครบรอบ
                    if (LstMtrCnt < 90000) {
                        ChkNearMT = false
                    }
                    PrsWtUsg = PrsMtrCnt + 100000 - LstMtrCnt + OldMtrUsg
                    CalWtUsg = PrsWtUsg
                } else {
                    //10000 ขนาดมิเตอร์เป็น 1 เลขที่สูงสุดที่หมุนได้คือ 9999
                    //ถ้าจะให้ดีต้องมีการ Comment ด้วยว่ามิเตอร์หมุนครบรอบ
                    if (LstMtrCnt < 9000) {
                        ChkNearMT = false
                    }
                    PrsWtUsg = PrsMtrCnt + 10000 - LstMtrCnt + OldMtrUsg
                    CalWtUsg = PrsWtUsg
                }

            } else if (PrsMtrCnt == LstMtrCnt) { //'มิเตอร์ไม่หมุน
                PrsWtUsg = 0 + OldMtrUsg
                CalWtUsg = PrsWtUsg
            }
        } else if (LstMtrCnt < Est) {
            //' ตรวจสอบบ้านปิด
            if (PrsMtrCnt >= Est) {
                PrsWtUsg = PrsMtrCnt - Est + OldMtrUsg
                CalWtUsg = PrsWtUsg
            } else if (PrsMtrCnt < Est) {
                PrsWtUsg = 0 + OldMtrUsg
                CalWtUsg = PrsWtUsg
            }

        }

    }

    fun getPriceAll() {
        val tmpUsg: Double

        when (controlmtr) {
            "0"//ปกติ
            -> CalPriceNormal()
            "1"//มาตรใหญ่
            -> CalBigmeter()
            "2"//มาตรย่อย
            -> CalPriceNormal()
            "3"//มาตรเคหะ
            -> {
                tmpUsg = PrsWtUsg
                CalHousing()
                PrsWtUsg = tmpUsg
            }
            "4"//รัฐบาลรับภาระ 50 หน่วย
            -> {
                tmpUsg = PrsWtUsg
                // CalHousing();
                PrsWtUsg = tmpUsg
            }
            "5"//รัฐบาลรับภาระ 30 หน่วย
            -> {
                tmpUsg = PrsWtUsg
                // CalHousing();
                PrsWtUsg = tmpUsg
            }
            "6"//มาตรใหญ่ทหาร
            -> {
            }
            else -> {
            }
        }//				CalBigMeterSold();
    }

    private fun CalPriceNormal() {
        // TODO Auto-generated method stub
        val count51: Int
        val count52: Int
        val count53: Int
        val count54: Int
        val count42: Int
        var isDisCntAmt = true


        when (usertype!!.subSequence(0, 1)) {
            "1" -> if (PrsWtUsg >= 11) {

                count51 = db.countDbst51
                val mCursor51 = db.curDbst51()
                mCursor51!!.moveToFirst()
                for (i in 0..count51 - 1) {
                    if (PrsWtUsg >= mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor51.getDouble(mCursor51.getColumnIndex("highusgran"))) {
                        NorTrfwt = mCursor51.getDouble(mCursor51.getColumnIndex("acwttrfamt"))
                        NorTrfwt += mCursor51.getDouble(mCursor51.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) - 1))
                        break
                    }

                    mCursor51.moveToNext()
                }
                LowPrice = 0.0
                DiscntAmt = 0.0
            } else {
                if (PrsWtUsg == 0.0) {
                    NorTrfwt = 0.0
                    LowPrice = 0.0
                    DiscntAmt = 0.0
                    isDisCntAmt = false
                } else {
                    count51 = db.countDbst51
                    val mCursor51 = db.curDbst51()
                    mCursor51!!.moveToFirst()
                    for (i in 0..count51 - 1) {
                        if (PrsWtUsg >= mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor51.getDouble(mCursor51.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor51.getDouble(mCursor51.getColumnIndex("acwttrfamt"))
                            NorTrfwt += mCursor51.getDouble(mCursor51.getColumnIndex("wttrfrt")) * PrsWtUsg
                            break
                        }

                        mCursor51.moveToNext()
                    }
                    LowPrice = 0.0
                    DiscntAmt = 0.0
                }

            }
            "2" -> if (usertype!!.subSequence(0, 2)=="28" || usertype!!.subSequence(0, 2)=="29") {
                Log.d("ประเภท29", usertype)
                //ประเภท2829
                if (PrsWtUsg >= 11) {
                    count54 = db.countDbst54
                    val mCursor54 = db.curDbst54()
                    mCursor54!!.moveToFirst()
                    for (i in 0..count54 - 1) {
                        if (PrsWtUsg >= mCursor54.getDouble(mCursor54.getColumnIndex("lowusgran"))
                                && PrsWtUsg <= mCursor54.getDouble(mCursor54.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor54.getDouble(mCursor54.getColumnIndex("acwttrfamt"))
                            NorTrfwt += mCursor54.getDouble(mCursor54.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor54.getDouble(mCursor54.getColumnIndex("lowusgran")) - 1))
                            Log.d("NorTrfwt", NorTrfwt.toString())
                            break
                        }
                        mCursor54.moveToNext()
                    }
                    DiscntAmt = 0.0
                } else {
                    if (PrsWtUsg == 10.0) {
                        count54 = db.countDbst54
                        val mCursor54 = db.curDbst54()
                        mCursor54!!.moveToFirst()
                        for (i in 0..count54 - 1) {
                            if (PrsWtUsg >= mCursor54.getDouble(mCursor54.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor54.getDouble(mCursor54.getColumnIndex("highusgran"))) {
                                NorTrfwt = mCursor54.getDouble(mCursor54.getColumnIndex("acwttrfamt"))
                                NorTrfwt += mCursor54.getDouble(mCursor54.getColumnIndex("wttrfrt")) * PrsWtUsg
                                break
                            }

                            mCursor54.moveToNext()
                        }
                        LowPrice = 0.0
                        DiscntAmt = 0.0
                    } else {
                        NorTrfwt = 150.0
                        LowPrice = 150.0
                        DiscntAmt = 0.0
                    }
                }
            } else {

                if (PrsWtUsg >= 11) {
                    count52 = db.countDbst52
                    val mCursor52 = db.curDbst52()
                    mCursor52!!.moveToFirst()
                    for (i in 0..count52 - 1) {
                        if (PrsWtUsg >= mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor52.getDouble(mCursor52.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor52.getDouble(mCursor52.getColumnIndex("acwttrfamt"))
                            NorTrfwt += mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) - 1))
                            Log.d("NorTrfwt", NorTrfwt.toString())
                            break
                        }
                        mCursor52.moveToNext()
                    }
                    DiscntAmt = 0.0
                } else {
                    if (PrsWtUsg == 10.0) {
                        count52 = db.countDbst52
                        val mCursor52 = db.curDbst52()
                        mCursor52!!.moveToFirst()
                        for (i in 0..count52 - 1) {
                            if (PrsWtUsg >= mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor52.getDouble(mCursor52.getColumnIndex("highusgran"))) {
                                NorTrfwt = mCursor52.getDouble(mCursor52.getColumnIndex("acwttrfamt"))
                                NorTrfwt += mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt")) * PrsWtUsg
                                break
                            }

                            mCursor52.moveToNext()
                        }
                        LowPrice = 0.0
                        DiscntAmt = 0.0
                    } else {
                        NorTrfwt = 150.0
                        LowPrice = 150.0
                        DiscntAmt = 0.0
                    }
                }
            }
            "3" -> if (PrsWtUsg > MIN3) {

                count53 = db.countDbst53
                val mCursor53 = db.curDbst53()
                mCursor53!!.moveToFirst()
                for (i in 0..count53 - 1) {
                    if (PrsWtUsg >= mCursor53.getDouble(mCursor53.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor53.getDouble(mCursor53.getColumnIndex("highusgran"))) {
                        NorTrfwt = mCursor53.getDouble(mCursor53.getColumnIndex("acwttrfamt"))
                        NorTrfwt += mCursor53.getDouble(mCursor53.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor53.getDouble(mCursor53.getColumnIndex("lowusgran")) - 1))
                        Log.d("OK", NorTrfwt.toString())
                        break
                    }
                    mCursor53.moveToNext()
                }
                DiscntAmt = 0.0
            } else {
                NorTrfwt = 300.0
                LowPrice = 300.0
                DiscntAmt = 0.0
            }
        }


        if (DisCntCode!!.equals("1", ignoreCase = true)) {
            if (usertype!!.subSequence(0, 1)== "1" && PrsWtUsg > 100) {
                isDisCntAmt = false
            }
        } else if (DisCntCode == "0") {
            isDisCntAmt = false
        }

        if (isDisCntAmt) {
            DiscntAmt = CalDiscount(NorTrfwt, PrsWtUsg)
        }

        if (DiscntAmt < 0) {
            Toast.makeText(this, "ส่วนลดติดลบ กรุณาตรวจสอบ", Toast.LENGTH_SHORT).show()
            return
        }

        //ค่าบริการ
        count42 = db.countDbst53
        val mCursor42 = db.curDbst42()
        mCursor42!!.moveToFirst()
        for (i in 0 until count42 - 1) {
            if (MeterSize == mCursor42.getString(mCursor42.getColumnIndex("metersize"))) {
                SrvFee = mCursor42.getDouble(mCursor42.getColumnIndex("srvfee"))
                Log.d("Service:", SrvFee.toString())
                break
            }
            mCursor42.moveToNext()
        }

        Log.d("==>Disc:", DisCntCode.toString())
        if (DisCntCode == "C" || DisCntCode == "K" || DisCntCode == "Z") {
            DiscntAmt = 0.0
            NetTrfWt = NorTrfwt
        } else if (DisCntCode == "J") {
            NetTrfWt = NorTrfwt
        } else if (DisCntCode!!.equals("4", ignoreCase = true)) {

            NetTrfWt = NorTrfwt - DiscntAmt

            if (NetTrfWt < 0) {
                NetTrfWt = 0.0
            }

            if (PrsWtUsg <= 20) {
                NetTrfWt = 0.0
                DiscntAmt = NorTrfwt
            }
        } else {
            NetTrfWt = NorTrfwt - DiscntAmt
            Log.d("==>Net:", NetTrfWt.toString())
        }

        //ตรวจสอบ ServiceFlag 1=คิดค่าบริการ ,0 = ไม่คิดค่าบริการ

        if (ServiceFlag.equals("0", ignoreCase = true)) {
            SrvFee = 0.0
            Toast.makeText(this, "ไม่คิดค่าบริการ", Toast.LENGTH_LONG).show()
        }

        if (tmpdupamt > 0) {
            Vat = (NetTrfWt + SrvFee) * 0.07
            Vat = Math.round(Vat * 100).toDouble() / 100


            val form = DecimalFormat("0.00")
            debtvat = java.lang.Double.parseDouble(form.format(debtvat))
            Vat = java.lang.Double.parseDouble(form.format(Vat))

            TotTrfwt = NetTrfWt + SrvFee + Vat
            Log.d("==>TotTrfwt:", TotTrfwt.toString())

            if (tmpdupamt > TotTrfwt)
            //รับซ้ำมากกว่าค่าน้ำปัจจุบัน
            {
                Log.d("ค่าน้ำรับซ้ำมากกว่า", tmpdupamt.toString())
                debtnet = NetTrfWt + SrvFee
                debtvat = (NetTrfWt + SrvFee) * 0.07
                debtamt = debtnet + debtvat
                dupamt = tmpdupamt - debtamt
                dupvat = dupamt * 7 / 107
                dupnet = dupamt - dupvat
                Vat = 0.0
                TotTrfwt = 0.0

            } else if (tmpdupamt == TotTrfwt) { //รับซ้ำเท่ากับค่าน้ำปัจจุบัน
                //Toast.makeText(this, "ยอดเงินรวม : " + Vat, Toast.LENGTH_SHORT).show();
                Log.d("ค่าน้ำรับซ้ำเท่ากับ", tmpdupamt.toString())
                debtnet = NetTrfWt + SrvFee
                debtvat = (NetTrfWt + SrvFee) * 0.07
                debtamt = debtnet + debtvat


                Vat = 0.0
                TotTrfwt = 0.0
                dupnet = 0.0
                dupvat = 0.0
                dupamt = 0.0

            } else { //รับซ้ำน้อยกว่าค่าน้ำปัจจะบัน
                Log.d("ค่าน้ำรับซ้ำน้อยกว่า", tmpdupamt.toString())
                debtnet = NetTrfWt + SrvFee
                debtvat = (NetTrfWt + SrvFee) * 0.07
                debtamt = debtnet + debtvat

                TotTrfwt = debtamt - tmpdupamt

                Vat = TotTrfwt * 7 / 107
                Vat = Math.round(Vat * 100).toDouble() / 100
                val formt = DecimalFormat("0.00")
                Vat = java.lang.Double.parseDouble(formt.format(Vat))

                dupnet = 0.0
                dupvat = 0.0
                dupamt = 0.0
            }
        } else {

            val form = DecimalFormat("0.00")
            Vat = (NetTrfWt + SrvFee) * 0.07
            Vat = Math.round(Vat * 100).toDouble() / 100
            Vat = java.lang.Double.parseDouble(form.format(Vat))
            TotTrfwt = NetTrfWt + SrvFee + Vat



            debtamt = TotTrfwt
            debtnet = NetTrfWt + SrvFee
            debtvat = (NetTrfWt + SrvFee) * 0.07
            debtvat = java.lang.Double.parseDouble(form.format(debtvat))
            //				showDialog(String.valueOf(debtamt));
            //				showDialog(String.valueOf(debtnet));
            //				showDialog(String.valueOf(debtvat));
        }

        if (PwaFlag!!.equals("4", ignoreCase = true)) {
            var TempDisPwaPro = ""
            TempDisPwaPro = db.disPwaPro
            if (TotTrfwt >= java.lang.Double.parseDouble(TempDisPwaPro)) {
                Log.d("เข้าเคส=====> มากกว่า ", PwaFlag)
                debtamt = TotTrfwt
                debtnet = NetTrfWt + SrvFee
                debtvat = Vat
                TotTrfwt = debtamt - java.lang.Double.parseDouble(TempDisPwaPro)
                Vat = TotTrfwt * 7 / 107
                Vat = Math.round(Vat * 100).toDouble() / 100
                val formt = DecimalFormat("0.00")
                Vat = java.lang.Double.parseDouble(formt.format(Vat))
                distempamt = java.lang.Double.parseDouble(TempDisPwaPro)
                distempvat = distempamt * 7 / 107
                distempnet = distempamt - distempvat
            } else {
                Log.d("เข้าเคส=====> น้อยกว่า ", PwaFlag)
                debtamt = TotTrfwt
                if (DisCntCode!!.equals("4", ignoreCase = true)) {
                    NetTrfWt += SrvFee
                    debtnet = NetTrfWt
                } else {
                    debtnet = NetTrfWt + SrvFee
                }
                debtvat = Vat
                TotTrfwt = 0.00
                Vat = 0.00
                distempamt = debtamt
                distempnet = distempamt * 100 / 107
                distempvat = distempamt - distempnet
            }

        }

        Log.d("Net:", String.format("%.2f", NetTrfWt))
        Log.d("Service:", String.format("%.2f", SrvFee))
        Log.d("vat:", String.format("%.2f", Vat))
        Log.d("TotTrfwt:", String.format("%.2f", TotTrfwt))

    }

    private fun CalHousing() {
        // TODO Auto-generated method stub
        Log.d("CalHouing", "housing")

        val tmpUsg: Double
        var PrsWtUsg2: Double
        var wttr = 0.0
        val count51: Int
        val count52: Int
        val count53: Int
        val count54: Int
        val count42: Int
        var isDisCntAmt = true

        tmpUsg = PrsWtUsg
        //หน่วยน้ำ หาร ด้วย จำนวนบ้านเคหะ
        PrsWtUsg2 = PrsWtUsg / NoofHouse
        PrsWtUsg2 = Math.round(PrsWtUsg2).toDouble()


        when (usertype!!.substring(0, 1)) {
            "1" -> if (PrsWtUsg >= 11) {

                count51 = db.countDbst51
                val mCursor51 = db.curDbst51()
                mCursor51!!.moveToFirst()
                for (i in 0..count51 - 1) {
                    if (PrsWtUsg >= mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor51.getDouble(mCursor51.getColumnIndex("highusgran"))) {
                        NorTrfwt = mCursor51.getDouble(mCursor51.getColumnIndex("acwttrfamt"))
                        NorTrfwt += mCursor51.getDouble(mCursor51.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) - 1))
                        break
                    }

                    mCursor51.moveToNext()
                }
                LowPrice = 0.0
                DiscntAmt = 0.0
            } else {
                if (PrsWtUsg == 0.0) {
                    NorTrfwt = 0.0
                    LowPrice = 0.0
                    DiscntAmt = 0.0
                    isDisCntAmt = false
                } else {
                    count51 = db.countDbst51
                    val mCursor51 = db.curDbst51()
                    mCursor51!!.moveToFirst()
                    for (i in 0..count51 - 1) {
                        if (PrsWtUsg >= mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor51.getDouble(mCursor51.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor51.getDouble(mCursor51.getColumnIndex("acwttrfamt"))
                            NorTrfwt += mCursor51.getDouble(mCursor51.getColumnIndex("wttrfrt")) * PrsWtUsg
                            break
                        }

                        mCursor51.moveToNext()
                    }
                    LowPrice = 0.0
                    DiscntAmt = 0.0
                }

            }
            "2" -> if (usertype!!.substring(0, 2).equals("28", ignoreCase = true) || usertype!!.substring(0, 2).equals("29", ignoreCase = true)) {
                Log.d("ประเภท29", usertype)
                //ประเภท2829
                if (PrsWtUsg >= 11) {
                    count54 = db.countDbst54
                    val mCursor54 = db.curDbst54()
                    mCursor54!!.moveToFirst()
                    for (i in 0..count54 - 1) {
                        if (PrsWtUsg >= mCursor54.getDouble(mCursor54.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor54.getDouble(mCursor54.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor54.getDouble(mCursor54.getColumnIndex("acwttrfamt"))
                            wttr = mCursor54.getDouble(mCursor54.getColumnIndex("wttrfrt"))
                            NorTrfwt += mCursor54.getDouble(mCursor54.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor54.getDouble(mCursor54.getColumnIndex("lowusgran")) - 1))
                            Log.d("NorTrfwt", NorTrfwt.toString())
                            break
                        }
                        mCursor54.moveToNext()
                    }
                    DiscntAmt = 0.0
                } else {
                    if (PrsWtUsg == 10.0) {
                        count52 = db.countDbst52
                        val mCursor52 = db.curDbst52()
                        mCursor52!!.moveToFirst()
                        for (i in 0..count52 - 1) {
                            if (PrsWtUsg >= mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor52.getDouble(mCursor52.getColumnIndex("highusgran"))) {
                                NorTrfwt = mCursor52.getDouble(mCursor52.getColumnIndex("acwttrfamt"))
                                wttr = mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt"))
                                NorTrfwt += mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt")) * PrsWtUsg
                                break
                            }

                            mCursor52.moveToNext()
                        }
                        LowPrice = 0.0
                        DiscntAmt = 0.0
                    } else {
                        NorTrfwt = 150.0
                        LowPrice = 150.0
                        DiscntAmt = 0.0
                    }
                }
            } else {

                if (PrsWtUsg >= 11) {
                    count52 = db.countDbst52
                    val mCursor52 = db.curDbst52()
                    mCursor52!!.moveToFirst()
                    for (i in 0..count52 - 1) {
                        if (PrsWtUsg >= mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor52.getDouble(mCursor52.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor52.getDouble(mCursor52.getColumnIndex("acwttrfamt"))
                            wttr = mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt"))
                            NorTrfwt += mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) - 1))
                            Log.d("NorTrfwt", NorTrfwt.toString())
                            break
                        }
                        mCursor52.moveToNext()
                    }
                    DiscntAmt = 0.0
                } else {
                    if (PrsWtUsg == 10.0) {
                        count52 = db.countDbst52
                        val mCursor52 = db.curDbst52()
                        mCursor52!!.moveToFirst()
                        for (i in 0..count52 - 1) {
                            if (PrsWtUsg >= mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor52.getDouble(mCursor52.getColumnIndex("highusgran"))) {
                                NorTrfwt = mCursor52.getDouble(mCursor52.getColumnIndex("acwttrfamt"))
                                wttr = mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt"))
                                NorTrfwt += mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt")) * PrsWtUsg
                                break
                            }

                            mCursor52.moveToNext()
                        }
                        LowPrice = 0.0
                        DiscntAmt = 0.0
                    } else {
                        NorTrfwt = 150.0
                        LowPrice = 150.0
                        DiscntAmt = 0.0
                    }
                }
            }
            "3" -> if (PrsWtUsg > MIN3) {

                count53 = db.countDbst53
                val mCursor53 = db.curDbst53()
                mCursor53!!.moveToFirst()
                for (i in 0..count53 - 1) {
                    if (PrsWtUsg >= mCursor53.getDouble(mCursor53.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor53.getDouble(mCursor53.getColumnIndex("highusgran"))) {
                        NorTrfwt = mCursor53.getDouble(mCursor53.getColumnIndex("acwttrfamt"))
                        wttr = mCursor53.getDouble(mCursor53.getColumnIndex("wttrfrt"))
                        NorTrfwt += mCursor53.getDouble(mCursor53.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor53.getDouble(mCursor53.getColumnIndex("lowusgran")) - 1))
                        Log.d("OK", NorTrfwt.toString())
                        break
                    }
                    mCursor53.moveToNext()
                }
                DiscntAmt = 0.0
            } else {
                NorTrfwt = 300.0
                LowPrice = 300.0
                DiscntAmt = 0.0
            }
        }



        if (DisCntCode!!.equals("1", ignoreCase = true)) {
            if (usertype!!.substring(0, 1).equals("1", ignoreCase = true) && PrsWtUsg > 100) {
                isDisCntAmt = false
            }
        } else if (DisCntCode == "0") {
            isDisCntAmt = false
        }

        if (isDisCntAmt) {
            DiscntAmt = CalDiscount(NorTrfwt, PrsWtUsg)
        }

        if (DiscntAmt < 0) {
            Toast.makeText(this, "ส่วนลดติดลบ กรุณาตรวจสอบ", Toast.LENGTH_SHORT).show()
            return
        }

        count42 = db.countDbst53
        val mCursor42 = db.curDbst42()
        mCursor42!!.moveToFirst()
        for (i in 0 until count42 - 1) {
            if (MeterSize == mCursor42.getString(mCursor42.getColumnIndex("metersize"))) {
                SrvFee = mCursor42.getDouble(mCursor42.getColumnIndex("srvfee"))
                Log.d("Service:", SrvFee.toString())
                break
            }
            mCursor42.moveToNext()
        }

        //ตรวจสอบ ServiceFlag 1=คิดค่าบริการ ,0 = ไม่คิดค่าบริการ

        if (ServiceFlag.equals("0", ignoreCase = true)) {
            SrvFee = 0.0
            Toast.makeText(this, "ไม่คิดค่าบริการ", Toast.LENGTH_LONG).show()
        }

        Log.d("==>Disc:", DisCntCode.toString())
        if (DisCntCode!!.equals("C", ignoreCase = true) || DisCntCode!!.equals("K", ignoreCase = true) || DisCntCode!!.equals("Z", ignoreCase = true)) {
            DiscntAmt = 0.0
            NetTrfWt = wttr * tmpUsg
        } else if (DisCntCode!!.equals("J", ignoreCase = true)) {
            NetTrfWt = wttr * tmpUsg
        } else {
            NetTrfWt = wttr * tmpUsg
            Log.d("==>Net:", NetTrfWt.toString())
        }

        if (NoofHouse > 1)
        //คำนวณแบบบ้านการเคหะ
        {
            DiscntAmt = NorTrfwt - NetTrfWt
        }


        Vat = (NetTrfWt + SrvFee) * 0.07
        Vat = Math.round(Vat * 100).toDouble() / 100

        //Toast.makeText(this, "ยอดเงินรวม : " + Vat, Toast.LENGTH_SHORT).show();

        val form = DecimalFormat("0.00")
        Vat = java.lang.Double.parseDouble(form.format(Vat))
        Log.d("Net:", String.format("%.2f", NetTrfWt))
        Log.d("Service:", String.format("%.2f", SrvFee))
        Log.d("vat:", String.format("%.2f", Vat))


        if (DisCntCode!!.equals(" ", ignoreCase = true)) {
            TotTrfwt = NetTrfWt + SrvFee + Vat
        } else {
            TotTrfwt = NetTrfWt + SrvFee + Vat
        }
        //Log.d("Tottrfwt:",String.valueOf(TotTrfwt));
        // Toast.makeText(this, "ยอดเงินรวม : " + String.format("%.2f", TotTrfwt), Toast.LENGTH_SHORT).show();

    }

    private fun CalBigmeter() {

        // TODO Auto-generated method stub
        val count51: Int
        val count52: Int
        val count53: Int
        val count54: Int
        val count42: Int
        var isDisCntAmt = true
        when (usertype!!.substring(0, 1)) {
            "1" -> if (PrsWtUsg >= 11) {

                count51 = db.countDbst51
                val mCursor51 = db.curDbst51()
                mCursor51!!.moveToFirst()
                for (i in 0..count51 - 1) {
                    if (PrsWtUsg >= mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor51.getDouble(mCursor51.getColumnIndex("highusgran"))) {
                        NorTrfwt = mCursor51.getDouble(mCursor51.getColumnIndex("acwttrfamt"))
                        NorTrfwt += mCursor51.getDouble(mCursor51.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) - 1))
                        break
                    }

                    mCursor51.moveToNext()
                }
                LowPrice = 0.0
                DiscntAmt = 0.0
            } else {
                if (PrsWtUsg == 0.0) {
                    NorTrfwt = 0.0
                    LowPrice = 0.0
                    DiscntAmt = 0.0
                    isDisCntAmt = false
                } else {
                    count51 = db.countDbst51
                    val mCursor51 = db.curDbst51()
                    mCursor51!!.moveToFirst()
                    for (i in 0..count51 - 1) {
                        if (PrsWtUsg >= mCursor51.getDouble(mCursor51.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor51.getDouble(mCursor51.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor51.getDouble(mCursor51.getColumnIndex("acwttrfamt"))
                            NorTrfwt += mCursor51.getDouble(mCursor51.getColumnIndex("wttrfrt")) * PrsWtUsg
                            break
                        }

                        mCursor51.moveToNext()
                    }
                    LowPrice = 0.0
                    DiscntAmt = 0.0
                }

            }
            "2" -> if (usertype!!.substring(0, 2).equals("28", ignoreCase = true) || usertype!!.substring(0, 2).equals("29", ignoreCase = true)) {
                Log.d("ประเภท29", usertype)
                //ประเภท2829
                if (PrsWtUsg >= 11) {
                    count54 = db.countDbst54
                    val mCursor54 = db.curDbst54()
                    mCursor54!!.moveToFirst()
                    for (i in 0..count54 - 1) {
                        if (PrsWtUsg >= mCursor54.getDouble(mCursor54.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor54.getDouble(mCursor54.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor54.getDouble(mCursor54.getColumnIndex("acwttrfamt"))
                            NorTrfwt += mCursor54.getDouble(mCursor54.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor54.getDouble(mCursor54.getColumnIndex("lowusgran")) - 1))
                            Log.d("NorTrfwt", NorTrfwt.toString())
                            break
                        }
                        mCursor54.moveToNext()
                    }
                    DiscntAmt = 0.0
                } else {
                    NorTrfwt = 150.0
                    LowPrice = 150.0
                    DiscntAmt = 0.0
                }

            } else {
                Log.d("ประเภท2ปกติ", usertype)
                if (PrsWtUsg >= 11) {
                    count52 = db.countDbst52
                    val mCursor52 = db.curDbst52()
                    mCursor52!!.moveToFirst()
                    for (i in 0..count52 - 1) {
                        if (PrsWtUsg >= mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor52.getDouble(mCursor52.getColumnIndex("highusgran"))) {
                            NorTrfwt = mCursor52.getDouble(mCursor52.getColumnIndex("acwttrfamt"))
                            NorTrfwt += mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) - 1))
                            Log.d("NorTrfwt", NorTrfwt.toString())
                            break
                        }
                        mCursor52.moveToNext()
                    }
                    DiscntAmt = 0.0
                } else {
                    if (PrsWtUsg == 10.0) {
                        count52 = db.countDbst52
                        val mCursor52 = db.curDbst52()
                        mCursor52!!.moveToFirst()
                        for (i in 0..count52 - 1) {
                            if (PrsWtUsg >= mCursor52.getDouble(mCursor52.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor52.getDouble(mCursor52.getColumnIndex("highusgran"))) {
                                NorTrfwt = mCursor52.getDouble(mCursor52.getColumnIndex("acwttrfamt"))
                                NorTrfwt += mCursor52.getDouble(mCursor52.getColumnIndex("wttrfrt")) * PrsWtUsg
                                break
                            }

                            mCursor52.moveToNext()
                        }
                        LowPrice = 0.0
                        DiscntAmt = 0.0
                    } else {
                        NorTrfwt = 150.0
                        LowPrice = 150.0
                        DiscntAmt = 0.0
                    }
                }
            }
            "3" -> if (PrsWtUsg > MIN3) {

                count53 = db.countDbst53
                val mCursor53 = db.curDbst53()
                mCursor53!!.moveToFirst()
                for (i in 0..count53 - 1) {
                    if (PrsWtUsg >= mCursor53.getDouble(mCursor53.getColumnIndex("lowusgran")) && PrsWtUsg <= mCursor53.getDouble(mCursor53.getColumnIndex("highusgran"))) {
                        NorTrfwt = mCursor53.getDouble(mCursor53.getColumnIndex("acwttrfamt"))
                        NorTrfwt += mCursor53.getDouble(mCursor53.getColumnIndex("wttrfrt")) * (PrsWtUsg - (mCursor53.getDouble(mCursor53.getColumnIndex("lowusgran")) - 1))
                        Log.d("OK", NorTrfwt.toString())
                        break
                    }
                    mCursor53.moveToNext()
                }
                DiscntAmt = 0.0
            } else {
                NorTrfwt = 300.0
                LowPrice = 300.0
                DiscntAmt = 0.0
            }
        }

        //		boolean isDisCntAmt = true;

        if (DisCntCode!!.equals("1", ignoreCase = true)) {
            if (usertype!!.substring(0, 1).equals("1", ignoreCase = true) && PrsWtUsg > 100) {
                isDisCntAmt = false
            }
        } else if (DisCntCode == "0") {
            isDisCntAmt = false
        }

        if (isDisCntAmt) {
            DiscntAmt = CalDiscount(NorTrfwt, PrsWtUsg)
        }

        if (DiscntAmt < 0) {
            Toast.makeText(this, "ส่วนลดติดลบ กรุณาตรวจสอบ", Toast.LENGTH_SHORT).show()
            return
        }

        count42 = db.countDbst53
        val mCursor42 = db.curDbst42()
        mCursor42!!.moveToFirst()
        for (i in 0 until count42 - 1) {
            if (MeterSize == mCursor42.getString(mCursor42.getColumnIndex("metersize"))) {
                SrvFee = mCursor42.getDouble(mCursor42.getColumnIndex("srvfee"))
                Log.d("Service:", SrvFee.toString())
                break
            }
            mCursor42.moveToNext()
        }

        //ตรวจสอบ ServiceFlag 1=คิดค่าบริการ ,0 = ไม่คิดค่าบริการ

        if (ServiceFlag.equals("0", ignoreCase = true)) {
            SrvFee = 0.0
            Toast.makeText(this, "ไม่คิดค่าบริการ", Toast.LENGTH_LONG).show()
        }

        Log.d("==>Disc:", DisCntCode.toString())
        if (DisCntCode == "C" || DisCntCode == "K" || DisCntCode == "Z") {
            DiscntAmt = 0.0
            NetTrfWt = NorTrfwt
        } else if (DisCntCode == "J") {
            NetTrfWt = NorTrfwt
        } else if (DisCntCode!!.equals("4", ignoreCase = true)) {

            NetTrfWt = NorTrfwt - DiscntAmt

            if (NetTrfWt < 0) {
                NetTrfWt = 0.0
            }

            if (PrsWtUsg <= 20) {
                NetTrfWt = 0.0
                DiscntAmt = NorTrfwt
            }
        } else {
            NetTrfWt = NorTrfwt - DiscntAmt
            Log.d("==>Net:", NetTrfWt.toString())
        }

        Vat = (NetTrfWt + SrvFee) * 0.07
        Vat = Math.round(Vat * 100).toDouble() / 100

        val form = DecimalFormat("0.00")
        Vat = java.lang.Double.parseDouble(form.format(Vat))
        Log.d("Net:", String.format("%.2f", NetTrfWt))
        Log.d("Service:", String.format("%.2f", SrvFee))
        Log.d("vat:", String.format("%.2f", Vat))


        if (DisCntCode === " ") {
            TotTrfwt = NetTrfWt + SrvFee + Vat
        } else {
            TotTrfwt = NetTrfWt + SrvFee + Vat
        }
        Log.d("Tottrfwt:", TotTrfwt.toString())
        //Toast.makeText(this, "ยอดเงินรวม : " + String.format("%.2f", TotTrfwt), Toast.LENGTH_SHORT).show();

    }

    private fun CalDiscount(price: Double, used: Double): Double {
        // TODO Auto-generated method stub

        var disccode: String
        var discType = ""

        if (DisCntCode!!.equals("0", ignoreCase = true) || DisCntCode!!.equals(" ", ignoreCase = true)) {
            return 0.0
        }

        var TmpCal = 0.0
        val count06: Double
        count06 = db.countDbst06.toDouble()
        val mCursor06 = db.curDbst06()
        mCursor06!!.moveToFirst()
        var i = 0
        while (i < count06 - 1) {
            disccode = mCursor06.getString(mCursor06.getColumnIndex("DISCNTCODE")).trim { it <= ' ' }
            Log.d("open1", DisCntCode)
            Log.d("open2", disccode)
            if (Integer.parseInt(DisCntCode!!.trim { it <= ' ' }) == Integer.parseInt(disccode)) {
                Log.d("open3", discUnit.toString())
                discUnit = mCursor06.getDouble(mCursor06.getColumnIndex("DISCNTUNIT"))
                discBaht = mCursor06.getDouble(mCursor06.getColumnIndex("DISCNTBAHT"))
                discNom = mCursor06.getDouble(mCursor06.getColumnIndex("DISCNTDNOM"))
                discPcen = mCursor06.getDouble(mCursor06.getColumnIndex("DISCNTPCEN"))
                discNum = mCursor06.getDouble(mCursor06.getColumnIndex("DISCNTNUMR"))
                discType = mCursor06.getString(mCursor06.getColumnIndex("DISCNTTYPE"))
                break
            }
            mCursor06.moveToNext()
            i++
        }

        when (Integer.parseInt(discType.trim { it <= ' ' })) {
        //ลด 1/3 ของค่าน้ำ
            2 -> {
                Log.d("price", price.toString())
                Log.d("prswtusg", used.toString())
                TmpCal = price * (discNum / discNom)
                Log.d("Discntamt", TmpCal.toString())
                if (price - TmpCal < LowPrice) {
                    TmpCal = price - LowPrice
                }
                return TmpCal
            }
        //ลด 10% สำหรับผู้มีอุปการะคุณ
            6 -> {
                TmpCal = price * discPcen / 100
                if (NorTrfwt - TmpCal < LowPrice) {
                    TmpCal = NorTrfwt - LowPrice
                }
                return TmpCal
            }
        //ต้นสังกัดจ่ายแทน 20 หน่วย
            7 -> {
                Log.d("test", "test")
                TmpCal = discBaht
                return TmpCal
            }
            else -> return 0.0
        }

    }


    private fun ReadDataAgain() {
        // TODO Auto-generated method stub

        try {
            //bn_readagain.setVisibility(View.INVISIBLE);
            bn_value = bn_readagain?.text.toString()
            if (bn_value.equals("จดซ้ำ", ignoreCase = true)) {
                chkRead = 2
                bn_readagain?.text = "กลับ"
                rposition = iposition
                cur = db.select_CISDataRead(chRoute!!)
                if (cur != null) {
                    if (cur!!.moveToFirst()) {

                        setData(cur!!)
                    }
                }
            } else if (bn_value.equals("กลับ", ignoreCase = true)) {
                chkRead = 1
                bn_readagain?.text = "จดซ้ำ"
                cur = db.select_CISData(chRoute!!)
                if (cur != null) {

                    if (cur!!.moveToFirst()) {
                        cur!!.moveToPosition(rposition)
                        setData(cur!!)
                    }
                }
            }


        } catch (e: Exception) {
            // TODO: handle exception
        }

    }


    /**
     * On activity result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //		Toast.makeText(this, String.valueOf(requestCode) + ":" + String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
        //		Log.d("OK1", String.valueOf(requestCode));
        //		Log.d("OK1", String.valueOf(resultCode));
        //		Log.d("OK1", String.valueOf(RESULT_OK));
        try {
            if (resultCode != RESULT_OK) {
                return
            }

            when (requestCode) {

                RESULT_REQUEST -> {

                    Comment = data?.getStringExtra("Comment")
                    ComMentDec = data?.getStringExtra("ComMentDec")
                    OkPrint = data?.getBooleanExtra("OkPrint", true)!!
                    BillSend = data!!.getStringExtra("BillSend")
                    Smcnt = data?.getDoubleExtra("SmallUsg", Smcnt)

                    if (Smcnt > 0) {
                        PrsWtUsg = PrsWtUsg - Smcnt
                    }

                    getPriceAll()

                    ChkDigit()
                    Log.d("ChkDigit==>", ChkDigit)
                    updateMtRrdData(CustCode)
                    backupAfterRead()
                    if (OkPrint) {

                        Str_report = printBillTSC()

                        try {
                           // val t = Thread(GotoPrint(Str_report))
                            //t.start()

                        } catch (e: Exception) {
                            // TODO: handle exception
                            showDialog("ติดต่อเครื่องพิมพ์ใบแจ้งหนี้ไม่ได้  ?? " + e.message)
                            return
                        }



                    }

                    //Toast.makeText(this, String.valueOf(chkRead), Toast.LENGTH_LONG).show();
                    if (chkRead == 2) {
                        if (cur != null) {
                            if (cur!!.moveToNext()) {

                                setData(cur!!)

                            }
                        }
                    } else {
                        showData(chRoute)

                    }

                    // var tFind = ""
                    // var idfind = 0

                    val tFind = data.getStringExtra("tFind")
                    val idfind = data.getIntExtra("idfind", 0)
                    when (idfind) {
                        1 -> {
                            cur = db.select_FindCust(tFind)
                            if (cur != null) {
                                if (cur!!.moveToFirst()) {

                                    setData(cur!!)
                                }
                            } else {
                                showDialog("ไม่พบข้อมูล")
                            }
                        }
                        2 -> {
                            cur = db.select_FindMeter(tFind, Mtrrdroute!!)
                            if (cur != null) {
                                if (cur!!.moveToFirst()) {

                                    setData(cur!!)
                                }
                            } else {
                                showDialog("ไม่พบข้อมูล")
                            }
                        }
                        3 -> {
                            cur = db.select_FindAddr(tFind, Mtrrdroute!!)
                            if (cur != null) {
                                if (cur!!.moveToFirst()) {

                                    setData(cur!!)
                                }
                            } else {
                                showDialog("ไม่พบข้อมูล")
                            }
                        }
                        4 -> {
                            cur = db.select_FindSeq(tFind, Mtrrdroute!!)
                            if (cur != null) {
                                if (cur!!.moveToFirst()) {

                                    setData(cur!!)
                                }
                            } else {
                                showDialog("ไม่พบข้อมูล")
                            }
                        }
                    }
                }
                FIND_OK -> {
                    var tFind: String= ""
                    var idfind: Int=0
                    tFind = data!!.getStringExtra("tFind")
                    idfind = data!!.getIntExtra("idfind", 0)
                    when (idfind) {
                        1 -> {
                            cur = db.select_FindCust(tFind)
                            if (cur != null) {
                                if (cur!!.moveToFirst()) {
                                    setData(cur!!)
                                }
                            } else {
                                showDialog("ไม่พบข้อมูล")
                            }
                        }
                        2 -> {
                            cur = db.select_FindMeter(tFind, Mtrrdroute!!)
                            if (cur != null) {
                                if (cur!!.moveToFirst()) {
                                    setData(cur!!)
                                }
                            } else {
                                showDialog("ไม่พบข้อมูล")
                            }
                        }
                        3 -> {
                            cur = db.select_FindAddr(tFind, Mtrrdroute!!)
                            if (cur != null) {
                                if (cur!!.moveToFirst()) {
                                    setData(cur!!)
                                }
                            } else {
                                showDialog("ไม่พบข้อมูล")
                            }
                        }
                        4 -> {
                            cur = db.select_FindSeq(tFind, Mtrrdroute!!)
                            if (cur != null) {
                                if (cur!!.moveToFirst()) {
                                    setData(cur!!)
                                }
                            } else {
                                showDialog("ไม่พบข้อมูล")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            return
        }

    }

    private fun backupAfterRead() {
        // TODO Auto-generated method stub
        try {

            val sd = Environment.getExternalStorageDirectory()
            val path = sd.toString() + "/PWA/AUTOBK/" + "PWAData" + USERID + ".xml"
            Log.d("BackupAfterread", "STARTxml")
            val databaseDump = DatabaseDump(db.readableDatabase, path)
            databaseDump.exportData()
            Log.d("BackupAfterread", "Endxml")

        } catch (e: Exception) {
            // TODO: handle exception
        }

    }

    private fun printBillExam(): String {
        val curConstant: Cursor?
        curConstant = db.select_ConstantData()
        if (curConstant != null) {
            if (curConstant.moveToFirst()) {

            }
        }

        val ca = Calendar.getInstance()
        val day = ca.get(Calendar.DATE)
        var month = ca.get(Calendar.MONTH)  //0 = JAN / 11 = DEC
        val year = ca.get(Calendar.YEAR)

        val time = Time()
        time.setToNow()

        month = month + 1 //Set int to correct month numeral, i.e 0 = JAN therefore set to 1.
        val `MONTH$`: String
        if (month <= 9) {
            `MONTH$` = "0$month"
        } else {
            `MONTH$` = "" + month
        }    //Set month to MM

        val df = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val df3 = SimpleDateFormat("dd/MM/yyyy")
        //day+"/"+MONTH$+"/"+((year+543)-2500),
        val formattedDate = day.toString() + "/" + `MONTH$` + "/" + (year + 543) + " " +
                +time.hour + ":" + String.format("%02d", time.minute)
        //วันที่ครบกำหนดชำระ()
        val m = Date()
        var cal = Calendar.getInstance()
        cal.time = m
        Log.d("A", "A")
        var Strtoprint = ""
        var StrP_Head = ""
        var StrP_wwName = ""
        val StrP_DiswwName = curConstant!!.getString(curConstant.getColumnIndex("wwnamet"))
        var StrP_Npay = ""
        val StrP_DisNpay = "โปรดชำระเงินในวันถัดไป"
        var StrP_Tel = ""
        val StrP_DisTel = curConstant.getString(curConstant.getColumnIndex("wwtel"))
        var StrP_Invoicecnt = ""
        val StrP_DisInvoicecnt = cur!!.getString(cur!!.getColumnIndex("invoicecnt"))
        var StrP_Custcode = ""
        val StrP_DisCustcode = cur!!.getString(cur!!.getColumnIndex("custcode"))
        var StrP_wwCode = ""
        var StrP_DiswwCode = cur!!.getString(cur!!.getColumnIndex("wwcode")) + "-" + ChkDigit

        if (debmonth > 0) {
            StrP_DiswwCode = cur!!.getString(cur!!.getColumnIndex("wwcode"))
        }

        Log.d("b", "b")
        var StrP_dueDate = ""
        var StrP_LastPay = ""
        if (debmonth == 0) {
            cal.add(Calendar.DATE, 7)
            val day2 = cal.get(Calendar.DATE)
            StrP_LastPay = "" + day2 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ครบชำระ", StrP_LastPay)
            if (invflag.equals("1", ignoreCase = true)) {
                StrP_LastPay = "-"
            }
        } else {
            cal.add(Calendar.DATE, 3) // 10 is the days you want to add or subtract
            val day3 = cal.get(Calendar.DATE)
            //StrP_LastPay = ""+day3+"/"+MONTH$+"/"+((year+543)-2500);
            StrP_LastPay = "-"
            if (invflag.equals("1", ignoreCase = true)) {
                StrP_LastPay = "-"
            }
        }

        var StrP_mtrrdroute = ""
        val StrP_Dismtrrdroute = cur!!.getString(cur!!.getColumnIndex("mtrrdroute")) +
                "." + cur!!.getString(cur!!.getColumnIndex("mtrseq"))
        var StrP_Custname = ""
        var StrP_DisCustname = cur!!.getString(cur!!.getColumnIndex("custname"))
        if (StrP_DisCustname.length > 39) {
            StrP_DisCustname = StrP_DisCustname.substring(0, 39)
        }
        var StrP_CustAddress = ""
        var StrP_DisCustAddress = cur!!.getString(cur!!.getColumnIndex("custaddr"))
        if (StrP_DisCustAddress.length > 39) {
            StrP_DisCustAddress = StrP_DisCustAddress.substring(0, 39)
        }

        var StrP_Lstmtrrddt = ""
        val StrP_DisLstmtrrddt = cur!!.getString(cur!!.getColumnIndex("lstmtrddt"))
        val strP_DisLstmtrrddt = StrP_DisLstmtrrddt.substring(4, 6) + "/" +
                StrP_DisLstmtrrddt.substring(2, 4) + "/" +
                StrP_DisLstmtrrddt.substring(0, 2)
        var StrP_Prsmtrrddt = ""
        Log.d("วันที่อ่านครั้งก่อน", strP_DisLstmtrrddt)
        val StrP_DisPrsmtrrddt = day.toString() + "/" + `MONTH$` + "/" + (year + 543 - 2500)
        var StrP_Lstmtrcnt = ""
        var StrP_DisLstmtrcnt = ""

        //เลขในมาตร ครั้งก่อน
        if (PrsMtrCnt >= Est) {
            StrP_DisLstmtrcnt = cur!!.getString(cur!!.getColumnIndex("meterest"))
        } else {
            StrP_DisLstmtrcnt = cur!!.getString(cur!!.getColumnIndex("lstmtrcnt"))
        }
        Log.d("วันที่อ่านครั้งนี้", StrP_DisPrsmtrrddt)
        var StrP_Prsmtrcnt = ""

        var StrP_DisPrsmtrcnt = ""
        //เลขในมาตร ครั้งนี้
        val df1 = DecimalFormat("######")
        if (Smcnt > 0) {
            StrP_DisPrsmtrcnt = "(" + PrsMtrCnt.toString() + "-" + Smcnt.toString() + ")"
        } else {
            StrP_DisPrsmtrcnt = df1.format(PrsMtrCnt)
        }
        var StrP_Prswtusg = ""
        var StrP_DisPrswtusg = ""
        if (PrsWtUsg > 0) {
            val df2 = DecimalFormat("#,###")
            StrP_DisPrswtusg = df2.format(PrsWtUsg * 1000)
        } else {
            StrP_DisPrswtusg = PrsWtUsg.toString()
        }
        if (CustStat.equals("3", ignoreCase = true)) {
            StrP_DisPrswtusg += " (เฉลี่ย)"
        }
        Log.d("F", "F")
        var StrP_Type = ""

        var StrP_DisType = "T" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
        if (usertype!!.substring(0, 1).equals("1", ignoreCase = true) && PrsWtUsg > 100) {
            StrP_DisType = "T1" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
        } else if (usertype!!.equals("29", ignoreCase = true) && PrsWtUsg <= 100) {
            val OldType: String
            OldType = cur!!.getString(cur!!.getColumnIndex("oldtype"))
            if (OldType.length == 3) {
                if (OldType.substring(0, 1).equals("1", ignoreCase = true)) {
                    StrP_DisType = "T1" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
                }
            }
        }

        var StrP_wtnor = ""
        val StrP_Diswtnor = String.format("%,.2f", NorTrfwt)
        var StrP_DISCOUNT = ""
        val StrP_DisDISCOUNT = String.format("%,.2f", DiscntAmt)
        var StrP_Service = ""
        val StrP_DisService = String.format("%,.2f", SrvFee)
        var StrP_Net = ""
        val StrP_DisNet = String.format("%,.2f", TotTrfwt)
        var StrP_Vat = ""
        val StrP_DisVat = String.format("%,.2f", Vat)
        var StrP_Res = ""
        val StrP_DisRes = debmonth.toString()
        var StrP_AmtRes = ""
        val StrP_DisAmtRes = String.format("%,.2f", debamt)
        var StrP_AmtTotal = ""
        var StrP_DisAmtTotal = String.format("%,.2f", TotTrfwt)
        var StrP_LatLng = ""
        val StrP_DisLatLng = "(lat,lon)" + lat.toString() + "," + lon.toString()
        var StrP_BarCodeA = ""
        var StrP_BarCodeB = ""
        var StrP_DisPwaPro = ""
        var StrP_DisPwaPro2 = ""


        //		if (DisCntCode.equalsIgnoreCase("4")){
        //			StrP_DisDISCOUNT="0.00";
        //		}
        val formt = DecimalFormat("0.00")
        StrP_DisPwaPro = "ได้รับส่วนลดโครงการประชารัฐร่วมใจ"
        StrP_DisPwaPro2 = "คนไทยประหยัดน้ำ " + java.lang.Double.parseDouble(formt.format(distempamt)).toString() + " บาท"


        if (invflag.equals("1", ignoreCase = true)) {
            StrP_BarCodeA = "หักบัญชีธนาคารภายในวันที่ 25 ของทุกเดือน "
            StrP_BarCodeB = "โปรดตรวจสอบยอดเงินในบัญชีของท่านด้วย"
            if (debmonth > 0) {
                StrP_DisAmtTotal = String.format("%.2f", TotTrfwt + debamt)
            }
        } else {
            if (debmonth == 0) {
                StrP_BarCodeA = StrP_DisInvoicecnt + CustCode!!
                StrP_BarCodeA += wwcode
                StrP_BarCodeA += ChkDigit
                val cal2 = Calendar.getInstance()
                cal2.add(Calendar.DATE, 10)
                val day4 = cal2.get(Calendar.DATE)
                StrP_BarCodeA += day4.toString() + `MONTH$` + (year + 543 - 2500)
                val df4 = DecimalFormat("000000000")
                StrP_BarCodeA += df4.format(TotTrfwt * 100)
                StrP_BarCodeB = StrP_BarCodeA
                Log.d("barcode", StrP_BarCodeA)
            } else {
                StrP_DisAmtTotal = String.format("%.2f", TotTrfwt + debamt)
                StrP_BarCodeA = "ท่านอยู่ระหว่างถูกระงับการใช้น้ำ (ตัดมาตร)"
                StrP_BarCodeB = "โปรดติดต่อชำระหนี้ค้าง ณ สำนักงานประปาพื้นที่"
            }
        }


        //ขยายเวลารับชำระที่ตัวแทนเอกชนได้อีก 3 วัน
        var StrP_Adddate = ""
        val StrP_DisAdddate = "**โปรดระวังมิจฉาชีพแอบอ้างเก็บเงินค่าน้ำประปา**"
        var StrP_Duedate2 = ""
        var StrP_Sdate = ""
        val StrP_DisSdate = StrP_LastPay
        var StrP_Edate = ""
        var StrP_DisEdate = ""
        var StrP_PwaPro = ""
        var StrP_PwaPro2 = ""
        stBarCode = ""

        if (debmonth == 0) {
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 14) //ปรับวันที่ระงับการใช้น้ำเป็น7วันหลังครบกำหนดรับชำระDEC58
            val day3 = cal.get(Calendar.DATE)
            StrP_DisEdate = "" + day3 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ระงับการใช้น้ำ", StrP_DisEdate)
        } else {
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 3)
            val day3 = cal.get(Calendar.DATE)
            StrP_DisEdate = "" + day3 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ระงับการใช้น้ำ", StrP_DisEdate)
        }
        var StrP_DisDupamt = ""
        var StrP_Dupamt = ""
        //dupnet
        if (tmpdupamt > 0) {
            if (dupamt > 0) {   //ค่าน้ำรับซ้ำมากกว่าค่าน้ำเดือนปัจจุบัน
                //dupnet = Format(dupnet, "0.00")
                StrP_DisDupamt = "ปรับปรุงค่าน้ำรับซ้ำคงเหลือ  : "
                // strOneil += "@920,3:AN10B,HM1,VM1| " & HK(str) & Format(dupamt, "#,##0.00") & HK(" บาท ") & "|" & vbCrLf
            } else {
                //ค่าน้ำรับซ้ำน้อยกว่าค่าน้ำเดือนปัจจุบัน
                //dupnet = Format(dupnet, "0.00")
                StrP_DisDupamt = "มีค่าน้ำรับซ้ำไว้  : "
                // strOneil += "@920,3:AN10B,HM1,VM1| " & HK(str) & Format(tmpdupamt, "#,##0.00") & HK(" บาท ") & "|" & vbCrLf

            }
        }


        var StrP_Barcode = ""
        var StrP_Version = ""
        val StrP_DisVersion = "V.0.0.1"
        var StrP_Comment = ""
        val StrP_DisComment = "ID." + USERID.toString() + "(" + Comment.toString() + ")"
        var StrP_Stop = ""

        Log.d("PASS", "PASS1")
        DecimalFormat("#.###")
        try {
            Log.d("PASS", "PASS2")
            var Str_VAT_RATE = "0"//  VAT_RATE เป็น  0 คือ คิด vat
            if (Str_VAT_RATE == "0") {
                Str_VAT_RATE = "07"
            }
            Log.d("วันที่อ่านครั้งก่อน", strP_DisLstmtrrddt)
            StrP_Head = "EZ{PRINT:\r\n"
            Log.d("PASS", "PASS3")
            //String VERSION =getString(R.string.version_app);
            StrP_Npay = "@20,210:AN12B,HM1,VM1|$StrP_DisNpay|\r\n"
            StrP_wwName = "@150,300:AN12B,HM1,VM1|$StrP_DiswwName|\r\n"
            StrP_Tel = "@180,290:AN12B,HM1,VM1|$StrP_DisTel|\r\n"
            StrP_Invoicecnt = "@285,3:AN12B,HM1,VM1|$StrP_DisInvoicecnt|\r\n"
            StrP_Custcode = "@285,185:AN12B,HM1,VM1|$StrP_DisCustcode|\r\n"
            StrP_wwCode = "@285,360:AN12B,HM1,VM1|$StrP_DiswwCode|\r\n"
            StrP_dueDate = "@350,1:AN12B,HM1,VM1,RIGHT|$formattedDate|\r\n"
            if (debmonth == 0) {
                StrP_LastPay = "@350,220:AN12B,HM1,VM1|$StrP_LastPay|\r\n"
            } else {
                StrP_LastPay = "@350,220:AN12B,HM1,VM1|" + " - " + "|\r\n"
            }
            StrP_mtrrdroute = "@350,360:AN12B,HM1,VM1|$StrP_Dismtrrdroute|\r\n"
            StrP_Custname = "@390,95:AN12B,HM1,VM1|$StrP_DisCustname|\r\n"
            StrP_CustAddress = "@420,50:AN12B,HM1,VM1|$StrP_DisCustAddress|\r\n"
            StrP_Lstmtrrddt = "@490,170:AN12B,HM1,VM1|$strP_DisLstmtrrddt|\r\n"
            StrP_Prsmtrrddt = "@490,340:AN12B,HM1,VM1|$StrP_DisPrsmtrrddt|\r\n"
            StrP_Lstmtrcnt = "@520,170:AN12B,HM1,VM1|$StrP_DisLstmtrcnt|\r\n"
            StrP_Prsmtrcnt = "@520,340:AN12B,HM1,VM1|$StrP_DisPrsmtrcnt|\r\n"
            StrP_Prswtusg = "@550,420:AN12B,HM1,VM1,RIGHT|$StrP_DisPrswtusg|\r\n"
            StrP_Type = "@585,115:AN12B,HM1,VM1|$StrP_DisType|\r\n"
            StrP_wtnor = "@585,420:AN12B,HM1,VM1,RIGHT|$StrP_Diswtnor|\r\n"
            StrP_DISCOUNT = "@620,420:AN12B,HM1,VM1,RIGHT|$StrP_DisDISCOUNT|\r\n"

            if (tmpdupamt > 0) {
                if (Vat > 0) {
                    //ค่าน้ำรับซ้ำน้อยกว่าค่าน้ำเดือนปัจจุบัน
                    StrP_Service = "@650,420:AN12B,HM1,VM1,RIGHT|$StrP_DisService|\r\n"
                    StrP_Net = "@710,420:AN12B,HM1,VM1,RIGHT|" + String.format("%.2f", debtamt) + "|\r\n"
                    StrP_Vat = "@680,420:AN12B,HM1,VM1,RIGHT|$StrP_DisVat|\r\n"
                } else {
                    //ค่าน้ำรับซ้ำมากกว่าค่าน้ำเดือนปัจจุบัน
                    StrP_Service = "@650,420:AN12B,HM1,VM1,RIGHT|$StrP_DisService|\r\n"
                    StrP_Net = "@710,420:AN12B,HM1,VM1,RIGHT|" + String.format("%.2f", debtamt) + "|\r\n"
                    StrP_Vat = "@680,420:AN12B,HM1,VM1,RIGHT|" + String.format("%.2f", debtvat) + "|\r\n"
                }
            } else {

                StrP_Service = "@650,420:AN12B,HM1,VM1,RIGHT|$StrP_DisService|\r\n"

                if (PwaFlag!!.equals("4", ignoreCase = true)) {

                    StrP_Net = "@710,420:AN12B,HM1,VM1,RIGHT|" + String.format("%.2f", debtamt) + "|\r\n"
                } else {
                    StrP_Net = "@710,420:AN12B,HM1,VM1,RIGHT|$StrP_DisNet|\r\n"
                }

                StrP_Vat = "@680,420:AN12B,HM1,VM1,RIGHT|$StrP_DisVat|\r\n"
            }

            StrP_Res = "@740,160:AN12B,HM1,VM1|$StrP_DisRes|\r\n"
            StrP_AmtRes = "@740,420:AN12B,HM1,VM1,RIGHT|$StrP_DisAmtRes|\r\n"
            StrP_AmtTotal = "@770,420:AN12B,HM1,VM1,RIGHT|$StrP_DisAmtTotal|\r\n"
            StrP_LatLng = "@910,30:AN12B,HM1,VM1|$StrP_DisLatLng|\r\n"
            //StrP_LatLng="@1060,545:AN07N,ROT90,HM1,VM1|" + StrP_DisLatLng +"|\r\n";
            //ค้างชำระและจ่ายธนาคาร
            if (debmonth == 0) {
                if (invflag.equals("1", ignoreCase = true)) {
                    StrP_Sdate = "@860,450:AN12B,HM1,VM1|" + "-" + "|\r\n"
                    StrP_Edate = "@890,450:AN12B,HM1,VM1|" + "-" + "|\r\n"
                    StrP_Barcode = "@850,515:AN12B,ROT90,HM1,VM1|$StrP_BarCodeA|\r\n"
                    StrP_Barcode += "@825,555:AN12N,ROT90,HM1,VM1|$StrP_BarCodeB|\r\n"

                } else {
                    StrP_Sdate = "@860,430:AN12B,HM1,VM1|$StrP_DisSdate|\r\n"
                    StrP_Edate = "@890,430:AN12B,HM1,VM1|$StrP_DisEdate|\r\n"
                    StrP_Barcode = "@850,515:BC128,ROT90,HIGH 10,WIDE 2|$StrP_BarCodeA|\r\n"
                    StrP_Barcode += "@850,555:AN12N,ROT90,HM1,VM1|$StrP_BarCodeB|\r\n"
                    StrP_Adddate = "@860,3:AN09B,HM1,VM1|$StrP_DisAdddate|\r\n"
                    //StrP_Duedate2= "@915,3:AN09B,HM1,VM1|นับจากวันครบกำหนด |\r\n";
                    stBarCode = StrP_BarCodeA
                }

            } else {
                StrP_Sdate = "@860,420:AN12B,HM1,VM1|" + "-" + "|\r\n"
                StrP_Edate = "@890,420:AN12B,HM1,VM1|" + "-" + "|\r\n"
                StrP_Barcode = "@850,530:AN12B,ROT90,HM1,VM1|$StrP_BarCodeA|\r\n"
                StrP_Barcode += "@890,565:AN12N,ROT90,HM1,VM1|$StrP_BarCodeB|\r\n"
            }
            StrP_Version = "@950,30:AN12N,HM1,VM1|$StrP_DisVersion|\r\n"
            StrP_Comment = "@950,200:AN12N,HM1,VM1|$StrP_DisComment|\r\n"

            if (tmpdupamt > 0) {
                if (dupamt > 0) {   //ค่าน้ำรับซ้ำมากกว่าค่าน้ำเดือนปัจจุบัน
                    //dupnet = Format(dupnet, "0.00")
                    StrP_Dupamt = "@830,3:AN09B,HM1,VM1| " + StrP_DisDupamt + String.format("%.2f", dupamt) + " บาท " + "|\r\n"
                } else {
                    //ค่าน้ำรับซ้ำน้อยกว่าค่าน้ำเดือนปัจจุบัน
                    //dupnet = Format(dupnet, "0.00")
                    StrP_Dupamt = "@830,3:AN09B,HM1,VM1| " + StrP_DisDupamt + String.format("%.2f", tmpdupamt) + " บาท " + "|\r\n"
                }
            }


            if (PwaFlag!!.equals("4", ignoreCase = true)) {
                StrP_PwaPro = "@830,20:AN09B,HM1,VM1| $StrP_DisPwaPro|\r\n"
                StrP_PwaPro2 = "@860,25:AN09B,HM1,VM1| $StrP_DisPwaPro2|\r\n"
                StrP_Adddate = ""
                StrP_Duedate2 = ""
            }


            Log.d("PASS", "PASS4")
            //StrP_Stop = "}"
            //StrP_Stop = "}\r\n"
            Log.d("PRINT", "PASS6")

            //Log.d("PRINT", stBarCode)
            Strtoprint = StrP_Head +
                    StrP_wwName +
                    StrP_Npay +
                    StrP_Tel +
                    StrP_Invoicecnt +
                    StrP_Custcode +
                    StrP_wwCode +
                    StrP_dueDate +
                    StrP_LastPay +
                    StrP_mtrrdroute +
                    StrP_Custname +
                    StrP_CustAddress +
                    StrP_Lstmtrrddt +
                    StrP_Prsmtrrddt +
                    StrP_Lstmtrcnt +
                    StrP_Prsmtrcnt +
                    StrP_Prswtusg +
                    StrP_Type +
                    StrP_wtnor +
                    StrP_DISCOUNT +
                    StrP_Service +
                    StrP_Net +
                    StrP_Vat +
                    StrP_Res +
                    StrP_AmtRes +
                    StrP_AmtTotal +
                    StrP_LatLng +
                    StrP_Sdate +
                    StrP_Edate +
                    StrP_Version +
                    StrP_Comment +
                    StrP_Barcode +
                    StrP_Adddate +
                    StrP_Duedate2 +
                    StrP_Dupamt +
                    StrP_PwaPro +
                    StrP_PwaPro2 +
                    StrP_Stop

            Log.d("",Strtoprint)
        } catch (e: Exception) {
            showDialog("report=" + e.toString())
        }

        return Strtoprint
    }


    private fun printBill(): String {
        val curConstant: Cursor?
        curConstant = db.select_ConstantData()
        if (curConstant != null) {
            if (curConstant.moveToFirst()) {

            }
        }

        val ca = Calendar.getInstance()
        val day = ca.get(Calendar.DATE)
        var month = ca.get(Calendar.MONTH)  //0 = JAN / 11 = DEC
        val year = ca.get(Calendar.YEAR)

        val time = Time()
        time.setToNow()

        month = month + 1 //Set int to correct month numeral, i.e 0 = JAN therefore set to 1.
        val `MONTH$`: String
        if (month <= 9) {
            `MONTH$` = "0$month"
        } else {
            `MONTH$` = "" + month
        }    //Set month to MM

        val df = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val df3 = SimpleDateFormat("dd/MM/yyyy")
        //day+"/"+MONTH$+"/"+((year+543)-2500),
        val formattedDate = day.toString() + "/" + `MONTH$` + "/" + (year + 543) + " " +
                +time.hour + ":" + String.format("%02d", time.minute)
        //วันที่ครบกำหนดชำระ()
        val m = Date()
        var cal = Calendar.getInstance()
        cal.time = m
        Log.d("A", "A")
        var Strtoprint = ""
        var StrP_Head = ""
        var StrP_wwName = ""
        val StrP_DiswwName = curConstant!!.getString(curConstant.getColumnIndex("wwnamet"))
        var StrP_Npay = ""
        val StrP_DisNpay = "โปรดชำระเงินในวันถัดไป"
        var StrP_Tel = ""
        val StrP_DisTel = curConstant.getString(curConstant.getColumnIndex("wwtel"))
        var StrP_Invoicecnt = ""
        val StrP_DisInvoicecnt = cur!!.getString(cur!!.getColumnIndex("invoicecnt"))
        var StrP_Custcode = ""
        val StrP_DisCustcode = cur!!.getString(cur!!.getColumnIndex("custcode"))
        var StrP_wwCode = ""
        var StrP_DiswwCode = cur!!.getString(cur!!.getColumnIndex("wwcode")) + "-" + ChkDigit

        if (debmonth > 0) {
            StrP_DiswwCode = cur!!.getString(cur!!.getColumnIndex("wwcode"))
        }

        Log.d("b", "b")
        var StrP_dueDate = ""
        var StrP_LastPay = ""
        if (debmonth == 0) {
            cal.add(Calendar.DATE, 7)
            val day2 = cal.get(Calendar.DATE)
            StrP_LastPay = "" + day2 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ครบชำระ", StrP_LastPay)
            if (invflag.equals("1", ignoreCase = true)) {
                StrP_LastPay = "-"
            }
        } else {
            cal.add(Calendar.DATE, 3) // 10 is the days you want to add or subtract
            val day3 = cal.get(Calendar.DATE)
            //StrP_LastPay = ""+day3+"/"+MONTH$+"/"+((year+543)-2500);
            StrP_LastPay = "-"
            if (invflag.equals("1", ignoreCase = true)) {
                StrP_LastPay = "-"
            }
        }

        var StrP_mtrrdroute = ""
        val StrP_Dismtrrdroute = cur!!.getString(cur!!.getColumnIndex("mtrrdroute")) +
                "." + cur!!.getString(cur!!.getColumnIndex("mtrseq"))
        var StrP_Custname = ""
        var StrP_DisCustname = cur!!.getString(cur!!.getColumnIndex("custname"))
        if (StrP_DisCustname.length > 39) {
            StrP_DisCustname = StrP_DisCustname.substring(0, 39)
        }
        var StrP_CustAddress = ""
        var StrP_DisCustAddress = cur!!.getString(cur!!.getColumnIndex("custaddr"))
        if (StrP_DisCustAddress.length > 39) {
            StrP_DisCustAddress = StrP_DisCustAddress.substring(0, 39)
        }

        var StrP_Lstmtrrddt = ""
        val StrP_DisLstmtrrddt = cur!!.getString(cur!!.getColumnIndex("lstmtrddt"))
        val strP_DisLstmtrrddt = StrP_DisLstmtrrddt.substring(4, 6) + "/" +
                StrP_DisLstmtrrddt.substring(2, 4) + "/" +
                StrP_DisLstmtrrddt.substring(0, 2)
        var StrP_Prsmtrrddt = ""
        Log.d("วันที่อ่านครั้งก่อน", strP_DisLstmtrrddt)
        val StrP_DisPrsmtrrddt = day.toString() + "/" + `MONTH$` + "/" + (year + 543 - 2500)
        var StrP_Lstmtrcnt = ""
        var StrP_DisLstmtrcnt = ""

        //เลขในมาตร ครั้งก่อน
        if (PrsMtrCnt >= Est) {
            StrP_DisLstmtrcnt = cur!!.getString(cur!!.getColumnIndex("meterest"))
        } else {
            StrP_DisLstmtrcnt = cur!!.getString(cur!!.getColumnIndex("lstmtrcnt"))
        }
        Log.d("วันที่อ่านครั้งนี้", StrP_DisPrsmtrrddt)
        var StrP_Prsmtrcnt = ""

        var StrP_DisPrsmtrcnt = ""
        //เลขในมาตร ครั้งนี้
        val df1 = DecimalFormat("######")
        if (Smcnt > 0) {
            StrP_DisPrsmtrcnt = "(" + PrsMtrCnt.toString() + "-" + Smcnt.toString() + ")"
        } else {
            StrP_DisPrsmtrcnt = df1.format(PrsMtrCnt)
        }
        var StrP_Prswtusg = ""
        var StrP_DisPrswtusg = ""
        if (PrsWtUsg > 0) {
            val df2 = DecimalFormat("#,###")
            StrP_DisPrswtusg = df2.format(PrsWtUsg * 1000)
        } else {
            StrP_DisPrswtusg = PrsWtUsg.toString()
        }
        if (CustStat.equals("3", ignoreCase = true)) {
            StrP_DisPrswtusg += " (เฉลี่ย)"
        }
        Log.d("F", "F")
        var StrP_Type = ""

        var StrP_DisType = "T" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
        if (usertype!!.substring(0, 1).equals("1", ignoreCase = true) && PrsWtUsg > 100) {
            StrP_DisType = "T1" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
        } else if (usertype!!.equals("29", ignoreCase = true) && PrsWtUsg <= 100) {
            val OldType: String
            OldType = cur!!.getString(cur!!.getColumnIndex("oldtype"))
            if (OldType.length == 3) {
                if (OldType.substring(0, 1).equals("1", ignoreCase = true)) {
                    StrP_DisType = "T1" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
                }
            }
        }

        var StrP_wtnor = ""
        val StrP_Diswtnor = String.format("%,.2f", NorTrfwt)
        var StrP_DISCOUNT = ""
        val StrP_DisDISCOUNT = String.format("%,.2f", DiscntAmt)
        var StrP_Service = ""
        val StrP_DisService = String.format("%,.2f", SrvFee)
        var StrP_Net = ""
        val StrP_DisNet = String.format("%,.2f", TotTrfwt)
        var StrP_Vat = ""
        val StrP_DisVat = String.format("%,.2f", Vat)
        var StrP_Res = ""
        val StrP_DisRes = debmonth.toString()
        var StrP_AmtRes = ""
        val StrP_DisAmtRes = String.format("%,.2f", debamt)
        var StrP_AmtTotal = ""
        var StrP_DisAmtTotal = String.format("%,.2f", TotTrfwt)
        var StrP_LatLng = ""
        val StrP_DisLatLng = "(lat,lon)" + lat.toString() + "," + lon.toString()
        var StrP_BarCodeA = ""
        var StrP_BarCodeB = ""
        var StrP_DisPwaPro = ""
        var StrP_DisPwaPro2 = ""


        //		if (DisCntCode.equalsIgnoreCase("4")){
        //			StrP_DisDISCOUNT="0.00";
        //		}
        val formt = DecimalFormat("0.00")
        StrP_DisPwaPro = "ได้รับส่วนลดโครงการประชารัฐร่วมใจ"
        StrP_DisPwaPro2 = "คนไทยประหยัดน้ำ " + java.lang.Double.parseDouble(formt.format(distempamt)).toString() + " บาท"


        if (invflag.equals("1", ignoreCase = true)) {
            StrP_BarCodeA = "หักบัญชีธนาคารภายในวันที่ 25 ของทุกเดือน "
            StrP_BarCodeB = "โปรดตรวจสอบยอดเงินในบัญชีของท่านด้วย"
            if (debmonth > 0) {
                StrP_DisAmtTotal = String.format("%.2f", TotTrfwt + debamt)
            }
        } else {
            if (debmonth == 0) {
                StrP_BarCodeA = StrP_DisInvoicecnt + CustCode!!
                StrP_BarCodeA += wwcode
                StrP_BarCodeA += ChkDigit
                val cal2 = Calendar.getInstance()
                cal2.add(Calendar.DATE, 10)
                val day4 = cal2.get(Calendar.DATE)
                StrP_BarCodeA += day4.toString() + `MONTH$` + (year + 543 - 2500)
                val df4 = DecimalFormat("000000000")
                StrP_BarCodeA += df4.format(TotTrfwt * 100)
                StrP_BarCodeB = StrP_BarCodeA
                Log.d("barcode", StrP_BarCodeA)
            } else {
                StrP_DisAmtTotal = String.format("%.2f", TotTrfwt + debamt)
                StrP_BarCodeA = "ท่านอยู่ระหว่างถูกระงับการใช้น้ำ (ตัดมาตร)"
                StrP_BarCodeB = "โปรดติดต่อชำระหนี้ค้าง ณ สำนักงานประปาพื้นที่"
            }
        }


        //ขยายเวลารับชำระที่ตัวแทนเอกชนได้อีก 3 วัน
        var StrP_Adddate = ""
        val StrP_DisAdddate = "**โปรดระวังมิจฉาชีพแอบอ้างเก็บเงินค่าน้ำประปา**"
        var StrP_Duedate2 = ""
        var StrP_Sdate = ""
        val StrP_DisSdate = StrP_LastPay
        var StrP_Edate = ""
        var StrP_DisEdate = ""
        var StrP_PwaPro = ""
        var StrP_PwaPro2 = ""
        stBarCode = ""

        if (debmonth == 0) {
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 14) //ปรับวันที่ระงับการใช้น้ำเป็น7วันหลังครบกำหนดรับชำระDEC58
            val day3 = cal.get(Calendar.DATE)
            StrP_DisEdate = "" + day3 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ระงับการใช้น้ำ", StrP_DisEdate)
        } else {
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 3)
            val day3 = cal.get(Calendar.DATE)
            StrP_DisEdate = "" + day3 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ระงับการใช้น้ำ", StrP_DisEdate)
        }
        var StrP_DisDupamt = ""
        var StrP_Dupamt = ""
        //dupnet
        if (tmpdupamt > 0) {
            if (dupamt > 0) {   //ค่าน้ำรับซ้ำมากกว่าค่าน้ำเดือนปัจจุบัน
                //dupnet = Format(dupnet, "0.00")
                StrP_DisDupamt = "ปรับปรุงค่าน้ำรับซ้ำคงเหลือ  : "
                // strOneil += "@920,3:AN10B,HM1,VM1| " & HK(str) & Format(dupamt, "#,##0.00") & HK(" บาท ") & "|" & vbCrLf
            } else {
                //ค่าน้ำรับซ้ำน้อยกว่าค่าน้ำเดือนปัจจุบัน
                //dupnet = Format(dupnet, "0.00")
                StrP_DisDupamt = "มีค่าน้ำรับซ้ำไว้  : "
                // strOneil += "@920,3:AN10B,HM1,VM1| " & HK(str) & Format(tmpdupamt, "#,##0.00") & HK(" บาท ") & "|" & vbCrLf

            }
        }


        var StrP_Barcode = ""
        var StrP_Version = ""
        val StrP_DisVersion = "V.0.0.1"
        var StrP_Comment = ""
        val StrP_DisComment = "ID." + USERID.toString() + "(" + Comment.toString() + ")"
        var StrP_Stop = ""

        Log.d("PASS", "PASS1")
        DecimalFormat("#.###")
        try {
            Log.d("PASS", "PASS2")
            var Str_VAT_RATE = "0"//  VAT_RATE เป็น  0 คือ คิด vat
            if (Str_VAT_RATE == "0") {
                Str_VAT_RATE = "07"
            }
            Log.d("วันที่อ่านครั้งก่อน", strP_DisLstmtrrddt)
            StrP_Head = "EZ{PRINT:\r\n"
            Log.d("PASS", "PASS3")
            //String VERSION =getString(R.string.version_app);
            StrP_Npay = "@50,210:AN12B,HM1,VM1|$StrP_DisNpay|\r\n"
            StrP_wwName = "@180,300:AN12B,HM1,VM1|$StrP_DiswwName|\r\n"
            StrP_Tel = "@217,320:AN12B,HM1,VM1|$StrP_DisTel|\r\n"
            StrP_Invoicecnt = "@290,3:AN12B,HM1,VM1|$StrP_DisInvoicecnt|\r\n"
            StrP_Custcode = "@290,185:AN12B,HM1,VM1|$StrP_DisCustcode|\r\n"
            StrP_wwCode = "@290,360:AN12B,HM1,VM1|$StrP_DiswwCode|\r\n"
            StrP_dueDate = "@370,1:AN12B,HM1,VM1,RIGHT|$formattedDate|\r\n"
            if (debmonth == 0) {
                StrP_LastPay = "@370,220:AN12B,HM1,VM1|$StrP_LastPay|\r\n"
            } else {
                StrP_LastPay = "@370,220:AN12B,HM1,VM1|" + " - " + "|\r\n"
            }
            StrP_mtrrdroute = "@370,340:AN12B,HM1,VM1|$StrP_Dismtrrdroute|\r\n"
            StrP_Custname = "@420,110:AN12B,HM1,VM1|$StrP_DisCustname|\r\n"
            StrP_CustAddress = "@460,60:AN12B,HM1,VM1|$StrP_DisCustAddress|\r\n"
            StrP_Lstmtrrddt = "@550,180:AN12B,HM1,VM1|$strP_DisLstmtrrddt|\r\n"
            StrP_Prsmtrrddt = "@550,370:AN12B,HM1,VM1|$StrP_DisPrsmtrrddt|\r\n"
            StrP_Lstmtrcnt = "@585,175:AN12B,HM1,VM1|$StrP_DisLstmtrcnt|\r\n"
            StrP_Prsmtrcnt = "@585,370:AN12B,HM1,VM1|$StrP_DisPrsmtrcnt|\r\n"
            StrP_Prswtusg = "@620,420:AN12B,HM1,VM1,RIGHT|$StrP_DisPrswtusg|\r\n"
            StrP_Type = "@655,120:AN12B,HM1,VM1|$StrP_DisType|\r\n"
            StrP_wtnor = "@655,420:AN12B,HM1,VM1,RIGHT|$StrP_Diswtnor|\r\n"
            StrP_DISCOUNT = "@690,420:AN12B,HM1,VM1,RIGHT|$StrP_DisDISCOUNT|\r\n"

            if (tmpdupamt > 0) {
                if (Vat > 0) {
                    //ค่าน้ำรับซ้ำน้อยกว่าค่าน้ำเดือนปัจจุบัน
                    StrP_Service = "@720,420:AN12B,HM1,VM1,RIGHT|$StrP_DisService|\r\n"
                    StrP_Net = "@790,420:AN12B,HM1,VM1,RIGHT|" + String.format("%.2f", debtamt) + "|\r\n"
                    StrP_Vat = "@755,420:AN12B,HM1,VM1,RIGHT|$StrP_DisVat|\r\n"
                } else {
                    //ค่าน้ำรับซ้ำมากกว่าค่าน้ำเดือนปัจจุบัน
                    StrP_Service = "@720,420:AN12B,HM1,VM1,RIGHT|$StrP_DisService|\r\n"
                    StrP_Net = "@790,420:AN12B,HM1,VM1,RIGHT|" + String.format("%.2f", debtamt) + "|\r\n"
                    StrP_Vat = "@755,420:AN12B,HM1,VM1,RIGHT|" + String.format("%.2f", debtvat) + "|\r\n"
                }
            } else {

                StrP_Service = "@720,420:AN12B,HM1,VM1,RIGHT|$StrP_DisService|\r\n"

                if (PwaFlag!!.equals("4", ignoreCase = true)) {

                    StrP_Net = "@790,420:AN12B,HM1,VM1,RIGHT|" + String.format("%.2f", debtamt) + "|\r\n"
                } else {
                    StrP_Net = "@790,420:AN12B,HM1,VM1,RIGHT|$StrP_DisNet|\r\n"
                }

                StrP_Vat = "@755,420:AN12B,HM1,VM1,RIGHT|$StrP_DisVat|\r\n"
            }

            StrP_Res = "@820,160:AN12B,HM1,VM1|$StrP_DisRes|\r\n"
            StrP_AmtRes = "@820,420:AN12B,HM1,VM1,RIGHT|$StrP_DisAmtRes|\r\n"
            StrP_AmtTotal = "@855,420:AN12B,HM1,VM1,RIGHT|$StrP_DisAmtTotal|\r\n"
            StrP_LatLng = "@950,30:AN12B,HM1,VM1|$StrP_DisLatLng|\r\n"
            //StrP_LatLng="@1060,545:AN07N,ROT90,HM1,VM1|" + StrP_DisLatLng +"|\r\n";
            //ค้างชำระและจ่ายธนาคาร
            if (debmonth == 0) {
                if (invflag.equals("1", ignoreCase = true)) {
                    StrP_Sdate = "@985,450:AN12B,HM1,VM1|" + "-" + "|\r\n"
                    StrP_Edate = "@1025,450:AN12B,HM1,VM1|" + "-" + "|\r\n"
                    StrP_Barcode = "@860,505:AN12B,ROT90,HM1,VM1|$StrP_BarCodeA|\r\n"
                    StrP_Barcode += "@830,545:AN12N,ROT90,HM1,VM1|$StrP_BarCodeB|\r\n"
                    stBarCode = ""

                } else {
                    StrP_Sdate = "@985,430:AN12B,HM1,VM1|$StrP_DisSdate|\r\n"
                    StrP_Edate = "@1025,430:AN12B,HM1,VM1|$StrP_DisEdate|\r\n"
                    StrP_Barcode = "@860,505:BC128,ROT90,HIGH 10,WIDE 2|$StrP_BarCodeA|\r\n"
                    StrP_Barcode += "@830,545:AN12N,ROT90,HM1,VM1|$StrP_BarCodeB|\r\n"
                    StrP_Adddate = "@900,3:AN09B,HM1,VM1|$StrP_DisAdddate|\r\n"
                    //StrP_Duedate2= "@915,3:AN09B,HM1,VM1|นับจากวันครบกำหนด |\r\n";
                    stBarCode = StrP_BarCodeA
                }


            } else {
                StrP_Sdate = "@985,430:AN12B,HM1,VM1|" + "-" + "|\r\n"
                StrP_Edate = "@1025,430:AN12B,HM1,VM1|" + "-" + "|\r\n"
                StrP_Barcode = "@860,505:AN12B,ROT90,HM1,VM1|$StrP_BarCodeA|\r\n"
                StrP_Barcode += "@830,545:AN12N,ROT90,HM1,VM1|$StrP_BarCodeB|\r\n"
                stBarCode = ""
            }
            StrP_Version = "@1010,3:AN12N,HM1,VM1|$StrP_DisVersion|\r\n"
            StrP_Comment = "@1010,190:AN12N,HM1,VM1|$StrP_DisComment|\r\n"

            if (tmpdupamt > 0) {
                if (dupamt > 0) {   //ค่าน้ำรับซ้ำมากกว่าค่าน้ำเดือนปัจจุบัน
                    //dupnet = Format(dupnet, "0.00")
                    StrP_Dupamt = "@940,3:AN09B,HM1,VM1| " + StrP_DisDupamt + String.format("%.2f", dupamt) + " บาท " + "|\r\n"
                } else {
                    //ค่าน้ำรับซ้ำน้อยกว่าค่าน้ำเดือนปัจจุบัน
                    //dupnet = Format(dupnet, "0.00")
                    StrP_Dupamt = "@940,3:AN09B,HM1,VM1| " + StrP_DisDupamt + String.format("%.2f", tmpdupamt) + " บาท " + "|\r\n"

                }
            }


            if (PwaFlag!!.equals("4", ignoreCase = true)) {
                StrP_PwaPro = "@940,20:AN09B,HM1,VM1| $StrP_DisPwaPro|\r\n"
                StrP_PwaPro2 = "@960,25:AN09B,HM1,VM1| $StrP_DisPwaPro2|\r\n"
                StrP_Adddate = ""
                StrP_Duedate2 = ""
            }


            Log.d("PASS", "PASS4")
            //StrP_Stop = "}"
            //StrP_Stop = "}\r\n"
            Log.d("PRINT", "PASS6")

            Log.d("PRINT", stBarCode)
            Strtoprint = StrP_Head +
                    StrP_wwName +
                    StrP_Npay +
                    StrP_Tel +
                    StrP_Invoicecnt +
                    StrP_Custcode +
                    StrP_wwCode +
                    StrP_dueDate +
                    StrP_LastPay +
                    StrP_mtrrdroute +
                    StrP_Custname +
                    StrP_CustAddress +
                    StrP_Lstmtrrddt +
                    StrP_Prsmtrrddt +
                    StrP_Lstmtrcnt +
                    StrP_Prsmtrcnt +
                    StrP_Prswtusg +
                    StrP_Type +
                    StrP_wtnor +
                    StrP_DISCOUNT +
                    StrP_Service +
                    StrP_Net +
                    StrP_Vat +
                    StrP_Res +
                    StrP_AmtRes +
                    StrP_AmtTotal +
                    StrP_LatLng +
                    StrP_Sdate +
                    StrP_Edate +
                    StrP_Version +
                    StrP_Comment +
                    StrP_Barcode +
                    StrP_Adddate +
                    StrP_Duedate2 +
                    StrP_Dupamt +
                    StrP_PwaPro +
                    StrP_PwaPro2 +
                    StrP_Stop

            Log.d("",Strtoprint)
        } catch (e: Exception) {
            showDialog("report=" + e.toString())
        }

        return Strtoprint
    }

    private fun printBillTSC(): String {
        val curConstant: Cursor?
        curConstant = db.select_ConstantData()

        if (curConstant != null) {
            if (curConstant.moveToFirst()) {

            }
        }

        val ca = Calendar.getInstance()
        val day = ca.get(Calendar.DATE)
        var month = ca.get(Calendar.MONTH)  //0 = JAN / 11 = DEC
        val year = ca.get(Calendar.YEAR)

        val time = Time()
        time.setToNow()

        month = month + 1 //Set int to correct month numeral, i.e 0 = JAN therefore set to 1.
        val `MONTH$`: String
        if (month <= 9) {
            `MONTH$` = "0$month"
        } else {
            `MONTH$` = "" + month
        }    //Set month to MM

        val df = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val df3 = SimpleDateFormat("dd/MM/yyyy")
        //day+"/"+MONTH$+"/"+((year+543)-2500),
        val formattedDate = day.toString() + "/" + `MONTH$` + "/" + (year + 543) + " " +
                +time.hour + ":" + String.format("%02d", time.minute)
        //วันที่ครบกำหนดชำระ()
        val m = Date()
        var cal = Calendar.getInstance()
        cal.time = m
        Log.d("A", "A")
        var Strtoprint = ""
        var StrP_Head = ""
        var StrP_wwName = ""
        val StrP_DiswwName = curConstant!!.getString(curConstant.getColumnIndex("wwnamet"))
        var StrP_Npay = ""
        val StrP_DisNpay = "โปรดชำระเงินในวันถัดไป"
        var StrP_Tel = ""
        val StrP_DisTel = curConstant.getString(curConstant.getColumnIndex("wwtel"))
        var StrP_Invoicecnt = ""
        val StrP_DisInvoicecnt = cur!!.getString(cur!!.getColumnIndex("invoicecnt"))
        var StrP_Custcode = ""
        val StrP_DisCustcode = cur!!.getString(cur!!.getColumnIndex("custcode"))
        var StrP_wwCode = ""
        var StrP_DiswwCode = cur!!.getString(cur!!.getColumnIndex("wwcode")) + "-" + ChkDigit

        if (debmonth > 0) {
            StrP_DiswwCode = cur!!.getString(cur!!.getColumnIndex("wwcode"))
        }

        Log.d("b", "b")
        var StrP_dueDate = ""
        var StrP_LastPay = ""
        if (debmonth == 0) {
            cal.add(Calendar.DATE, 7)
            val day2 = cal.get(Calendar.DATE)
            StrP_LastPay = "" + day2 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ครบชำระ", StrP_LastPay)
            if (invflag.equals("1", ignoreCase = true)) {
                StrP_LastPay = "-"
            }
        } else {
            cal.add(Calendar.DATE, 3) // 10 is the days you want to add or subtract
            val day3 = cal.get(Calendar.DATE)
            //StrP_LastPay = ""+day3+"/"+MONTH$+"/"+((year+543)-2500);
            StrP_LastPay = "-"
            if (invflag.equals("1", ignoreCase = true)) {
                StrP_LastPay = "-"
            }
        }

        var StrP_mtrrdroute = ""
        val StrP_Dismtrrdroute = cur!!.getString(cur!!.getColumnIndex("mtrrdroute")) +
                "." + cur!!.getString(cur!!.getColumnIndex("mtrseq"))
        var StrP_Custname = ""
        var StrP_DisCustname = cur!!.getString(cur!!.getColumnIndex("custname"))
        if (StrP_DisCustname.length > 39) {
            StrP_DisCustname = StrP_DisCustname.substring(0, 39)
        }
        var StrP_CustAddress = ""
        var StrP_DisCustAddress = cur!!.getString(cur!!.getColumnIndex("custaddr"))
        if (StrP_DisCustAddress.length > 39) {
            StrP_DisCustAddress = StrP_DisCustAddress.substring(0, 39)
        }

        var StrP_Lstmtrrddt = ""
        val StrP_DisLstmtrrddt = cur!!.getString(cur!!.getColumnIndex("lstmtrddt"))
        val strP_DisLstmtrrddt = StrP_DisLstmtrrddt.substring(4, 6) + "/" +
                StrP_DisLstmtrrddt.substring(2, 4) + "/" +
                StrP_DisLstmtrrddt.substring(0, 2)
        var StrP_Prsmtrrddt = ""
        Log.d("วันที่อ่านครั้งก่อน", strP_DisLstmtrrddt)
        val StrP_DisPrsmtrrddt = day.toString() + "/" + `MONTH$` + "/" + (year + 543 - 2500)
        var StrP_Lstmtrcnt = ""
        var StrP_DisLstmtrcnt = ""

        //เลขในมาตร ครั้งก่อน
        if (PrsMtrCnt >= Est) {
            StrP_DisLstmtrcnt = cur!!.getString(cur!!.getColumnIndex("meterest"))
        } else {
            StrP_DisLstmtrcnt = cur!!.getString(cur!!.getColumnIndex("lstmtrcnt"))
        }
        Log.d("วันที่อ่านครั้งนี้", StrP_DisPrsmtrrddt)
        var StrP_Prsmtrcnt = ""

        var StrP_DisPrsmtrcnt = ""
        //เลขในมาตร ครั้งนี้
        val df1 = DecimalFormat("######")
        if (Smcnt > 0) {
            StrP_DisPrsmtrcnt = "(" + PrsMtrCnt.toString() + "-" + Smcnt.toString() + ")"
        } else {
            StrP_DisPrsmtrcnt = df1.format(PrsMtrCnt)
        }
        var StrP_Prswtusg = ""
        var StrP_DisPrswtusg = ""
        if (PrsWtUsg > 0) {
            val df2 = DecimalFormat("#,###")
            StrP_DisPrswtusg = df2.format(PrsWtUsg * 1000)
        } else {
            StrP_DisPrswtusg = PrsWtUsg.toString()
        }
        if (CustStat.equals("3", ignoreCase = true)) {
            StrP_DisPrswtusg += " (เฉลี่ย)"
        }
        Log.d("F", "F")
        var StrP_Type = ""

        var StrP_DisType = "T" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
        if (usertype!!.substring(0, 1).equals("1", ignoreCase = true) && PrsWtUsg > 100) {
            StrP_DisType = "T1" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
        } else if (usertype!!.equals("29", ignoreCase = true) && PrsWtUsg <= 100) {
            val OldType: String
            OldType = cur!!.getString(cur!!.getColumnIndex("oldtype"))
            if (OldType.length == 3) {
                if (OldType.substring(0, 1).equals("1", ignoreCase = true)) {
                    StrP_DisType = "T1" + usertype + "(" + `MONTH$` + "/" + (year + 543 - 2500) + ")"
                }
            }
        }

        var StrP_wtnor = ""
        val StrP_Diswtnor = String.format("%,.2f", NorTrfwt)
        var StrP_DISCOUNT = ""
        val StrP_DisDISCOUNT = String.format("%,.2f", DiscntAmt)
        var StrP_Service = ""
        val StrP_DisService = String.format("%,.2f", SrvFee)
        var StrP_Net = ""
        val StrP_DisNet = String.format("%,.2f", TotTrfwt)
        var StrP_Vat = ""
        val StrP_DisVat = String.format("%,.2f", Vat)
        var StrP_Res = ""
        val StrP_DisRes = debmonth.toString()
        var StrP_AmtRes = ""
        val StrP_DisAmtRes = String.format("%,.2f", debamt)
        var StrP_AmtTotal = ""
        var StrP_DisAmtTotal = String.format("%,.2f", TotTrfwt)
        var StrP_LatLng = ""
        val StrP_DisLatLng = "(lat,lon)" + lat.toString() + "," + lon.toString()
        var StrP_BarCodeA = ""
        var StrP_BarCodeB = ""
        var StrP_DisPwaPro = ""
        var StrP_DisPwaPro2 = ""


        //		if (DisCntCode.equalsIgnoreCase("4")){
        //			StrP_DisDISCOUNT="0.00";
        //		}
        val formt = DecimalFormat("0.00")
        StrP_DisPwaPro = "ได้รับส่วนลดโครงการประชารัฐร่วมใจ"
        StrP_DisPwaPro2 = "คนไทยประหยัดน้ำ " + java.lang.Double.parseDouble(formt.format(distempamt)).toString() + " บาท"


        if (invflag.equals("1", ignoreCase = true)) {
            StrP_BarCodeA = "หักบัญชีธนาคารภายในวันที่ 25 ของทุกเดือน "
            StrP_BarCodeB = "โปรดตรวจสอบยอดเงินในบัญชีของท่านด้วย"
            if (debmonth > 0) {
                StrP_DisAmtTotal = String.format("%.2f", TotTrfwt + debamt)
            }
        } else {
            if (debmonth == 0) {
                StrP_BarCodeA = StrP_DisInvoicecnt + CustCode!!
                StrP_BarCodeA += wwcode
                StrP_BarCodeA += ChkDigit
                val cal2 = Calendar.getInstance()
                cal2.add(Calendar.DATE, 10)
                val day4 = cal2.get(Calendar.DATE)
                StrP_BarCodeA += day4.toString() + `MONTH$` + (year + 543 - 2500)
                val df4 = DecimalFormat("000000000")
                StrP_BarCodeA += df4.format(TotTrfwt * 100)
                StrP_BarCodeB = StrP_BarCodeA
                Log.d("barcode", StrP_BarCodeA)
            } else {
                StrP_DisAmtTotal = String.format("%.2f", TotTrfwt + debamt)
                StrP_BarCodeA = "ท่านอยู่ระหว่างถูกระงับการใช้น้ำ (ตัดมาตร)"
                StrP_BarCodeB = "โปรดติดต่อชำระหนี้ค้าง ณ สำนักงานประปาพื้นที่"
            }
        }


        //ขยายเวลารับชำระที่ตัวแทนเอกชนได้อีก 3 วัน
        var StrP_Adddate = ""
        val StrP_DisAdddate = "**โปรดระวังมิจฉาชีพแอบอ้างเก็บเงินค่าน้ำประปา**"
        var StrP_Duedate2 = ""
        var StrP_Sdate = ""
        val StrP_DisSdate = StrP_LastPay
        var StrP_Edate = ""
        var StrP_DisEdate = ""
        var StrP_PwaPro = ""
        var StrP_PwaPro2 = ""
        stBarCode = ""

        if (debmonth == 0) {
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 14) //ปรับวันที่ระงับการใช้น้ำเป็น7วันหลังครบกำหนดรับชำระDEC58
            val day3 = cal.get(Calendar.DATE)
            StrP_DisEdate = "" + day3 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ระงับการใช้น้ำ", StrP_DisEdate)
        } else {
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 3)
            val day3 = cal.get(Calendar.DATE)
            StrP_DisEdate = "" + day3 + "/" + `MONTH$` + "/" + (year + 543 - 2500)
            Log.d("วันที่ระงับการใช้น้ำ", StrP_DisEdate)
        }
        var StrP_DisDupamt = ""
        var StrP_Dupamt = ""
        //dupnet
        if (tmpdupamt > 0) {
            if (dupamt > 0) {   //ค่าน้ำรับซ้ำมากกว่าค่าน้ำเดือนปัจจุบัน
                //dupnet = Format(dupnet, "0.00")
                StrP_DisDupamt = "ปรับปรุงค่าน้ำรับซ้ำคงเหลือ  : "
                // strOneil += "@920,3:AN10B,HM1,VM1| " & HK(str) & Format(dupamt, "#,##0.00") & HK(" บาท ") & "|" & vbCrLf
            } else {
                //ค่าน้ำรับซ้ำน้อยกว่าค่าน้ำเดือนปัจจุบัน
                //dupnet = Format(dupnet, "0.00")
                StrP_DisDupamt = "มีค่าน้ำรับซ้ำไว้  : "
                // strOneil += "@920,3:AN10B,HM1,VM1| " & HK(str) & Format(tmpdupamt, "#,##0.00") & HK(" บาท ") & "|" & vbCrLf

            }
        }


        var StrP_Barcode = ""
        var StrP_Version = ""
        val StrP_DisVersion = "V.0.0.1"
        var StrP_Comment = ""
        val StrP_DisComment = "ID." + USERID.toString() + "(" + Comment.toString() + ")"
        var StrP_Stop = ""

        Log.d("PASS", "PASS1")
        DecimalFormat("#.###")
        try {
            Log.d("PASS", "PASS2")
            var Str_VAT_RATE = "0"//  VAT_RATE เป็น  0 คือ คิด vat
            if (Str_VAT_RATE == "0") {
                Str_VAT_RATE = "07"
            }

            Log.d("วันที่อ่านครั้งก่อน", strP_DisLstmtrrddt)

            var A:String=""
            var B:String=""
            var C:String=""
            var D:String=""
            var E:String=""
            var F:String=""
            var G:String=""
            var H:String=""


            tsc.openport("00:19:0E:A1:38:AF")
            tsc.clearbuffer()
            tsc.sendcommand("! 0 200 200 1590 1\n")
            tsc.sendcommand("LABEL\n")
            tsc.sendcommand("CONTRAST 0\n")
            tsc.sendcommand("TONE 0\n")
            tsc.sendcommand("SPEED 5\n")
            tsc.sendcommand("PAGE-WIDTH 600\n")
            tsc.sendcommand("BAR-SENSE\n")
            A = "T ANG12BPT.CPF 0 350 120" + StrP_DiswwName
            tsc.sendcommand(A.toByteArray(charset("TIS-620")))
            tsc.sendcommand("\n")
            E="T ANG12BPT.CPF 0 350 150 " + StrP_DisTel
            tsc.sendcommand(E.toByteArray(charset("TIS-620")))
            tsc.sendcommand("\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 20 250 " + StrP_DisInvoicecnt + "\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 210 250 " + StrP_DisCustcode + "\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 370 250 " + StrP_DiswwCode + "\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 20 325 " + formattedDate + "\n")
            if (debmonth==0){
                tsc.sendcommand("T ANG12BPT.CPF 0 210 325 " + StrP_LastPay + "\n")
            }else{
                tsc.sendcommand("T ANG12BPT.CPF 0 210 325 " + " - " + "\n")
            }
            tsc.sendcommand("T ANG12BPT.CPF 0 380 325 " + StrP_Dismtrrdroute + "\n")
            B ="T ANG12BPT.CPF 0 130 370 " + StrP_DisCustname
            tsc.sendcommand(B.toByteArray(charset("TIS-620")))
            tsc.sendcommand("\n")
            C="T ANG12BPT.CPF 0 70 410 " + StrP_DisCustAddress
            tsc.sendcommand(C.toByteArray(charset("TIS-620")))
            tsc.sendcommand("\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 210 510 " + strP_DisLstmtrrddt+ "\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 400 510 " + StrP_DisPrsmtrrddt + "\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 230 540 " + StrP_DisLstmtrcnt + "\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 400 540 " + StrP_DisPrsmtrcnt + "\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 360 580 " + StrP_DisPrswtusg + "\n")
            tsc.sendcommand("LEFT\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 150 615 " + StrP_DisType + "\n")
            tsc.sendcommand("RIGHT 440\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 615 "+ StrP_Diswtnor +"\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 650 " + StrP_DisDISCOUNT +"\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 400 680 " + StrP_DisService +"\n")
            if (tmpdupamt > 0) {
                if (Vat > 0) {
                    //ค่าน้ำรับซ้ำน้อยกว่าค่าน้ำเดือนปัจจุบัน

                    tsc.sendcommand("T ANG12BPT.CPF 0 440 750 " + String.format("%.2f", debtamt) + "\n")
                    tsc.sendcommand("T ANG12BPT.CPF 0 440 715 " + StrP_DisVat + "\n")
                } else {
                    //ค่าน้ำรับซ้ำมากกว่าค่าน้ำเดือนปัจจุบัน
                    tsc.sendcommand("T ANG12BPT.CPF 0 440 750 " + String.format("%.2f", debtamt) + "\n")
                    tsc.sendcommand("T ANG12BPT.CPF 0 440 715 " + String.format("%.2f", debtvat) + "\n")
                }
            } else {

                if (PwaFlag!!.equals("4", ignoreCase = true)) {

                    tsc.sendcommand("T ANG12BPT.CPF 0 440 750 " + String.format("%.2f", debtamt) + "\n")
                } else {
                    tsc.sendcommand("T ANG12BPT.CPF 0 440 750 " + StrP_DisNet + "\n")
                }

                tsc.sendcommand("T ANG12BPT.CPF 0 440 715 " + StrP_DisVat + "\n")
            }
//
            tsc.sendcommand("LEFT\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 200 780 " + StrP_DisRes +"\n")
            tsc.sendcommand("RIGHT 440\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 780 "+ StrP_DisAmtRes +"\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 815 " + StrP_DisAmtTotal + "\n")
            tsc.sendcommand("LEFT\n")
            tsc.sendcommand("\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 20 850 " + StrP_DisLatLng + "\n")

            //ค้างชำระและจ่ายธนาคาร
            if (debmonth == 0) {
                //ไม่ค้างชำระแต่จ่ายธนาคาร
//                StrP_BarCodeA = "หักบัญชีธนาคารภายในวันที่ 25 ของทุกเดือน "
//                StrP_BarCodeB = "โปรดตรวจสอบยอดเงินในบัญชีของท่านด้วย"

                if (invflag.equals("1", ignoreCase = true)) {
                    D = "VT ANG12BPT.CPF 0 500 850" + StrP_BarCodeA
                    tsc.sendcommand(D.toByteArray(charset("TIS-620")))
                    tsc.sendcommand("\n")
                    F = "VT ANG12BPT.CPF 0 530 850" + StrP_BarCodeB
                    tsc.sendcommand(F.toByteArray(charset("TIS-620")))
                    tsc.sendcommand("\n")
                    tsc.sendcommand("T ANG12BPT.CPF 0 450 890 -\n")
                    tsc.sendcommand("T ANG12BPT.CPF 0 440 930 -\n")

                } else {//ปกติพิมพ์บาร์โคด
                    tsc.sendcommand("T ANG12BPT.CPF 0 450 890 " +StrP_DisSdate +"\n")
                    tsc.sendcommand("T ANG12BPT.CPF 0 440 930 " +StrP_DisEdate +"\n")
                    tsc.sendcommand("BT 7 0 0\n")
                    tsc.sendcommand("VB 128 1 0 50 520 860 " +StrP_BarCodeA +"\n")
                    tsc.sendcommand("BT OFF\n")

                    stBarCode = StrP_BarCodeA
                }


            } else {//ค้างชำระ
                //"ท่านอยู่ระหว่างถูกระงับการใช้น้ำ (ตัดมาตร)"
                //"โปรดติดต่อชำระหนี้ค้าง ณ สำนักงานประปาพื้นที่"
                G = "VT ANG12BPT.CPF 0 500 820" + StrP_BarCodeA
                tsc.sendcommand(G.toByteArray(charset("TIS-620")))
                tsc.sendcommand("\n")
                H = "VT ANG12BPT.CPF 0 530 850" + StrP_BarCodeB
                tsc.sendcommand(H.toByteArray(charset("TIS-620")))
                tsc.sendcommand("\n")
                tsc.sendcommand("T ANG12BPT.CPF 0 450 890 -\n")
                tsc.sendcommand("T ANG12BPT.CPF 0 440 930 -\n")

            }
            tsc.sendcommand("\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 310 960 " +StrP_DisVersion +"\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 400 960 " +StrP_DisComment +"\n")
            tsc.sendcommand("\n")
            val StrP_QR = stBarCode
            tsc.sendcommand("B QR 470 1050 M 4 U 4" + "\r\n")
            tsc.sendcommand("MA,$StrP_QR\r\n")
            tsc.sendcommand("ENDQR" + "\r\n")
            tsc.sendcommand("\n")
            tsc.sendcommand("PRINT 1\n")
            tsc.closeport()
            Log.d("PASS", "PRINTING.......")

        } catch (e: Exception) {
            showDialog("report=" + e.toString())
        }

        return Strtoprint
    }

    private fun updateMtRrdData(Customer: String?) {

        try {
            println(Customer)
            db.updateData(Customer.toString(), OkPrint, CustStat, Comment, ComMentDec,
                    NorTrfwt, SrvFee, Vat, TotTrfwt, PrsMtrCnt, PrsWtUsg, DiscntAmt, NetTrfWt,
                    Usgcalmthd, USERID, ChkDigit, BillSend, lat.toString(), lon.toString(), HLN,
                    CalWtUsg, distempamt, distempnet, distempvat)

        } catch (e: Exception) {

            Toast.makeText(this, "เกิดข้อผิดพลาดในการบันทึกข้อมูล !!" + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    private fun ChkDigit() {
        // TODO Auto-generated method stub

        val a = ""
        val b: String
        val c: String
        val strDigit: String
        var Str: String
        var Tdigit: Long = 0
        var itot: Long = 0

        Log.d("เริ่ม", "a11")
        try {

            Str = wwcode!!.trim { it <= ' ' } //เลขที่สำนักงานประปา
            Tdigit += java.lang.Long.parseLong(CalPrime(Str))
            Log.d("wwcode", Tdigit.toString())

            Str = CustCode!!.trim { it <= ' ' } //เลขที่ ป.ผู้ใช้น้ำ
            Tdigit += java.lang.Long.parseLong(CalPrime(Str))
            Log.d("CustCode", Tdigit.toString())

            Str = Invoicecnt!!.trim { it <= ' ' } //ลขที่ใบแจ้งหนี้
            Tdigit += java.lang.Long.parseLong(CalPrime(Str))
            Log.d("Invoicecnt", Tdigit.toString())

            val ittot: String
            var inttot: Double
            //Log.d("test", String.valueOf(TotTrfwt));
            ittot = String.format("%.2f", TotTrfwt)//259.66

            //Log.d("test", ittot); //577.80
            inttot = java.lang.Double.parseDouble(ittot)
            //Log.d("test1", String.valueOf(inttot)); //577.80
            inttot = inttot * 100
            //Log.d("test2", String.valueOf(inttot)); //577.80
            itot = java.lang.Long.parseLong(String.format("%.0f", inttot))//57780000
            //Log.d("aaa", String.valueOf(itot)); //577


            Str = itot.toString()
            Tdigit += java.lang.Long.parseLong(CalPrime(Str))
            Log.d("tottrfwt", Tdigit.toString())

            //			strDigit = String.valueOf(Tdigit);
            //			Log.d("a",strDigit);
            //			a = strDigit.substring(0, 1);
            //			Log.d("a", a);
            //			b = strDigit.substring(strDigit.length()-1, strDigit.length());
            //			Log.d("b", b);
            //			c = b + a;
            //			Log.d("c", c);
            //			Tdigit = Tdigit * Integer.parseInt(c);
            //			Log.d("Tdigit", String.valueOf(Tdigit));
            //			int i;
            //			i =String.valueOf(Tdigit).length()-2;
            Log.d("Bdigit", "" + Tdigit)
            ChkDigit = (Tdigit * 52 % 100).toString()
            val foo = Integer.parseInt(ChkDigit)
            ChkDigit = String.format("%02d", foo)
            Log.d("Cisdigit", ChkDigit)

        } catch (e: Exception) {
            Log.d("bCIS", "bCIS")
            ChkDigit = "00"
            e.printStackTrace()
        }

    }

    private fun CalPrime(str: String): String {
        // TODO Auto-generated method stub

        var digit: Long = 0
        var tempdigi: Long = 0
        var i = 0

        i = 0
        while (i <= str.length - 1) {
            when (i) {
                0 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 0
                    tempdigi = digit
                }
                1 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 1
                    tempdigi = digit
                }
                2 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 2
                    tempdigi = digit
                }
                3 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 3
                    tempdigi = digit
                }
                4 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 4
                    tempdigi = digit
                }
                5 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 5
                    tempdigi = digit
                }
                6 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 6
                    tempdigi = digit
                }
                7 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 7
                    tempdigi = digit
                }
                8 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 8
                    tempdigi = digit
                }
                9 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 9
                    tempdigi = digit
                }
                10 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 10
                    tempdigi = digit
                }
                11 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 11
                    tempdigi = digit
                }
                12 -> {
                    digit += java.lang.Long.parseLong(str.substring(i, i + 1).toString()) * 12
                    tempdigi = digit
                }
            }
            Log.d("digit", "digit $str / $i: $tempdigi")
            i++
        }
        return digit.toString()
    }

    internal inner class GotoPrint(var Re_Message: String) : Runnable {


        override fun run() {
            try {

                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val pairedDevices = mBluetoothAdapter.bondedDevices

                val testDevice = pairedDevices.toString()

                val address = testDevice.substring(1, 18)
                val device = mBluetoothAdapter.getRemoteDevice(address)
                mCommandService!!.connect_1(device)

                var nWaitTime = 8

                while (mCommandService!!.state != bluetoothCommandService.STATE_CONNECTED) {
                    Thread.sleep(1000)
                    nWaitTime--
                    if (nWaitTime == 0) {

                        throw Throwable("\nชื่อ: " + device.getName() + "\nรหัส: " +
                                device.getAddress() + " ไม่สามารถเชื่อมต่อได้")
                    }
                }

                //Print Bill
               // mCommandService!!.write(Re_Message)
                mCommandService!!.write(Re_Message)

                //QR CODE
                docEZ = DocumentEZ("MF204")
                paramEZ = ParametersEZ()
                paramEZ!!.setHorizontalMultiplier(5)
                paramEZ!!.setVerticalMultiplier(1)

                if (stBarCode.equals("", ignoreCase=true)) {

                } else{

                    docEZ!!.writeBarCodeQRCode(stBarCode.toString(), 2, 4, 9,
                            950, 450, paramEZ)
                    printData = docEZ!!.getDocumentData()
                    mCommandService!!.write(printData)

                }

            } catch (e: Throwable) {
                //B_printBill.setEnabled(true);
                runOnUiThread { showDialog("กรุณาตรวจสอบเครื่องพิมพ์ " + e.message) }

            } finally {
                if (mCommandService != null) {
                    mCommandService!!.stop()
                    if (mCommandService!!.state != bluetoothCommandService.STATE_NONE) {
                        Statust_Connect = false
                    }
                }
            }

        }

    }

    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {

            when (msg.what) {

                MESSAGE_STATE_CHANGE -> when (msg.arg1) {

                    bluetoothCommandService.STATE_CONNECTED ->
                        /* mTitle.setText(R.string.title_connected_to);
	                    mTitle.append(mConnectedDeviceName);*/
                        Statust_Connect = true

                    bluetoothCommandService.STATE_CONNECTING -> {
                    }
                    bluetoothCommandService.STATE_LISTEN,

                    bluetoothCommandService.STATE_NONE ->
                        // mTitle.setText(R.string.title_not_connected);
                        Statust_Connect = false
                }// mTitle.setText(R.string.title_connecting);
                MESSAGE_DEVICE_NAME -> msg.data.getString(DEVICE_NAME)
                MESSAGE_TOAST -> Toast.makeText(applicationContext, msg.data.getString(TOAST), Toast.LENGTH_SHORT).show()
            }// Toast.makeText(getApplicationContext(), "Connected to "+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
        }
    }

    override fun onStart() {
        super.onStart()
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        } else {
            if (mCommandService == null)
                setupCommand()
        }
    }

    override fun onResume() {
        super.onResume()

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mCommandService != null) {
            if (mCommandService!!.state == bluetoothCommandService.STATE_NONE) {
                mCommandService!!.start()
            }
        }
    }

    private fun setupCommand() {
        mCommandService = bluetoothCommandService(this, mHandler)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mCommandService != null)
            mCommandService!!.stop()
        db.close()

    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub
        when (v.id) {
            R.id.bn_ok -> calMeter()
            R.id.bn_moveN -> Nextdata()
            R.id.bn_moveP -> Previousdata()
            R.id.bn_moveF -> Firstdata()
            R.id.bn_moveL -> Lastdata()
            R.id.bn_readagain -> ReadDataAgain()
            R.id.bn_notread ->   try {
                PrsWtUsg = AvgWtUsg.toDouble()
                CalWtUsg = PrsWtUsg
                PrsMtrCnt = Est + PrsWtUsg
                Comment = "01"
                BillSend = "1"
                CustStat = "2" //มาตรผิดปกติ กดเข้าอ่านไม่ได้
                HLN = "N"


                Log.d("กดเข้าอ่านไม่ได้", PrsWtUsg.toString())
                val readPlus = Intent(this,Page_ReadMeterPlus::class.java)


                readPlus.putExtra("userid", USERID)
                readPlus.putExtra("TwwCode", wwcode)
                readPlus.putExtra("TCustCode", CustCode)
                readPlus.putExtra("TRoute", chRoute)
                readPlus.putExtra("MtrSeq", Mtrseq)
                readPlus.putExtra("TMeterno", MeterNo)
                readPlus.putExtra("TPrsmtrcnt", PrsMtrCnt)
                readPlus.putExtra("TPrswtusg", PrsWtUsg)
                readPlus.putExtra("THln", HLN)
                readPlus.putExtra("TSmcnt", Smcnt)
                readPlus.putExtra("TComment", Comment)
                readPlus.putExtra("SentAVG", SendAVGSt)
                readPlus.putExtra("Tmsg", false)
                readPlus.putExtra("Bmsg", false)
                readPlus.putExtra("OkRead", false)
                readPlus.putExtra("OkPrint", false)
                readPlus.putExtra("SendOutSt", true)




                startActivityForResult(readPlus, RESULT_REQUEST)

                } catch (e: Exception) {

                }
            }
        }

    private fun showDialog(message: String) {

        val alt_bld = AlertDialog.Builder(this)
        alt_bld.setMessage(message)
        alt_bld.setTitle("คำอธิบาย")
        alt_bld.setPositiveButton("ตกลง", null)
        alt_bld.show()

    }

    }







