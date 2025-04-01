package com.acelya.lawyerapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acelya.lawyerapp.adapter.ClientAdapter
import com.acelya.lawyerapp.models.Client
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CaseManagement : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clientAdapter: ClientAdapter
    private val clientList = mutableListOf<Client>()
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_case_management)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Intent'ten gelen name değerini alıyoruz
        val localActivity = "Dosya Yönetimi"
        val name = intent.getStringExtra("name")
        val surname = intent.getStringExtra("surname")
        val lawyerId = intent.getStringExtra("lawyerId")

        drawerLayout = findViewById(R.id.drawerLayoutCase)
        clientAdapter = ClientAdapter(clientList,lawyerId,name,surname)
        recyclerView.adapter = clientAdapter

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

    fun openDrawer(){
        drawerLayout.openDrawer(GravityCompat.END)
    }
}