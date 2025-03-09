package com.acelya.lawyerapp.firebaseCrud

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.R
import com.acelya.lawyerapp.adapter.UserInfoAdapter
import com.acelya.lawyerapp.databinding.ActivityGetDataBinding
import com.acelya.lawyerapp.models.UserInfo
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class GetDataActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityGetDataBinding.inflate(layoutInflater)
    }
    companion object{
        fun launch(activity: Activity){
            activity.startActivity(Intent(activity,GetDataActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchUsers()
    }

    private fun fetchUsers(){
        val tempList = mutableListOf<UserInfo>()
        Firebase.database.getReference("Users")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(item in snapshot.children){
                        val userInfo = item.getValue(UserInfo::class.java)
                        if (userInfo != null)
                            tempList.add(userInfo)
                    }
                    val adapter = UserInfoAdapter(this@GetDataActivity,tempList)
                    binding.GetDataRecyclerView.adapter = adapter
                    Log.i("MyDataList","onDataChange: ${tempList.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@GetDataActivity,error.message,Toast.LENGTH_LONG).show()
                }
            })
    }
}