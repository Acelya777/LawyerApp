package com.acelya.lawyerapp

import android.graphics.Rect
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.acelya.lawyerapp.databinding.ActivityLogInBinding
import com.acelya.lawyerapp.models.Lawyers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LogIn : AppCompatActivity() {
    private val binding by lazy{
        ActivityLogInBinding.inflate(layoutInflater)
    }
    private lateinit var rootView: View
    private lateinit var myLayout: LinearLayout
    private lateinit var saveBtn: Button
    val isLogginIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val LawEmail = findViewById<EditText>(R.id.LawUsername)
        val LawPassword = findViewById<EditText>(R.id.LawPassword)
        val rootView = findViewById<View>(android.R.id.content)
        saveBtn = findViewById(R.id.LoginScreenUploadButton)


        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private val rect = Rect()

            override fun onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) { // Klavye açık
                    setLayoutMargin(1000)
                } else {
                    setLayoutMargin(0)
                }
            }
        })

        binding.LoginScreenUploadButton.setOnClickListener {
            var isValid = true



            fun validateField(editText: EditText, errorMessage: String) {
                if (editText.text.toString().trim().isEmpty()) {
                    editText.error = errorMessage
                    isValid = false
                }
            }
            val LEmail = binding.LawUsername.text.toString().trim()
            val LPassword = binding.LawPassword.text.toString().trim()

            validateField(LawEmail,"Email Boş Olamaz")
            validateField(LawPassword,"Password Boş Olamaz")

            loginLawyer()
        }

        binding.RegisterCardView.setOnClickListener {
            val intent = Intent(this@LogIn, SignUp::class.java)
            startActivity(intent)
        }

        binding.ForgotPassword.setOnClickListener {
            val intent = Intent(this@LogIn,ForgotPassword::class.java)
            startActivity(intent)
        }
    }

    private fun setLayoutMargin(margin: Int) {
        val loginLayoutT = findViewById<LinearLayout>(R.id.LoginLayout)
        val layoutParams = loginLayoutT.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        loginLayoutT.layoutParams = layoutParams
    }
    fun loginLawyer() {
        val emailInput = findViewById<EditText>(R.id.LawUsername).text.toString().trim()
        val passwordInput = findViewById<EditText>(R.id.LawPassword).text.toString().trim()

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "E-posta ve şifre boş olamaz!", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().reference.child("LawyersTable")

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                var found = false

                for (lawyer in snapshot.children) {
                    val email = lawyer.child("email").value.toString()
                    val password = lawyer.child("password").value.toString()

                    if (email == emailInput && password == passwordInput) {
                        found = true
                        val name = lawyer.child("name").value.toString()
                        val surname = lawyer.child("surname").value.toString()
                        val lawyerId = lawyer.child("lawyerId").value.toString()
                        val specialization = lawyer.child("specialization").value.toString()

                        saveBtn.isEnabled = false

                        Toast.makeText(this, "Hoş geldiniz, $name!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, Home::class.java)
                        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("lawyerName", name)
                        intent.putExtra("lawyerId", lawyerId)
                        intent.putExtra("name", name)
                        intent.putExtra("surname", surname)
                        editor.apply()
                        startActivity(intent)

                        finish()
                        break
                    }
                }

                if (!found) {
                    Toast.makeText(this, "E-posta veya şifre hatalı!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Veritabanında kayıtlı avukat bulunamadı!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Veri çekme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



}