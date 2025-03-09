package com.acelya.lawyerapp

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayoutHome)
        val settingButton = findViewById<ImageButton>(R.id.HomeSettingImageButton)
        val navigationView = findViewById<NavigationView>(R.id.navigationViewHome)
        val menuLanguageOption = navigationView?.menu?.findItem(R.id.nav_language)

        settingButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
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
    private fun showLanguageMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.language_menu, popup.menu)
        popup.show()
    }

}