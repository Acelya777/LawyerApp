package com.acelya.lawyerapp

import android.os.Bundle
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
        val caseId = intent.getStringExtra("caseId") ?: return

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