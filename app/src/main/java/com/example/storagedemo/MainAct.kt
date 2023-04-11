package com.example.storagedemo

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainAct : AppCompatActivity() {
    private val requestCodePermission: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        initViews()

        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Grant Permission Success", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                ), requestCodePermission)
        }
    }

    private fun initViews() {
        findViewById<TextView>(R.id.tvTaskImage).setOnClickListener {
            startActivity(Intent(this, ImageAct::class.java))
        }

        findViewById<TextView>(R.id.tvTaskDocument).setOnClickListener {
            startActivity(Intent(this, DocumentAct::class.java))
        }
    }
}