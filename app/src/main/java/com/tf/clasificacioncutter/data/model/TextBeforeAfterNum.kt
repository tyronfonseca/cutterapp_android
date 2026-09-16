package com.tf.clasificacioncutter.data.model

/**
 * Determines whether to use the author's surname or book title when formatting call numbers.
 */
enum class TextBeforeAfterNum(val value: Int) {
    AUTHOR_SURNAME(0),
    TITLE(1);

    companion object {
        fun fromInt(value: Int): TextBeforeAfterNum =
            entries.firstOrNull { it.value == value } ?: AUTHOR_SURNAME
    }
}