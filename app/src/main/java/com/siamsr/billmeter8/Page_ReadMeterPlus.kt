package com.siamsr.billmeter8

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Page_ReadMeterPlus : AppCompatActivity() {

    private var chRoute: String? = null
    var tx1: TextView? = null
    var tx2: TextView? = null
    var tx3: TextView? = null
    var tx4: TextView? = null
    var tx5: TextView? = null
    var USERID: String? = null
    var TXCOMMENT = arrayOf("ปกติ (00)", "บ้านปิด หรือมาตรอยู่ภายใน (01)", "มีสิ่งกีดขวางทำให้อ่านตัวเลขไม่ได้ (02)", "มีสัตว์เลี้ยงดุร้าย (03)", "บ้านว่าง (04)", "ลวดตีมาตรขาด (05)", "ยังไม่มีลวดตีมาตร (06)", "ผู้ใช้น้ำที่ชำระเงินหักผ่านบัญชีธนาคาร (07)", "เลขมาตรขึ้นตลอด (08)", "หลังมาตรมีการใช้ปั้มสูบน้ำ (09)", "มีการลักใช้น้ำ (10)", "ยูเนี่ยนมาตรรั่ว (11)", "ท่อเมนรองรั่ว (12)", "ประตูน้ำรั่ว (13)", "หน้าปัดมาตรแตกแต่อ่านตัวเลขได้ (14)", "มาตรตายตัวเลขไม่หมุน (15)", "มาตรจมดินหรือซีเมนต์(อ่านตัวเลขได้) (16)", "มาตรจมน้ำ แต่อ่านตัวเลขได้ (17)", "มาตรครบรอบ (18)", "ตัวเลขที่อ่านได้มีจำนวนน้อยกว่าที่ได้อ่านไว้ในรอบการอ่านมาตรก่อนหน้านี้(19)", "มีการติดมาตรสลับบ้าน (20)", "มีการติดมาตรกลับด้าน (21)", "มาตรเดินถอยหลัง (22)", "มาตรที่มองไม่เห็นตัวเลขหน่วยน้ำ (23)", "มาตรเดินเร็วผิดปกติ (24)", "มาตรที่ตัวเลขขึ้นแบบก้าวกระโดด (25)", "ใช้น้ำน้อยผิดปกติ (26)", "ใช้น้ำมากผิดปกติ (27)", "มาตรหาย (28)", "อื่นๆ (99)")

    private val db = DBHelper(this)
    private var txComment: TextView? = null
    private var MeterNo: String? = null
    private var HLN: String? = null
    private var Comment: String? = null
    private var txtcomment: String? = null
    private var PrsMtrCnt: Double = 0.toDouble()
    private var PrsWtUsg: Double = 0.toDouble()
    private var Smcnt: Double = 0.toDouble()
    private var etComment: EditText? = null
    private var et_smcnt: EditText? = null
    internal var isPrint: Boolean = false
    internal var OkPrint: Boolean = false
    internal var SendOutSt: Boolean = false
    internal var OkRead: Boolean = false
    internal var SendNotIn: Boolean = false
    internal var SendAVG: Boolean=false
    internal var Tmsg: Boolean = false
    internal var Bmsg = false
    internal var CustCode: String? = null
    internal var commentCode: String? = null
    internal var commentDesc = ""
    internal var billStatus: String? = null
    internal var sComment: String? = null
    internal var wwCode: String? = ""
    internal var MtrSeq: Int = 0
    internal var uri: Uri? = null
    internal var imageFileUri: Uri? = null


        private val CAMERA_REQUEST = 1

        private val TXCOMMENT2 = arrayOf("บ้านปิด หรือมาตรอยู่ภายใน (01)", "มีสิ่งกีดขวางทำให้อ่านตัวเลขไม่ได้ (02)", "มีสัตว์เลี้ยงดุร้าย (03)", "มาตรหาย (28)", "อื่นๆ (99)")

        private val TXCOMMENT3 = arrayOf("มาตรครบรอบ (18)", "บ้านปิด หรือมาตรอยู่ภายใน (01)", "มีสิ่งกีดขวางทำให้อ่านตัวเลขไม่ได้ (02)", "มีสัตว์เลี้ยงดุร้าย (03)", "มาตรหาย (28)", "อื่นๆ (99)")

        private val TXCOMMENT4 = arrayOf("ปกติ (00)", "บ้านว่าง (04)", "ลวดตีมาตรขาด (05)", "ยังไม่มีลวดตีมาตร (06)", "ผู้ใช้น้ำที่ชำระเงินหักผ่านบัญชีธนาคาร (07)", "เลขมาตรขึ้นตลอด (08)", "หลังมาตรมีการใช้ปั้มสูบน้ำ (09)", "มีการลักใช้น้ำ (10)", "ยูเนี่ยนมาตรรั่ว (11)", "ท่อเมนรองรั่ว (12)", "ประตูน้ำรั่ว (13)", "หน้าปัดมาตรแตกแต่อ่านตัวเลขได้ (14)", "มาตรตายตัวเลขไม่หมุน (15)", "มาตรจมดินหรือซีเมนต์(อ่านตัวเลขได้) (16)", "มาตรจมน้ำ แต่อ่านตัวเลขได้ (17)", "มาตรครบรอบ (18)", "มีการติดมาตรสลับบ้าน (20)", "มีการติดมาตรกลับด้าน (21)", "มาตรเดินถอยหลัง (22)", "มาตรที่มองไม่เห็นตัวเลขหน่วยน้ำ (23)", "มาตรเดินเร็วผิดปกติ (24)", "มาตรที่ตัวเลขขึ้นแบบก้าวกระโดด (25)", "ใช้น้ำน้อยผิดปกติ (26)", "ใช้น้ำมากผิดปกติ (27)", "มาตรหาย (28)", "อื่นๆ (99)")
        private val TXBILL = arrayOf("ใส่กล่องใบแจ้งหนี้ (1)", "ส่งให้ผู้ใช้น้ำ (2)", "แปะไว้หน้าบ้าน (3)")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.p_readmeterplus)

        tx1 = findViewById(R.id.tx_route2) as TextView
        tx2 = findViewById(R.id.tx_meterno2) as TextView

        tx3 = findViewById(R.id.tx_prsmtrcnt2) as TextView
        tx4 = findViewById(R.id.tx_prswtusg2) as TextView
        tx5 = findViewById(R.id.tx_hln2) as TextView
        txComment = findViewById(R.id.tx_comment) as TextView
        etComment = findViewById(R.id.et_comment) as EditText
        val btnOK: Button = findViewById(R.id.bn_ok)
        val bnComment: Button = findViewById(R.id.bn_comment)
        val bncap2: Button = findViewById(R.id.bn_capture2)
        val bnbill: Button = findViewById(R.id.bn_billstatus)

        etComment!!.isEnabled = true
        bnComment.isEnabled = true
        bnComment.text = "COMMENT"

        USERID = intent.getStringExtra("userid")
        wwCode = intent.getStringExtra("TwwCode")
        MtrSeq = intent.getIntExtra("MtrSeq", 1)
        CustCode = intent.getStringExtra("TCustCode")
        chRoute = intent.getStringExtra("TRoute")
        MeterNo = intent.getStringExtra("TMeterno")
        PrsMtrCnt = intent.getDoubleExtra("TPrsmtrcnt", 10.0)
        PrsWtUsg = intent.getDoubleExtra("TPrswtusg", PrsWtUsg)
        HLN = intent.getStringExtra("THln")
        Smcnt = intent.getDoubleExtra("TSmcnt", Smcnt)
        Comment = intent.getStringExtra("TComment")
        SendAVG = intent.getBooleanExtra("SentAVG",true)
        Tmsg = intent.getBooleanExtra("Tmsg", true)
        Bmsg = intent.getBooleanExtra("Bmsg", true)
        SendOutSt = intent.getBooleanExtra("SendOutSt", true)
        OkRead = intent.getBooleanExtra("OkRead", true)
        OkPrint = intent.getBooleanExtra("OkPrint", true)
        SendOutSt = intent.getBooleanExtra("SendOutSt", true)
        SendNotIn = intent.getBooleanExtra("SendNotIn", true)

        if (SendOutSt) {

        } else {
            if (Tmsg) {
                etComment!!.isEnabled = false
                bnComment.isEnabled = false
                bnComment.text = "ห้ามเปลี่ยน"
            }
        }


        val builder = AlertDialog.Builder(this)


        when (HLN) {
            "N" -> tx5?.text = "ปกติ"
            "H" -> {
                tx5?.text = "น้ำสูง"

                builder.setMessage("HIGH น้ำสูง !!")
                builder.setPositiveButton("OK", null)
                val alertDialog1: AlertDialog = builder.show()
                val messageText1 = alertDialog1.findViewById(android.R.id.message) as TextView
                messageText1.gravity = Gravity.CENTER
                showNotificationUP()
            }
            "L" -> {
                tx5?.text = "น้ำต่ำ"
                builder.setMessage("LOW น้ำต่ำ !!")
                builder.setPositiveButton("OK", null)
                val alertDialog2: AlertDialog = builder.show()
                val messageText2 = alertDialog2.findViewById(android.R.id.message) as TextView
                messageText2.gravity = Gravity.CENTER
                showNotificationDown()
            }
        }


        if (SendAVG) {

            Comment = "15"
            chkComment(Comment)
            builder.setMessage("มาตรตายหรือตัวเลขไม่หมุน(เฉลี่ย)")
            builder.setPositiveButton("OK", null)
            val alertDialog: AlertDialog = builder.show()
            val messageText = alertDialog.findViewById(android.R.id.message) as TextView
            messageText.gravity = Gravity.CENTER
            showNotificationUP()

        }

        val myCnt = PrsMtrCnt.toInt()
        val myUsg = PrsWtUsg.toInt()
        val mySmcnt = Smcnt.toInt()

        tx1?.text = chRoute
        tx2?.text = MeterNo
        tx3?.text = myCnt.toString()
        tx4?.text = myUsg.toString()


        et_smcnt = findViewById(R.id.et_smcnt) as EditText
        et_smcnt!!.setText(mySmcnt.toString())

        etComment = findViewById(R.id.et_comment) as EditText
        etComment!!.setRawInputType(Configuration.KEYBOARD_12KEY)
        etComment!!.setText(Comment)

        etComment!!.requestFocus()
        etComment!!.setSelectAllOnFocus(true)
        etComment!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            // If the event is a key-down event on the "enter" button
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                txtcomment = etComment!!.text.toString()
                chkComment(txtcomment)
                CalMeterSubmit()
                return@OnKeyListener true
            }
            false
        })

        chkComment(Comment)


        btnOK.setOnClickListener {
            // TODO Auto-generated method stub

            CalMeterSubmit()
        }

        bnComment.setOnClickListener {
            // TODO Auto-generated method stub
            val builder = AlertDialog.Builder(this)

            builder.setTitle("เลือกหมายเหตุ")
            if (SendOutSt) {
                if (SendNotIn) {
                    TXCOMMENT = TXCOMMENT2
                } else {
                    TXCOMMENT = TXCOMMENT3
                }
            } else {
                TXCOMMENT = TXCOMMENT4
            }
            builder.setItems(TXCOMMENT) { dialog, which ->
                // TODO Auto-generated method stub

                val selected = TXCOMMENT[which]


                commentCode = selected.substring(selected.length - 3, selected.length - 1)
                commentDesc = selected
                sComment = commentDesc

                etComment!!.setText(commentCode)
                txComment!!.text = commentDesc
                chkComment(commentCode)
            }
            builder.setNegativeButton("ไม่เลือก", null)
            builder.create()

            builder.show()
        }

        bncap2.setOnClickListener {
            // TODO Auto-generated method stub
            CallCamera()
        }

        bnbill.setOnClickListener {
            // TODO Auto-generated method stub
            val builder = AlertDialog.Builder(this)
            builder.setTitle("เลือกสถานะการส่งใบแจ้งหนี้")
            builder.setItems(TXBILL) { dialog, which ->
                // TODO Auto-generated method stu
                val selected = TXBILL[which]


                billStatus = selected.substring(selected.length - 2, selected.length - 1)
                val etBill = findViewById<EditText>(R.id.et_billstatus)
                etBill.setText(billStatus)
            }
            builder.setNegativeButton("ไม่เลือก", null)
            builder.create()

            builder.show()
        }


    }

    fun CallCamera() {
        val sd = Environment.getExternalStorageDirectory()
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            val imageFileName = "C$CustCode$timeStamp.jpg"
            val imagefile = File(sd, "PWA/IMAGE/$imageFileName")
            imageFileUri = Uri.fromFile(imagefile)

            if (imagefile != null) {

                //intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                //intent.putExtra("crop", "true");
                //intent.putExtra("outputX", 600);
                //intent.putExtra("outputY", 600);
                //intent.putExtra("aspectX", 1);
                //intent.putExtra("aspectY", 1);
                //intent.putExtra("scale", true);
                //intent.putExtra("return-data", true);
                //startActivityForResult( intent, CAMERA_REQUEST);

                //intent.putExtra("crop", "true");
                intent.putExtra("outputX", 200)
                intent.putExtra("outputY", 200)
                intent.putExtra("aspectX", 1)
                intent.putExtra("aspectY", 1)
                intent.putExtra("scale", true)
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                startActivityForResult(intent, CAMERA_REQUEST)

            }


        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }

    private fun CalMeterSubmit() {
        // TODO Auto-generated method stub
        try {

            val builder = AlertDialog.Builder(this)
            builder.setMessage("PRINT ??")
            builder.setPositiveButton("พิมพ์") { dialog, which ->
                // TODO Auto-generated method stub
                //Toast.makeText(getApplicationContext(), String.valueOf(isPrint), Toast.LENGTH_SHORT).show();
                if (!isPrint) {
                    //Toast.makeText(getApplicationContext(), "No Print!!", Toast.LENGTH_SHORT).show();
                    OkPrint = false
                    val etComment = findViewById(R.id.et_comment) as EditText
                    val bnComment = findViewById(R.id.bn_comment) as Button
                    etComment.isEnabled = false
                    bnComment.isEnabled = false
                    bnComment.text = "COMMENT"
                    val strComment = etComment.text.toString()
                    val SmallUsg = java.lang.Double.parseDouble(et_smcnt!!.text.toString())
                    val i = Intent()
                    i.putExtra("Comment", strComment)
                    i.putExtra("ComMentDec", sComment)
                    i.putExtra("OkPrint", OkPrint)
                    i.putExtra("BillSend", billStatus)
                    i.putExtra("Smallusg", SmallUsg)
                    setResult(-1, i)
                    finish()
                } else {
                    OkPrint = true
                    val etComment = findViewById<EditText>(R.id.et_comment)
                    val bnComment = findViewById<Button>(R.id.bn_comment)
                    etComment.isEnabled = false
                    bnComment.isEnabled = false
                    bnComment.setText("COMMENT")
                    val strComment = etComment.text.toString()
                    val SmallUsg = java.lang.Double.parseDouble(et_smcnt!!.text.toString())
                    val i = Intent()
                    i.putExtra("Comment", strComment)
                    i.putExtra("ComMentDec", sComment)
                    i.putExtra("OkPrint", OkPrint)
                    i.putExtra("BillSend", billStatus)
                    i.putExtra("Smallusg", SmallUsg)
                    setResult(-1, i)
                    finish()
                }
            }
            builder.setNegativeButton("ไม่พิมพ์") { dialog, which ->
                // TODO Auto-generated method stub
                OkPrint = false
                etComment!!.requestFocus()
                etComment!!.setSelectAllOnFocus(true)
            }

            // อย่าลืมคำสั่ง show
            builder.show()

        } catch (e: Exception) {
            println(e)
        }


    }

    private fun chkComment(message: String?): String? {
        try {


            sComment = "00"
            sComment = message
            //Toast.makeText(ReadMeterPlus.this, "เลือก " +sComment, Toast.LENGTH_SHORT).show();
            when (sComment!!.toString()) {
                "" -> {
                    sComment = ""
                    isPrint = true
                }
                "00" -> {
                    sComment = "ปกติ (00)"
                    isPrint = true
                }
                "01" -> {
                    sComment = "บ้านปิด หรือมาตรอยู่ภายใน (01)"
                    isPrint = true
                }
                "02" -> {
                    sComment = "มีสิ่งกีดขวางทำให้อ่านตัวเลขไม่ได้ (02)"
                    isPrint = true
                }
                "03" -> {
                    sComment = "มีสัตว์เลี้ยงดุร้าย (03)"
                    isPrint = true
                }
                "04" -> {
                    sComment = "บ้านปิดไม่มีคนอยู่(ไม่มีการใช้น้ำ)(04)"
                    isPrint = true
                }
                "05" -> {
                    sComment = "ลวดตีมาตรขาด (05)"
                    isPrint = true
                }
                "06" -> {
                    sComment = "ยังไม่มีลวดตรีมาตร (06)"
                    isPrint = true
                }
                "07" -> {
                    sComment = "ผู้ใช้น้ำที่ชำระเงินหักผ่านบัญชีธนาคาร (07)"
                    isPrint = true
                }
                "08" -> {
                    sComment = "เลขมาตรขึ้นตลอด (08)"
                    isPrint = true
                }
                "09" -> {
                    sComment = "หลังมาตรมีการใช้ปั้มสูบน้ำ (09)"
                    isPrint = true
                }
                "10" -> {
                    sComment = "มีการลักใช้น้ำ (10)"
                    isPrint = true
                }
                "11" -> {
                    sComment = "ยูเนี่ยนมาตรรั่ว (11)"
                    isPrint = true
                }
                "12" -> {
                    sComment = "ท่อเมนรองรั่ว (12)"
                    isPrint = true
                }
                "13" -> {
                    sComment = "ประตูน้ำรั่ว (13)"
                    isPrint = true
                }
                "14" -> {
                    sComment = "หน้าปัดมาตรแตกแต่อ่านตัวเลขได้ (14)"
                    isPrint = true
                }
                "15" -> {
                    sComment = "มาตรตายหรือตัวเลขไม่หมุน(เฉลี่ย) (15)"
                    isPrint = true
                }
                "16" -> {
                    sComment = "มาตรอยู่ในดินหรือซีเมนต์ แต่อ่านตัวเลขได้ (16)"
                    isPrint = true
                }
                "17" -> {
                    sComment = "มาตรจมน้ำ แต่อ่านตัวเลขได้ (17)"
                    isPrint = true
                }
                "18" -> {
                    sComment = "มาตรครบรอบ (18)"
                    isPrint = true
                }
                "19" -> {
                    sComment = "ตัวเลขที่อ่านได้มีจำนวนน้อยกว่าที่ได้อ่านไว้ในรอบการอ่านมาตรก่อนหน้านี้ (19)"
                    isPrint = false
                }
                "20" -> {
                    sComment = "มีการติดมาตรสลับด้าน (20)"
                    isPrint = false
                }
                "21" -> {
                    sComment = "มีการติดมาตรกลับด้าน (21)"
                    isPrint = false
                }
                "22" -> {
                    sComment = "มาตรเดินถอยหลัง (22)"
                    isPrint = false
                }
                "23" -> {
                    sComment = "มาตรที่มองไม่เห็นตัวเลขหน่วยน้ำ (23)"
                    isPrint = false
                }
                "24" -> {
                    sComment = "มาตรเดินเร็วผิดปกติ (24)"
                    isPrint = false
                }
                "25" -> {
                    sComment = "มาตรที่ตัวเลขขึ้นแบบก้าวกระโดด (25)"
                    isPrint = false
                }
                "26" -> {
                    sComment = "ใช้น้ำน้อยผิดปกติ (26)"
                    isPrint = false
                }
                "27" -> {
                    sComment = "ใช้น้ำมากผิดปกติ (27)"
                    isPrint = false
                }
                "28" -> {
                    sComment = "มาตรหาย (28)"
                    isPrint = false
                }
                "99" -> {
                    sComment = "อื่นๆ (99)"
                    isPrint = false
                }
                else -> {
                }
            }
            //  Toast.makeText(ReadMeterPlus.this, "เลือก " +sComment, Toast.LENGTH_SHORT).show();
            txComment = findViewById(R.id.tx_comment) as TextView
            txComment!!.text = sComment
            return sComment as String
        } catch (e: Exception) {
            // TODO: handle exception
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return message
        }

    }

    /**
     * On activity result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            return
        Log.d("TAG", "onActivityResult, requestCode: $requestCode, resultCode: $resultCode")

        when (requestCode) {
            CAMERA_REQUEST -> {

                val extras = data.extras

                if (extras != null) {
                    val yourImage = extras.getParcelable<Bitmap>("data")
                    // convert bitmap to byte
                    val stream = ByteArrayOutputStream()
                    yourImage!!.compress(Bitmap.CompressFormat.PNG, 85, stream)
                    val imageInByte = stream.toByteArray()
                    Log.e("output before con", imageInByte.toString())
                    db.insertIMG("5531011", chRoute!!, MtrSeq!!, CustCode!!, USERID!!,imageInByte)
                   // db.insertIMG("5532011", "060001", 1,"11852159","0001",imageInByte)
                    showDialog("บันทึกรูปเรียบร้อยแล้ว")
                    Log.d("2", "out")
                }
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

    private fun showNotificationUP() {

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
    }

    private fun showNotificationDown() {

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
    }

}
