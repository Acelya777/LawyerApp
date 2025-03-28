package com.acelya.lawyerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.databinding.ActivityCalendarBinding
import com.acelya.lawyerapp.databinding.ActivityCreateCaseFileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class Calendar : AppCompatActivity() {
    private val binding by lazy {
        ActivityCalendarBinding.inflate(layoutInflater)
    }

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)
        val calenderReminder = findViewById<ImageButton>(R.id.CalenderReminderButton)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ToolbarFragment())
                .commit()
        }
        calenderReminder.setOnClickListener {
            val intent = Intent(this,Reminder::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val localActivity = "Takvim"
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


        val calendarView: CalendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            fetchEvent(selectedDate)
            Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show()
        }


    }
    private fun fetchEvent(date: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("CalenderTable").child(date)

        Log.d("FirebaseDebug", "Firebase'den çekilecek tarih: $date") // Debug için

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val eventName = snapshot.child("eventName").getValue(String::class.java) ?: "Etkinlik yok"
                    val eventTime = snapshot.child("eventTime").getValue(String::class.java) ?: "Zaman belirtilmemiş"
                    val eventDescription = snapshot.child("eventDescription").getValue(String::class.java) ?: "Açıklama yok"

                    Log.d("FirebaseDebug", "Etkinlik bulundu: $eventName - $eventTime - $eventDescription")

                    val eventTextView = findViewById<TextView>(R.id.tvEvents)
                    eventTextView.text = "📅 Etkinlik adı: $eventName\n⏰ Saat: $eventTime\n📌 Açıklama: $eventDescription"
                } else {
                    Log.d("FirebaseDebug", "Bu tarihte etkinlik yok: $date")
                    val eventTextView = findViewById<TextView>(R.id.tvEvents)
                    eventTextView.text = "📌 Bu tarihte etkinlik bulunmamaktadır."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Etkinlikler çekilemedi: ${error.message}")
            }
        })
    }

}