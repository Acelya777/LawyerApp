package com.acelya.lawyerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acelya.lawyerapp.R
import com.acelya.lawyerapp.models.UserInfo

class UserInfoAdapter(private val context: Context,private val users:MutableList<UserInfo>): RecyclerView.Adapter<UserInfoAdapter.UserInfoHolder>() {
    class UserInfoHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txtTitle : TextView = itemView.findViewById(R.id.ItemTextViewTitle)
        val txtDes : TextView = itemView.findViewById(R.id.ItemTextViewDes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoHolder {
        return UserInfoHolder(LayoutInflater.from(context).inflate(R.layout.layout_users_item,parent,false))
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserInfoHolder, position: Int) {
        holder.txtTitle.text = users[position].title
        holder.txtDes.text = users[position].description
    }
}