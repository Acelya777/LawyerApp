package com.acelya.lawyerapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.models.CaseFile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ClientCaseFile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_case_file)

        val clientName = findViewById<TextView>(R.id.tvNameClientCase)
        val caseType = findViewById<TextView>(R.id.tvCaseTypeClientCase)
        val clientPhone = findViewById<TextView>(R.id.tvPhoneClientCase)
        val status = findViewById<TextView>(R.id.tvStatusClientCase)
        val clientEmail = findViewById<TextView>(R.id.tvEmailClientCase)
        val clientAddress = findViewById<TextView>(R.id.tvAddressClientCase)
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

                    case?.let {

                    }
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