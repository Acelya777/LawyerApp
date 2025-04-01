package com.acelya.lawyerapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.acelya.lawyerapp.R
import com.acelya.lawyerapp.models.Client
import com.acelya.lawyerapp.models.pdfFile
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream

class PdfFileAdapter(private val pdfList: MutableList<pdfFile>, private val context: Context,private val caseId: String) :
    RecyclerView.Adapter<PdfFileAdapter.PdfViewHolder>() {

    class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTV: TextView = itemView.findViewById(R.id.tvCaseFileClientCase)
        val deleteIV: ImageView = itemView.findViewById(R.id.ivCaseFileDeleteClientCase)
        val downloadIV: ImageView = itemView.findViewById(R.id.ivCaseFileDownloadClientCase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_file, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val pdfFile = pdfList[position]

        holder.fileNameTV.text = pdfFile.fileName ?: "Bilinmeyen Dosya"
        //Toast.makeText(holder.itemView.context, "${pdfFile.fileType} ${pdfFile.base64Data}" ?: "Dosya adı yok", Toast.LENGTH_SHORT).show()


        // İndirme butonuna tıklanınca dosyayı indir
        holder.downloadIV.setOnClickListener {
            val base64Data = pdfFile.base64Data
            val fileName = pdfFile.fileName
            val fileType = pdfFile.fileType


            if (!base64Data.isNullOrEmpty() && !fileName.isNullOrEmpty() && !fileType.isNullOrEmpty()) {
                downloadPdf(base64Data, fileName, fileType, context)
            } else {
                Toast.makeText(context, "PDF indirilemedi: Eksik veri", Toast.LENGTH_SHORT).show()
            }
        }

        // Silme işlemi (Firebase'den kaldırma)
        holder.deleteIV.setOnClickListener {
            pdfFile.pdfId.let { pdfId ->
                val dbRef = FirebaseDatabase.getInstance()
                    .getReference("ClientCaseFileTable")
                    .child(caseId)
                    .child("pdfFile")
                    .child(pdfId) // pdfId'nin doğru eklendiğinden emin ol!

                dbRef.removeValue()
                    .addOnSuccessListener {
                        pdfList.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "$pdfId dosyası başarıyla silindi.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Silme işlemi başarısız: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    override fun getItemCount(): Int = pdfList.size

    private fun downloadPdf(base64Data: String, fileName: String, fileType: String, context: Context) {
        try {
            val pdfBytes = Base64.decode(base64Data, Base64.DEFAULT)
            val file = File(context.getExternalFilesDir(null), fileName)

            FileOutputStream(file).use { it.write(pdfBytes) }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)

            Toast.makeText(context, "PDF başarıyla indirildi.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "PDF indirilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("pdfFile",e.toString())
        }
    }
}
