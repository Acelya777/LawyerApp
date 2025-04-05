package com.acelya.lawyerapp.petitions

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.R
import com.acelya.lawyerapp.ToolbarFragment

class CasePetitions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_case_petitions)
        val divorceBtn = findViewById<Button>(R.id.casePetitionsDivorceBtn)

        val localActivity = "Dava Dilekçe Şablonları"
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

        divorceBtn.setOnClickListener {
            val intent = Intent(this@CasePetitions,DivorcePetitions::class.java)
            intent.putExtra("lawyerId",lawyerId)
            intent.putExtra("name",name)
            intent.putExtra("surname",surname)
            startActivity(intent)
        }
    }
}