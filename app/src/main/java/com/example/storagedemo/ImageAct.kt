package com.example.storagedemo

import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ImageAct: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_image)

        initViews()
    }

    private fun initViews() {

        findViewById<TextView>(R.id.tvCreateFolder).setOnClickListener {
            handleCreateFolder()
        }

    }

    private fun handleCreateFolder() {
        // create Folder in Environment Documents
        val pathNameFolder = "ManchesterUnited"
        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path+"/"+pathNameFolder)

        if(folder.exists()){
            Toast.makeText(this, "Folder Exists", Toast.LENGTH_SHORT).show()
        }else{
            folder.mkdir()
            Toast.makeText(this, "Folder Not Exists", Toast.LENGTH_SHORT).show()
        }
    }
}