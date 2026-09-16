package com.tf.clasificacioncutter.data.model

import com.tf.clasificacioncutter.data.settings.IAppSettings
import com.tf.clasificacioncutter.utils.extensions.strippingLeadingArticles
import java.util.UUID

data class CutterData(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val code: String,
    val authorName: String = "",
    private var _authorSurname: String = "",
    val isbn: String = "",
    val bookName: String = "",
    val ddcs: List<String> = emptyList(),
    val ddcSelected: String = "",
    val needsReview: Boolean = false,
    val dontSeparateName: Boolean = false, // Don't split the name and surname
    val ignoreArticles: Boolean = true,
    val useFormatMac: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
) {
    val selectedDdc: String = ddcSelected.ifEmpty { ddcs.firstOrNull() ?: "" }

    val number: String
        get() {
            val firstLetter = name.firstOrNull() ?: return code
            return "$firstLetter$code".uppercase()
        }

    val cutterUsed: String
        get() = "$name:$code".uppercase()

    val searchValue: String
        get() {
            if (authorName.isEmpty() || dontSeparateName) {
                return authorSurname
            }
            return "$authorSurname, $authorName"
        }

    var authorSurname: String
        get() {
            if (this.dontSeparateName) {
                val combined = "${this.authorName} ${this._authorSurname}"
                return combined.trim()
            }
            return this._authorSurname
        }
        set(value) {
            this._authorSurname = value
        }
}

// Extensions

/**
 * Generates a fully formatted Call/Cutter Number based on active AppSettings.
 */
fun CutterData.fullCallNumber(settings: IAppSettings): String {
    // Resolve prefix text (Author Surname vs Title)
    val prefixSource = if (settings.textBeforeNum == TextBeforeAfterNum.AUTHOR_SURNAME) {
        authorSurname
    } else {
        bookName
    }

    val cleanPrefix = prefixSource.strippingLeadingArticles(ignoreArticles)
    val prefix = cleanPrefix.take(settings.charsBeforeNum).trim()

    // Resolve suffix text (Author Surname vs Title)
    val suffixSource = if (settings.textAfterNum == TextBeforeAfterNum.AUTHOR_SURNAME) {
        authorSurname
    } else {
        bookName
    }

    val cleanSuffix = suffixSource.strippingLeadingArticles(ignoreArticles)
    val suffix = cleanSuffix.take(settings.charsAfterNum).trim()

    // Assemble: [Prefix][Cutter Number][Suffix] (e.g. "Bs825T")
    return "$prefix${this.code}$suffix"
}