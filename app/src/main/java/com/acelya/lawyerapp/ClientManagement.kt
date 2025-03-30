package com.acelya.lawyerapp

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.SignUp.CustomSpinnerAdapter
import com.google.firebase.database.FirebaseDatabase

class ClientManagement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_management)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ToolbarFragment())
                .commit()
        }
        val rootView = findViewById<View>(android.R.id.content)
        val clientSpinnerCity = findViewById<Spinner>(R.id.ClientCitySpinner)
        val clientUploadBtn = findViewById<Button>(R.id.ClientUploadButton)

        clientUploadBtn.setOnClickListener {
            setClientInfo()
        }

        val localActivity = "Müvekkil Yönetimi"
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

        val spinnerOptionsCity = arrayListOf("Seçiniz", "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Aksaray", "Amasya", "Ankara",
            "Antalya", "Ardahan", "Artvin", "Aydın", "Balıkesir", "Bartın", "Batman", "Bayburt",
            "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı",
            "Çorum", "Denizli", "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan", "Erzurum",
            "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Iğdır",
            "Isparta", "İstanbul", "İzmir", "Kahramanmaraş", "Karabük", "Karaman", "Kars",
            "Kastamonu", "Kayseri", "Kırıkkale", "Kırklareli", "Kırşehir", "Kilis", "Kocaeli",
            "Konya", "Kütahya", "Malatya", "Manisa", "Mardin", "Mersin", "Muğla", "Muş",
            "Nevşehir", "Niğde", "Ordu", "Osmaniye", "Rize", "Sakarya", "Samsun", "Siirt",
            "Sinop", "Sivas", "Şanlıurfa", "Şırnak", "Tekirdağ", "Tokat", "Trabzon", "Tunceli",
            "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak")
        val adapterCity = CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, spinnerOptionsCity)
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        clientSpinnerCity.adapter = adapterCity

        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private val rect = Rect()

            override fun onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) { // Klavye açık
                    setLayoutMargin(1200)
                } else {
                    setLayoutMargin(0)
                }
            }
        })
    }
    private fun setLayoutMargin(margin: Int) {
        val loginLayoutT = findViewById<ScrollView>(R.id.ClientManagementView)
        val layoutParams = loginLayoutT.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        loginLayoutT.layoutParams = layoutParams
    }
    fun setClientInfo(){
        val name = findViewById<EditText>(R.id.ClientName).text.toString()
        val surname = findViewById<EditText>(R.id.ClientSurname).text.toString()
        val tc = findViewById<EditText>(R.id.ClientTC).text.toString()
        val phone = findViewById<EditText>(R.id.ClientPhone).text.toString()
        val email = findViewById<EditText>(R.id.ClientEmail).text.toString()
        val address = findViewById<EditText>(R.id.ClientAddress).text.toString()
        val city = findViewById<Spinner>(R.id.ClientCitySpinner).selectedItem.toString()

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("ClientTable")

        val clientId = reference.push().key ?: return

        val client = hashMapOf(
            "clientId" to clientId,
            "name" to name,
            "surname" to surname,
            "tc" to tc,
            "phone" to phone,
            "email" to email,
            "address" to address,
            "city" to city,
        )

        reference.child(clientId).setValue(client)
            .addOnSuccessListener {
                Toast.makeText(this, "Kayıt başarılı! ID: $clientId", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,LogIn::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}