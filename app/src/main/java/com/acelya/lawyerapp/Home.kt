package com.acelya.lawyerapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale
import androidx.core.net.toUri

class Home : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)


        drawerLayout = findViewById(R.id.drawerLayoutHome)
        val navigationView = findViewById<NavigationView>(R.id.navigationViewHome)
        val menuLanguageOption = navigationView?.menu?.findItem(R.id.nav_language)
        val caseManagement = findViewById<CardView>(R.id.CaseManagementCardView)
        val contratManagement = findViewById<CardView>(R.id.ContratManagementCardView)
        val calender = findViewById<CardView>(R.id.CalenderCardView)
        val clientManagement = findViewById<CardView>(R.id.ClientManagement)
        val finance = findViewById<CardView>(R.id.FinanceCardView)
        val btnOpenEmail = findViewById<ImageButton>(R.id.HomeEmailImageButton)


        // Intent'ten gelen name değerini alıyoruz
        val localActivity = "Ana sayfa"
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
        caseManagement.setOnClickListener {
            val intent = Intent(this@Home,CaseManagement::class.java)
            intent.putExtra("lawyerId",lawyerId)
            intent.putExtra("name",name)
            intent.putExtra("surname",surname)
            startActivity(intent)
        }
        contratManagement.setOnClickListener {
            val intent = Intent(this@Home,ContratManagement::class.java)
            intent.putExtra("lawyerId",lawyerId)
            intent.putExtra("name",name)
            intent.putExtra("surname",surname)
            startActivity(intent)
        }
        calender.setOnClickListener {
            val intent = Intent(this@Home,Calendar::class.java)
            intent.putExtra("lawyerId",lawyerId)
            intent.putExtra("name",name)
            intent.putExtra("surname",surname)
            startActivity(intent)
        }
        clientManagement.setOnClickListener {
            val intent = Intent(this@Home,ClientManagement::class.java)
            intent.putExtra("lawyerId",lawyerId)
            intent.putExtra("name",name)
            intent.putExtra("surname",surname)
            startActivity(intent)
        }
        finance.setOnClickListener {
            val intent = Intent(this@Home,Finance::class.java)
            intent.putExtra("lawyerId",lawyerId)
            intent.putExtra("name",name)
            intent.putExtra("surname",surname)
            startActivity(intent)
        }

        btnOpenEmail.setOnClickListener {
            openEmailApp()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_language -> {
                    // Dil menüsünü aç
                    val languageButton = findViewById<View>(R.id.nav_language) // Menü öğesinin görünümünü al
                    showLanguageMenu(languageButton)
                    true
                }
                // Diğer menü öğeleri...
                else -> false
            }
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END)
    }

    private fun showLanguageMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.language_menu, popup.menu)
        popup.show()
    }

    private fun openEmailApp() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "mailto:".toUri()
            setPackage("com.google.android.gm")
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Gmail uygulaması bulunamadı!", Toast.LENGTH_SHORT).show()
        }
    }

}