package com.example.storagedemo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageAct : AppCompatActivity() {
    private lateinit var imvCaptureImage: ImageView
    private lateinit var imvGetSingleImageUsingMediaStore: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_image)

        initViews()
    }

    private fun initViews() {

        imvCaptureImage = findViewById(R.id.imvCaptureImage)
        imvGetSingleImageUsingMediaStore = findViewById(R.id.imvGetSingleImageUsingMediaStore)

        findViewById<TextView>(R.id.tvCreateFolder).setOnClickListener {
            handleCreateFolder()
        }

        findViewById<TextView>(R.id.tvCaptureImage).setOnClickListener {
            handleCaptureImage()
        }


        findViewById<TextView>(R.id.tvGetSingleImageUsingMediaStore).setOnClickListener {
            handleGetImageUsingMediaStore()
        }

    }

    private fun handleGetImageUsingMediaStore() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startForResultGetImageUsingMediaStore.launch(intent)
    }


    private fun handleCaptureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForResultCaptureImage.launch(cameraIntent)
    }

    private fun handleCreateFolder() {
        // create Folder in Environment Documents
        val pathNameFolder = "ManchesterUnited"
        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/" + pathNameFolder)

        if (folder.exists()) {
            Toast.makeText(this, "Folder Exists", Toast.LENGTH_SHORT).show()
        } else {
            folder.mkdir()
            Toast.makeText(this, "Folder Not Exists", Toast.LENGTH_SHORT).show()
        }
    }

    private val startForResultCaptureImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        //  you will get result here in result.data
        if (result.resultCode == Activity.RESULT_OK) {

            // save External Storage
            val pathNameFolder = "ManchesterUnited"
            val nameImage = "my_image.jpg"
            val file = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).path+"/${pathNameFolder}", nameImage)
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            val outputStream: OutputStream = FileOutputStream(file)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Show image in app use Bitmap
            // imvCaptureImage.setImageBitmap(imageBitmap)

            // Show image in app use Uri
            Log.e("Logger", Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).path+"/${pathNameFolder}/${nameImage}")
            imvCaptureImage.setImageURI(Uri.parse(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).path+"/${pathNameFolder}/${nameImage}"))
        }
    }

    private val startForResultGetImageUsingMediaStore = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        //  you will get result here in result.data
        if (result.resultCode == Activity.RESULT_OK) {
            imvGetSingleImageUsingMediaStore.setImageURI(result.data!!.data)
        }
    }
}