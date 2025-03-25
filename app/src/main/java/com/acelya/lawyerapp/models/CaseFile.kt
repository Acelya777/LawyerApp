package com.acelya.lawyerapp.models

data class CaseFile(val caseId: String = "",
                    val caseName: String = "",
                    val caseType: String = "",
                    val clientId: String = "",
                    val endDate: String = "",
                    val lawyerId: String = "",
                    val notes: String = "",
                    val startDate: String = "",
                    val status: String = "")

