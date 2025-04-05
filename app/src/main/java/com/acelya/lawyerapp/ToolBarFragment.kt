package com.acelya.lawyerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth

class ToolbarFragment : Fragment(R.layout.tool_bar) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val name = arguments?.getString("name")
        val surname = arguments?.getString("surname")
        val localActivity = arguments?.getString("locatedActivity")

        // TextView'ı bulup güncelliyoruz
        val nameTextView = view.findViewById<TextView>(R.id.ToolBarUsername)
        val locatedActivityTextView = view.findViewById<TextView>(R.id.locatedActivity)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawerLayoutHome)
        val toolbarMenu = view.findViewById<LinearLayout>(R.id.ToolBarMenu)

        nameTextView.text = "Av. $name $surname"
        locatedActivityTextView.text = localActivity

        // Menu açma işlemi
        toolbarMenu.setOnClickListener {
            (activity as? Home)?.openDrawer()
            (activity as? CaseManagement)?.openDrawer()
        }

//        toolbarLogoutLayout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(activity, LogIn::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            activity?.finish()
//            Toast.makeText(context,"Çıkış yapıldı!",Toast.LENGTH_SHORT).show()
//        }
    }
}
