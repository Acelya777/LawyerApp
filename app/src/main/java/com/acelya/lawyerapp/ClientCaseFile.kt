package com.acelya.lawyerapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acelya.lawyerapp.adapter.PdfFileAdapter
import com.acelya.lawyerapp.models.CaseFile
import com.acelya.lawyerapp.models.Client
import com.acelya.lawyerapp.models.pdfFile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.HashMap


class ClientCaseFile : AppCompatActivity() {
    private var pdfUri: Uri? = null
    private var pdfFileData: HashMap<String, String>? = null
    val pdfId = FirebaseDatabase.getInstance().reference.push().key ?: ""
    private val pdfFiles = mutableListOf<pdfFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_case_file)

        val clientNameTV = findViewById<TextView>(R.id.tvNameClientCase)
        val caseTypeTV = findViewById<TextView>(R.id.tvCaseTypeClientCase)
        val clientPhoneTV = findViewById<TextView>(R.id.tvPhoneClientCase)
        val statusTV = findViewById<TextView>(R.id.tvStatusClientCase)
        val clientEmailTV = findViewById<TextView>(R.id.tvEmailClientCase)
        val clientAddressTV = findViewById<TextView>(R.id.tvAddressClientCase)
        val clientInfo = findViewById<TextView>(R.id.tvCaseInfoClientCase)
        val addFileBtn = findViewById<Button>(R.id.btnNewFileClientCase)
        val saveFileBtn = findViewById<Button>(R.id.btnSaveClientCase)

        val caseId = intent.getStringExtra("caseId") ?: return

        val localActivity = "Kullanıcı Dosyası"
        val name = intent.getStringExtra("name")
        val surname = intent.getStringExtra("surname")
        val lawyerId = intent.getStringExtra("lawyerId")
        val clientName = intent.getStringExtra("clientName")
        val clientSurname = intent.getStringExtra("clientSurname")
        val clientPhone = intent.getStringExtra("clientPhone")
        val clientEmail = intent.getStringExtra("clientEmail")
        val clientAddress = intent.getStringExtra("clientAddress")



        clientNameTV.text = "$clientName $clientSurname"
        clientEmailTV.text = "Email : $clientEmail"
        clientAddressTV.text = "Adres : $clientAddress"
        clientPhoneTV.text = "Telefon Numarası : $clientPhone"

        addFileBtn.setOnClickListener {
            selectPdfFile()
        }

        saveFileBtn.setOnClickListener {
            savePdfFileData(caseId)
        }

        //ToolBarFragment toolbar başlıkları gönderme
        if (savedInstanceState == null) {
            val fragment = ToolbarFragment()
            val bundle = Bundle()
            bundle.putString("name", name)
            bundle.putString("surname", surname)
            bundle.putString("locatedActivity",localActivity)
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        val db = FirebaseDatabase.getInstance().getReference("ClientCaseFileTable")
        db.child(caseId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val case = snapshot.getValue(CaseFile::class.java)

                    val caseType = snapshot.child("caseType").getValue(String::class.java)
                    val status = snapshot.child("status").getValue(String::class.java)
                    val caseName = snapshot.child("caseName").getValue(String::class.java)
                    val notes = snapshot.child("notes").getValue(String::class.java)
                    val startDate = snapshot.child("startDate").getValue(String::class.java)
                    val endDate = snapshot.child("endDate").getValue(String::class.java)
                    val tempList = snapshot.child("pdfFile").children.mapNotNull {
                        val data = it.getValue(pdfFile::class.java)
                        Log.d("FirebaseData", "Çekilen Veri: $data")
                        data
                    }

                    pdfFiles.clear()
                    pdfFiles.addAll(tempList)

                    val rvCaseFiles = findViewById<RecyclerView>(R.id.recyclerViewCaseFile)
                    rvCaseFiles.layoutManager = LinearLayoutManager(this@ClientCaseFile)

                    if (rvCaseFiles.adapter == null) {
                        val pdfAdapter = PdfFileAdapter(pdfFiles, this@ClientCaseFile,caseId)
                        rvCaseFiles.adapter = pdfAdapter
                    } else {
                        rvCaseFiles.adapter?.notifyDataSetChanged()
                    }


//                    if (pdfFiles.isNotEmpty()) {
//                        val fileNames = pdfFiles.joinToString(separator = "\n") { it.fileName ?: "Bilinmeyen Dosya" }
//                        caseTypeTV.text = fileNames
//                    } else {
//                        caseTypeTV.text = "PDF bulunamadı"
//                    }
                    if (status?.equals("active", ignoreCase = true) == true) {
                        statusTV.text = "Aktif"
                        statusTV.setTextColor(ContextCompat.getColor(this@ClientCaseFile, R.color.active_case))
                    } else if (status?.equals("passive", ignoreCase = true) == true) {
                        statusTV.text = "Pasif"
                        statusTV.setTextColor(ContextCompat.getColor(this@ClientCaseFile, R.color.maroon))
                    }

                    //caseTypeTV.text = caseType
                    clientInfo.text = "Dava Adı : $caseName \nDava Notu : $notes \nBaşlangıç Tarihi : $startDate \nBitiş Tarihi : $endDate"


                } else {
                    Toast.makeText(this@ClientCaseFile, "Dava bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClientCaseFile, "Veri alınırken hata oluştu.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 📌 URI'den Dosya Adını Alma
     */
    private fun getFileName(uri: Uri): String {
        var fileName = "unknown.pdf"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    /**
     * 📌 PDF'yi Base64 Formatına Çevirme
     */
    private fun convertPdfToBase64(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int

        while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    // PDF dosyası seçme ve işlemler
    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, 1001)
    }

    // Veriyi Firebase'e kaydetme işlemi
    private fun savePdfFileData(caseId: String) {
        if (pdfUri == null) {
            Toast.makeText(this, "Lütfen bir PDF dosyası seçin.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = getFileName(pdfUri!!)
        val pdfBase64 = convertPdfToBase64(pdfUri!!)

        pdfFileData = hashMapOf(
            "pdfId" to pdfId,
            "base64Data" to pdfBase64,
            "fileName" to fileName,
            "fileType" to "application/pdf"
        )

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("ClientCaseFileTable")
        val caseReference = reference.child(caseId).child("pdfFile")

        // PDF verilerini Firebase'e kaydediyoruz
        caseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Eğer pdfFile altında mevcut veriler varsa, onları alıyoruz
                val existingPdfFiles = snapshot.value as? HashMap<String, Any> ?: HashMap()

                // Yeni veriyi mevcut verilere ekliyoruz
                existingPdfFiles[pdfId] = pdfFileData!!

                // Güncellenmiş veriyi tekrar Firebase'e kaydediyoruz
                caseReference.setValue(existingPdfFiles)
                    .addOnSuccessListener {
                        Toast.makeText(this@ClientCaseFile, "Dava başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@ClientCaseFile, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClientCaseFile, "Veri alınırken hata oluştu.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                pdfUri = uri
                val fileName = getFileName(uri)
                Toast.makeText(this, "PDF Seçildi: $fileName", Toast.LENGTH_SHORT).show()
            }
        }
    }
}