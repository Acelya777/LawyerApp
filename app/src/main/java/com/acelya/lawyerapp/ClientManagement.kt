package com.acelya.lawyerapp

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.SignUp.CustomSpinnerAdapter
import com.google.firebase.database.FirebaseDatabase

class ClientManagement : AppCompatActivity() {
    private var isValid = true
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
        val clientName = findViewById<EditText>(R.id.ClientName)
        val clientSurname = findViewById<EditText>(R.id.ClientSurname)
        val clientEmail = findViewById<EditText>(R.id.ClientEmail)
        val clientPhone = findViewById<EditText>(R.id.ClientPhone)
        val clientTC = findViewById<EditText>(R.id.ClientTC)
        val clientAddress = findViewById<EditText>(R.id.ClientAddress)

        clientTC.filters = arrayOf(InputFilter.LengthFilter(11))
        clientTC.inputType = InputType.TYPE_CLASS_NUMBER
        clientTC.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 11) {
                    clientTC.error = null
                } else {
                    clientTC.error = "TC kimlik numarası 11 haneli olmalıdır!"
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clientPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Şu an için herhangi bir işlem yapmıyoruz
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Eğer metin başında "+90" yoksa, ekleyin
                if (!charSequence.isNullOrEmpty() && !charSequence.startsWith("+90")) {
                    clientPhone.setText("+90" + charSequence.toString())
                    clientPhone.setSelection(clientPhone.text.length)  // İmleci sona taşır
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // Şu an için herhangi bir işlem yapmıyoruz
            }
        })

        // E-posta geçerliliği kontrolü
        clientEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    clientEmail.error = "Geçersiz e-posta formatı"
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clientUploadBtn.setOnClickListener {

            isValid = true

            validateField(clientName, "Ad alanı boş olamaz")
            validateField(clientSurname, "Soyad alanı boş olamaz")
            validateField(clientTC, "TC Kimlik No alanı boş olamaz")
            validateField(clientPhone, "Telefon alanı boş olamaz")
            validateField(clientEmail, "E-posta alanı boş olamaz")
            validateField(clientAddress, "Adres alanı boş olamaz")

            // Spinner kontrolü
            if (clientSpinnerCity.selectedItemPosition == 0) {
                (clientSpinnerCity.selectedView as? TextView)?.error = "Baro il seçmelisiniz"
                isValid = false
            }

            // Tüm doğrulamalar geçildiyse
            if (isValid) {
                setClientInfo()
                Toast.makeText(this@ClientManagement, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
            }


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
                    setLayoutMargin(0)
                } else {
                    setLayoutMargin(0)
                }
            }
        })
    }

    private fun validateField(editText: EditText, errorMessage: String) {
        if (editText.text.toString().trim().isEmpty()) {
            editText.error = errorMessage
            isValid = false
        }
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
                val intent = Intent(this,Home::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}