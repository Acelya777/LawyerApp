package com.acelya.lawyerapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.databinding.ActivityCreateCaseFileBinding
import com.acelya.lawyerapp.databinding.ActivityLogInBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.play.core.integrity.v
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.*
import android.util.Base64
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.SetOptions


class CreateCaseFile : AppCompatActivity() {
    private val binding by lazy {
        ActivityCreateCaseFileBinding.inflate(layoutInflater)
    }

    private lateinit var rootView: View
    private val firestore = FirebaseFirestore.getInstance()
    private var pdfUri: Uri? = null
    private var clientId: String? = null
    private var lawyerId: String? = null
    private var pdfFileData: HashMap<String, String>? = null
    val pdfId = FirebaseDatabase.getInstance().reference.push().key ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        // Intent'ten gelen name değerini alıyoruz
        val localActivity = "Dosya Oluşturma"
        val name = intent.getStringExtra("name")

        //ToolBarFragment toolbar başlıkları gönderme
        if (savedInstanceState == null) {
            val fragment = ToolbarFragment()
            val bundle = Bundle()
            bundle.putString("name", name)
            bundle.putString("locatedActivity",localActivity)
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        rootView = findViewById(android.R.id.content)
        val caseNameEditText = findViewById<TextInputEditText>(R.id.caseNameEditText)
        val caseTypeEditText = findViewById<TextInputEditText>(R.id.caseTypeEditText)
        val notesEditText = findViewById<TextInputEditText>(R.id.notesEditText)
        val startDateEditText = findViewById<EditText>(R.id.startDateEditText)
        val endDateEditText = findViewById<EditText>(R.id.endDateEditText)
        val cbOngoing = findViewById<CheckBox>(R.id.checkboxOngoing)

        val dataSetCaseFile = findViewById<Button>(R.id.dataSetCaseFileBtn)
        val uploadPdfBtn = findViewById<Button>(R.id.uploadPdfBtn)

        //CheckBox
        cbOngoing.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                endDateEditText.isEnabled = false
                endDateEditText.setText("Dava devam ediyor...")
            } else {
                endDateEditText.isEnabled = true
            }
        }

        startDateEditText.setOnClickListener {
            showDatePickerDialog(startDateEditText)
        }

        endDateEditText.setOnClickListener {
            showDatePickerDialog(endDateEditText)
        }
        //Margin Bottom değerleri
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private val rect = Rect()

            override fun onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) {
                    adjustScrollViewMargin(1000)
                } else {
                    adjustScrollViewMargin(150)
                }
            }
        })

        // Klavye açıldığında otomatik kaydırma için WindowInsets kullanımı
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (imeVisible) {
                adjustScrollViewMargin(500)
            } else {
                adjustScrollViewMargin(0)
            }
            insets
        }
        //Pdf buttonun dosyalarından pdf seçmesi
        uploadPdfBtn.setOnClickListener { selectPdfFile() }
        //Create Case File
        dataSetCaseFile.setOnClickListener {
            val caseName = caseNameEditText.text.toString()
            val caseType = caseTypeEditText.text.toString()
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()
            val notes = notesEditText.text.toString()
            val isOngoing = cbOngoing.isChecked
            clientId = intent.getStringExtra("clientId")
            lawyerId = intent.getStringExtra("lawyerId")

                saveCaseToRealtimeDatabase(
                    caseName, caseType,
                    clientId!!, lawyerId!!, startDate,
                    endDate, notes, isOngoing
                )


        }
    }
    /**
     * EditTextte Tairh seçmesi için function
     */
    private fun showDatePickerDialog(editText: EditText){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear,selectedMonth,selectedDay)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
                editText.setText(dateFormat.format(selectedDate.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
    /**
     * Sayfa için Margin Bottom ekleme function
     */
    private fun adjustScrollViewMargin(margin: Int) {
        val layoutParams = binding.scrollView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        binding.scrollView.layoutParams = layoutParams
    }
    /**
     * PDF Dosyası Seçme
     */
    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                pdfUri = uri
                val fileName = getFileName(uri)
                val pdfBase64 = convertPdfToBase64(uri)


                pdfFileData = hashMapOf(
                    "pdfId" to pdfId,
                    "base64Data" to pdfBase64,
                    "fileName" to fileName,
                    "fileType" to "application/pdf"
                )
                // 📌 Seçilen PDF'nin adını ekranda göster
                findViewById<TextView>(R.id.pdfNameTextView).text = fileName
                Toast.makeText(this, "PDF Seçildi: $fileName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 📌 URI'den Dosya Adını Alma
     */
    private fun getFileName(uri: Uri): String {
        var fileName = "unknown.pdf"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    /**
     * 📌 PDF'yi Base64 Formatına Çevirme
     */
    private fun convertPdfToBase64(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int

        while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
    /**
     * 📌 Firebase Realtime Database'e Kaydetme
     */
    fun saveCaseToRealtimeDatabase(
        caseName: String,
        caseType: String,
        clientId: String,
        lawyerId: String,
        startDate: String,
        endDate: String,
        notes: String,
        isOngoing: Boolean
    ) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("ClientCaseFileTable")

        val caseId = reference.push().key ?: return


        val caseData = hashMapOf(
            "caseId" to caseId,
            "caseName" to caseName,
            "caseType" to caseType,
            "clientId" to clientId,
            "lawyerId" to lawyerId,
            "startDate" to startDate,
            "endDate" to if (isOngoing) "continue" else endDate,
            "status" to if (isOngoing) "active" else "passive",
            "notes" to notes,
            "pdfFile" to  hashMapOf(pdfId to pdfFileData)
        )

        reference.child(caseId).setValue(caseData)
            .addOnSuccessListener {
                Toast.makeText(this, "Dava başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}