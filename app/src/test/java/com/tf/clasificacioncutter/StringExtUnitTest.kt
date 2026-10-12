package com.tf.clasificacioncutter

import com.tf.clasificacioncutter.utils.CutterGetter
import com.tf.clasificacioncutter.utils.extensions.extractISBNs
import com.tf.clasificacioncutter.utils.extensions.removeAccents
import com.tf.clasificacioncutter.utils.extensions.strippingLeadingArticles
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for String extensions and helper utilities.
 */
class StringExtUnitTest {

    @Test
    fun removeAccents_works() {
        assertEquals("FONSECA", "FONSECA".removeAccents())
        assertEquals("AEIOU", "ÁÉÍÓÚ".removeAccents())
        assertEquals("NINO", "NIÑO".removeAccents())
    }

    @Test
    fun removeAccents_emptyString() {
        assertEquals("", "".removeAccents())
    }

    @Test
    fun removeAccents_withNumbersAndSpecialChars() {
        assertEquals("PRUEBA 123!", "Prueba 123!".removeAccents())
        assertEquals("HOLA-MUNDO", "hóla-múndö".removeAccents())
    }

    @Test
    fun removeAccents_lowercaseInput() {
        assertEquals("ABC", "abc".removeAccents())
    }

    @Test
    fun removeAccents_mixedCharacters() {
        assertEquals("CAFE", "café".removeAccents())
        assertEquals("CANON", "cañón".removeAccents())
        assertEquals("LEON", "León".removeAccents())
    }

    @Test
    fun removeAccents_cedilla() {
        assertEquals("GONCALVES", "Gonçalves".removeAccents())
        assertEquals("FRANCA", "França".removeAccents())
    }

    @Test
    fun extractISBNs_validFormats() {
        val result1 = "ISBN 978-3-16-148410-0".extractISBNs()
        assertEquals(1, result1.size)
        assertEquals("9783161484100", result1[0])

        val result2 = "Check this 9780321356680 book".extractISBNs()
        assertEquals(1, result2.size)
        assertEquals("9780321356680", result2[0])
    }

    @Test
    fun extractISBNs_invalidFormats() {
        val result = "Invalid 12345 number".extractISBNs()
        assertEquals(0, result.size)
    }

    @Test
    fun splitText_commaSeparated() {
        val (name, surname) = CutterGetter.splitText("Borges, Jorge Luis")
        assertEquals("Jorge Luis", name)
        assertEquals("Borges", surname)
    }

    @Test
    fun splitText_spaceSeparated() {
        val (name, surname) = CutterGetter.splitText("Jorge Luis Borges")
        assertEquals("Jorge Luis", name)
        assertEquals("Borges", surname)
    }

    @Test
    fun splitText_singleWord() {
        val (name, surname) = CutterGetter.splitText("Borges")
        assertEquals("", name)
        assertEquals("Borges", surname)
    }

    @Test
    fun strippingLeadingArticles_works() {
        assertEquals("King in Yellow", "The King in Yellow".strippingLeadingArticles(shouldIgnore = true))
        assertEquals("Los Miserables", "Los Miserables".strippingLeadingArticles(shouldIgnore = false))
    }
}
