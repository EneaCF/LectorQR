//https://github.com/yuriy-budiyev/code-scanner

package com.example.lectorqr

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private val PERMISSION_REQUEST_CODE: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        //icono action bar, uff que mal...
        getSupportActionBar()!!.setHomeButtonEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true);
        getSupportActionBar()!!.setDisplayUseLogoEnabled(true);
        getSupportActionBar()!!.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false); //optional

        //ocultar action bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        */

        if(checkPersmission()){
            //Toast.makeText(this, "entra if", Toast.LENGTH_SHORT).show()
            scan()
        }else{
            //Toast.makeText(this, "entra else", Toast.LENGTH_SHORT).show()
            requestPermission()
        }
    }
    private fun scan(){
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val scannerText = findViewById<TextView>(R.id.scanner_text)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // SAFE or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // SINGLE or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                //Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                //Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();

                //println(it.barcodeFormat)

                scannerText.setText(it.getText())
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                        Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED)
                // && ContextCompat.checkSelfPermission(this,
                //android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        //ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA), PERMISSION_REQUEST_CODE)
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA),
                PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //&&grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    //scan()
                    return
                } else {
                    Toast.makeText(this, "Permisos denegados. La app no puede funcionar", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            codeScanner.startPreview()
        }catch (e: Exception){
            Toast.makeText(this, "Error $e", android.widget.Toast.LENGTH_SHORT).show()
        }
        //codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}