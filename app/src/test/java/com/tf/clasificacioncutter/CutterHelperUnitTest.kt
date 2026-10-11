package com.tf.clasificacioncutter

import com.tf.clasificacioncutter.utils.extensions.removeAccents
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testing String removeAccents extension methods.
 */
class CutterHelperUnitTest {

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
}
