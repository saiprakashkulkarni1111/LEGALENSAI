package com.example.legalens.data.security

import java.util.regex.Pattern

data class PiiSanitizeResult(
    val sanitizedText: String,
    val redactionCount: Int,
    val detectedTypes: List<String>
)

object PiiSanitizer {
    private val PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]")
    private val AADHAAR_PATTERN = Pattern.compile("\\b[2-9]{1}[0-9]{3}\\s?[0-9]{4}\\s?[0-9]{4}\\b")
    private val PHONE_PATTERN = Pattern.compile("(?:\\+91[\\-\\s]?)?[6-9]\\d{9}\\b")
    private val EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val BANK_AC_PATTERN = Pattern.compile("\\b(?:A/C|Account|Acc)?\\s*(?:No\\.?)?\\s*:?\\s*([0-9]{9,18})\\b", Pattern.CASE_INSENSITIVE)

    fun sanitize(input: String): PiiSanitizeResult {
        var text = input
        var count = 0
        val detected = mutableListOf<String>()

        // 1. Aadhaar
        val aadhaarMatcher = AADHAAR_PATTERN.matcher(text)
        if (aadhaarMatcher.find()) {
            detected.add("Aadhaar Number")
            text = aadhaarMatcher.replaceAll("[REDACTED_AADHAAR]")
            count++
        }

        // 2. PAN
        val panMatcher = PAN_PATTERN.matcher(text)
        if (panMatcher.find()) {
            detected.add("Income Tax PAN")
            text = panMatcher.replaceAll("[REDACTED_PAN]")
            count++
        }

        // 3. Email
        val emailMatcher = EMAIL_PATTERN.matcher(text)
        if (emailMatcher.find()) {
            detected.add("Email Address")
            text = emailMatcher.replaceAll("[REDACTED_EMAIL]")
            count++
        }

        // 4. Phone
        val phoneMatcher = PHONE_PATTERN.matcher(text)
        if (phoneMatcher.find()) {
            detected.add("Phone Number")
            text = phoneMatcher.replaceAll("[REDACTED_PHONE]")
            count++
        }

        // 5. Bank Account
        val bankMatcher = BANK_AC_PATTERN.matcher(text)
        if (bankMatcher.find()) {
            detected.add("Bank Account")
            text = bankMatcher.replaceAll("[REDACTED_BANK_ACCOUNT]")
            count++
        }

        return PiiSanitizeResult(text, count, detected)
    }
}
