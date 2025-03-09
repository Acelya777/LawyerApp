package com.acelya.lawyerapp

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        val rootView = findViewById<View>(android.R.id.content)
        val itemSpinnerBar = findViewById<Spinner>(R.id.RegisterSpecializationSpinnerBar)
        val itemSpinnerCity = findViewById<Spinner>(R.id.RegisterSpecializationSpinnerCity)
        val registerButton = findViewById<Button>(R.id.RegisterButton)

        val spinnerOptionsBar = arrayListOf("Seçiniz", "Ceza Hukuku", "Boşanma Hukuku", "İcra Hukuku", "Gayrimenkul Hukuku", "Ticaret Hukuku", "Diğer")
        val adapterBar = CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, spinnerOptionsBar)
        adapterBar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        itemSpinnerBar.adapter = adapterBar

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
        itemSpinnerCity.adapter = adapterCity

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

        registerButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.RegisterName)
            val surname = findViewById<EditText>(R.id.RegisterSurname)
            val tc = findViewById<EditText>(R.id.RegisterTC)
            val phone = findViewById<EditText>(R.id.RegisterPhone)
            val email = findViewById<EditText>(R.id.RegisterEmail)
            val password = findViewById<EditText>(R.id.RegisterPassword)
            val passwordRepeat = findViewById<EditText>(R.id.RegisterPasswordRepeat)
            val officeNumber = findViewById<EditText>(R.id.RegisterOfficeNumber)
            val registrationNumber = findViewById<EditText>(R.id.RegisterRegistrationNumber)
            val citySpinner = findViewById<Spinner>(R.id.RegisterSpecializationSpinnerCity)
            val specializationSpinner = findViewById<Spinner>(R.id.RegisterSpecializationSpinnerBar)

            var isValid = true

            fun validateField(editText: EditText, errorMessage: String) {
                if (editText.text.toString().trim().isEmpty()) {
                    editText.error = errorMessage
                    isValid = false
                }
            }

            validateField(name, "Ad alanı boş olamaz")
            validateField(surname, "Soyad alanı boş olamaz")
            validateField(tc, "TC Kimlik No alanı boş olamaz")
            validateField(phone, "Telefon alanı boş olamaz")
            validateField(email, "E-posta alanı boş olamaz")
            validateField(password, "Parola alanı boş olamaz")
            validateField(passwordRepeat, "Parola tekrar alanı boş olamaz")
            validateField(officeNumber, "Büro No alanı boş olamaz")
            validateField(registrationNumber, "Sicil No alanı boş olamaz")

            // Spinner kontrolü (Seçiniz seçeneği seçilmişse hata ver)
            if (citySpinner.selectedItemPosition == 0) {
                (citySpinner.selectedView as? TextView)?.error = "Baro il seçmelisiniz"
                isValid = false
            }

            if (specializationSpinner.selectedItemPosition == 0) {
                (specializationSpinner.selectedView as? TextView)?.error = "Uzmanlık alanı seçmelisiniz"
                isValid = false
            }

            if (isValid) {
                // Tüm alanlar doluysa devam et
                Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
            }
        }

    }
    class CustomSpinnerAdapter(context: Context, resource: Int, objects: List<String>) :
        ArrayAdapter<String>(context, resource, objects) {

        override fun isEnabled(position: Int): Boolean {
            return position != 0
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent) as TextView
            if (position == 0) {
                view.setTextColor(Color.GRAY)
            } else {
                view.setTextColor(Color.BLACK)
            }
            return view
        }
    }
    private fun setLayoutMargin(margin: Int) {
        val loginLayoutT = findViewById<LinearLayout>(R.id.RegisterLayout)
        val layoutParams = loginLayoutT.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        loginLayoutT.layoutParams = layoutParams
    }
}