package com.example.util

data class TrxValidationResult(
    val isValid: Boolean,
    val message: String
)

object TrxValidator {
    /**
     * Strict validation for Bangladesh Mobile Financial Services (bKash, Nagad, Rocket, Upay).
     * Prevents fake, dummy, malformed, or duplicate transaction IDs from verifying.
     */
    fun validate(
        methodName: String,
        trxId: String,
        existingTrxIds: Set<String> = emptySet()
    ): TrxValidationResult {
        val trimmed = trxId.trim().uppercase()

        if (trimmed.isEmpty()) {
            return TrxValidationResult(false, "ট্রানজেকশন আইডি (TrxID) লিখুন")
        }

        // Must be alphanumeric only
        if (!trimmed.all { it.isLetterOrDigit() }) {
            return TrxValidationResult(false, "TrxID-তে কোনো স্পেস বা স্পেশাল ক্যারেক্টার দেওয়া যাবে না")
        }

        // Minimum length check
        if (trimmed.length < 8) {
            return TrxValidationResult(false, "TrxID অন্তত ৮ অক্ষরের হতে হবে (বর্তমানে ${trimmed.length} অক্ষর)")
        }

        // Maximum length check
        if (trimmed.length > 14) {
            return TrxValidationResult(false, "TrxID সর্বোচ্চ ১৪ অক্ষরের হতে পারে (বর্তমানে ${trimmed.length} অক্ষর)")
        }

        // Prevent duplicate TrxIDs
        if (existingTrxIds.any { it.equals(trimmed, ignoreCase = true) }) {
            return TrxValidationResult(false, "এই ট্রানজেকশন আইডিটি ইতিমধ্যে ব্যবহৃত হয়েছে! সঠিক TrxID দিন")
        }

        // Block all repeated identical characters like "00000000", "11111111", "AAAAAAAA"
        if (trimmed.all { it == trimmed[0] }) {
            return TrxValidationResult(false, "ভুল বা ফেক ট্রানজেকশন আইডি! অনুগ্রহ করে সঠিক TrxID দিন")
        }

        // Block obvious dummy sequences
        val blockedDummies = setOf(
            "12345678", "123456789", "1234567890", "0123456789",
            "87654321", "987654321", "0987654321",
            "ABCDEFGH", "ABCDEFGHIJ", "QWERTYUI", "QWERTYUIOP",
            "ASDFGHJK", "ASDFGHJKLM", "ZXCVBNMM", "TESTTEST", "PAIDPAID",
            "OKOKOKOK", "DEMODEMO", "BKASHTRX", "NAGADTRX"
        )
        if (blockedDummies.contains(trimmed)) {
            return TrxValidationResult(false, "ভুল বা ফেক ট্রানজেকশন আইডি! আপনার এসএমএস থেকে সঠিক TrxID দেখে লিখুন")
        }

        val normMethod = methodName.lowercase()

        // Method-specific strict checks
        if (normMethod.contains("bkash") || normMethod.contains("বিকাশ")) {
            // bKash TrxID is exactly 10 alphanumeric characters (e.g. 9J87K56L2A or BL9K87M12P)
            if (trimmed.length != 10) {
                return TrxValidationResult(
                    false,
                    "বিকাশ TrxID অবশ্যই ঠিক ১০ অক্ষরের হতে হবে (বর্তমানে ${trimmed.length} অক্ষর, যেমন: 9J87K56L2A)"
                )
            }
            // bKash TrxIDs have both letters and digits
            if (!trimmed.any { it.isLetter() } || !trimmed.any { it.isDigit() }) {
                return TrxValidationResult(
                    false,
                    "ভুল বিকাশ TrxID! বিকাশ ট্রানজেকশন আইডিতে ইংরেজি অক্ষর ও সংখ্যা উভয়ই থাকে"
                )
            }
            return TrxValidationResult(true, "সঠিক বিকাশ TrxID ফরম্যাট")
        }

        if (normMethod.contains("nagad") || normMethod.contains("নগদ")) {
            // Nagad TrxID is 8 alphanumeric characters (e.g. 7K9J2L1M), occasionally 10
            if (trimmed.length != 8 && trimmed.length != 10) {
                return TrxValidationResult(
                    false,
                    "নগদ TrxID সাধারণত ৮ বা ১০ অক্ষরের হতে হবে (বর্তমানে ${trimmed.length} অক্ষর, যেমন: 7K9J2L1M)"
                )
            }
            return TrxValidationResult(true, "সঠিক নগদ TrxID ফরম্যাট")
        }

        if (normMethod.contains("rocket") || normMethod.contains("রকেট")) {
            if (trimmed.length < 8 || trimmed.length > 12) {
                return TrxValidationResult(
                    false,
                    "রকেট TrxID সাধারণত ৮ থেকে ১২ অক্ষরের হতে হবে"
                )
            }
            return TrxValidationResult(true, "সঠিক রকেট TrxID ফরম্যাট")
        }

        if (normMethod.contains("upay") || normMethod.contains("উপায়")) {
            if (trimmed.length < 8 || trimmed.length > 12) {
                return TrxValidationResult(
                    false,
                    "উপায় TrxID সাধারণত ৮ থেকে ১২ অক্ষরের হতে হবে"
                )
            }
            return TrxValidationResult(true, "সঠিক উপায় TrxID ফরম্যাট")
        }

        return TrxValidationResult(true, "ট্রানজেকশন আইডি ফরম্যাট সঠিক")
    }
}
