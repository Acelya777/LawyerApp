package com.acelya.lawyerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.acelya.lawyerapp.databinding.ActivityMainBinding
import com.acelya.lawyerapp.firebaseCrud.SendDataActivity

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.getData.setOnClickListener{
//            val intent = Intent(this@MainActivity,GetDataActivity::class.java)
//            startActivity(intent)
            //loadFragment(LogIn())
        }

        binding.sendData.setOnClickListener{
            SendDataActivity.launch(this@MainActivity)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.BtnLayoutParent, fragment)
            .addToBackStack(null)  // Geri tuşuyla önceki sayfaya dönmek için
            .commit()
    }

}