package com.acelya.lawyerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.petitions.CasePetitions

class ContratManagement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contrat_management)

        val firstTemplate = findViewById<CardView>(R.id.caseManagementFirstTemplate)

        val localActivity = "Sözleşme Yönetimi"
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

        firstTemplate.setOnClickListener {
            val intent = Intent(this@ContratManagement, CasePetitions::class.java)
            intent.putExtra("lawyerId",lawyerId)
            intent.putExtra("name",name)
            intent.putExtra("surname",surname)
            startActivity(intent)
        }


    }
}