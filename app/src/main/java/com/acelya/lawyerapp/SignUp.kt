package com.acelya.lawyerapp

import android.content.Context
import android.content.Intent
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.UUID

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

            if(password.text.toString() != passwordRepeat.text.toString()){
                Toast.makeText(this, "Şifreler Uyuşmuyor! $password,$passwordRepeat", Toast.LENGTH_SHORT).show()
                validateField(password, "Tekrar giriniz!")
                validateField(passwordRepeat, "Tekrar giriniz!")

            }else{
                if (isValid) {
                    getRegisterLawyer()
                    Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                }
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

    fun getNextLawyerId(callback: (Int) -> Unit){
        val db = FirebaseFirestore.getInstance()

        db.collection("Lawyers")
            .orderBy("lawyerId", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                val lastId = if(!documents.isEmpty){
                    documents.documents[0].getLong("lawyerId")?.toInt() ?: 0
                } else {
                    0
                }
                callback(lastId + 1)
            }
            .addOnFailureListener { e ->
                callback(1)
            }
    }
    //FirebaseAuth Add
    fun getRegisterLawyer() {
        val name = findViewById<EditText>(R.id.RegisterName).text.toString().trim()
        val surname = findViewById<EditText>(R.id.RegisterSurname).text.toString().trim()
        val tc = findViewById<EditText>(R.id.RegisterTC).text.toString().trim()
        val phone = findViewById<EditText>(R.id.RegisterPhone).text.toString().trim()
        val email = findViewById<EditText>(R.id.RegisterEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.RegisterPassword).text.toString()
        val passwordRepeat = findViewById<EditText>(R.id.RegisterPasswordRepeat).text.toString()
        val officeNo = findViewById<EditText>(R.id.RegisterOfficeNumber).text.toString().trim()
        val registrationNo = findViewById<EditText>(R.id.RegisterRegistrationNumber).text.toString().trim()
        val city = findViewById<Spinner>(R.id.RegisterSpecializationSpinnerCity).selectedItem.toString()
        val specialization = findViewById<Spinner>(R.id.RegisterSpecializationSpinnerBar).selectedItem.toString()

        // Şifre eşleşme kontrolü
        if (password != passwordRepeat) {
            Toast.makeText(this, "Şifreler uyuşmuyor!", Toast.LENGTH_SHORT).show()
            return
        }

        // Boş alan kontrolü
        if (name.isEmpty() || surname.isEmpty() || tc.isEmpty() || phone.isEmpty() ||
            email.isEmpty() || password.isEmpty() || officeNo.isEmpty() || registrationNo.isEmpty()
        ) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            return
        }

        registerLawyer(email, password, name, surname, phone, city, specialization, officeNo, registrationNo, tc)
    }

    fun registerLawyer(
        email: String,
        password: String,
        name: String,
        surname: String,
        phone: String,
        city: String,
        specialization: String,
        officeNumber: String,
        registrationNumber: String,
        tc: String
    ) {
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference.child("LawyersTable")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid

                    if (userId != null) {
                        val lawyerData = mapOf(
                            "lawyerId" to userId,
                            "name" to name,
                            "surname" to surname,
                            "email" to email,
                            "phone" to phone,
                            "city" to city,
                            "specialization" to specialization,
                            "officeNumber" to officeNumber,
                            "registrationNumber" to registrationNumber,
                            "tc" to tc
                        )

                        database.child(userId).setValue(lawyerData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@SignUp, "Kayıt başarılı!", Toast.LENGTH_SHORT
                                ).show()

                                // Giriş ekranına yönlendir
                                val intent = Intent(this@SignUp, LogIn::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this@SignUp, "Veritabanına eklenirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(this@SignUp, "Kullanıcı kimliği alınamadı!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignUp, "Kayıt başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //FirebaseDatabase Add
    fun getRegisterLawyerFirst(){
        val name = findViewById<EditText>(R.id.RegisterName).text.toString()
        val surname = findViewById<EditText>(R.id.RegisterSurname).text.toString()
        val tc = findViewById<EditText>(R.id.RegisterTC).text.toString()
        val phone = findViewById<EditText>(R.id.RegisterPhone).text.toString()
        val email = findViewById<EditText>(R.id.RegisterEmail).text.toString()
        val password = findViewById<EditText>(R.id.RegisterPassword).text.toString()
        val passwordRepeat = findViewById<EditText>(R.id.RegisterPasswordRepeat).text.toString()
        val officeNo = findViewById<EditText>(R.id.RegisterOfficeNumber).text.toString()
        val registrationNo = findViewById<EditText>(R.id.RegisterRegistrationNumber).text.toString()
        val city = findViewById<Spinner>(R.id.RegisterSpecializationSpinnerCity).selectedItem.toString()
        val specialization = findViewById<Spinner>(R.id.RegisterSpecializationSpinnerBar).selectedItem.toString()

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("LawyersTable")

        val lawyerId = reference.push().key ?: return

        val lawyer = hashMapOf(
            "lawyerId" to lawyerId,
            "name" to name,
            "surname" to surname,
            "password" to password,
            "tc" to tc,
            "phone" to phone,
            "email" to email,
            "officeNumber" to officeNo,
            "registrationNumber" to registrationNo,
            "city" to city,
            "specialization" to specialization
        )

        reference.child(lawyerId).setValue(lawyer)
            .addOnSuccessListener {
                Toast.makeText(this, "Kayıt başarılı! ID: $lawyerId", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,LogIn::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}