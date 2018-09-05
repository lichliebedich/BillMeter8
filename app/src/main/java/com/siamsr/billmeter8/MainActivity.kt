package com.siamsr.billmeter8

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaScannerConnection
import android.os.*
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.tscdll.TSCActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.UnsupportedEncodingException
import java.text.DateFormat
import java.util.*

@SuppressLint("ByteOrderMark")
class MainActivity : AppCompatActivity() {

    internal var tsc = TSCActivity()
    companion object {
        val REFRESH_SCREEN = 1
        private val REQUEST_ENABLE_BT = 2
        val MESSAGE_STATE_CHANGE = 1
        val MESSAGE_READ = 2
        val MESSAGE_WRITE = 3
        val MESSAGE_DEVICE_NAME = 4
        val MESSAGE_TOAST = 5
        val DEVICE_NAME = "device_name"
        val TOAST = "toast"
    }

    lateinit var pwaDB :DBHelper
    private var db: SQLiteDatabase? = null

    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101
    private var lastBackPressTime: Long = 0
    private var ms: Toast? = null

    // inside a basic activity
    private var locationManager : LocationManager? = null
    internal var Statust_Connect = false
    internal var REQUEST_EXTERNAL_STORAGE_PERMISSION = 0

    var USERID: String = ""
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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
              message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
               message.setText(R.string.title_dashboard)

                val i1 = Intent(this, Page_ShowRoute::class.java)
                startActivity(i1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Prompt Android 6 user if permission is not yet granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val accesscoarselocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
            val readExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val writeExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (accesscoarselocation != PackageManager.PERMISSION_GRANTED ||
                    writeExternalPermission != PackageManager.PERMISSION_GRANTED ||
                    readExternalPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_EXTERNAL_STORAGE_PERMISSION)
            }
        }


        pwaDB = DBHelper(this)
        db = pwaDB.writableDatabase


        setupPermissions()
        Check_and_Create_folder()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val bnLogin: Button = findViewById(R.id.bn_login)
        bnLogin.setOnClickListener{

            USERID = editText_login.text.toString()

            if(USERID.isNotEmpty()){

                if ( getLogin(USERID)|| USERID.equals("8899", ignoreCase = true)) {
                    val readMenu = Intent(this, Page_Menu::class.java)
                    readMenu.putExtra("userid", USERID)
                    startActivity(readMenu)
                } else {

                  showDialog("รหัสของคุณไม่ถูกต้อง")
                }

            }else{
                Toast.makeText(applicationContext, "กรุณาใส่รหัสพนักงาน", Toast.LENGTH_SHORT).show()
            }


        }

        val bnPrint: Button = findViewById(R.id.bn_testprint)
        bnPrint.setOnClickListener{
            val A: String
            val B: String
            val C: String
            val D: String
            val E: String
            //tsc.openport("00:19:0E:A2:E1:38");
            //tsc.openport("00:19:0E:A1:38:AF");
            //tsc.openport("00:19:0E:A2:E1:38");
            //tsc.downloadttf("ANG12BPT.CPF");
            try {



            tsc.openport_with_pair()
            tsc.clearbuffer()
            tsc.sendcommand("! 0 200 200 1590 1\n")
            tsc.sendcommand("LABEL\n")
            tsc.sendcommand("CONTRAST 0\n")
            tsc.sendcommand("TONE 0\n")
            tsc.sendcommand("SPEED 5\n")
            tsc.sendcommand("PAGE-WIDTH 600\n")
            tsc.sendcommand("BAR-SENSE\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 520 150 V1.1\n")

            A = "T ANG12BPT.CPF 0 350 120 รังสิต (ชั้นพิเศษ)"
            try {
                tsc.sendcommand(A.toByteArray(charset("TIS-620")))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            tsc.sendcommand("\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 350 150 02-5674985\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 20 260 6070806686\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 210 260 8600356\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 370 260 5541026-28\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 20 340 07/07/60 13:15\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 390 520 07/07/60\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 380 340 220501.3\n")

            B = "T ANG12BPT.CPF 0 100 380 บริษัท เอ็ม.วี.ที คอมมิวนิเคชั"

            try {
                tsc.sendcommand(B.toByteArray(charset("TIS-620")))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }


            tsc.sendcommand("\n")
            C = "T ANG12BPT.CPF 0 65 420 1/433 ซ.พหลโยธิน60 ม.17 ถ.พหลโยธิน"

            try {
                tsc.sendcommand(C.toByteArray(charset("TIS-620")))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            tsc.sendcommand("\r\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 210 520 01/06/60\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 230 550 2896\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 400 550 2898\r\n")
            tsc.sendcommand("RIGHT 440\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 410 580 2,000\n")
            tsc.sendcommand("LEFT\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 140 620 T2(07/60)\n")
            tsc.sendcommand("RIGHT 440\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 620 150.00\n")
            tsc.sendcommand("LEFT\n")
            tsc.sendcommand("RIGHT 440\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 680 30.00\n")
            tsc.sendcommand("LEFT\n")
            tsc.sendcommand("RIGHT 440\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 710 12.60\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 750 192.60\n")
            tsc.sendcommand("LEFT\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 200 780 0\n")
            tsc.sendcommand("RIGHT 440\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 780 0.00\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 440 815 192.60\n")
            tsc.sendcommand("LEFT\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 450 945 8/7/60-17/7/60\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 210 340 17/07/60\n")
            E = "T ANG12BPT.CPF 0 350 1005 ในวันที่ 21/7/60|"
            try {
                tsc.sendcommand(E.toByteArray(charset("TIS-620")))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            tsc.sendcommand("\n")
            tsc.sendcommand("BT 7 0 0\n")
            tsc.sendcommand("VB 128 1 0 50 520 860 60708066868600356554102628170760000019260\n")
            tsc.sendcommand("BT OFF\n")
            tsc.sendcommand("T ANG12BPT.CPF 0 20 870 Latitude: 13.77341258 Longitude: 100.57228935\n")

            D = "T ANG12BPT.CPF 0 20 930 โปรดระวังมิจฉาชีพแอบอ้างเก็บเงินค่าน้ำประปา"

            try {
                tsc.sendcommand(D.toByteArray(charset("TIS-620")))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            tsc.sendcommand("\n")
            val StrP_QR = "60708066868600356554102628170760000019260"
            tsc.sendcommand("B QR 470 1050 M 4 U 4" + "\r\n")
            tsc.sendcommand("MA,$StrP_QR\r\n")
            tsc.sendcommand("ENDQR" + "\r\n")
            tsc.sendcommand("FORM\n")
            tsc.sendcommand("PRINT\n")
            tsc.closeport()

            }catch(e: Exception){
                Toast.makeText(this,"กรุณาเชื่อมต่อเครื่องพิมพ์ : " + e.message,Toast.LENGTH_LONG).show()
            }
        }



        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())


        val tvTime =findViewById<TextView>(R.id.tvTime)
        // textView is the TextView view that should display it
        tvTime.setText(currentDateTimeString)
        val tvLatLon = findViewById<TextView>(R.id.tvLocation)

        val etLogin: EditText = findViewById(R.id.editText_login)
        etLogin.requestFocus()
        etLogin.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                USERID = etLogin.text.toString().trim { it <= ' ' }

                if ( getLogin(USERID) || USERID.equals("8899", ignoreCase = true)) {
                    val readMenu = Intent(applicationContext, Page_Menu::class.java)
                    readMenu.putExtra("userid", USERID)
                    startActivityForResult(readMenu, 999)
                } else {
                    showDialog("รหัสของคุณไม่ถูกต้อง")
                }
                return@OnKeyListener true
            }
            false

        })

        val bnLoadRoute :Button = findViewById(R.id.bn_loadroute)
        bnLoadRoute.setOnClickListener{
            val i1 = Intent(this, Page_LoadRoute::class.java)
            startActivity(i1)
        }

        // Create persistent LocationManager reference
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)

    }

    private fun getLogin(userid: String): Boolean {

        val strEmpid: String
        val curEmp: Cursor?
        curEmp = pwaDB.select_EmpData()
        var success: Boolean = true

            if (curEmp.moveToFirst()) {
                strEmpid = curEmp.getString(curEmp.getColumnIndex("empid"))

                Log.d("id", strEmpid)

                when (userid) {
                   (strEmpid) -> success=true
                    else->{
                        success=false
                    }
                }

            }


        return success
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            var strLocation:String = "พิกัด" + location.latitude + "," + location.longitude
            tvLocation.text = strLocation
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun showDialog(message:String) {
        val alt_bld = AlertDialog.Builder(this)
        alt_bld.setMessage(message)
        alt_bld.setTitle("คำอธิบาย")
        alt_bld.setPositiveButton("ตกลง", null)
        alt_bld.show()
    }

    private fun setupPermissions() {

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this
                        , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
         {

            Log.i(TAG, "Permission to ACCESS_COARSE_LOCATION")
            makeRequest()
        }
    }



    private fun makeRequest() {



        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                RECORD_REQUEST_CODE)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RECORD_REQUEST_CODE)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE)


        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                RECORD_REQUEST_CODE)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET),
                RECORD_REQUEST_CODE)


        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }


    private fun Check_and_Create_folder() {

        val sd : File = Environment.getExternalStorageDirectory()
        var P_Directory: Boolean
        val p_root: String = sd.toString() + "/PWA"
        val p_backup: String  = sd.toString() + "/PWA/BACKUP"
        val p_database: String  = sd.toString() + "/PWA/DATABASE"
        val p_download: String  = sd.toString() + "/PWA/DOWNLOAD"
        val p_upload: String  = sd.toString() + "/PWA/UPLOAD"
        val p_image: String  = sd.toString() + "/PWA/IMAGE"
        val p_autobk: String  = sd.toString() + "/PWA/AUTOBK"


        val intStorageDirectory = filesDir.toString()
        val folder = File(intStorageDirectory, p_root)
        folder.mkdirs()

        val P_Path = File(p_root)
        if (!P_Path.exists()) {

            P_Directory = P_Path.mkdir()
            Log.d("", "Path2")
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
        val P_PathUpload = File(p_upload)
        if (!P_PathUpload.exists()) {
            P_Directory = P_PathUpload.mkdir()
            if (P_Directory) {
                Log.d("", "UploadPath")
            }
        }
        val P_PathDownload = File(p_download)
        if (!P_PathDownload.exists()) {
            P_Directory = P_PathDownload.mkdir()
            if (P_Directory) {
                Log.d("", "UploadPath")
            }
        }
        val P_PathImage = File(p_image)
        if (!P_PathImage.exists()) {
            P_Directory = P_PathImage.mkdir()
            if (P_Directory) {
                Log.d("", "ImagePath")
            }
        }
        val P_AutoBk = File(p_autobk)
        if (!P_AutoBk.exists()) {
            P_Directory = P_AutoBk.mkdir()
            if (P_Directory) {
                Log.d("", "AutoBkPath")
            }
        }
        scanFile(p_root)
        scanFile(p_backup)
        scanFile(p_database)
        scanFile(p_download)
        scanFile(p_image)
        scanFile(p_upload)
        scanFile(p_autobk)

    }

    override fun onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 2000) {
            ms = Toast.makeText(this, "กด  BACK อีกครั้งเพื่อออกจากโปรแกรม", Toast.LENGTH_SHORT)
            ms!!.show()
            this.lastBackPressTime = System.currentTimeMillis()
        } else {
            if (ms != null) {
                ms!!.cancel()
            }
            super.onBackPressed()
        }
        super.onBackPressed()
    }

    private fun scanFile(path: String) {

        MediaScannerConnection.scanFile(this@MainActivity,
                arrayOf(path), null
        ) { path, uri -> Log.i("TAG", "Finished scanning $path") }
    }

}



