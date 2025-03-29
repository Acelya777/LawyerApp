package com.acelya.lawyerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Finance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_finance)
        val localActivity = "Finans Yönetimi"
        val name = intent.getStringExtra("name")

        //ToolBarFragment toolbar başlıkları gönderme
        if (savedInstanceState == null) {
            val fragment = ToolbarFragment()
            val bundle = Bundle()
            bundle.putString("name", name)
            bundle.putString("locatedActivity",localActivity)
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
        val caseTest = findViewById<Button>(R.id.CaseTest)

        caseTest.setOnClickListener {
            val intent = Intent(this@Finance,CaseManagement::class.java)
            startActivity(intent)
        }

    }
}