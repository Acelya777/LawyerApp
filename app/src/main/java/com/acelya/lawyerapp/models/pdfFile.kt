package com.acelya.lawyerapp.models

import kotlin.io.encoding.Base64

data class pdfFile(
    val pdfId: String = "",
    val base64Data: String? = null,
    val fileName: String? = null,
    val fileType: String? = null)
