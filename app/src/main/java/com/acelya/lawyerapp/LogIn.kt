package com.acelya.lawyerapp

import android.graphics.Rect
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.acelya.lawyerapp.databinding.ActivityLogInBinding
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val LawUsername = findViewById<EditText>(R.id.LawUsername)
        val LawPassword = findViewById<EditText>(R.id.LawPassword)
        val rootView = findViewById<View>(android.R.id.content)
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
            val LUsername = binding.LawUsername.text.toString().trim()
            val LPassword = binding.LawPassword.text.toString().trim()

            validateField(LawUsername,"Email Boş Olamaz")
            validateField(LawPassword,"Password Boş Olamaz")

            val intent = Intent(this@LogIn, Home::class.java)
            startActivity(intent)
            //checkUserCredentials(LUsername, LPassword)
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

    private fun checkUserCredentials(username: String, password: String) {
        val database = FirebaseDatabase.getInstance()
        val lawyerRef = database.getReference("Lawyers")

        lawyerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val storedUsername = snapshot.child("Email").getValue(String::class.java)
                    val storedPassword = snapshot.child("Password").getValue(Long::class.java) // Password Long olarak saklanmış

                    if (storedUsername == username && storedPassword == password.toLong()) {
                        Toast.makeText(this@LogIn, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LogIn, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LogIn, "Kullanıcı adı veya şifre yanlış!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LogIn, "Kullanıcı adı veya şifre bulunamadı!", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LogIn, "Veritabanı hatası: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLayoutMargin(margin: Int) {
        val loginLayoutT = findViewById<LinearLayout>(R.id.LoginLayout)
        val layoutParams = loginLayoutT.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        loginLayoutT.layoutParams = layoutParams
    }
}