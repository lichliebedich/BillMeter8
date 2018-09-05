package com.siamsr.billmeter8

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

class bluetoothCommandService(context: Context, private val mHandler: Handler)  {

    companion object {
        // Debugging
        //private static final String TAG = "BluetoothCommandService";
        private val D = true

        // Unique UUID for this application
        //private static final UUID MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        //    private BluetoothDevice mSavedDevice;
        //    private int mConnectionLostCount;

        // Constants that indicate the current connection state
        val STATE_NONE = 0       // we're doing nothing
        val STATE_LISTEN = 1     // now listening for incoming connections
        val STATE_CONNECTING = 2 // now initiating an outgoing connection
        val STATE_CONNECTED = 3  // now connected to a remote device

        // Constants that indicate command to computer
        val EXIT_CMD = -1
        val VOL_UP = 1
        val VOL_DOWN = 2
        val MOUSE_MOVE = 3


        private var mThreadconnect_1: Threadconnect_1? = null
        private var mThreadconnect_2: Threadconnect_2? = null
        private var mState: Int = 0
    }

    // Member fields
    private val mAdapter: BluetoothAdapter
    // Give the new state to the Handler so the UI Activity can update
    //   mHandler.obtainMessage(RemoteBluetooth.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    var state: Int
        @Synchronized get() = mState
        @Synchronized private set(state) {
            if (D) Log.d("setState :", mState.toString() + " -> " + state)
            mState = state
        }


    init {
        mAdapter = BluetoothAdapter.getDefaultAdapter()
        mState = STATE_NONE
    }//mConnectionLostCount = 0;

    @Synchronized
    fun start() {
        if (D) Log.d("start Bluetooth", "start")

        // Cancel any thread attempting to make a connection
        if (mThreadconnect_1 != null) {
            mThreadconnect_1!!.cancel_1()
            mThreadconnect_1 = null
        }

        // Cancel any thread currently running a connection
        if (mThreadconnect_2 != null) {
            mThreadconnect_2!!.cancel_2()
            mThreadconnect_2 = null
        }

        state = STATE_LISTEN
    }

    @Synchronized
    fun connect_1(device: BluetoothDevice) {
        if (D) Log.d("connect_1 to: ", device.toString() + "")

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mThreadconnect_1 != null) {
                mThreadconnect_1!!.cancel_1()
                mThreadconnect_1 = null
            }
        }

        // Cancel any thread currently running a connection
        if (mThreadconnect_2 != null) {
            mThreadconnect_2!!.cancel_2()
            mThreadconnect_2 = null
        }

        // Start the thread to connect with the given device
        mThreadconnect_1 = Threadconnect_1(device)
        mThreadconnect_1!!.start()
        state = STATE_CONNECTING
    }

    @Synchronized
    fun connect_2(socket: BluetoothSocket, device: BluetoothDevice) {
        if (D) Log.d("connect_2-->>", "BluetoothSocket:" + socket + "device:" + device)

        // Cancel the thread that completed the connection
        if (mThreadconnect_1 != null) {
            mThreadconnect_1!!.cancel_1()
            mThreadconnect_1 = null
        }

        // Cancel any thread currently running a connection
        if (mThreadconnect_2 != null) {
            mThreadconnect_2!!.cancel_2()
            mThreadconnect_2 = null
        }

        // Start the thread to manage the connection and perform transmissions
        mThreadconnect_2 = Threadconnect_2(socket)
        mThreadconnect_2!!.start()



        state = STATE_CONNECTED
    }

    /**
     * Stop all threads
     */

    @Synchronized
    fun stop() {
        if (D) Log.d("stop:", "D:$D")
        if (mThreadconnect_1 != null) {
            mThreadconnect_1!!.cancel_1()
            mThreadconnect_1 = null
        }
        if (mThreadconnect_2 != null) {
            mThreadconnect_2!!.cancel_2()
            mThreadconnect_2 = null
        }

        state = STATE_NONE
    }

    fun write(out: ByteArray) {
        // Create temporary object
        var r: Threadconnect_2? = null
        // Synchronize a copy of the Threadconnect_2
        synchronized(this) {
            if (mState != STATE_CONNECTED) return
            r = mThreadconnect_2
        }
        // Perform the write unsynchronized
        r!!.write(out)
    }

    fun write(out: Int) {
        // Create temporary object
        var r: Threadconnect_2? = null
        // Synchronize a copy of the Threadconnect_2
        synchronized(this) {
            if (mState != STATE_CONNECTED) return
            r = mThreadconnect_2
        }
        // Perform the write unsynchronized
        r!!.write(out)
    }

    fun write(out: String) {
        // Create temporary object
        var r: Threadconnect_2? = null
        // Synchronize a copy of the Threadconnect_2
        synchronized(this) {
            if (mState != STATE_CONNECTED) return
            r = mThreadconnect_2
        }
        // Perform the write unsynchronized
        r!!.write(out)
    }


    private fun connectionFailed() {
        state = STATE_LISTEN

    }

    private fun connectionLost() {
        state = STATE_LISTEN

    }


    private inner class Threadconnect_1(private val mmDevice: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket?

        init {
            Log.d("create Threadconnect_1", mmDevice.toString() + "")
            var tmp: BluetoothSocket? = null
            var m: Method? = null
            try {
                m = mmDevice.javaClass.getMethod("createRfcommSocket", *arrayOf<Class<*>>(Int::class.javaPrimitiveType!!))
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }

            try {
                tmp = m!!.invoke(mmDevice, 1) as BluetoothSocket
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

            mmSocket = tmp
        }

        override fun run() {
            Log.i("BEGIN run mThreadconne1", "")
            name = "Threadconnect_1"
            mAdapter.cancelDiscovery()
            try {
                mmSocket!!.connect()
            } catch (e: IOException) {

                connectionFailed()

                try {
                    mmSocket!!.close()
                } catch (e2: IOException) {
                    Log.e("Bluetooth_1:", "unable to close() socket during connection failure", e2)
                }

                this@bluetoothCommandService.start()
                return
            }
            //catch

            // Reset the Threadconnect_1 because we're done
            synchronized(this@bluetoothCommandService) {
                mThreadconnect_1 = null
            }

            // Start the connect_2 thread
            connect_2(mmSocket, mmDevice)
        }

        fun cancel_1() {
            try {
                Log.i("Fn cancel_1 Threadconn1", "mmSocket.close")
                mmSocket!!.close()
            } catch (e: IOException) {
                Log.e("cancel_1 Bluetooth:", "close() of connect socket failed", e)
            }

        }
    }


    private inner class Threadconnect_2(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        init {
            Log.d("create Threadconnect_2", "")
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {
                Log.e("Threadconnect_2:", "temp sockets not created", e)
            }

            mmInStream = tmpIn
            mmOutStream = tmpOut
        }

        override fun run() {
            Log.i("BEGIN run mThreadconn2", "")
            val buffer = ByteArray(1024)

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    val bytes = mmInStream!!.read(buffer)
                } catch (e: IOException) {
                    Log.e("Bluetooth_2:", "disconnected", e)
                    connectionLost()
                    break
                }

            }//while
        }

        fun write(buffer: ByteArray) {
            try {
                Log.e("Fn write byte Threadco2", buffer.toString() + "|")
                mmOutStream!!.write(buffer)
            } catch (e: IOException) {
                Log.e("Error write byte:", "Exception during write", e)
            }

        }

        fun write(out: Int) {
            try {
                Log.e("write int Threadco2", out.toString() + "|")
                mmOutStream!!.write(out)
            } catch (e: IOException) {
                Log.e("Error write int:", "Exception during write", e)
            }

        }


        /* public void write(String message) {
        	try {
        			int code;
        			for(int i = 0; i < message.length(); i++) {
        			code = (int)message.charAt(i);
        			if ((0xE01<=code) && (code <= 0xE5B ))
        				mmOutStream.write((char)(code - 0xD60));
        			else
        				mmOutStream.write(code);
        			}
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }*/
        fun write(message: String) {
            try {
                Log.e("Fn write String Thre2", message)
                val largemessage = message.toByteArray(charset("TIS-620"))
                mmOutStream!!.write(largemessage)

                //Log.w("largemessage:", largemessage[largemessage.length]+"/");
            } catch (e: IOException) {
                Log.e("Error write String:", "Exception during write", e)
            }

        }

        fun cancel_2() {
            try {
                Log.e("Fn cancel_2 Threadc2", "mmSocket.close")
                //mmOutStream.write(EXIT_CMD);
                mmSocket.close()
            } catch (e: IOException) {
                Log.e("cancel_2 Bluetooth:", "close() of connect socket failed", e)
            }

        }
    }


}
