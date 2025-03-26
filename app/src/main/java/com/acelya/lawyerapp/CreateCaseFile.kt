package com.acelya.lawyerapp

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acelya.lawyerapp.databinding.ActivityCreateCaseFileBinding
import com.acelya.lawyerapp.databinding.ActivityLogInBinding

class CreateCaseFile : AppCompatActivity() {
    private val binding by lazy {
        ActivityCreateCaseFileBinding.inflate(layoutInflater)
    }

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        rootView = findViewById(android.R.id.content)

        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private val rect = Rect()

            override fun onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) {
                    // Klavye açıkken ScrollView kaydırma yap
                    adjustScrollViewMargin(1200) // Margin değerini ayarlayabilirsin
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
    }
    private fun adjustScrollViewMargin(margin: Int) {
        val layoutParams = binding.scrollView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        binding.scrollView.layoutParams = layoutParams
    }
}