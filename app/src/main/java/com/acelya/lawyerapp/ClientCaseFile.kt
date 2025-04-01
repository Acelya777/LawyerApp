package com.acelya.lawyerapp

import android.os.Bundle
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


class ClientCaseFile : AppCompatActivity() {

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
        val lineCaseFile = findViewById<LinearLayout>(R.id.lineCaseFileClientCase)
        val fileDelete = findViewById<ImageView>(R.id.ivCaseFileDeleteClientCase)
        val fileDownload = findViewById<ImageView>(R.id.ivCaseFileDownloadClientCase)
        val btnNewFile = findViewById<Button>(R.id.btnNewFileClientCase)
        val btnAddInterView = findViewById<Button>(R.id.btnAddInterViewDate)
        val btnSaveClient = findViewById<Button>(R.id.btnSaveClientCase)





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

}