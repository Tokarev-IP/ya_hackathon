package com.it.ya_hackathon.basic

import com.it.ya_hackathon.data.services.DataConstants.LANGUAGE_TEXT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round
import kotlin.text.iterator

fun Int.isNotZero(): Boolean = this != 0
fun Int.isPositive(): Boolean = this > 0
fun Int.isMoreThanOne(): Boolean = this > 1
fun Float.isNotZero(): Boolean = this != 0F
fun Float.isPositive(): Boolean = this >= 0
fun Int?.getOrZero(): Int {
    return if (this == null || this == 0) 0 else this
}

fun String.isEmail(): Boolean {
    val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return emailRegex.matches(this)
}

fun String.isCorrectPassword(): Boolean {

    for (char in this) {
        if (char.isWhitespace())
            return false
    }

    fun isLength(): Boolean {
        return this.length >= 10
    }

    fun isContainNumeric(): Boolean {
        for (char in this) {
            if (char.isDigit())
                return true
        }
        return false
    }

    fun isContainLowercaseLetter(): Boolean {
        for (char in this) {
            if (char.isLetter() && char.isLowerCase())
                return true
        }
        return false
    }

    fun isContainUppercaseLetter(): Boolean {
        for (char in this) {
            if (char.isLetter() && char.isUpperCase())
                return true
        }
        return false
    }

    return isLength() && isContainNumeric() &&
            isContainLowercaseLetter() && isContainUppercaseLetter()
}

fun Float.roundToTwoDecimalPlaces(): Float {
    return (round(this * 100) / 100f)
}

fun Long.convertMillisToDate(): String {
    runCatching {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date(this))
    }.getOrElse {
        return ""
    }
}

fun String.convertDateToMillis(): Long {
    runCatching {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = formatter.parse(this)
        return date?.time ?: 0L
    }.getOrElse {
        return 0L
    }
}

fun String.getLanguageString(
    translateTo: String?,
    languageText: String = LANGUAGE_TEXT,
): String {
    return if (translateTo != null) {
        "$this $languageText $translateTo"
    } else
        this
}