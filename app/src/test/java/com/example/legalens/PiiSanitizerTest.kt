package com.example.legalens

import com.example.legalens.data.security.PiiSanitizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PiiSanitizerTest {

    @Test
    fun testAadhaarRedaction() {
        val raw = "The employee's Aadhaar number is 9876 5432 1098 as verified."
        val result = PiiSanitizer.sanitize(raw)
        assertTrue(result.detectedTypes.any { it.contains("Aadhaar", ignoreCase = true) })
        assertFalse(result.sanitizedText.contains("9876 5432 1098"))
        assertTrue(result.sanitizedText.contains("[REDACTED_AADHAAR]"))
    }

    @Test
    fun testPanRedaction() {
        val raw = "Income tax PAN: ABCDE1234F provided for TDS processing."
        val result = PiiSanitizer.sanitize(raw)
        assertTrue(result.detectedTypes.any { it.contains("PAN", ignoreCase = true) })
        assertFalse(result.sanitizedText.contains("ABCDE1234F"))
        assertTrue(result.sanitizedText.contains("[REDACTED_PAN]"))
    }

    @Test
    fun testPhoneRedaction() {
        val raw = "Contact phone: +91 9876543210 for notice delivery."
        val result = PiiSanitizer.sanitize(raw)
        assertTrue(result.detectedTypes.contains("Phone Number"))
        assertTrue(result.sanitizedText.contains("[REDACTED_PHONE]"))
    }

    @Test
    fun testCleanTextPreserved() {
        val clean = "This agreement is governed by Section 27 of the Indian Contract Act, 1872."
        val result = PiiSanitizer.sanitize(clean)
        assertEquals(0, result.redactionCount)
        assertEquals(clean, result.sanitizedText)
    }
}
