package com.acelya.lawyerapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acelya.lawyerapp.models.Client
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CaseManagement : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var clientAdapter: ClientAdapter
    private val clientList = mutableListOf<Client>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_case_management)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        clientAdapter = ClientAdapter(clientList)
        recyclerView.adapter = clientAdapter

        fetchClients()

    }
    private fun fetchClients() {
        val db = FirebaseDatabase.getInstance().getReference("ClientTable")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<Client>() // Yeni liste oluşturuyoruz
                for (clientSnapshot in snapshot.children) {
                    val client = clientSnapshot.getValue(Client::class.java)
                    client?.let { newList.add(it) } // Null değilse listeye ekle
                }

                // Eğer yeni liste boş değilse adapter'ı güncelle
                if (newList.isNotEmpty()) {
                    clientAdapter.updateData(newList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CaseManagement, "Veri Alınamadı!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}