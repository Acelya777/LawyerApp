package com.acelya.lawyerapp.firebaseCrud

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.R
import com.acelya.lawyerapp.databinding.ActivitySendBinding
import com.acelya.lawyerapp.models.UserInfo
import com.google.firebase.Firebase
import com.google.firebase.database.database

class SendDataActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivitySendBinding.inflate(layoutInflater)
    }
    companion object{
        fun launch(activity: Activity){
            activity.startActivity(Intent(activity,SendDataActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.SendDataActivityBtnUpload.setOnClickListener {
            if(TextUtils.isEmpty(binding.SendActivityEditTextTitle.text.toString().trim())){
                binding.SendActivityEditTextTitle.error = "Title can't be empty"
                binding.SendActivityEditTextTitle.requestFocus()
            }else if (TextUtils.isEmpty(binding.SendActivityEditTextDes.text.toString().trim())){
                binding.SendActivityEditTextDes.error = "Description can't be empty"
                binding.SendActivityEditTextDes.requestFocus()
            } else{
                val userInfo = UserInfo()
                userInfo.title = binding.SendActivityEditTextTitle.text.toString().trim()
                userInfo.description = binding.SendActivityEditTextDes.text.toString().trim()

                upload(userInfo)
            }
        }
    }

    private fun upload(userInfo: UserInfo){
        Firebase.database.getReference("Users")
            .child(System.currentTimeMillis().toString())
            .setValue(userInfo)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    binding.SendActivityEditTextTitle.setText("")
                    binding.SendActivityEditTextDes.setText("")
                    Toast.makeText(this@SendDataActivity,"Saved!",Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener() {
                Toast.makeText(this@SendDataActivity, it.message,Toast.LENGTH_SHORT).show()
            }
    }
}