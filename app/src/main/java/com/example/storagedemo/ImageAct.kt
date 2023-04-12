package com.example.storagedemo

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
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
    private lateinit var imvGetSingleImageUsingPhotoPicker: ImageView
    private lateinit var imvGetMultipleImageUsingPhotoPickerOne: ImageView
    private lateinit var imvGetMultipleImageUsingPhotoPickerTwo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_image)

        initViews()
    }

    private fun initViews() {

        imvCaptureImage = findViewById(R.id.imvCaptureImage)
        imvGetSingleImageUsingMediaStore = findViewById(R.id.imvGetSingleImageUsingMediaStore)
        imvGetSingleImageUsingPhotoPicker = findViewById(R.id.imvGetSingleImageUsingPhotoPicker)
        imvGetMultipleImageUsingPhotoPickerOne = findViewById(R.id.imvGetMultipleImageUsingPhotoPickerOne)
        imvGetMultipleImageUsingPhotoPickerTwo = findViewById(R.id.imvGetMultipleImageUsingPhotoPickerTwo)

        // Registers a photo picker activity launcher in single-select mode.
        // Not working when pickMedia in OnClickListener because happen error: LifecycleOwners must call register before they are STARTED
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("Logger", "Selected URI: $uri")
                imvGetSingleImageUsingPhotoPicker.setImageURI(uri)
            } else {
                Log.d("Logger", "No media selected")
            }
        }

        // Registers a photo picker activity launcher in multi-select mode.
        // In this example, the app allows the user to select up to 5 media files.
        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                // Callback is invoked after the user selects media items or closes the
                // photo picker.
                if (uris.isNotEmpty()) {
                    Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
                    imvGetMultipleImageUsingPhotoPickerOne.setImageURI(uris[0])
                    imvGetMultipleImageUsingPhotoPickerTwo.setImageURI(uris[1])
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        findViewById<TextView>(R.id.tvCreateFolder).setOnClickListener {
            handleCreateFolder()
        }

        findViewById<TextView>(R.id.tvCaptureImage).setOnClickListener {
            handleCaptureImage()
        }


        findViewById<TextView>(R.id.tvGetSingleImageUsingMediaStore).setOnClickListener {
            handleGetImageUsingMediaStore()
        }

        findViewById<TextView>(R.id.tvGetSingleImageUsingPhotoPicker).setOnClickListener {
            handleGetSingleImageUsingPhotoPicker(pickMedia)
        }

        findViewById<TextView>(R.id.tvGetMultipleImageUsingPhotoPicker).setOnClickListener {
            handleGetMultipleImageUsingPhotoPicker(pickMultipleMedia)
        }

        findViewById<TextView>(R.id.tvDownloadImage).setOnClickListener {
            handleDownloadImage()
        }

    }

    private fun handleDownloadImage() {
        // require URL of Image & using DownloadManager 
        val imageUrl = "https://play-lh.googleusercontent.com/WNYUy5Hlma6DgvtP7NJEWtQIT0nEyYbfuLWiU8ZAr7O5IEa_R50_jVtOtknvl4cHJSk"

        val request = DownloadManager.Request(Uri.parse(imageUrl))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "myImage.jpg")
            .setTitle("Downloading image")
            .setDescription("Please wait...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setMimeType("image/jpeg")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

    }

    private fun handleGetMultipleImageUsingPhotoPicker(pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun handleGetSingleImageUsingPhotoPicker(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        // Launch the photo picker and allow the user to choose images and videos.
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
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
            Log.d("Logger", result.data!!.data.toString())
            imvGetSingleImageUsingMediaStore.setImageURI(result.data!!.data)
        }
    }
}