package com.acelya.lawyerapp.adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.acelya.lawyerapp.ClientCaseFile
import com.acelya.lawyerapp.CreateCaseFile
import com.acelya.lawyerapp.R
import com.acelya.lawyerapp.models.CaseFile
import com.acelya.lawyerapp.models.Client
import com.acelya.lawyerapp.models.pdfFile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ClientAdapter(private var clientList: MutableList<Client>,private val lawyerId:String?,private val name:String?,private val surname:String?) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val lawCategory: TextView = view.findViewById(R.id.lawCategory)
        val btnCaseFile: Button = view.findViewById(R.id.btnCaseFile)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.case_manager_item, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clientList[position]
        holder.userName.text = "${client.name} ${client.surname}"
        holder.lawCategory.text = client.city

        holder.btnDelete.setOnClickListener {
            val db = FirebaseDatabase.getInstance().getReference("ClientTable")
            db.child(client.clientId).removeValue()
                .addOnSuccessListener {
                    clientList.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(holder.itemView.context, "Kullanıcı Silindi", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Log.e("FirebaseDelete", "Silme işlemi başarısız: ${error.message}")
                    Toast.makeText(holder.itemView.context, "Silme Hatası: ${error.message}", Toast.LENGTH_SHORT).show()
                }

        }

        holder.btnCaseFile.setOnClickListener {
            val db = FirebaseDatabase.getInstance().getReference("ClientCaseFileTable")
            db.orderByChild("clientId").equalTo(client.clientId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Kullanıcının davaları varsa, ilk davayı al ve sayfaya yönlendir
                            for (caseSnapshot in snapshot.children) {
                                val case = caseSnapshot.getValue(CaseFile::class.java)
                                case?.let {
                                    val intent = Intent(holder.itemView.context, ClientCaseFile::class.java)
                                    intent.putExtra("caseId", it.caseId)
                                    intent.putExtra("name", name)
                                    intent.putExtra("surname", surname)
                                    intent.putExtra("lawyerId", lawyerId)
                                    intent.putExtra("clientName",client.name)
                                    intent.putExtra("clientSurname",client.surname)
                                    intent.putExtra("clientPhone",client.phone)
                                    intent.putExtra("clientEmail",client.email)
                                    intent.putExtra("clientAddress", client.address)
                                    holder.itemView.context.startActivity(intent)
                                    return  // İlk bulduğumuz dava için yönlendirme yaptık, döngüyü bitir
                                }
                            }
                        } else {
                            Toast.makeText(holder.itemView.context, "Bu kullanıcıya ait dava dosyası bulunamadı.", Toast.LENGTH_SHORT).show()

                            // AlertDialog oluştur
                            val builder = AlertDialog.Builder(holder.itemView.context)
                            builder.setTitle("Dava Dosyası Yok")
                            builder.setMessage("Bu kullanıcıya ait dava dosyası bulunamadı. Yeni bir dosya oluşturmak ister misiniz?")

                            // "Evet" butonu
                            builder.setPositiveButton("Evet") { dialog, which ->
                                // Burada dosya oluşturma işlemi yapılacak
                                val intent = Intent(holder.itemView.context, CreateCaseFile::class.java)
                                intent.putExtra("clientId", client.clientId)  // clientId'yi yeni sayfaya gönder
                                intent.putExtra("name", name)
                                intent.putExtra("lawyerId", lawyerId)
                                holder.itemView.context.startActivity(intent)
                            }

                            // "Hayır" butonu
                            builder.setNegativeButton("Hayır") { dialog, which ->
                                // Dialogu kapat
                                dialog.dismiss()
                            }

                            // Dialogu göster
                            builder.show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(holder.itemView.context, "Veri alınırken hata oluştu.", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun getItemCount(): Int = clientList.size

    // Yeni veri ile listeyi güncellemek için bu fonksiyonu ekledik
    fun updateData(newList: List<Client>) {
        clientList.clear()
        clientList.addAll(newList)
        notifyDataSetChanged()
    }
}
