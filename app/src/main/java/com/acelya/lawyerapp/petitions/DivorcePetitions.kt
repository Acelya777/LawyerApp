package com.acelya.lawyerapp.petitions

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.R
import com.acelya.lawyerapp.ToolbarFragment
import com.acelya.lawyerapp.databinding.ActivityDivorcePetitionsBinding
import java.io.File
import java.io.FileOutputStream

class DivorcePetitions : AppCompatActivity() {
    private val binding by lazy {
        ActivityDivorcePetitionsBinding.inflate(layoutInflater)
    }

    private lateinit var rootView: View
    private val timestamp = System.currentTimeMillis()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val claimantName = findViewById<EditText>(R.id.claimant_name)
        val claimantSurname = findViewById<EditText>(R.id.claimant_surname)
        val claimantAddress = findViewById<EditText>(R.id.claimant_address)
        val respondentName = findViewById<EditText>(R.id.respondent_name)
        val respondentSurname = findViewById<EditText>(R.id.respondent_surname)
        val respondentAddress = findViewById<EditText>(R.id.respondent_address)
        val marriageDate = findViewById<EditText>(R.id.marriage_date)
        val childStatus = findViewById<EditText>(R.id.child_status)
        val cbNoChild = findViewById<CheckBox>(R.id.checkboxNoChild)
        val test = findViewById<Button>(R.id.test)

        val localActivity = "Boşanma Dilekçe Şablonu"
        val lawyerName = intent.getStringExtra("name")
        val lawyerSurname = intent.getStringExtra("surname")
        val lawyerId = intent.getStringExtra("lawyerId")

        //ToolBarFragment toolbar başlıkları gönderme
        if (savedInstanceState == null) {
            val fragment = ToolbarFragment()
            val bundle = Bundle()
            bundle.putString("name", lawyerName)
            bundle.putString("surname", lawyerSurname)
            bundle.putString("locatedActivity",localActivity)
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rootView = findViewById(android.R.id.content)

        //Margin Bottom değerleri
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private val rect = Rect()

            override fun onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) {
                    adjustScrollViewMargin(850)
                } else {
                    adjustScrollViewMargin(0)
                }
            }
        })

        // Klavye açıldığında otomatik kaydırma için WindowInsets kullanımı
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        cbNoChild.setOnCheckedChangeListener{_ , isCheched ->
            if(isCheched){
                childStatus.isEnabled = false
                childStatus.setText("Çocuk Yok!")
            }
            else{
                childStatus.isEnabled = true
                childStatus.setText("")
            }
        }

        // Butona tıklanınca PDF oluştur ve aç
        test.setOnClickListener {
            val dilekceMetni = """
        KÜTAHYA AİLE MAHKEMESİ HÂKİMLİĞİ’NE  

        DAVACI: {davaci_ad} {davaci_soyad}  
        Adres: {davaci_adres}  

        DAVALI: {davali_ad} {davali_soyad}  
        Adres: {davali_adres}  

        KONU: Evlilik birliğinin temelinden sarsılması nedeniyle TMK madde 166/3 uyarınca anlaşmalı boşanma talebimizin sunulmasıdır.  

        AÇIKLAMALAR:  
        1. Müvekkilim {davaci_ad} {davaci_soyad} ile davalı {davali_ad} {davali_soyad}, {evlilik_tarihi} tarihinde evlenmiş olup, zaman içinde evlilikten bekledikleri mutluluğu sağlayamamışlardır.  

        2. Taraflar, evlilik birliğinin sürdürülebilir olmadığı konusunda ortak bir kanaate varmış ve karşılıklı anlaşarak boşanmaya karar vermişlerdir.  

        3. {cocuk_durumu}  

        4. Mal paylaşımı ve nafaka hususlarında taraflar karşılıklı anlaşmış olup, mahkemeye sunulacak protokol çerçevesinde hareket edilecektir.  

        5. Taraflar, evlilik birliğinin sona erdirilmesinde mutabık olup, anlaşmalı boşanmanın gerçekleşmesini talep etmektedirler.  

        SONUÇ VE İSTEM:  
        Yukarıda arz ve izah edilen sebepler doğrultusunda;  
        1. Müvekkilim {davaci_ad} {davaci_soyad} ile davalı {davali_ad} {davali_soyad}’ın TMK 166/3 uyarınca anlaşmalı olarak boşanmalarına karar verilmesini,  
        2. Taraflar arasında hazırlanan anlaşmalı boşanma protokolünün onaylanmasını,  
        3. Yargılama giderleri ve vekâlet ücretinin taraflarca kendi üzerlerinde bırakılmasını,  

        Saygılarımızla arz ve talep ederiz.  

        Davacı Vekili  
        Av. {avukat_ad} {avukat_soyad}
    """.trimIndent()

            val dilekce = dilekceMetni
                .replace("{davaci_ad}", claimantName.text.toString())
                .replace("{davaci_soyad}", claimantSurname.text.toString())
                .replace("{davaci_adres}", claimantAddress.text.toString())
                .replace("{davali_ad}", respondentName.text.toString())
                .replace("{davali_soyad}", respondentSurname.text.toString())
                .replace("{davali_adres}", respondentAddress.text.toString())
                .replace("{evlilik_tarihi}", marriageDate.text.toString())
                .replace("{cocuk_durumu}", if (childStatus.text.toString().equals("Çocuk Yok!", ignoreCase = true))
                    "Tarafların ortak çocukları bulunmamaktadır."
                else childStatus.text.toString()
                )
                .replace("{avukat_ad}", lawyerName.toString())
                .replace("{avukat_soyad}", lawyerSurname.toString())

            // PDF oluştur
            createPdf(this,dilekce)

            // PDF'yi aç
            openPdf()
        }

    }

    /**
     * Sayfa için Margin Bottom ekleme function
     */
    private fun adjustScrollViewMargin(margin: Int) {
        val layoutParams = binding.divorcePetitionsScrolView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        binding.divorcePetitionsScrolView.layoutParams = layoutParams
    }


    /**
     * 📌 PDF Dosyası Oluşturma Fonksiyonu
     */
    private fun createPdf(context: Context, content: String) {
        try {

            val fileName = "bosanma_dilekcesi_$timestamp.pdf"
            val file = File(filesDir, fileName)

            val pdfDocument = android.graphics.pdf.PdfDocument()
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 boyutu
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val textPaint = TextPaint().apply {
                color = Color.BLACK
                textSize = 14f
                isAntiAlias = true
            }

            val leftMargin = 40
            val topMargin = 50
            val rightMargin = 40
            val contentWidth = pageInfo.pageWidth - leftMargin - rightMargin

            val staticLayout = StaticLayout.Builder.obtain(
                content, 0, content.length, textPaint, contentWidth
            )
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(1.0f, 1.2f)
                .setIncludePad(false)
                .build()

            canvas.save()
            canvas.translate(leftMargin.toFloat(), topMargin.toFloat()) // kenar boşluğu ayarı
            staticLayout.draw(canvas)
            canvas.restore()

            pdfDocument.finishPage(page)

            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)

            pdfDocument.close()
            outputStream.close()

            Toast.makeText(context, "✅ PDF başarıyla oluşturuldu!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "⚠️ PDF oluşturulurken hata oluştu!", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * 📌 PDF Dosyasını Açma Fonksiyonu
     */
    private fun openPdf() {
        try {
            // Dahili depolama alanındaki dosyayı al
            val file = File(filesDir, "bosanma_dilekcesi_$timestamp.pdf")  // Aynı alanda açıyoruz

            // Eğer dosya yoksa hata ver
            if (!file.exists()) {
                Toast.makeText(this, "⚠️ PDF bulunamadı!", Toast.LENGTH_SHORT).show()
                return
            }

            // FileProvider ile dosyanın URI'sini al
            val uri = FileProvider.getUriForFile(this, "${this.packageName}.provider", file)

            // PDF dosyasını açacak intent
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // PDF dosyasını aç
            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "⚠️ PDF açılırken hata oluştu!", Toast.LENGTH_SHORT).show()
        }
    }


}
