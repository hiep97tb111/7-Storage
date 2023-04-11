package com.example.storagedemo

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class DocumentAct: AppCompatActivity() {
    // Define Directory Save File In Storage
    private lateinit var pdfUri: Uri
    companion object {
        const val CREATE_FILE = 1
        const val OPEN_PDF_FILE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_document)

        findViewById<TextView>(R.id.tvCreateFile).setOnClickListener {
            handleCreateFilePDF()
        }

        findViewById<TextView>(R.id.tvOpenFile).setOnClickListener {
            handleOpenFilePDF()
        }
    }

    private fun handleOpenFilePDF() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, OPEN_PDF_FILE)
    }

    private fun handleCreateFilePDF() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "my_document.pdf")
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    private fun writePDFContent(uri: Uri){
        val outputStream = contentResolver.openOutputStream(uri)
        val pdfDocument = PdfDocument()
        val pageInfo = PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        paint.color = Color.BLACK
        canvas.drawText("Hello, I am Developer Mobile", 80f, 50f, paint)
        pdfDocument.finishPage(page)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        outputStream!!.close()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CREATE_FILE && resultCode == RESULT_OK){
            // Result directory save file is pdfUri
            pdfUri = data!!.data!!
            Log.e("Logger pdfUri", pdfUri.toString())
            try {
                // Show Content: Hello, I am Developer Mobile
                writePDFContent(pdfUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else if(requestCode == OPEN_PDF_FILE && resultCode == RESULT_OK){
            val uri = data!!.data
            val inputStream = contentResolver.openInputStream(uri!!)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8")) // specify UTF-8 encoding
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                 stringBuilder.append(line)
                line = bufferedReader.readLine()
            }
            inputStream?.close()
            val documentContents = stringBuilder.toString()
            Log.e("Logger DocumentContents", documentContents)
        }
    }
}