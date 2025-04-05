package com.acelya.lawyerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.models.Client
import com.acelya.lawyerapp.models.Lawyers
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.mail.MessagingException

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        val btnResetPassword = findViewById<Button>(R.id.buttonResetPassword)

        btnResetPassword.setOnClickListener {
            fetchLawyers()
        }

    }
    private fun fetchLawyers() {
        FirebaseDatabase.getInstance().purgeOutstandingWrites()
        val dbRef = FirebaseDatabase.getInstance().getReference("LawyersTable")
        val emailInput = findViewById<EditText>(R.id.editTextEmailForgot)
        val email = emailInput.text.toString().trim()

        if (email.isEmpty()) {
            emailInput.error = "Lütfen bir e-posta adresi girin!"
            //Toast.makeText(this, "Lütfen bir e-posta adresi girin!", Toast.LENGTH_SHORT).show()
            return
        }

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var found = false
                for (lawyerSnapshot in snapshot.children) {
                    val lawyer = lawyerSnapshot.getValue(Lawyers::class.java)
                        if (lawyer?.email != email) {
                            Toast.makeText(this@ForgotPassword, "Bu e-posta sistemde kayıtlı değil!", Toast.LENGTH_LONG).show()
                            break
                        }else{
                            sendPasswordResetEmail(lawyer.email!!, lawyer.password)
                            break
                        }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForgotPassword, "Veri alınamadı! Hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendPasswordResetEmail(email: String, password: String?) {
        if (password.isNullOrEmpty()) {
            Toast.makeText(this, "Şifre bulunamadı!", Toast.LENGTH_SHORT).show()
            return
        }

        val subject = "Şifre Sıfırlama Talebi"
        val message = "Merhaba,\n\n" +
                "Şifreniz: $password\n\n" +
                "Lütfen güvenliğiniz için şifrenizi değiştirin."

            EmailSender.sendEmail(email, subject, message) { success, errorMessage ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Şifre e-posta adresinize gönderildi!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ForgotPassword, LogIn::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "E-posta gönderme hatası: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

}